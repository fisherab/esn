package com.eyespynature.server;

import java.util.List;

import javax.ejb.EJB;

import com.eyespynature.client.service.PaypalOrderService;
import com.eyespynature.server.bean.PaypalOrderBean;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.PaypalOrderTransferObject;

@SuppressWarnings("serial")
public class PaypalOrderServiceImpl extends ContextRemoteServiceServlet implements
		PaypalOrderService {

	@EJB
	private PaypalOrderBean paypalOrderBean;

	@Override
	public List<PaypalOrderTransferObject> search(String query) throws InternalException {
		return paypalOrderBean.search(query);
	}

	@Override
	public void dispatch(String sn, String trackNum) throws InternalException {
		paypalOrderBean.dispatch(sn, trackNum);
	}

}
