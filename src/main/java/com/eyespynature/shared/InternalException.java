package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class InternalException extends Exception implements IsSerializable{
	
	// Needed for RPC stuff
	public InternalException() {
	}

	public InternalException(String message) {
		super(message);
	}

}
