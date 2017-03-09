/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.bidtime.memcachesession.SessionMemcache;
import org.bidtime.session.bean.SessionUserBase;

/**
 * @author Administrator
 * 
 */
public class UserSessionMemcache extends SessionMemcache {

	private static final Logger logger = Logger
			.getLogger(UserSessionMemcache.class);
	
	public UserSessionMemcache(String userFlag) {
		this(userFlag, false);
	}
	
	public UserSessionMemcache(String userFlag, boolean singleLogin) {
		super(userFlag, singleLogin);
	}
	
	public String getSessionId(HttpServletRequest req, boolean newSession) {
		return RequestSessionUtils.getSessionId(req, newSession);
	}
	
//	public static void main(String[] args) {
//		UserSessionMemcache us = new UserSessionMemcache("");
//	}
	
	public String getSessionId(HttpServletRequest req) {
		return RequestSessionUtils.getSessionId(req);
	}
	
	public SessionLoginState getSessionLoginState(HttpServletRequest request) {
		return getSessionLoginState(request, false);
	}
	
	public SessionLoginState getSessionLoginState(HttpServletRequest request, boolean force) {
		String sessionId = getSessionId(request, force);
	    // 0:未登陆, 1:正常登陆, 2:被其它用户踢, 3: 没有权限
	    SessionLoginState sessionLogin = user_getSessionLoginState(sessionId);
	    return sessionLogin;
	}
	
//	public static SessionLoginState getSessionTokenState(HttpServletRequest request, MemcacheManage mm) {
//		return getSessionTokenState(request, false, mm);
//	}
//	
//	public static SessionLoginState getSessionTokenState(HttpServletRequest request, boolean force, MemcacheManage mm) {
//		// 先从 sessionId 中取，是否有存储的
//		SessionLoginState ss = getSessionLoginState(request, force);
//		int nLoginState = 0;
//	    if (ss != null) {
//	    	nLoginState = ss.getLoginState();
//	    }
//	    if (nLoginState == 0) {
//			String token = RequestSessionUtils.getToken(request);
//			if (token != null && !token.isEmpty()) {
//				String sessionId = (String)SessionOnlineMemcache.getInstance().get(token);
//				if (sessionId != null) {
//					mm.replace(token, sessionId);
//				}
//				SessionLoginState sessionLogin = Userthis.user_getSessionLoginState(sessionId);
//				if (sessionLogin == null) {
//					sessionLogin = new SessionLoginState(null, 4);	// 4:token 重新登陆
//				}
//				return sessionLogin;
//			} else {
//				return null;
//			}
//		}
//	    return ss;
//	}
	
//	public static void setTokenToSession(HttpServletRequest request, HttpServletResponse res, MemcacheManage mm) {
//		String token = UUID.randomUUID().toString();
//		String sessionId = Userthis.getSessionId(request, true);
//		mm.set("token", sessionId);
//		RequestSessionUtils.setToken(res, token, mm.getDefaultTm());
//	}
	
//	public static void setTokenToSession(String openId, HttpServletResponse res, MemcacheManage mm) {
//		mm.set("token", openId);
//		RequestSessionUtils.setToken(res, openId, mm.getDefaultTm());
//	}
	
	// httpSession_removeAttr
	public void httpSession_destroyAttr(HttpSession session) {
		httpSession_destroy(session, true);
	}

	// request_logout
	public void request_logout(HttpServletRequest request) {
		httpSession_destroy(getSessionId(request), true);
	}
	
	public boolean request_login(HttpServletRequest request,
			SessionUserBase u) {
		return request_login(request, u, true);
	}
	
	// request_login
	private boolean request_login(HttpServletRequest request,
			SessionUserBase u, boolean newSession) {
		// 强制将当前用户退出登陆
		httpSession_destroy(getSessionId(request), true);
		// request login
		HttpSession session = request.getSession(false);
		if (session == null && newSession) {
			session = request.getSession(true);
		}
		return userToSession_DoubleOnLine(session, u);
	}

	// re_login
	public boolean re_login(HttpServletRequest request) {
		SessionUserBase u = getUserOfRequest(request);
		return request_login(request, u, false);
	}

	// re_login
	public boolean re_login(HttpServletRequest request, SessionUserBase u) {
		return request_login(request, u, false);
	}

	// getUserOfRequest
	public SessionUserBase getUserOfRequest(HttpServletRequest request) {
		return user_getUserOfSessionId(getSessionId(request));
	}

	// getUserOfRequest
	public SessionUserBase getUserOfRequest(HttpServletRequest request, boolean force) {
		return user_getUserOfSessionId(getSessionId(request, force));
	}
	
