/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.bidtime.memcachesession.SessionMemcache;
import org.bidtime.memcachesession.SessionOnlineMemcache;
import org.bidtime.session.bean.SessionUserBase;

/**
 * @author Administrator
 * 
 */
public class UserSessionMemcache {

	private static final Logger logger = Logger
			.getLogger(UserSessionMemcache.class);
	
	public static String getSessionId(HttpServletRequest req, boolean newSession) {
		return RequestSessionUtils.getSessionId(req, newSession);
	}
	
	public static String getSessionId(HttpServletRequest req) {
		return RequestSessionUtils.getSessionId(req);
	}
	
	public static SessionLoginState getSessionLoginState(HttpServletRequest request) {
		String sessionId = UserSessionMemcache.getSessionId(request, false);
	    //CSessionUser cSessionUser = null;    //UserSessionInfo.user_getUserOfRequest(request);
	    // 0:未登陆, 1:正常登陆, 2:被其它用户踢, 3: 没有权限 4: 游客访问商城
	    SessionLoginState sessionLogin = UserSessionMemcache
	            .user_getSessionLoginState(sessionId); // 0:未登陆, 1:正常登陆,2:被其它用户踢
	    return sessionLogin;
	}
	
	// httpSession_removeAttr
	public static void httpSession_destroyAttr(HttpSession session) {
		httpSession_destroy(session, true);
	}

	// request_logout
	public static void request_logout(HttpServletRequest request) {
		httpSession_destroy(getSessionId(request), true);
	}
	
	public static boolean request_login(HttpServletRequest request,
			SessionUserBase u) {
		return request_login(request, u, true);
	}
	
