package org.bidtime.memcachesession;

import org.bidtime.memcache.MemcacheKeyManage;
import org.bidtime.utils.spring.SpringContextUtils;

/**
 * 不做登录验证枚举
 * 
 * @author karl
 * 
 */
public class SessionMemcache extends MemcacheKeyManage {

	private static SessionMemcache instance;

	public static SessionMemcache getInstance() {
		if (instance == null) {
			synchronized (SessionMemcache.class) {
				if (instance == null) {
					instance = (SessionMemcache) SpringContextUtils
							.getBean("sessionMemcache");
				}
			}
		}
		return instance;
	}

}
