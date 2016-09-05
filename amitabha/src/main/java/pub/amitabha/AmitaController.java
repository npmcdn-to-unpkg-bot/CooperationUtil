package pub.amitabha;

import java.io.IOException;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;

import pub.amitabha.domain.SessionUser;
import pub.amitabha.domain.SessionUserRepository;
import pub.amitabha.domain.User;
import pub.amitabha.domain.UserLoginForm;
import pub.amitabha.domain.UserRepository;
import pub.amitabha.util.AuthorizationInfo;
import pub.amitabha.util.Base64;
import pub.amitabha.util.ObjectStringConverter;

@Controller
public class AmitaController {

	@Autowired
	private UserRepository repo;

	@Autowired
	private SessionUserRepository repoSession;

	@RequestMapping("/")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "Bro") String name,
			Model model) {
		model.addAttribute("name", name);
		return "default";
	}

	@RequestMapping(value = "/QQLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public void qqLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			response.sendRedirect(new Oauth().getAuthorizeURL(request));
		} catch (QQConnectException e) {
			e.printStackTrace();
		}
	}

	private String thirdPartyChecking(HttpServletRequest request, Model model) {
		String accessToken = request.getParameter("access_token");
		long tokenExpiredId = 0L;
		String openID = request.getParameter("openId");

		try {
			tokenExpiredId = Long.parseLong(request.getParameter("token_expirein"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "Token Expired Time Incorrect!");
			return "error";
		}

		if (accessToken == null || accessToken.trim().equals("")) {
			model.addAttribute("errorMessage", "Access Token Incorrect!");
			return "error";
		}
		if (openID == null || openID.trim().equals("")) {
			model.addAttribute("errorMessage", "openId Incorrect!");
			return "error";
		}

		// If the token will expired in a minute, Rather treat it as expired
		if (tokenExpiredId < 60) {
			model.addAttribute("errorMessage", "The Access Token is expired!");
			return "error";
		}

		return "";
	}

	@RequestMapping(value = "/bindThirdParty", method = RequestMethod.POST)
	public String bindThirdParty(HttpServletRequest request, @Valid UserLoginForm userLoginForm,
			@RequestParam(value = "bindType", required = true) String bindType, BindingResult bindingResult,
			Model model) {
		Function<String, String> showError = message -> {
			if (!message.equals(""))
				bindingResult.addError(new FieldError("userLoginForm", "userName", message));

			model.addAttribute("access_token", request.getParameter("access_token"));
			model.addAttribute("token_expirein", request.getParameter("token_expirein"));
			model.addAttribute("openId", request.getParameter("openId"));
			model.addAttribute("bindType", "QQ");
			model.addAttribute("qqName", request.getParameter("qqName"));
			model.addAttribute("qqLogo", request.getParameter("qqLogo"));

			return "bindThirdParty";
		};

		if (bindingResult.hasErrors()) {
			return showError.apply("");
		}
		if (bindType.equals("QQ")) {
			String checking = thirdPartyChecking(request, model);
			if (checking != "") {
				return checking;
			}

			// Expired in tokenExpiredId seconds
			Long tokenExpiredId = Long.parseLong(request.getParameter("token_expirein"));
			String openID = request.getParameter("openId");

			String name = userLoginForm.getUserName().trim();
			User user = repo.findByName(name);

			if (user == null) {
				return showError.apply("User not exist or password error!");
			}

			String password = User.getHash(userLoginForm.getPassword());
			if (!user.getPassword().equals(password)) {
				return showError.apply("User not exist or password error!");
			} else {
				user.setQqOpenId(openID);
				repo.save(user);
			}

			AuthorizationInfo authorizationInfo = new AuthorizationInfo();
			try {
				authorizationInfo.signIn(user);
				String sessionId = authorizationInfo.getSessionId();
				model.addAttribute("AuthorizationInfo", authorizationInfo);
				model.addAttribute("sessionId", sessionId);
				repoSession.save(new SessionUser(sessionId, authorizationInfo.getObjString(), tokenExpiredId * 1000));
			} catch (Exception e) {
				model.addAttribute("errorMessage", e.getMessage());
				return "error";
			}

			model.addAttribute("id", user.getId());
		}

		return "redirect:/user/details";
	}

	@RequestMapping(value = "/bindThirdParty", method = RequestMethod.GET)
	public String bindThirdParty(@RequestParam(value = "bindType", required = true) String bindType,
			HttpServletRequest request, UserLoginForm userLoginForm, Model model) {
		if (bindType.equals("QQ")) {
			String checking = thirdPartyChecking(request, model);
			if (checking != "") {
				return checking;
			}
			String accessToken = request.getParameter("access_token");
			String tokenExpiredId = request.getParameter("token_expirein");
			String openID = request.getParameter("openId");
			String qqName = request.getParameter("qqName");
			String qqLogo = request.getParameter("qqLogo");

			model.addAttribute("userLoginForm", userLoginForm);
			model.addAttribute("access_token", accessToken);
			model.addAttribute("token_expirein", tokenExpiredId);
			model.addAttribute("openId", openID);
			model.addAttribute("bindType", "QQ");
			model.addAttribute("qqName", qqName);
			model.addAttribute("qqLogo", qqLogo);

			return "bindThirdParty";
		} else {
			model.addAttribute("errorMessage", "Unknown bind Type");
		}

		return "error";
	}

	@RequestMapping(value = "/QQLoginCallback", method = { RequestMethod.GET, RequestMethod.POST })
	public String qqLoginCallback(HttpServletRequest request, Model model) throws Exception {

		if (request.getParameter("test") != null) {
			model.addAttribute("qqName", Base64.getBase64("五山（柱子）"));
			model.addAttribute("qqLogo",
					Base64.getBase64("https://q1.qlogo.cn/g?b=qq&k=JSPCLDnKIdujNwPGKIsjnQ&s=100&t=1370885885"));

			model.addAttribute("access_token", "8B0CD988241F3D8AB92695DD42C8CB3B");
			model.addAttribute("token_expirein", "7776000");
			model.addAttribute("openId", "F60604F40C7B5BD47186E3C0BB65527F");
			model.addAttribute("bindType", "QQ");
			return "redirect:/bindThirdParty";
		}

		if (request.getParameter("testQQ") != null) {
			model.addAttribute("qqName", Base64.getBase64("五山（柱子）"));
			model.addAttribute("qqLogo",
					Base64.getBase64("https://q1.qlogo.cn/g?b=qq&k=JSPCLDnKIdujNwPGKIsjnQ&s=100&t=1370885885"));

			model.addAttribute("access_token", "8B0CD988241F3D8AB92695DD42C8CB3B");
			model.addAttribute("sessionId", "453CC5E61A5DF02B0FA71D267992BF8B91472043253044");
			model.addAttribute("openId", "QQ12345678");
			model.addAttribute("bindType", "QQ");
			SessionUser su = repoSession.findOne("453CC5E61A5DF02B0FA71D267992BF8B91472043253044");
			AuthorizationInfo a = (AuthorizationInfo) ObjectStringConverter.stringToObject(su.getObjString());
			model.addAttribute("AuthorizationInfo", a);
			model.addAttribute("redirectUrl", "/user/details");
			model.addAttribute("fromUrl", "qqCallBack");

			model.addAttribute("id", a.getUserId());
			return "qqLoginSuccess";
		}

		try {
			/*-- Add session state, for some browser does not support session----*/
			HttpSession session = request.getSession();
			String state = request.getParameter("state");
			String connState = (String) session.getAttribute("qq_connect_state");
			if (connState == null || connState.trim().equals("")) {
				session.setAttribute("qq_connect_state", state);
			}
			/*------------ Ends the session adding section---------------*/

			AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

			String accessToken = null, openID = null;
			long tokenExpireIn = 0L;

			if (accessTokenObj.getAccessToken().equals("")) {
				// 我们的网站被CSRF攻击了或者用户取消了授权
				// 做一些数据统计工作
				model.addAttribute("errorMessage", "没有获取到响应参数");
				return "error";
			} else {
				accessToken = accessTokenObj.getAccessToken();
				tokenExpireIn = accessTokenObj.getExpireIn();

				// 利用获取到的accessToken 去获取当前用的openid -------- start
				OpenID openIDObj = new OpenID(accessToken);
				openID = openIDObj.getUserOpenID();
				UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
				UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
				if (userInfoBean.getRet() == 0) {
					model.addAttribute("qqName", Base64.getBase64(userInfoBean.getNickname()));
					model.addAttribute("qqLogo", Base64.getBase64(userInfoBean.getAvatar().getAvatarURL50()));
				} else {
					model.addAttribute("qqName", "");
					model.addAttribute("qqLogo", "");
				}

				User user = repo.findByQqOpenId(openID);

				model.addAttribute("access_token", accessToken);
				model.addAttribute("token_expirein", String.valueOf(tokenExpireIn));
				model.addAttribute("openId", openID);
				model.addAttribute("bindType", "QQ");
				model.addAttribute("redirectUrl", "/user/details");
				model.addAttribute("fromUrl", "qqCallBack");

				if (user == null) {
					return "redirect:/bindThirdParty";
				} else {
					AuthorizationInfo authorizationInfo = new AuthorizationInfo();
					try {
						authorizationInfo.signIn(user);
						String sessionId = authorizationInfo.getSessionId();
						model.addAttribute("AuthorizationInfo", authorizationInfo);
						model.addAttribute("sessionId", sessionId);
						repoSession.save(
								new SessionUser(sessionId, authorizationInfo.getObjString(), tokenExpireIn * 1000));
					} catch (Exception e) {
						model.addAttribute("errorMessage", e.getMessage());
						return "error";
					}

					model.addAttribute("id", user.getId());
					return "qqLoginSuccess";
				}

			}
		} catch (QQConnectException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "error";
		}

	}
}
