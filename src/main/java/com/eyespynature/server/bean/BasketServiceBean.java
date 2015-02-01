package com.eyespynature.server.bean;

import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.eyespynature.server.PaypalTokenHolder;
import com.eyespynature.server.Properties;
import com.eyespynature.server.entity.PaypalOrder;
import com.eyespynature.server.entity.ProductType;
import com.eyespynature.shared.Address;
import com.eyespynature.shared.BasketTransferItemObject;
import com.eyespynature.shared.DeliveryCharges;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.json.Amount;
import com.eyespynature.shared.json.Details;
import com.eyespynature.shared.json.ItemList;
import com.eyespynature.shared.json.Order;
import com.eyespynature.shared.json.OrderItem;
import com.eyespynature.shared.json.Payer;
import com.eyespynature.shared.json.Payment;
import com.eyespynature.shared.json.RedirectUrls;
import com.eyespynature.shared.json.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@DeclareRoles({ "StockManager", "ProductManager" })
public class BasketServiceBean {

	@EJB
	PaypalOrderBean paypalOrderBean;

	@EJB
	PaypalTokenHolder paypalTokenHolder;

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	private String esnUrl;

	@PostConstruct
	private void init() {
		esnUrl = Properties.getEsnUrl();
	}

	private final static Logger logger = Logger.getLogger(BasketServiceBean.class);

	public String paypalCheckout(List<BasketTransferItemObject> btios,
			DeliveryMethod deliveryMethod, Address address) throws InternalException {
		try {
			logger.debug("PayPal Checkout requested");

			HttpsURLConnection con = (HttpsURLConnection) (new URL(Properties.getPaypalUrl()
					+ "payments/payment")).openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + paypalTokenHolder.getToken());
			con.setRequestProperty("Content-Type", "application/json");

			con.setDoOutput(true);
			Payment payment = new Payment();
			payment.intent = "authorize";
			payment.redirect_urls = new RedirectUrls();
			payment.redirect_urls.cancel_url = esnUrl + "paypal/cancel";
			payment.redirect_urls.return_url = esnUrl + "paypal/return";
			payment.payer = new Payer();
			payment.payer.payment_method = "paypal";
			Transaction transaction = new Transaction();
			ItemList itemList = new ItemList();
			transaction.item_list = itemList;
			long totalPence = 0;
			int totalWeight = 0;
			int countBig = 0;
			Order order = new Order();
			order.delivery_method = deliveryMethod;
			for (BasketTransferItemObject bitem : btios) {
				com.eyespynature.shared.json.Item item = new com.eyespynature.shared.json.Item();
				item.quantity = Integer.toString(bitem.getCount());
				item.name = cleanName(bitem.getName());
				// Don't trust the price passed in.
				ProductType pt = em.find(ProductType.class, bitem.getId());
				if (pt == null) {
					throw new InternalException("Product type " + bitem.getId() + " not found");
				}
				item.price = formatPrice(pt.getPrice());
				item.currency = "GBP";
				totalPence += bitem.getCount() * pt.getPrice();
				totalWeight += bitem.getCount() * pt.getWeight();
				if (pt.isLarge()) {
					countBig += 1;
				}
				itemList.items.add(item);
				OrderItem orderItem = new OrderItem();
				orderItem.sku = bitem.getId();
				orderItem.quantity = bitem.getCount();
				order.order_items.add(orderItem);
			}

			transaction.amount = new Amount();
			transaction.amount.details = new Details();
			transaction.amount.details.subtotal = formatPrice(totalPence);
			long deliveryPence = getDeliveryCharge(deliveryMethod, totalWeight, totalPence,
					countBig);
			transaction.amount.details.shipping = formatPrice(deliveryPence);
			transaction.amount.total = formatPrice(totalPence + deliveryPence);
			transaction.amount.currency = "GBP";
			payment.transactions.add(transaction);

			ObjectMapper mapper = new ObjectMapper();

			mapper.writeValue(con.getOutputStream(), payment);

			int responseCode = con.getResponseCode();
			if (responseCode / 100 != 2) {
				throw new InternalException("Response code " + responseCode);
			}

			JsonNode rootNode = mapper.readValue(con.getInputStream(), JsonNode.class);

			JsonNode links = rootNode.get("links");
			String approval_url = null;
			for (JsonNode link : links) {
				if (link.get("rel").asText().equals("approval_url")) {
					approval_url = link.get("href").asText();
				}
			}

			URL url = new URL(approval_url);
			String[] pairs = url.getQuery().split("&");
			for (String pair : pairs) {
				int loc = pair.indexOf('=');
				String key = pair.substring(0, loc).trim();
				String value = pair.substring(loc + 1).trim();
				if (key.equals("token")) {
					paypalOrderBean.createPaypalOrder(mapper.writeValueAsString(order),
							mapper.writeValueAsString(rootNode), value,
							mapper.writeValueAsString(address));
				}
			}
			return approval_url;

		} catch (Exception e) {
			logger.debug(e);
			throw new InternalException(e.getClass() + " " + e.getMessage());
		}

	}

	private String cleanName(String name) {
		StringBuilder sb = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (Character.isLetter(c) || Character.isDigit(c) || ".".indexOf(c) >= 0) {
				sb.append(c);
			} else {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	private static String formatPrice(long price) {
		int pence = (int) (price % 100);
		int pounds = (int) (price / 100);
		return Integer.toString(pounds) + "." + (Integer.toString(pence) + "00").substring(0, 2);
	}

	private long getDeliveryCharge(DeliveryMethod deliveryMethod, int weight, long subtotalLong,
			int countBig) throws InternalException {
		if (deliveryMethod == DeliveryMethod.FIRST_CLASS) {
			return DeliveryCharges.firstClassCharge(weight, countBig);
		} else if (deliveryMethod == DeliveryMethod.NEXT_DAY) {
			return DeliveryCharges.nextDayCharge(weight, countBig);
		} else if (deliveryMethod == DeliveryMethod.ECONOMY) {
			return DeliveryCharges.economyCharge(weight, subtotalLong, countBig);
		} else if (deliveryMethod == DeliveryMethod.STANDARD) {
			return DeliveryCharges.standardCharge(weight, subtotalLong, countBig);
		} else if (deliveryMethod == DeliveryMethod.PICK_UP) {
			return DeliveryCharges.pickupCharge(weight, subtotalLong, countBig);
		} else {
			throw new InternalException("Delivery method " + deliveryMethod + " not recognised");
		}
	}

	public void confirmOrder(String orderId) throws InternalException {
		PaypalOrder ppOrder = em.find(PaypalOrder.class, orderId);
		if (ppOrder.getState().equals("approved")) {
			ppOrder.setState("confirmed");
			logger.debug("Order " + orderId + " has been confirmed " + ppOrder.getState());
		} else {
			throw new InternalException("Order " + orderId + " is in state " + ppOrder.getState());
		}
	}

}
