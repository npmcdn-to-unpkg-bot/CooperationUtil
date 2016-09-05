package pub.amitabha;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pub.amitabha.domain.SessionUser;
import pub.amitabha.domain.SessionUserRepository;
import pub.amitabha.domain.User;
import pub.amitabha.domain.UserForm;
import pub.amitabha.domain.UserLoginForm;
import pub.amitabha.domain.UserRepository;
import pub.amitabha.util.AuthorizationInfo;
import pub.amitabha.util.ObjectStringConverter;

@Controller
public class UserController {
	@Autowired
	private UserRepository repo;

	@Autowired
	private SessionUserRepository repoSession;

	@RequestMapping(value = "/user/details")
	public String userDetails(@RequestParam(value = "id", required = true) long id, Model model) {
		if (repo.exists(id)) {
			model.addAttribute("user", repo.findOne(id));
			return "userDetails";
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/user/reg", method = RequestMethod.GET)
	public String userRegistration(UserForm userForm) {
		return "userReg";
	}

	/**
	 * Logout the system. And remove the session record.
	 */
	public void logOut(HttpServletRequest request) {
		String sessionId = request.getParameter("sessionId");
		try {
			repoSession.delete(sessionId);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public String userLogout(HttpServletRequest request) {
		logOut(request);

		return "redirect:/user/login";
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public String userLogin(UserLoginForm userLoginForm, HttpServletRequest request) {
		logOut(request);
		return "userLogin";
	}

	/**
	 * Only verify the session Id, and ID. No need to verify the password if
	 * sign on with third party.
	 * 
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/loginThirdParty", method = RequestMethod.POST)
	public String userLoginThirdParty(HttpServletRequest request, Model model) {
		System.out.println("DEBUG: Entering loginThirdParty");
		String openID = request.getParameter("openId");
		String qqName = request.getParameter("qqName");
		String qqLogo = request.getParameter("qqLogo");
		String sessionId = request.getParameter("sessionId");
		String userId = request.getParameter("id");
		System.out.println("DEBUG: sessionId is " + sessionId);
		System.out.println("DEBUG: id is " + userId);
		System.out.println("DEBUG: openID is " + openID);

		if ((userId != null) && (!userId.trim().equals("")) && (sessionId != null) && (!sessionId.trim().equals(""))
				&& (openID != null) && (!openID.trim().equals(""))) {
			SessionUser su = repoSession.findOne(sessionId);
			if (su == null) {
				model.addAttribute("errorMessage", "Session id does not exist!");
			} else
				try {
					AuthorizationInfo a = (AuthorizationInfo) ObjectStringConverter.stringToObject(su.getObjString());
					if (a.getUserId() == Long.valueOf(userId)) {
						model.addAttribute("openId", openID);
						model.addAttribute("qqName", qqName);
						model.addAttribute("qqLogo", qqLogo);
						model.addAttribute("sessionId", sessionId);
						model.addAttribute("AuthorizationInfo", a);
						model.addAttribute("id", a.getUserId());
						System.out.println("DEBUG: about to turn to details");
						return "redirect:/user/details";
					} else
						model.addAttribute("errorMessage", "User Id not align");
				} catch (Exception e) {
					model.addAttribute("errorMessage", "Session id does not exist!");
				}
		}

		return "error";
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public String userLogin(@Valid UserLoginForm userLoginForm, HttpServletRequest request, BindingResult bindingResult,
			Model model) {
		Function<String, String> showError = message -> {
			if (!message.equals(""))
				bindingResult.addError(new FieldError("userLoginForm", "userName", message));

			return "userLogin";
		};

		if (bindingResult.hasErrors()) {
			return showError.apply("");
		}

		String name = userLoginForm.getUserName().trim();
		User user = repo.findByName(name);

		if (user == null) {
			return showError.apply("User not exist or password error!");
		}

		String password = User.getHash(userLoginForm.getPassword());
		if (!user.getPassword().equals(password)) {
			return showError.apply("User not exist or password error!");
		}

		AuthorizationInfo authorizationInfo = new AuthorizationInfo();
		try {
			authorizationInfo.signIn(user);
			String sessionId = authorizationInfo.getSessionId();
			model.addAttribute("AuthorizationInfo", authorizationInfo);
			model.addAttribute("sessionId", sessionId);
			repoSession.save(new SessionUser(sessionId, authorizationInfo.getObjString(), 2 * 60 * 60 * 1000));
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "error";
		}

		model.addAttribute("id", user.getId());
		return "redirect:/user/details";
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public String userAddSumission(@Valid UserForm userForm, BindingResult bindingResult, Model model) {
		if (!userForm.getConfirmPassword().equals(userForm.getPassword())) {
			bindingResult.addError(new FieldError("userForm", "confirmPassword", "Should be equal to password!"));
		}

		// Check if the user name exist or not
		if (!bindingResult.hasErrors()) {
			User user = null;
			try {
				user = repo.findByName(userForm.getName());
			} catch (Exception e) {
				user = null;
			}
			if (user != null)
				bindingResult.addError(new FieldError("userForm", "name", "Name already exist!"));
		}

		if (bindingResult.hasErrors()) {
			return "userReg";
		}
		User user = userForm.cloneUser();
		user.encryptPassword();
		repo.save(user);

		model.addAttribute("id", user.getId());
		return "redirect:/user/details";
	}

	@RequestMapping(value = "/user/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String userEdit(UserForm userForm, @RequestParam(value = "id", required = true) long id, Model model) {
		if (repo.exists(id)) {
			model.addAttribute("user", repo.findOne(id));
			User user = repo.findOne(id);
			userForm.copyExceptPassword(user);
			model.addAttribute("userForm", userForm);
			model.addAttribute("user", user);
			return "userEdit";
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/user/update", method = RequestMethod.POST)
	public String userUpdateSumission(UserForm user, BindingResult bindingResult, Model model) {
		if (user.getPassword() != null)
			if (!user.getConfirmPassword().equals(user.getPassword())) {
				bindingResult.addError(new FieldError("userForm", "confirmPassword", "Must equal to password"));
			}

		if (bindingResult.hasErrors()) {
			model.addAttribute("userForm", user);
			model.addAttribute("user", user);
			return "userEdit";
		}
		User user1 = repo.findOne(user.getId());

		user1.UpdateExceptPassword(user);
		if (user.getPassword() != null) {
			if (!user.getPassword().trim().equals(""))
				user1.UpdatePassword(user);
		}

		repo.save(user1);

		model.addAttribute("id", user.getId());
		return "redirect:/user/details";
	}
}