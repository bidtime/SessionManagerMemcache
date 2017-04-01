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
	
	protected SessionLoginState getSessionTokenState(String sessionIdReq, String token) {
		// 先从 sessionId 中取，是否有存储的
		SessionLoginState ss = getSessionLoginState(sessionIdReq);
		int nLoginState = StateConst.NOT_LOGIN;
	    if (ss != null) {
	    	nLoginState = ss.getLoginState();
	    }
	    if (nLoginState == StateConst.NOT_LOGIN) {
			if (token != null && !token.isEmpty()) {
				String sessionId_token = (String)this.getOnlineCache().get(token);
				if (sessionId_token != null) {
					this.getSessionCache().replace(token, sessionId_token);
				}
				SessionLoginState sessionLogin = getSessionLoginState(sessionId_token);
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

	public SessionLoginState getSessionTokenState(HttpServletRequest req, boolean newSession) {
		String sessionId = getSessionId(req, newSession);
		String token = RequestSessionUtils.getToken(req);
		return getSessionTokenState(sessionId, token);
	}
	
	public void setTokenToSession(HttpServletRequest req, HttpServletResponse res) {
		String token = UUID.randomUUID().toString();
		setTokenToSession(token, req, res);
	}
	
	public void setTokenToSession(String token, HttpServletRequest req, HttpServletResponse res) {
		String sessionId = getSessionId(req, true);
		this.getSessionCache().set(token, sessionId);
		RequestSessionUtils.setToken(res, token, this.getSessionCache().getDefaultTm());
	}
	
}