package com.eyespynature.client.service;

import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.LoginResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

	LoginResponse login(String email, String password) throws InternalException, AuthException;

	void logout(String sessionid) throws InternalException, AuthException;

	void register(String sessionid, String email, String pwd, String priv) throws AuthException, InternalException;

	void unregister(String sessionid, String email) throws InternalException, AuthException;

	void chPwd(String sessionid, String email, String newPassword) throws InternalException, AuthException;

	void chEmail(String sessionid, String email, String newEmail) throws InternalException, AuthException;
	
	void chPriv(String sessionid, String email, String newPriv) throws InternalException, AuthException;

}
