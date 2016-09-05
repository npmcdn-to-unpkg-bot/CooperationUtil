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

public class SettingSecurityInterceptor extends HandlerInterceptorAdapter {

	private static boolean isRedirectView(ModelAndView mv) {

		String viewName = mv.getViewName();
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			return true;
		}

		View view = mv.getView();
		return (view != null && view instanceof SmartView && ((SmartView) view).isRedirectView());
	}
	public static final String SESSION_ID = "sessionId";

	private SessionUserRepository repoSession;
	private String loginPath = "/admin/login";


	public SettingSecurityInterceptor(ApplicationContext context) {
		repoSession = context.getBean(SessionUserRepository.class);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sessionId = request.getParameter(SESSION_ID);// request.getParameter(SESSION_ID);
		System.out.println("DEBUG: session is " + sessionId);
		boolean passedVerification = false;
		if (sessionId != null && repoSession.exists(sessionId)) {
			SessionUser su = repoSession.findOne(sessionId);
			AuthorizationInfo a = (AuthorizationInfo) ObjectStringConverter.stringToObject(su.getObjString());
			if (a.getRole() == User.ROLE_SUPER_ADMIN) {
				passedVerification = true;
			} else
				passedVerification = false;

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
	}

	// @Override
	// public void afterCompletion(HttpServletRequest request,
	// HttpServletResponse response, Object handler, Exception ex)
	// throws Exception {
	// }

}