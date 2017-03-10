package org.bidtime.memcachesession;

import org.apache.commons.lang.StringUtils;
import org.bidtime.memcache.MemcacheFlagKeyManage;

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
		String val = getString(id);
		if (val != null) {
			if (StringUtils.isNotEmpty(val)) {
				return StringUtils.equals(val, sessionId);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isUserLogined(String userId) {
		String val = getString(userId);
		return (StringUtils.isEmpty(val)) ? false : true;
	}
	
	public boolean isDoubleOnLine(String id, String sessionId) {
		String val = getString(id);
		if (val != null) {
			if (StringUtils.isNotEmpty(val)) {
				return !StringUtils.equals(val, sessionId);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
