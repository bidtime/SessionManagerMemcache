package org.bidtime.memcachesession;

import org.bidtime.memcache.MemcacheKeyManage;
import org.bidtime.session.SessionLoginState;
import org.bidtime.session.StateConst;
import org.bidtime.session.bean.SessionUserBase;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionMemcache {
	
	private MemcacheKeyManage sessionCache;
	
	public SessionMemcache() {
		this.singleLogin = false;
	}
	
	public SessionMemcache(boolean singleLogin) {
		this.singleLogin = singleLogin;
	}

	private boolean singleLogin;

	private SessionOnlineMemcache onlineCache;
	
	public boolean isSingleLogin() {
		return singleLogin;
	}

	public void setSingleLogin(boolean singleLogin) {
		this.singleLogin = singleLogin;
	}
	
	public MemcacheKeyManage getSessionCache() {
		return sessionCache;
	}

	public void setSessionCache(MemcacheKeyManage sessionCache) {
		this.sessionCache = sessionCache;
	}

	public SessionOnlineMemcache getOnlineCache() {
		return onlineCache;
	}

	public void setOnlineCache(SessionOnlineMemcache onlineCache) {
		this.onlineCache = onlineCache;
	}

	// session_destroy
	protected void sessionDestroy(String sessionId, boolean bInvalid) {
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
				if (isSingleLogin()) {
					loginState = StateConst.LOGGED_IN;
				} else {				
					if (getOnlineCache().notEquals(u.getId(), sessionId)) {
						loginState = StateConst.ANOTHER_LOGIN;
					} else {
						// replace sessionId's user memcache
						// this.sessionCache.replace(sessionId, u);
						loginState = StateConst.LOGGED_IN;
					}
				}
			}
			return new SessionLoginState(u, loginState);
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
	
	// getUser
	protected SessionUserBase getUser(String sessionId) {
		Object obj = get(sessionId);
		if (obj != null) {
			return (SessionUserBase)obj;
		} else {
			return null;
		}
	}
	
	// get
	protected Object get(String key) {
		if (key != null) {
			return this.sessionCache.get(key);
		} else {
			return null;
		}
	}
	
	// get
	protected Object get(String key, String ext, boolean delete) {
		if (key != null) {
			return this.sessionCache.get(key, ext, delete);
		} else {
			return null;
		}
	}

	// getUserId
//	public Long getUserId(String sessionId) {
//		SessionUserBase u = getUser(sessionId);
//		if (u != null) {
//			return ();
//		} else {
//			return null;
//		}
//	}
	
	// getUserName
//	private String user_getUserName(String sessionId) {
//		SessionUserBase u = user_getUserOfSessionId(sessionId);
//		if (u != null) {
//			return u.getName();
//		} else {
//			return null;
//		}
//	}

//	// isUserLogin
//	public boolean isUserLogin(String userId) {
//		return this.getOnlineCache().isUserLogined(userId);
//	}
	
	// set
	protected void set(String sessionId, String ext, Object value) {
		if (sessionId != null) {
			this.sessionCache.set(sessionId, ext, value);
		}
	}

}