	// user_getUserIdOfRequest
	public String user_getUserIdOfRequest(HttpServletRequest request) {
		String sessionId = getSessionId(request);
		return user_getUserIdOfSessionId(sessionId);
	}

	// user_getUserIdOfSessionId
	public String user_getUserIdOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return u.getId();
		} else {
			return null;
		}
	}
	
	// user_getUserNameOfRequest
	public String user_getUserNameOfRequest(HttpServletRequest request) {
		String sessionId = getSessionId(request);
		return user_getUserNameOfSessionId(sessionId);
	}
	
	// user_getUserNameOfSessionId
	public String user_getUserNameOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return u.getName();
		} else {
			return null;
		}
	}

	// user_getUserOfHttpSession
	public SessionUserBase user_getUserOfSessionId(String sessionId) {
		Object obj = user_get(sessionId);
		if (obj != null) {
			return (SessionUserBase)obj;
		} else {
			return null;
		}
	}

	// user_isLoginOfHttpSession
	public boolean user_isLoginOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return true;
		} else {
			return false;
		}
	}

	// isUserLogin
	public boolean isUserLogined(String userId) {
		return this.getOnlineCache().isUserLogined(userId);
	}
	
	// isUserLogin
	public boolean isUserAlive(String userId) {
		return this.getOnlineCache().isUserLogined(userId);
	}

	////////////////////////////////////////////////////////////////////////////////////
	/*
	protected static boolean setDoubleUserOneLine(HttpSession session, SessionUserBase u) {
		// 判断是否当前用户,是否已经登陆,如果登陆,则踢出
		return null;
	} */
	
	protected void httpSession_destroy(HttpSession session,
			boolean bInvalid) {
		if (session != null) {
			httpSession_destroy(session.getId(), bInvalid);
		}
	}

	// httpSession_destroy
	protected void httpSession_destroy(String sessionId,
			boolean bInvalid) {
		if (sessionId != null) {
			try {
				this.delete(sessionId);
				SessionUserBase u = user_getUserOfSessionId(sessionId);
				if (u != null) {
					if (bInvalid) {
						if (this.getOnlineCache().isOnLine(
								u.getId(), sessionId)) {
							this.getOnlineCache().delete(
									u.getId());
						}
					} else {
						this.getOnlineCache().delete(u.getId());
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	// user_isDoubleOnLineOfHttpSession
	public boolean user_isDoubleOnLineOfSessionId(String sessionId) {
		boolean bDoubleOnLine = false;
		if (sessionId != null) {
			SessionUserBase u = user_getUserOfSessionId(sessionId);
			if (u != null) {
				bDoubleOnLine = this.getOnlineCache().isDoubleOnLine(
					u.getId(), sessionId);
			}
		}
		return bDoubleOnLine;
	}
	
	public SessionLoginState user_getSessionLoginState(String sessionId) {
		if (sessionId != null) {
			int nLoginState = 0;
			SessionUserBase u = user_getUserOfSessionId(sessionId);
			if (u != null) {
				if (this.getOnlineCache().isDoubleOnLine(
					u.getId(), sessionId)) {
					nLoginState = 2;
				} else {
					//replace sessionId's user memcache
					this.replace(sessionId, u);
					nLoginState = 1;
				}
			}
			SessionLoginState sessionBean = new SessionLoginState(u, nLoginState);
			return sessionBean;
		} else {
			return null;
		}
	}
	
	protected boolean userToSession_DoubleOnLine(HttpSession session, SessionUserBase u) {
		if (session != null && u != null) {
			// 设置user对象
			String sessionId = session.getId();
			this.set(sessionId, u);
			if (this.isSingleLogin()) {
				this.getOnlineCache().set(u.getId(), session.getId());
			}
			return true;
		} else {
			return false;
		}
	}
	
	public Object user_get(HttpServletRequest req, String key) {
		return user_get(req, key, false);
	}
	
	public Object user_get(HttpServletRequest req, String key, boolean delete) {
		String sessionId = getSessionId(req, false);
		return user_get(sessionId, key, delete);
	}	
	
	public Object user_get(String sessionId) {
		if (sessionId != null) {
			return this.get(sessionId);
		} else {
			return null;
		}
	}
	
	public Object user_get(String sessionId, String ext, boolean delete) {
		if (sessionId != null) {
			return this.get(sessionId, ext, delete);
		} else {
			return null;
		}
	}
	
	public void user_set(HttpServletRequest req, String key, Object o) {
		user_set(req, key, o, true);
	}
	
	public void user_set(HttpServletRequest req, String key, Object o, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
		user_set(sessionId, key, o);
	}
	
	public void user_set(String sessionId, String ext, Object o) {
		if (sessionId != null) {
			this.set(sessionId, ext, o);
		}
	}

}