package pub.amitabha.wechat;

import java.io.Serializable;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pub.amitabha.domain.Setting;
import pub.amitabha.domain.SettingRepository;
import pub.amitabha.domain.User;
import pub.amitabha.domain.UserForm;
import pub.amitabha.domain.UserRepository;
import pub.amitabha.wechat.domain.MyAuthorizationCode;
import pub.amitabha.wechat.domain.MyAuthorizationCodeRepository;

@Controller
public class WechatWebController {

	@Autowired
	MyAuthorizationCodeRepository repoAut;
	@Autowired
	SettingRepository repoSetting;

	@RequestMapping(value = "/wechat/setting/detail", method = RequestMethod.GET)
	public String viewSetting(@RequestParam(value = "wechatId", required = true) String wechatId,
			Model model) {
		Setting wechatSetting = repoSetting.findOne(wechatId);
		if(wechatSetting != null) {
			model.addAttribute("setting", wechatSetting.getSetting(WechatSetting.class));
		} else {
			WechatSetting setting = new WechatSetting();
			model.addAttribute("setting", setting);
		}

		return "wechatSettingDetails";
	}

	@RequestMapping(value = "/wechat/setting", method = RequestMethod.GET)
	public String preSetting(@RequestParam(value = "wechatId", required = true) String wechatId,
			Model model) {
		Setting genSetting = repoSetting.getGeneralSetting();
		Setting wechatSetting = repoSetting.findOne(wechatId);
		if(genSetting.getSetting() == null) {
			if(wechatSetting != null) {
				model.addAttribute("setting", wechatSetting.getSetting(WechatSetting.class));
			} else {
				WechatSetting setting = new WechatSetting();
				model.addAttribute("setting", setting);
			}
		} else {
			if(wechatSetting != null) {
				model.addAttribute("setting", wechatSetting.getSetting(WechatSetting.class));
			} else {
				WechatSetting setting = new WechatSetting();
				setting.setOpenId(wechatId);
				setting.setToken((String)genSetting.getSetting().get(WechatSetting.TOKEN));
				model.addAttribute("setting", setting);
			}
		}

		return "wechatSetting";
	}

	@RequestMapping(value = "/wechat/setting", method = RequestMethod.POST)
	public String setting(@RequestParam(value = "wechatId", required = true) String wechatId,
			WechatSetting setting,
			Model model) throws Exception {
		Setting genSetting = repoSetting.getGeneralSetting();
		Setting wechatSetting = repoSetting.findOne(wechatId);

		if(wechatSetting == null)
			wechatSetting = new Setting(wechatId);
		wechatSetting.setSettingDetails(setting);
		repoSetting.save(wechatSetting);

		//After updated the wechat setting, update the general setting with wechat token.
		java.util.Hashtable<String, Serializable> ht = genSetting.getSetting();
		if(genSetting.getSetting() == null) {
			ht = new java.util.Hashtable<String, Serializable>();
		} 
		ht.put(WechatSetting.TOKEN, setting.getToken());
		genSetting.setSettingDetails(ht);
		repoSetting.save(genSetting);

		model.addAttribute("wechatId", wechatId);
		return "redirect:/wechat/setting/detail";
	}

	@RequestMapping(value = "/wechat/reg", method = RequestMethod.GET)
	public String registration(@RequestParam(value = "wechatId", required = true) String wechatId,
			@RequestParam(value = "nonce", required = true) String nonce, Model model, UserForm userForm) {
		MyAuthorizationCode autCode = repoAut.findByWechatIdAndNonce(wechatId, nonce);
		if (autCode == null) {
			model.addAttribute("errorMessage", "This registration address has been expired! Please request again!");
			return "error";
		}
		model.addAttribute("wechatId", wechatId);
		model.addAttribute("nonce", nonce);

		userForm.setForeignId("wechat" + wechatId);

		return "wechatReg";
	}

	@Autowired
	UserRepository repoUser;

	@RequestMapping(value = "/wechat/add", method = RequestMethod.POST)
	public String userAddSumission(@Valid UserForm userForm, Model model, BindingResult bindingResult,
			@RequestParam(value = "wechatId", required = true) String wechatId,
			@RequestParam(value = "nonce", required = true) String nonce) {
		MyAuthorizationCode autCode = repoAut.findByWechatIdAndNonce(wechatId, nonce);
		if (autCode == null) {
			model.addAttribute("errorMessage", "This registration address has been expired! Please request again!");
			return "error";
		}

		if (!userForm.getConfirmPassword().equals(userForm.getPassword())) {
			bindingResult.addError(new FieldError("userForm", "confirmPassword", "Should be equal to password!"));
		}

		// Check if the user name exist or not
		if (!bindingResult.hasErrors()) {
			User user = null;
			try {
				user = repoUser.findByName(userForm.getName());
			} catch (Exception e) {
				user = null;
			}
			if (user != null)
				bindingResult.addError(new FieldError("userForm", "name", "Name already exist!"));
		}

		if (bindingResult.hasErrors()) {
			return "wechatReg";
		}
		User user = userForm.cloneUser();
		user.encryptPassword();
		repoUser.save(user);

		String sfmt = "redirect:/wechat/details?id=%s&wechatId=%s&nonce=%s";
		return String.format(sfmt, user.getId(), wechatId, nonce);
	}

	@RequestMapping(value = "/wechat/details", method = RequestMethod.GET)
	public String userDetails(@RequestParam(value = "id", required = true) long id,
			@RequestParam(value = "wechatId", required = true) String wechatId,
			@RequestParam(value = "nonce", required = true) String nonce, Model model) {
		MyAuthorizationCode autCode = repoAut.findByWechatIdAndNonce(wechatId, nonce);
		if (autCode == null) {
			model.addAttribute("errorMessage", "This registration address has been expired! Please request again!");
			return "error";
		}

		if (repoUser.exists(id)) {
			model.addAttribute("user", repoUser.findOne(id));
			return "wechatDetails";
		} else {
			model.addAttribute("errorMessage", "This registration address has been expired! Please request again!");
		}
		return "error";
	}
}
