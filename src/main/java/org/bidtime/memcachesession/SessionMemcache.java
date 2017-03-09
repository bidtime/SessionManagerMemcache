package org.bidtime.memcachesession;

import org.bidtime.memcache.MemcacheFlagKeyManage;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionMemcache extends MemcacheFlagKeyManage {
	
	public SessionMemcache(String userFlag) {
		this(userFlag, false);
	}
	
	public SessionMemcache(String userFlag, boolean singleLogin) {
		super(userFlag);
		setSingleLogin(singleLogin);
	}
	
	private boolean singleLogin;

	private SessionOnlineMemcache onlineCache;
	
	public boolean isSingleLogin() {
		return singleLogin;
	}

	public void setSingleLogin(boolean singleLogin) {
		this.singleLogin = singleLogin;
		if (!singleLogin) {
			onlineCache = new SessionOnlineMemcache(userFlag + "_doubleuseronline_");
		} else {
			onlineCache = null;
		}
	}

	public SessionOnlineMemcache getOnlineCache() {
		return onlineCache;
	}

	public void setOnlineCache(SessionOnlineMemcache onlineCache) {
		this.onlineCache = onlineCache;
	}
	
}
