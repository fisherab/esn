package com.eyespynature.server;

import javax.ejb.EJB;

import com.eyespynature.client.service.LoginService;
import com.eyespynature.server.bean.LoginServiceBean;
import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.LoginResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends ContextRemoteServiceServlet implements LoginService {

	@EJB
	private LoginServiceBean loginServiceBean;

	@Override
	public LoginResponse login(String email, String password) throws InternalException,
			AuthException {
		return loginServiceBean.login(email, password);
	}

	@Override
	public void logout(String sessionid) throws InternalException, AuthException {
		loginServiceBean.logout(sessionid);
	}

	@Override
	public void register(String sessionid, String email, String pwd, String priv)
			throws AuthException, InternalException {
		loginServiceBean.register(sessionid, email, pwd, priv);
	}

	@Override
	public void unregister(String sessionid, String email) throws InternalException, AuthException {
		loginServiceBean.unregister(sessionid, email);
	}

	@Override
	public void chPwd(String sessionid, String email, String newPassword) throws InternalException,
			AuthException {
		loginServiceBean.chPwd(sessionid, email, newPassword);
	}

	@Override
	public void chEmail(String sessionid, String email, String newEmail) throws InternalException,
			AuthException {
		loginServiceBean.chEmail(sessionid, email, newEmail);
	}

	@Override
	public void chPriv(String sessionid, String email, String newPriv) throws InternalException,
			AuthException {
		loginServiceBean.chPriv(sessionid, email, newPriv);
	}

}
