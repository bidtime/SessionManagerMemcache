/**
 * 
 */
package org.bidtime.session.utils;

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
	
	private static HttpSession getSession(HttpServletRequest request,
			boolean bForce) {
		HttpSession session = request.getSession(false);
		if (bForce && session == null) {
			session = request.getSession(true);
		}
		return session;
	}
	
	public static String getSessionId(HttpServletRequest request,
			boolean bForce) {
		HttpSession session = getSession(request, bForce);
		if (session!=null) {
			return session.getId();
		} else {
			return null;
		}
	}
	
	public static String getSessionId(HttpServletRequest request) {
		return getSessionId(request, false);
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
	
//	public static String getSessionIdOfCookie(HttpServletRequest req) {
//		// 返回Cookie
//		Object[] serverCookies = req.getCookies();
//		int count = 0;
//		if (serverCookies != null) {
//			count = serverCookies.length;
//		}
//		
//		if (count < 1 ) {	// <=0
//			return null;
//		}
//
//		for (int i = count - 1; i >= 0; i--) {
//			Cookie cookie = (Cookie) serverCookies[i];
//			if (StringUtils.equals(cookie.getName(), "JSESSIONID")) {
//				return cookie.getValue();
//			}
//		}
//		return null;
//	}

}