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
	
	public SessionLoginState getSessionLoginState(HttpServletRequest req, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
	    // 0:未登陆, 1:正常登陆, 2:被其它用户踢, 3: 没有权限
	    SessionLoginState sessionLogin = getSessionLoginState(sessionId);
	    return sessionLogin;
	}

	// req_logout
	public void request_logout(HttpServletRequest req) {
		sessionDestroy(getSessionId(req), true);
	}
	
	public boolean request_login(HttpServletRequest req, SessionUserBase u) {
		return request_login(req, u, true);
	}
	
	// req_login
	private boolean request_login(HttpServletRequest req, SessionUserBase u, boolean newSession) {
		// 强制将当前用户退出登陆
		sessionDestroy(getSessionId(req), true);
		// req login
		HttpSession session = req.getSession(newSession);
		return user2DoubleOnLine(session.getId(), u);
	}

	// re_login
	public boolean re_login(HttpServletRequest req) {
		SessionUserBase u = getUser(req);
		return request_login(req, u, false);
	}

	// re_login
	public boolean re_login(HttpServletRequest req, SessionUserBase u) {
		return request_login(req, u, false);
	}

	// getUser
	private SessionUserBase getUser(String sessionId) {
		Object obj = get(sessionId);
		if (obj != null) {
			return (SessionUserBase)obj;
		} else {
			return null;
		}
	}

	// getUserOfRequest
	public SessionUserBase getUser(HttpServletRequest req) {
		return getUser(req, false);
	}

	// getUserOfRequest
	public SessionUserBase getUser(HttpServletRequest req, boolean newSession) {
		return getUser(getSessionId(req, newSession));
	}

	// session_destroy
	private void sessionDestroy(String sessionId, boolean bInvalid) {
		if (sessionId != null) {
			this.sessionCache.delete(sessionId);
			this.onlineCache.delete(sessionId);
		}
	}
	
	protected SessionLoginState getSessionLoginState(String sessionId) {
		if (sessionId != null) {
			int loginState = StateConst.NOT_LOGIN;
			SessionUserBase u = getUser(sessionId);
			if (u != null) {
				if (this.getOnlineCache().isDoubleOnLine(u.getId(), sessionId)) {
					loginState = StateConst.ANOTHER_LOGIN;
				} else {
					// replace sessionId's user memcache
					this.sessionCache.replace(sessionId, u);
					loginState = StateConst.LOGGED_IN;
				}
			}
			SessionLoginState sessionBean = new SessionLoginState(u, loginState);
			return sessionBean;
		} else {
			return null;
		}
	}
	
	protected boolean user2DoubleOnLine(String sessionId, SessionUserBase u) {
		if (sessionId != null && u != null) {
			// 设置user对象
			this.sessionCache.set(sessionId, u);
			if (this.isSingleLogin()) {
				this.onlineCache.set(u.getId(), sessionId);
			}
			return true;
		} else {
			return false;
		}
	}
	
	// get ext
	
	public Object get(HttpServletRequest req, String ext) {
		return get(req, ext, false);
	}
	
	public Object get(HttpServletRequest req, String ext, boolean delete) {
		String sessionId = getSessionId(req);
		return get(sessionId, ext, delete);
	}
	
	private Object get(String key) {
		if (key != null) {
			return this.sessionCache.get(key);
		} else {
			return null;
		}
	}
	
	private Object get(String key, String ext, boolean delete) {
		if (key != null) {
			return this.sessionCache.get(key, ext, delete);
		} else {
			return null;
		}
	}
	
	// set ext
	
	public void set(HttpServletRequest req, String ext, Object o) {
		set(req, ext, o, true);
	}
	
	public void set(HttpServletRequest req, String ext, Object value, boolean newSession) {
		String sessionId = this.getSessionId(req, newSession);
		set(sessionId, ext, value);
	}
	
	private void set(String sessionId, String ext, Object value) {
		if (sessionId != null) {
			this.sessionCache.set(sessionId, ext, value);
		}
	}

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
//		Long id = 
//	}

	// user_getUserIdOfSessionId
//	private Long user_getUserIdOfSessionId(String sessionId) {
//		SessionUserBase u = user_getUserOfSessionId(sessionId);
//		if (u != null) {
//			return u.get();
//		} else {
//			return null;
//		}
//	}
	
	// user_getUserNameOfRequest
//	public String user_getUserNameOfRequest(HttpServletRequest req) {
//		String sessionId = getSessionId(req);
//		return user_getUserNameOfSessionId(sessionId);
//	}
	
	// user_getUserNameOfSessionId
//	private String user_getUserNameOfSessionId(String sessionId) {
//		SessionUserBase u = user_getUserOfSessionId(sessionId);
//		if (u != null) {
//			return u.getName();
//		} else {
//			return null;
//		}
//	}

	// user_isLoginOfHttpSession
//	public boolean user_isLoginOfSessionId(String sessionId) {
//		SessionUserBase u = user_getUserOfSessionId(sessionId);
//		if (u != null) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	// isUserLogin
//	public boolean isUserLogin(String userId) {
//		return this.getOnlineCache().isUserLogined(userId);
//	}

}