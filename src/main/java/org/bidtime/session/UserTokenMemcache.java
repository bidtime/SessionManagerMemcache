/**
 * 
 */
package org.bidtime.session;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 * 
 */
public class UserTokenMemcache extends UserSessionMemcache {
	
	public UserTokenMemcache() {
		super();
	}
	
	public UserTokenMemcache(boolean singleLogin) {
		super(singleLogin);
	}
	
	public SessionLoginState getSessionTokenState(HttpServletRequest req) {
		return getSessionTokenState(req, false);
	}

	public SessionLoginState getSessionTokenState(HttpServletRequest req, boolean newSession) {
		// 先从 sessionId 中取，是否有存储的
		SessionLoginState ss = getSessionLoginState(req, newSession);
		int nLoginState = StateConst.NOT_LOGIN;
	    if (ss != null) {
	    	nLoginState = ss.getLoginState();
	    }
	    if (nLoginState == StateConst.NOT_LOGIN) {
			String token = RequestSessionUtils.getToken(req);
			if (token != null && !token.isEmpty()) {
				String sessionId = (String)this.onlineCache.get(token);
				if (sessionId != null) {
					this.sessionCache.replace(token, sessionId);
				}
				SessionLoginState sessionLogin = getSessionLoginState(sessionId);
				if (sessionLogin == null) {
					sessionLogin = new SessionLoginState(null, StateConst.TOKEN_RELOGIN);	// token 有效，需要重新登陆
				}
				return sessionLogin;
			} else {
				return null;
			}
		}
	    return ss;
	}
	
	public void setTokenToSession(HttpServletRequest req, HttpServletResponse res) {
		String token = UUID.randomUUID().toString();
		setTokenToSession(token, req, res);
	}
	
	public void setTokenToSession(String token, HttpServletRequest req, HttpServletResponse res) {
		String sessionId = getSessionId(req, true);
		this.sessionCache.set(token, sessionId);
		RequestSessionUtils.setToken(res, token, sessionCache.getDefaultTm());
	}
	
}