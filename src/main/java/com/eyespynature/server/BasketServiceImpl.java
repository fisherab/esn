package com.eyespynature.server;

import java.util.List;

import javax.ejb.EJB;

import com.eyespynature.client.service.BasketService;
import com.eyespynature.server.bean.BasketServiceBean;
import com.eyespynature.shared.Address;
import com.eyespynature.shared.BasketTransferItemObject;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.Provider;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BasketServiceImpl extends ContextRemoteServiceServlet implements BasketService {

	@EJB
	private BasketServiceBean basketServiceBean;

	@Override
	public void confirmOrder(String orderId) throws InternalException {
		basketServiceBean.confirmOrder(orderId);
	}

	@Override
	public String checkOut(List<BasketTransferItemObject> btios, DeliveryMethod deliveryMethod,
			Provider provider, Address address) throws InternalException {
		if (provider == Provider.PAYPAL) {
			return basketServiceBean.paypalCheckout(btios, deliveryMethod, address);
		} else {
			throw new InternalException("Provider " + provider + " not recognised");
		}
	}

}
