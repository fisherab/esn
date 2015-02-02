package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.Address;
import com.eyespynature.shared.BasketTransferItemObject;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.Provider;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BasketServiceAsync {

	public static final class Util {
		private static BasketServiceAsync instance;

		public static final BasketServiceAsync getInstance() {
			if (instance == null) {
				instance = (BasketServiceAsync) GWT.create(BasketService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void checkOut(List<BasketTransferItemObject> btios, DeliveryMethod deliveryMethod,
			Provider provider, Address address, AsyncCallback<String> callback);

	void confirmOrder(String orderId, AsyncCallback<Void> asyncCallback);

}
