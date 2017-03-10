package org.bidtime.memcachesession;

import org.bidtime.memcache.MemcacheFlagKeyManage;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionMemcache {
	
	protected MemcacheFlagKeyManage sessionCache;
	
	public SessionMemcache() {
		this(false);
	}
	
	public SessionMemcache(boolean singleLogin) {
		this.singleLogin = singleLogin;
	}

	private boolean singleLogin;

	protected SessionOnlineMemcache onlineCache;
	
	public boolean isSingleLogin() {
		return singleLogin;
	}

	public void setSingleLogin(boolean singleLogin) {
		this.singleLogin = singleLogin;
	}
	
	public MemcacheFlagKeyManage getSessionCache() {
		return sessionCache;
	}

	public void setSessionCache(MemcacheFlagKeyManage sessionCache) {
		this.sessionCache = sessionCache;
	}

	public SessionOnlineMemcache getOnlineCache() {
		return onlineCache;
	}

	public void setOnlineCache(SessionOnlineMemcache onlineCache) {
		this.onlineCache = onlineCache;
	}
	
}
