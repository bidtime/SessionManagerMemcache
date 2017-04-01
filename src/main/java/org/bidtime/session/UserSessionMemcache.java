/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bidtime.memcachesession.SessionMemcache;
import org.bidtime.session.bean.SessionUserBase;

/**
 * @author Administrator
 * 
 */
public class UserSessionMemcache extends SessionMemcache implements IUserSessionBase {
	
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
	
	public SessionLoginState getSessionLoginState(HttpServletRequest req) {
		return getSessionLoginState(req, false);
	}

	/*
	 * 0:未登陆, 1:正常登陆, 2:被其它用户踢, 3: 没有权限
	 */
	public SessionLoginState getSessionLoginState(HttpServletRequest req, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
		return getSessionLoginState(sessionId);
	}

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
		// 强制将当前用户退出登陆
		sessionDestroy(getSessionId(req), true);
		// req login
		HttpSession session = req.getSession(newSession);
		return user2DoubleOnLine(session.getId(), u);
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

	// getUserOfRequest
	public SessionUserBase getUser(HttpServletRequest req) {
		return getUser(req, false);
	}

	// getUserOfRequest
	public SessionUserBase getUser(HttpServletRequest req, boolean newSession) {
		return getUser(getSessionId(req, newSession));
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
	
	// user_getUserIdOfRequest
//	public Long getUserIdOfRequest(HttpServletRequest req) {
//		String sessionId = getSessionId(req);
//		return user_getUserIdOfSessionId(sessionId);
//	}
	
//	public String user_getUserIdOfRequest(HttpServletRequest req, Long defaultVal) {
//	}

}