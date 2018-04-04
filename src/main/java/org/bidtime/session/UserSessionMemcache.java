/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;

import org.bidtime.cachesession.SessionMemcache;
import org.bidtime.session.bean.SessionUserBase;

/**
 * @author Administrator
 * 
 */
public class UserSessionMemcache extends SessionMemcache implements IUserSessionBase {
	
	private static final String SESSION_ATTRIBUTE_KEY = "CSessionUser_Attr_Keyx";
	
	public UserSessionMemcache() {
		super();
	}
	
	public UserSessionMemcache(boolean singleLogin) {
		super(singleLogin);
	}
	
	protected String getSessionId(HttpServletRequest req) {
		return RequestSessionUtils.getSessionId(req);
	}

	protected String getSessionId(HttpServletRequest req, boolean newSession) {
		return RequestSessionUtils.getSessionId(req, newSession);
	}
	
	public SessionLoginState getSessionLoginState(HttpServletRequest req, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
		SessionLoginState sessionLogin = getSessionLoginState(sessionId);
		if (sessionLogin != null) {
			req.setAttribute(SESSION_ATTRIBUTE_KEY, sessionLogin.getSessionUser());
		}
		return sessionLogin;
	}
	
	public SessionLoginState getSessionLoginState(HttpServletRequest req) {
		return getSessionLoginState(req, false);
	}

	/*
	 * 0:未登陆, 1:正常登陆, 2:被其它用户踢, 3: 没有权限
	 */
//	private SessionLoginState getSessionLoginState(HttpServletRequest req, boolean newSession) {
//		String sessionId = getSessionId(req, newSession);
//		return getSessionLoginState(sessionId);
//	}
//	
//	public SessionLoginState getSessionLoginCache(HttpServletRequest req) {
//		return getSessionLoginCache(req, false);
//	}

	// req_logout
	@Deprecated
	public void request_logout(HttpServletRequest req) {
		sessionDestroy(getSessionId(req), true);
	}
	
	@Deprecated
	public boolean request_login(HttpServletRequest req, SessionUserBase u) {
		return request_login(req, u, true);
	}
	
	// req_login
	@Deprecated
	private boolean request_login(HttpServletRequest req, SessionUserBase u, boolean newSession) {
//		String sessionId = RequestSessionUtils.getSessionIdOfCookie(req);
//		if (sessionId == null) {
//			HttpSession session = req.getSession(false);
//			if (session == null) {
//				session = req.getSession(true);
//				sessionId = session.getId();
//			} else {
//				sessionId = session.getId();
//			}
//		//} else {
//			// 强制将当前用户退出登陆
//			// sessionDestroy(sessionId, true);
//		}
		String sessionId = this.getSessionId(req, newSession);
		// req login
		return user2DoubleOnLine(sessionId, u);
	}

	// re_login
	@Deprecated
	public boolean re_login(HttpServletRequest req) {
		SessionUserBase u = getUser(req);
		return request_login(req, u, false);
	}

	// re_login
	@Deprecated
	public boolean re_login(HttpServletRequest req, SessionUserBase u) {
		return request_login(req, u, false);
	}

	// getUser
	public SessionUserBase getUser(HttpServletRequest req) {
		return getUser(req, false);
	}

	// getUser
	public SessionUserBase getUser(HttpServletRequest req, boolean newSession) {
		SessionUserBase user = (SessionUserBase)req.getAttribute(SESSION_ATTRIBUTE_KEY);
		if (user == null) {
			user = this.getUser(getSessionId(req, newSession));
		}
		return user;
	}
	
	// get ext
	
	public Object get(HttpServletRequest req, String ext) {
		return get(req, ext, false);
	}
	
	public Object get(HttpServletRequest req, String ext, boolean delete) {
		String sessionId = getSessionId(req);
		return get(sessionId, ext, delete);
	}
	
	// set ext
	
	public void set(HttpServletRequest req, String ext, Object o) {
		set(req, ext, o, true);
	}
	
	public void set(HttpServletRequest req, String ext, Object value, boolean newSession) {
		String sessionId = this.getSessionId(req, newSession);
		set(sessionId, ext, value);
	}
	
	// implments

	public void logout(HttpServletRequest req) {
		request_logout(req);
	}

	public boolean login(HttpServletRequest req, SessionUserBase u) {
		return request_login(req, u);
	}

	public boolean relogin(HttpServletRequest req) {
		return re_login(req);
	}

	public boolean relogin(HttpServletRequest req, SessionUserBase u) {
		return re_login(req, u);
	}

}