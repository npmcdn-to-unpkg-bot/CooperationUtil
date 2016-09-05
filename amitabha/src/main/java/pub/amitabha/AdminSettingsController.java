package pub.amitabha;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pub.amitabha.domain.Setting;
import pub.amitabha.domain.SettingRepository;

@Controller
public class AdminSettingsController {
	@Autowired
	private SettingRepository repo;


	/**
	 * Access as /admin/settings?type=GENERAL
	 * The default value of the type is GENERAL, you can specify some.
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/admin/settings", method = RequestMethod.GET)
	public String adminPreSetting(HttpServletRequest request, Model model) {
		String type = request.getParameter("type");
		if(type == null)
			type = Setting.GENERAL;

		Setting setting = repo.findOne(type);
		List<String> keys;
		Hashtable<String, Serializable> map;

		map = (setting==null) ? null : setting.getSetting();
		keys = (map==null) ? Collections.emptyList() : Collections.list(map.keys());

		model.addAttribute("type", type);
		model.addAttribute("keys", keys);
		model.addAttribute("settingMap", map);

		return "settings";
	}

	@RequestMapping(value = "/admin/settings", method = RequestMethod.POST)
	public String adminSetting(HttpServletRequest request, Model model) {
		String type = request.getParameter("type");
		if(type == null)
			type = Setting.GENERAL;

		return "adminAddUser";
	}
}
