package com.eyespynature.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	public static final class Util {
		private static LoginServiceAsync instance;

		public static final LoginServiceAsync getInstance() {
			if (instance == null) {
				instance = (LoginServiceAsync) GWT.create(LoginService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void login(java.lang.String email, java.lang.String password,
			AsyncCallback<com.eyespynature.shared.LoginResponse> callback);

	void logout(java.lang.String sessionid, AsyncCallback<Void> callback);

	void register(java.lang.String sessionid, java.lang.String email, java.lang.String pwd,
			java.lang.String priv, AsyncCallback<Void> callback);

	void unregister(java.lang.String sessionid, java.lang.String email, AsyncCallback<Void> callback);

	void chPwd(java.lang.String sessionid, java.lang.String email, java.lang.String newPassword,
			AsyncCallback<Void> callback);

	void chEmail(java.lang.String sessionid, java.lang.String email, java.lang.String newEmail,
			AsyncCallback<Void> callback);

	void chPriv(java.lang.String sessionid, java.lang.String email, java.lang.String newPriv,
			AsyncCallback<Void> callback);

}
