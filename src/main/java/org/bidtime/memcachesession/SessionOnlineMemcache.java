package org.bidtime.memcachesession;

import org.apache.commons.lang.StringUtils;
import org.bidtime.memcache.MemcacheKeyManage;
import org.bidtime.utils.spring.SpringContextUtils;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionOnlineMemcache extends MemcacheKeyManage {

	private static SessionOnlineMemcache instance;

	public static SessionOnlineMemcache getInstance() {
		if (instance == null) {
			synchronized (SessionOnlineMemcache.class) {
				if (instance == null) {
					instance = (SessionOnlineMemcache) SpringContextUtils
							.getBean("sessionOnlineMemcache");
				}
			}
		}
		return instance;
	}
	
	public String getSessionOfCustId(String id) {
		return getValue(id);
	}
	
	public boolean isOnLine(String id, String sessionId) {
		String val = getValue(id);
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
		String val = getValue(userId);
		return (StringUtils.isEmpty(val)) ? false:true;
	}
	
	public boolean isDoubleOnLine(String id, String sessionId) {
		String val = getValue(id);
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
