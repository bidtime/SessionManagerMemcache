/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.HttpServletRequest;

import org.bidtime.memcache.MemcacheKeyManage;

/**
 * @author Administrator
 * 
 */
public class SessionValueMemcache extends MemcacheKeyManage {

	public String getSessionId(HttpServletRequest req, boolean newSession) {
		return RequestSessionUtils.getSessionId(req, newSession);
	}
	
	public String getSessionId(HttpServletRequest req) {
		return RequestSessionUtils.getSessionId(req);
	}

	public boolean equals(String value, HttpServletRequest request) {
		String id = getSessionId(request, false);
		if (id != null) {
			return equals(id, value);
		} else {
			return false;
		}
	}

	public boolean equalsIgnoreCase(String value, HttpServletRequest request) {
		String id = getSessionId(request, false);
		if (id != null) {
			return equalsIgnoreCase(id, value);
		} else {
			return false;
		}
	}

	public void set(String value, HttpServletRequest request) {
		set(value, request, false);
	}

	public void set(String value, HttpServletRequest request, boolean newSession) {
		String id = getSessionId(request, newSession);
		set(id, value);
	}

	public void set(Object value, HttpServletRequest request) {
		set(value, request, false);
	}

	public void set(Object value, HttpServletRequest request, boolean newSession) {
		String id = getSessionId(request, newSession);
		set(id, value);
	}
	
	public String getString(HttpServletRequest request) {
		String id = String.valueOf(getSessionId(request, false));
		return id;
	}

	public Object get(HttpServletRequest request) {
		String id = getSessionId(request, false);
		return get(id);
	}

	public boolean delete(HttpServletRequest request) {
		String id = getSessionId(request, false);
		if (id != null) {
			delete(id);
			return true;
		} else {
			return false;
		}
	}


}