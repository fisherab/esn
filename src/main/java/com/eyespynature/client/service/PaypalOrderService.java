package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.PaypalOrderTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("order")
public interface PaypalOrderService extends RemoteService {

	List<PaypalOrderTransferObject> search(String query) throws InternalException;

	void dispatch(String sn, String trackNum) throws InternalException;

}
