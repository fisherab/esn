package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.PaypalOrderTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PaypalOrderServiceAsync {

	public static final class Util {
		private static PaypalOrderServiceAsync instance;

		public static final PaypalOrderServiceAsync getInstance() {
			if (instance == null) {
				instance = (PaypalOrderServiceAsync) GWT.create(PaypalOrderService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void search(String query, AsyncCallback<List<PaypalOrderTransferObject>> callback);

	void dispatch(String sn, String trackNum, AsyncCallback<Void> callback);

}
