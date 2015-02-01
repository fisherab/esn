package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.Address;
import com.eyespynature.shared.BasketTransferItemObject;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.Provider;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("basket")
public interface BasketService extends RemoteService {

	String checkOut(List<BasketTransferItemObject> btios, DeliveryMethod deliveryMethod,
			Provider provider, Address address) throws InternalException;

	void confirmOrder(String orderId) throws InternalException;

}
