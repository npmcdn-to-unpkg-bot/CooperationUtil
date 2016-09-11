package pub.amitabha.aop;

import static org.springframework.web.servlet.view.UrlBasedViewResolver.REDIRECT_URL_PREFIX;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

import pub.amitabha.domain.SessionUser;
import pub.amitabha.domain.SessionUserRepository;
import pub.amitabha.domain.User;
import pub.amitabha.util.AuthorizationInfo;
import pub.amitabha.util.ObjectStringConverter;

public class UserSecurityInterceptor extends HandlerInterceptorAdapter {

	private static boolean isRedirectView(ModelAndView mv) {
		if(mv == null)
			return false;

		String viewName = mv.getViewName();
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			return true;
		}

		View view = mv.getView();
		return (view != null && view instanceof SmartView && ((SmartView) view).isRedirectView());
	}

	public static final int TYPE_ADMIN = 1;
	public static final int TYPE_NORMAL = 2;
	public static final String URI_ADMIN_LOGIN = "/admin/login";
	public static final String URI_USER_LOGIN = "/user/login";
	public static final String URI_PREFIX_USER = "/user/";
	public static final String URI_PREFIX_ADMIN = "/admin/";
	public static final String SESSION_ID = "sessionId";

	private SessionUserRepository repoSession;
	private String loginPath;
	private int type;

	/**
	 * Check request type.
	 * 
	 * @param request
	 */
	private void checkRequestType(HttpServletRequest request) {
		if (request.getServletPath().startsWith(URI_PREFIX_ADMIN))
			type = TYPE_ADMIN;
		if (request.getServletPath().startsWith(URI_PREFIX_USER))
			type = TYPE_NORMAL;

		if (type == TYPE_ADMIN)
			loginPath = URI_ADMIN_LOGIN;
		if (type == TYPE_NORMAL)
			loginPath = URI_USER_LOGIN;
	}

	public UserSecurityInterceptor(ApplicationContext context) {
		repoSession = context.getBean(SessionUserRepository.class);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		checkRequestType(request);

		if (request.getServletPath().startsWith(loginPath))
			return true;

		String sessionId = request.getParameter(SESSION_ID);// request.getParameter(SESSION_ID);
		System.out.println("DEBUG: session is " + sessionId);
		boolean passedVerification = false;
		if (sessionId != null && repoSession.exists(sessionId)) {
			SessionUser su = repoSession.findOne(sessionId);
			if (type == TYPE_ADMIN) {
				AuthorizationInfo a = (AuthorizationInfo) ObjectStringConverter.stringToObject(su.getObjString());
				if (a.getRole() >= User.ROLE_ADMIN) {
					passedVerification = true;
				}
			} else
				passedVerification = true;

			// For each authorized action, lasts the session another 2 hours.
			if (passedVerification) {
				su.setCreationTime(System.currentTimeMillis());
				repoSession.save(su);
			}
		}

		if (!passedVerification)
			response.sendRedirect(request.getContextPath() + loginPath);
		return passedVerification;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model)
			throws Exception {
		if (request.getServletPath().endsWith("logout") || model==null) {
			//For restful view, model is null, return
			return;
		}

		String sessionId = request.getParameter(SESSION_ID);
		AuthorizationInfo a = null;
		if (sessionId != null) {
			SessionUser su = repoSession.findOne(sessionId);
			a = (AuthorizationInfo) ObjectStringConverter.stringToObject(su.getObjString());
		}

		if (isRedirectView(model)) {
			// RedirectView view = (RedirectView)model.getView();
			System.out.println("DEBUG: It's now redirecting: " + model.getViewName());
		}

		if (sessionId != null && !model.getModel().containsKey(SESSION_ID)) {
			model.getModelMap().addAttribute(SESSION_ID, sessionId);
		}

		if (a != null) {
			if (!model.getModelMap().containsKey("AuthorizationInfo")) {
				model.getModelMap().addAttribute("AuthorizationInfo", a);
			}
		}
		response.setContentType("text/html;charset=UTF-8");
	}

	// @Override
	// public void afterCompletion(HttpServletRequest request,
	// HttpServletResponse response, Object handler, Exception ex)
	// throws Exception {
	// }

}