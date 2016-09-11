package pub.amitabha;

import java.io.Serializable;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pub.amitabha.domain.Setting;
import pub.amitabha.domain.SettingRepository;
import pub.amitabha.json.PageList;

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

		return "adminSettings";
	}

	/**
	 * Access as /admin/settings/list?type=GENERAL
	 * The default value of the type is GENERAL, you can specify some.
	 * @param request
	 * @return A JSON like {"totalPage":2,"currentPage":1,"list":{"key 1":"value 1","key 2":"value 2"}}
	 */
	@RequestMapping(value = "/admin/settings/list", method = RequestMethod.GET)
	public @ResponseBody PageList adminSettingList(HttpServletRequest request) {
		String type = request.getParameter("type");
		if(type == null)
			type = Setting.GENERAL;
		Setting setting = repo.findOne(type);
		Hashtable<String, Serializable> map;

		map = (setting==null) ? null : setting.getSetting();

		PageList respJSON = new PageList();
		respJSON.setCurrentPage(1);
		respJSON.setTotalPage(1);
		respJSON.setList(map);

		return respJSON;
	}

	@RequestMapping(value = "/admin/settings", method = RequestMethod.POST)
	public String adminSetting(HttpServletRequest request, Model model) {
		String type = request.getParameter("type");
		if(type == null)
			type = Setting.GENERAL;

		return "adminAddUser";
	}
}
