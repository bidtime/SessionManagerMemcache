package org.bidtime.cachesession;

import org.apache.commons.lang.StringUtils;
import org.bidtime.cache.MemcacheFlagKeyManage;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionOnlineMemcache extends MemcacheFlagKeyManage {
	
	public SessionOnlineMemcache() {
	}
	
	public String getSessionOfCustId(String id) {
		return getString(id);
	}
	
	public boolean isOnLine(String id, String sessionId) {
		return equals(id, sessionId, false);
	}
	
	public boolean isUserLogined(String userId) {
		String val = getString(userId);
		return (StringUtils.isEmpty(val)) ? false : true;
	}
	
	public String getSessionId(String userId, boolean delete) {
		String sessionId = getString(userId, delete);
		return sessionId;
	}
	
	public String getSessionId(Object userId, boolean delete) {
		return getSessionId( String.valueOf(userId), delete );
	}
	
//	public boolean isDoubleOnLine(String id, String sessionId) {
//		return notEquals(id, sessionId, false);
//	}

}