	// request_login
	private static boolean request_login(HttpServletRequest request,
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
	public static boolean re_login(HttpServletRequest request) {
		SessionUserBase u = UserSessionMemcache.getUserOfRequest(request);
		return request_login(request, u, false);
	}

	// re_login
	public static boolean re_login(HttpServletRequest request, SessionUserBase u) {
		return request_login(request, u, false);
	}

	// user_getUserOfRequest
	public static SessionUserBase getUserOfRequest(HttpServletRequest request) {
		return user_getUserOfSessionId(getSessionId(request));
	}
	
	// user_getUserIdOfRequest
	public static String user_getUserIdOfRequest(HttpServletRequest request) {
		String sessionId = getSessionId(request);
		return user_getUserIdOfSessionId(sessionId);
	}

	// user_getUserIdOfSessionId
	public static String user_getUserIdOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return u.getId();
		} else {
			return null;
		}
	}
	
	// user_getUserNameOfRequest
	public static String user_getUserNameOfRequest(HttpServletRequest request) {
		String sessionId = getSessionId(request);
		return user_getUserNameOfSessionId(sessionId);
	}
	
	// user_getUserNameOfSessionId
	public static String user_getUserNameOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return u.getName();
		} else {
			return null;
		}
	}

	// user_getUserOfHttpSession
	public static SessionUserBase user_getUserOfSessionId(String sessionId) {
		Object obj = user_get(sessionId);
		if (obj != null) {
			return (SessionUserBase)obj;
		} else {
			return null;
		}
	}

	// user_isLoginOfHttpSession
	public static boolean user_isLoginOfSessionId(String sessionId) {
		SessionUserBase u = user_getUserOfSessionId(sessionId);
		if (u != null) {
			return true;
		} else {
			return false;
		}
	}

	// isUserLogin
	public static boolean isUserLogined(String userId) {
		return SessionOnlineMemcache.getInstance().isUserLogined(userId);
	}
	
	// isUserLogin
	public static boolean isUserAlive(String userId) {
		return SessionOnlineMemcache.getInstance().isUserLogined(userId);
	}

	////////////////////////////////////////////////////////////////////////////////////
	/*
	protected static boolean setDoubleUserOneLine(HttpSession session, SessionUserBase u) {
		// 判断是否当前用户,是否已经登陆,如果登陆,则踢出
		return null;
	} */
	
	protected static void httpSession_destroy(HttpSession session,
			boolean bInvalid) {
		if (session != null) {
			httpSession_destroy(session.getId(), bInvalid);
		}
	}

	// httpSession_destroy
	protected static void httpSession_destroy(String sessionId,
			boolean bInvalid) {
		if (sessionId != null) {
			try {
				SessionMemcache.getInstance().delete(sessionId);
				SessionUserBase u = user_getUserOfSessionId(sessionId);
				if (u != null) {
					if (bInvalid) {
						if (SessionOnlineMemcache.getInstance().isOnLine(
								u.getId(), sessionId)) {
							SessionOnlineMemcache.getInstance().delete(
									u.getId());
						}
					} else {
						SessionOnlineMemcache.getInstance().delete(u.getId());
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	// user_isDoubleOnLineOfHttpSession
	public static boolean user_isDoubleOnLineOfSessionId(String sessionId) {
		boolean bDoubleOnLine = false;
		if (sessionId != null) {
			SessionUserBase u = user_getUserOfSessionId(sessionId);
			if (u != null) {
				bDoubleOnLine = SessionOnlineMemcache.getInstance().isDoubleOnLine(
					u.getId(), sessionId);
			}
		}
		return bDoubleOnLine;
	}
	
	/*
	 * return
	 * 	0: not login, 1: logined, 2: another user logined.
	 */
	public static int user_loginStateOfSessionId(String sessionId) {
		int nReturn = 0;
		if (sessionId != null) {
			SessionUserBase u = user_getUserOfSessionId(sessionId);
			if (u != null) {
				if (SessionOnlineMemcache.getInstance().isDoubleOnLine(
					u.getId(), sessionId)) {
					nReturn = 2;
				} else {
					nReturn = 1;
				}
			}
		}
		return nReturn;
	}
	
	public static SessionLoginState user_getSessionLoginState(String sessionId) {
		//String sessionId = getSessionId(request);
		if (sessionId != null) {
			int nLoginState = 0;
			SessionUserBase u = user_getUserOfSessionId(sessionId);
			if (u != null) {
				if (SessionOnlineMemcache.getInstance().isDoubleOnLine(
					u.getId(), sessionId)) {
					nLoginState = 2;
				} else {
					//replace sessionId's user memcache
					SessionMemcache.getInstance().replace(sessionId, u);
					nLoginState = 1;
				}
			}
			SessionLoginState sessionBean = new SessionLoginState(u, nLoginState);
			return sessionBean;
		} else {
			return null;
		}
	}
	
	protected static boolean userToSession_DoubleOnLine(HttpSession session, SessionUserBase u) {
		if (session != null && u != null) {
			// 设置user对象
			SessionMemcache.getInstance().set(session.getId(), u);
			SessionOnlineMemcache.getInstance().set(u.getId(), session.getId());
			return true;
		} else {
			return false;
		}
	}
	
	public static Object user_get(HttpServletRequest req, String key) {
		return user_get(req, key, false);
	}
	
	public static Object user_get(HttpServletRequest req, String key, boolean delete) {
		String sessionId = getSessionId(req, false);
		return user_get(sessionId, key, delete);
	}	
	
	public static Object user_get(String sessionId) {
		if (sessionId != null) {
			return SessionMemcache.getInstance().get(sessionId);
		} else {
			return null;
		}
	}
	
	public static Object user_get(String sessionId, String ext, boolean delete) {
		if (sessionId != null) {
			return SessionMemcache.getInstance().get(sessionId, ext, delete);
		} else {
			return null;
		}
	}
	
	public static void user_set(HttpServletRequest req, String key, Object o) {
		user_set(req, key, o, true);
	}
	
	public static void user_set(HttpServletRequest req, String key, Object o, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
		user_set(sessionId, key, o);
	}
	
	public static void user_set(String sessionId, String ext, Object o) {
		if (sessionId != null) {
			SessionMemcache.getInstance().set(sessionId, ext, o);
		}
	}

}