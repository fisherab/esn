package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginResponse  implements IsSerializable {

	private String priv;
	private String sessionid;

	public LoginResponse(String sessionid, String priv) {
		this.sessionid = sessionid;
		this.priv = priv;
	}

	public String getSessionid() {
		return this.sessionid;
	}

	public String getPriv() {
		return this.priv;
	}

	public LoginResponse() {
	}

}
