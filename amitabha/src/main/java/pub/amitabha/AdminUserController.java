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
import pub.amitabha.util.Page;

@Controller
public class AdminUserController {
	@Autowired
	private UserRepository repo;

	@Autowired
	private SessionUserRepository repoSession;

	private User userForm = User.createUser();

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

	@RequestMapping(value = "/admin/logout", method = RequestMethod.GET)
	public String userLogout(HttpServletRequest request) {
		logOut(request);

		return "redirect:/admin/login";
	}

	@RequestMapping(value = "/admin/addUser", method = RequestMethod.GET)
	public String adminAddUser(UserForm userForm) {
		return "adminAddUser";
	}

	@RequestMapping(value = "/admin/add", method = RequestMethod.POST)
	public String adminAddSumission(@Valid UserForm userForm, BindingResult bindingResult) {
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
			return "adminAddUser";
		}
		User user = userForm.cloneUser();
		user.encryptPassword();
		repo.save(user);

		// For posting method, need to add session Id when redirect, for GET
		// method, no need because redirect will adopt existing query string.
		return "redirect:/admin/details?id=" + user.getId();
	}

	@RequestMapping(value = "/admin/details", method = RequestMethod.GET)
	public String adminUserDetails(Model model, @RequestParam(value = "id", required = true) long id) {
		if (repo.exists(id)) {
			model.addAttribute("user", repo.findOne(id));
			return "adminDetails";
		} else {
			model.addAttribute("errorMessage", "User does not exist");
			return "error";
		}
	}

	@RequestMapping("/admin/list")
	public String adminList(HttpServletRequest request, Model model) {
		model.addAttribute("userForm", userForm);
		model.addAttribute("users", Page.paging(request, model, repo.findAll()));

		return "adminList";
	}

	@RequestMapping("/admin/delete")
	public String adminDeleteUser(@RequestParam(value = "id", required = true) long id) {
		if (repo.exists(id))
			repo.delete(id);

		return "redirect:/admin/list";
	}

	@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
	public String adminLoginVerify(@Valid UserLoginForm userLoginForm, HttpServletRequest request,
			BindingResult bindingResult, Model model) {

		Function<String, String> showError = message -> {
			if (!message.equals(""))
				bindingResult.addError(new FieldError("userLoginForm", "userName", message));

			return "adminLogin";
		};
		// HttpSession session = request.getSession();

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
		String sessionId;
		try {
			authorizationInfo.signIn(user);
			sessionId = authorizationInfo.getSessionId();
			model.addAttribute("AuthorizationInfo", authorizationInfo);
			repoSession.save(new SessionUser(sessionId, authorizationInfo.getObjString(), 2 * 60 * 60 * 1000));
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "error";
		}

		model.addAttribute("sessionId", sessionId);
		return "redirect:/admin/list";
	}

	@RequestMapping(value = "/admin/login", method = RequestMethod.GET)
	public String adminLogin(UserLoginForm userLoginForm, HttpServletRequest request) {
		logOut(request);
		return "adminLogin";
	}

	@RequestMapping(value = "/admin/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String adminEdit(UserForm userForm, @RequestParam(value = "id", required = true) long id, Model model) {
		if (repo.exists(id)) {
			User user = repo.findOne(id);
			userForm.copyExceptPassword(user);
			model.addAttribute("userForm", userForm);
			model.addAttribute("user", user);
			return "adminEdit";
		} else {
			model.addAttribute("errorMessage", "The user does not exist!");
			return "error";
		}
	}

	@RequestMapping(value = "/admin/update", method = RequestMethod.POST)
	public String adminUpdateSumission(UserForm user, HttpServletRequest request, BindingResult bindingResult) {
		if (user.getPassword() != null)
			if (!user.getConfirmPassword().equals(user.getPassword())) {
				bindingResult.addError(new FieldError("userForm", "confirmPassword", "Must equal to password"));
			}

		if (bindingResult.hasErrors()) {
			return "adminEdit";
		}

		User user1 = repo.findOne(user.getId());

		user1.UpdateExceptPassword(user);
		if (user.getPassword() != null) {
			if (!user.getPassword().trim().equals(""))
				user1.UpdatePassword(user);
		}

		repo.save(user1);

		return "redirect:/admin/details?id=" + user.getId();
	}
}
