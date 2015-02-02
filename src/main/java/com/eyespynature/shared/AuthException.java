package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class AuthException extends Exception implements IsSerializable {

	// Needed for RPC stuff
	public AuthException() {
	}

	public AuthException(String message) {
		super(message);
	}

}
