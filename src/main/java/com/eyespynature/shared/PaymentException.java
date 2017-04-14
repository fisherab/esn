package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class PaymentException extends Exception implements IsSerializable {

	// Needed for RPC stuff
	public PaymentException() {
	}

	public PaymentException(String name, String msg) {
		super("Payment problem " + name + " " + msg);
	}

}
