package pub.amitabha.wechat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import pub.amitabha.Application;
import pub.amitabha.domain.PersonalRecord;
import pub.amitabha.domain.PersonalRecordCompareByDate;
import pub.amitabha.domain.PersonalRecordRepository;
import pub.amitabha.domain.SettingRepository;
import pub.amitabha.domain.User;
import pub.amitabha.domain.UserForm;
import pub.amitabha.domain.UserRepository;
import pub.amitabha.util.DateToTimestamp;
import pub.amitabha.wechat.domain.MyAuthorizationCode;
import pub.amitabha.wechat.domain.MyAuthorizationCodeRepository;

@SuppressWarnings("unused")
@RestController
public class WechatController {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserRepository repoUser;
	@Autowired
	PersonalRecordRepository repoPersonalRecord;
	@Autowired
	SettingRepository repoSetting;

	@Autowired
	List<HttpMessageConverter<?>> converters;

	private HttpServletResponse response;
	private HttpServletRequest request;
	private HttpSession session;
	
	Hashtable<String, WechatSetting> setting = new Hashtable<>();

	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
		// Iterator<HttpMessageConverter<?>> it = converters.iterator();
		// while(it.hasNext()){
		// HttpMessageConverter<?> convertor = it.next();
		// if(convertor.getClass().equals(StringHttpMessageConverter.class)){
		// StringHttpMessageConverter strConvertor =
		// (StringHttpMessageConverter)convertor;
		// strConvertor.setSupportedMediaTypes(Arrays.asList(new
		// MediaType("text", "plain", Charset.forName("UTF-8"))));
		// }
		// if(convertor.getClass().equals(MappingJackson2HttpMessageConverter.class)){
		// MappingJackson2HttpMessageConverter jsonConvertor =
		// (MappingJackson2HttpMessageConverter)convertor;
		// jsonConvertor.setSupportedMediaTypes(Arrays.asList(new
		// MediaType("application", "json", Charset.forName("UTF-8"))));
		// jsonConvertor.setSupportedMediaTypes(Arrays.asList(new
		// MediaType("application", "xml", Charset.forName("UTF-8"))));
		// }
		//
		// }
	}

	@RequestMapping(value = "/wechat", method = RequestMethod.GET)
	public String checkSignature(@RequestParam(value = "signature", required = true) String signature,
			@RequestParam(value = "timestamp", required = true) String timestamp,
			@RequestParam(value = "nonce", required = true) String nonce,
			@RequestParam(value = "echostr", required = true) String echostr) {
		String TOKEN = (String) repoSetting.getGeneralSetting().getSetting().get(WechatSetting.TOKEN);
		boolean valid = WeChat.validate(TOKEN, signature, timestamp, nonce);

		if (valid)
			return echostr;
		else
			return "";
	}

	@RequestMapping(value = "/wechatSafe", method = RequestMethod.GET)
	public String checkSafeSignature(@RequestParam(value = "signature", required = true) String signature,
			@RequestParam(value = "timestamp", required = true) String timestamp,
			@RequestParam(value = "nonce", required = true) String nonce,
			@RequestParam(value = "echostr", required = true) String echostr) {
		return checkSignature(signature, timestamp, nonce, echostr);
	}

	@RequestMapping(value = "/wechatSafe", method = RequestMethod.POST)
	public String handleSafeRequest(@RequestParam(value = "msg_signature", required = true) String msgSignature,
			@RequestParam(value = "timestamp", required = true) String timestamp,
			@RequestParam(value = "nonce", required = true) String nonce, @RequestBody String xmlContent) {
		try {
			XmlRequest encryptedXml = XmlRequest.fromXmlString(xmlContent);
			String openId = encryptedXml.getToUserName();
			if(!setting.containsKey(openId))
				setting.put(openId, repoSetting.findOne(openId).getSetting(WechatSetting.class));
			WechatSetting ws = setting.get(openId);
			WXBizMsgCrypt pc = WXBizMsgCrypt.getInstance(ws.getToken(), ws.getEncodingAesKey(), ws.getAppId());
			String xmlDecrypted = pc.decryptMsg(msgSignature, timestamp, nonce, xmlContent);
			log.info(xmlDecrypted);

			// String replyMsg = "\nFrom: " + xr.getFromUserName() +
			// "\nTo: " + xr.getToUserName() +
			// "\nMessageType: " + xr.getMsgType() +
			// "\nSaying: " + xr.getContent() +
			// "\nAt: " + DateToTimestamp.getTimestamp(xr.getCreateTime());
			// log.info(replyMsg);

			XmlRequest xRequest = XmlRequest.fromXmlString(xmlDecrypted);
			XmlResponseText xResponse = processRequest(xRequest, nonce);
			if (xResponse == null) {
				return "Sorry! Your request type has not been supported!";
			}

			// String format =
			// "<xml><ToUserName><![CDATA[%1$s]]></ToUserName><Encrypt><![CDATA[%2$s]]></Encrypt></xml>";
			return pc.encryptMsg(xResponse.toString(), timestamp, nonce);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Autowired
	MyAuthorizationCodeRepository repoAut;

	public XmlResponseText processRequest(XmlRequest xmlRequest, String nonce) {
		if (xmlRequest.isText()) {
			String reqStr = xmlRequest.getContent().trim();

			log.info("Locale: " + LocaleContextHolder.getLocale());
			String welcomeMessage = context.getMessage("wechat.welcomeMessage", null, LocaleContextHolder.getLocale());
			if (reqStr.contains("阿弥陀佛")) {
				System.out.println("A buddhist says 'Namas Amitabha!'");
			}

			if (reqStr.equals("注册")) {
				User user = repoUser.findByForeignId("wechat" + xmlRequest.getFromUserName());
				if (user != null) {
					welcomeMessage = "你已经注册过了";
				} else {
					repoAut.save(new MyAuthorizationCode(xmlRequest.getFromUserName(), nonce));
					String servlet = request.getServletPath();
					StringBuffer url = request.getRequestURL();
					url.delete(url.indexOf(servlet), url.length());
					url.append("/wechat/reg");
					String fmt = "<a href=\"%s?wechatId=%s&nonce=%s\" >Click to register</a>";
					return new XmlResponseText(xmlRequest.getFromUserName(), xmlRequest.getToUserName(),
							String.format(fmt, url, xmlRequest.getFromUserName(), nonce));
				}
			}

			if (reqStr.startsWith("记录")) {
				// System.out.println(reqStr);
				String regexStr1 = "记录(\\D+)(\\d+)";
				String regexStr2 = "记录(\\D+)(\\d+)\\D+([0-9]{4,})\\D([0-9]{1,2})\\D([0-9]{1,2})";
				Pattern p = null;
				String updateDate = null;

				if (Pattern.matches(regexStr1, reqStr)) {
					p = Pattern.compile(regexStr1);
					updateDate = DateToTimestamp.getDate();
				}
				if (Pattern.matches(regexStr2, reqStr)) {
					p = Pattern.compile(regexStr2);
				}

				if (p == null) {
					welcomeMessage = "格式有误！请用如下格式 ‘记录佛号10000’ 或者 ‘记录佛号10000 2016-01-01’";
				} else {

					Matcher m = p.matcher(reqStr);
					m.find();

					String type = m.group(1).trim();
					String amountX = m.group(2);
					if (updateDate == null) {
						String yyyy = m.group(3);
						String mm = m.group(4);
						String dd = m.group(5);

						updateDate = String.format("%s-%02d-%02d", yyyy, Integer.valueOf(mm), Integer.valueOf(dd));
					}

					System.out.println(type + " " + updateDate);

					String welcomePrefix;
					User user = repoUser.findByForeignId("wechat" + xmlRequest.getFromUserName());
					if (user == null) {
						welcomeMessage = "You have not yet register an account";
					} else {
						int amount;
						try {
							PersonalRecord pr = repoPersonalRecord.findByUserIdAndTypeAndRecordDate(user.getId(), type,
									updateDate);
							amount = Integer.parseInt(amountX);
							if (pr == null) {
								pr = new PersonalRecord(user.getId(), type, amount);
								welcomePrefix = "已记录：";
							} else {
								pr.setAmount(amount);
								welcomePrefix = "已更新：";
							}

							pr.setRecordDate(updateDate);
							repoPersonalRecord.save(pr);
							welcomeMessage = welcomePrefix + pr.getRecordDate() + " " + pr.getAmount();
						} catch (Exception e) {
							welcomeMessage = "格式有误！请用如下格式 ‘记录佛号10000’ 或者 ‘记录佛号10000 2016-01-01’";
						}
					}
				}
			}

			if (reqStr.startsWith("我的")) {
				String regexStr1 = "我的(.+)详细";
				String regexStr2 = "我的(.+)";
				Pattern p = null;
				boolean getDetails = false;

				if (Pattern.matches(regexStr1, reqStr)) {
					p = Pattern.compile(regexStr1);
					getDetails = true;
				} else
					p = Pattern.compile(regexStr2);

				Matcher m = p.matcher(reqStr);
				m.find();

				if (m.matches()) {
					String type = m.group(1).trim();

					User user = repoUser.findByForeignId("wechat" + xmlRequest.getFromUserName());
					if (user == null) {
						welcomeMessage = "You have not yet register an account";
					} else {
						List<PersonalRecord> prs = repoPersonalRecord.findByUserIdAndType(user.getId(), type);
						long amount = 0;
						if (!getDetails) {
							for (PersonalRecord pr : prs) {
								amount += pr.getAmount();
							}
							welcomeMessage = reqStr + "总数: " + amount;
						} else {
							prs.sort(new PersonalRecordCompareByDate());
							StringBuilder sb = new StringBuilder();
							for (PersonalRecord pr : prs) {
								sb.append(pr.getRecordDate());
								sb.append(" ");
								sb.append(pr.getAmount());
								sb.append("\n");
							}
							welcomeMessage = sb.toString();
						}
					}
				} else {
					welcomeMessage = "格式有误！请用如下格式 ‘我的佛号’，‘我的大悲咒’等。";
				}
			}

			// response.setContentType("text/plain;charset=UTF-8");
			// response.setCharacterEncoding("UTF-8");
			// response.setHeader("contentType", "text/plain;charset=UTF-8");
			return new XmlResponseText(xmlRequest.getFromUserName(), xmlRequest.getToUserName(), welcomeMessage);
		} else
			return null;
	}

	@RequestMapping(value = "/wechat", method = RequestMethod.POST)
	public String handleRequest(@RequestParam(value = "signature", required = true) String signature,
			@RequestParam(value = "timestamp", required = true) String timestamp,
			@RequestParam(value = "nonce", required = true) String nonce, @RequestBody XmlRequest xmlContent) {
		String openId = xmlContent.getToUserName();
		if(!setting.containsKey(openId))
			setting.put(openId, repoSetting.findOne(openId).getSetting(WechatSetting.class));
		boolean valid = WeChat.validate(setting.get(openId).getToken(), signature, timestamp, nonce);

		if (valid) {
			// log.info("\nFrom: " + xmlContent.getFromUserName() +
			// "\nTo: " + xmlContent.getToUserName() +
			// "\nMessageType: " + xmlContent.getMsgType() +
			// "\nSaying: " + xmlContent.getContent() +
			// "\nAt: " +
			// DateToTimestamp.getTimestamp(xmlContent.getCreateTime()));
			XmlResponseText xResponse = processRequest(xmlContent, nonce);
			if (xResponse == null) {
				return "Sorry! Your request type has not been supported!";
			}

			return xResponse.toString();
		} else
			return "Your request is unauthorized!";
	}

}
