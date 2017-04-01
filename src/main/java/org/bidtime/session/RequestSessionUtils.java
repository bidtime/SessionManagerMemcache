/**
 * 
 */
package org.bidtime.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bidtime.session.utils.CookieUtils;

/**
 * @author jss
 * 
 */
public class RequestSessionUtils {
	
	private static final String JSESSIONID = "JSESSIONID";
	
	public static String getSessionId(HttpServletRequest req, boolean newSession) {
		String sessionId = getSessionIdOfCookie(req);
		if (sessionId == null) {
			HttpSession session = req.getSession(newSession);
			if (session != null) {
				sessionId = session.getId();
			}
		}
		return sessionId;
	}
	
	public static String getSessionId(HttpServletRequest req) {
		return getSessionId(req, false);
	}
	
	public static String getToken(HttpServletRequest req) {
		Cookie c = CookieUtils.getCookie(req, "token");
		return c.getValue();
	}
	
	public static void setToken(HttpServletResponse res, String value, int age) {
		CookieUtils.addCookie(res, "token", value, age);
	}
	
//	public static void setToken(HttpServletResponse res, String value) {
//		CookieUtils.addCookie(res, "token", value, 7, EnumAge.DAY);
//	}
	
	public static String getSessionIdOfCookie(HttpServletRequest req) {
		// 返回Cookie
		Object[] serverCookies = req.getCookies();
		int count = 0;
		if (serverCookies != null) {
			count = serverCookies.length;
		}
		
		if (count < 1 ) {	// <=0
			return null;
		}
		
		for (int i = count - 1; i >= 0; i--) {
			Cookie cookie = (Cookie) serverCookies[i];
			if (equals(cookie.getName(), JSESSIONID)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
    
}