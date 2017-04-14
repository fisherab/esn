package com.eyespynature.server;

import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.eyespynature.server.bean.PaypalOrderBean;
import com.eyespynature.shared.PaymentException;
import com.eyespynature.shared.StockLevelException;

@SuppressWarnings("serial")
public class PaypalReceiverServlet extends HttpServlet {

	private String baseUrl;

	@Override
	public void init() {
		baseUrl = Properties.getBaseUrl();
	}

	private final static Logger logger = Logger.getLogger(PaypalReceiverServlet.class);

	@EJB
	PaypalOrderBean orderBean;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getPathInfo().equals("/cancel")) {
				orderBean.cancelPaypal(request.getParameter("token"));
				response.sendRedirect(baseUrl + "index.html#cancel");
			} else if (request.getPathInfo().equals("/return")) {
				String id = null;
				try {
					id = orderBean.executePaypal(request.getParameter("token"), request.getParameter("PayerID"));
					response.sendRedirect(baseUrl + "index.html#return " + id);
				} catch (StockLevelException e) {
					response.sendRedirect(baseUrl + "index.html#reject");
				} catch (PaymentException e) {
					response.sendRedirect(baseUrl + "index.html#badPayment");
				}
			}
		} catch (Exception e) {
			logger.error("Unexpected exception", e);
		}
	}
}