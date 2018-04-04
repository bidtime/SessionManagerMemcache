package org.bidtime.cachesession;

import org.bidtime.cache.MemcacheKeyManage;
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
			SessionUserBase u = getUser(sessionId, true);
			if (u != null) {
				this.onlineCache.delete(u.getId());
			}
		}
	}
	
	protected SessionLoginState getSessionLoginState(String sessionId) {
		if (sessionId != null) {
			int loginState = StateConst.NOT_LOGIN;
			SessionUserBase u = getUser(sessionId);
			if (u != null) {
				if (isSingleLogin() && getOnlineCache().notEquals(u.getId(), sessionId)) {
					loginState = StateConst.ANOTHER_LOGIN;
				} else {
					// replace sessionId's user memcache
					// this.sessionCache.replace(sessionId, u);
					loginState = StateConst.LOGGED_IN;
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
	
	protected SessionUserBase getUser(String sessionId, boolean delete) {
		Object obj = get(sessionId, delete);
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
	
	protected Object get(String key, boolean delete) {
		if (key != null) {
			return this.sessionCache.get(key, delete);
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
	
	// isUserLogined
	
	public boolean isUserLogined(String userId) {
		return this.onlineCache.isUserLogined(userId);
	}
	
	public boolean isUserLogined(Long userId) {
		return this.isUserLogined(String.valueOf(userId));
	}
	
	public boolean isUserLogined(Integer userId) {
		return this.isUserLogined(String.valueOf(userId));
	}
	
	// logout
	public boolean logout(String userId) {
		String sessionId = onlineCache.getSessionId(userId, true);
		if (sessionId == null) {
			return false;
		} else {
			this.sessionCache.delete(sessionId);
			return true;
		}
	}
	
	public boolean logout(Long userId) {
		return this.logout(String.valueOf(userId));
	}
	
	public boolean logout(Integer userId) {
		return this.logout(String.valueOf(userId));
	}

}
