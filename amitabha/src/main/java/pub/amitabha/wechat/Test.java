package pub.amitabha.wechat;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import pub.amitabha.Application;
import pub.amitabha.util.DateToTimestamp;

//While token is "abcd", signature is "7fa7c49f09ee70232257bcdc23cb7fb9846cf012"
//While token is "NamasAmitabha", signature is "af574c698bf28704f7802ae1d90403722cba0e1f"
//http://localhost:8080/wechat/checkSignature?timestamp=1463897822821&nonce=lightapp5536&signature=af574c698bf28704f7802ae1d90403722cba0e1f&echostr=Success

@SuppressWarnings("unused")
@RestController
public class Test {
	public static final String TOKEN = "EmsNamasAmitabha";
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String encodingAesKey = "r6HnsBY3xSffXtylLFWN5q56OmU0fsfv6zKIY48vh88";
	private static final String appId = "wx0853ea616bcedc07";

	@Autowired
	private ApplicationContext context;
	@Autowired
	List<HttpMessageConverter<?>> converters;

	private HttpServletResponse response;
	private HttpServletRequest request;
	private HttpSession session;

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

	@RequestMapping(value = "/wechat/test")
	public String checkSignature(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println(request.getRequestURL() + " " + request.getQueryString());
		if (request.getMethod().equals("POST")) {
			BufferedReader reader = request.getReader();
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
		}
		return "Hello";
	}
}
