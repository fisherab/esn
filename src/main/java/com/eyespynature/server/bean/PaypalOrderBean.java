package com.eyespynature.server.bean;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.eyespynature.server.PaypalTokenHolder;
import com.eyespynature.server.entity.PaypalOrder;
import com.eyespynature.server.entity.ProductType;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.PaypalOrderTransferObject;
import com.eyespynature.shared.StockLevelException;
import com.eyespynature.shared.json.Order;
import com.eyespynature.shared.json.OrderItem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Stateless
@DeclareRoles({ "StockManager", "ProductManager" })
public class PaypalOrderBean {

	@Schedule(hour = "*", minute = "*")
	private void cleanUp() {

		try {
			logger.debug("Cleanup starting");
			Date expiryCreated = new Date(new Date().getTime() - 15 * 60 * 1000);
			Date expiryApproved = new Date(new Date().getTime() - 2 * 60 * 1000);

			List<PaypalOrder> ps = em.createNamedQuery(PaypalOrderTransferObject.GET_DEAD,
					PaypalOrder.class).getResultList();
			for (PaypalOrder p : ps) {
				logger.debug("Consider deletion of " + p.getState() + " transaction " + p.getId()
						+ " updated " + p.getUpdateTime());
				if (p.getState().equals("created") && p.getUpdateTime().before(expiryCreated)) {
					em.remove(p);
					logger.debug("Deleted created transaction " + p.getId() + " updated "
							+ p.getUpdateTime());
				} else if (p.getState().equals("approved")
						&& p.getUpdateTime().before(expiryApproved)) {
					em.remove(p);
					logger.debug("Deleted approved transaction " + p.getId() + " updated "
							+ p.getUpdateTime());
				}
			}

			ps = em.createNamedQuery(PaypalOrderTransferObject.GET_PENDING, PaypalOrder.class)
					.getResultList();
			ObjectMapper mapper = new ObjectMapper();
			for (PaypalOrder o : ps) {
				logger.debug("Get " + o);
				JsonNode rootNode = mapper.readValue(o.getResponse(), JsonNode.class);
				String href = null;
				String method = null;
				for (JsonNode link : rootNode.get("links")) {
					if (link.get("rel").asText().equals("self")) {
						href = link.get("href").asText();
						method = link.get("method").asText();
					}
				}

				HttpsURLConnection con = (HttpsURLConnection) (new URL(href)).openConnection();
				con.setRequestMethod(method);
				con.setRequestProperty("Accept", "application/json");
				con.setRequestProperty("Authorization", "Bearer " + paypalTokenHolder.getToken());
				con.setRequestProperty("Content-Type", "application/json");

				processError(con);

				rootNode = mapper.readValue(con.getInputStream(), JsonNode.class);

				logger.debug("Get capture response "
						+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

				updatePaypalOrder(o.getId(), mapper.writeValueAsString(rootNode), "capture-"
						+ rootNode.get("state").asText());
			}
		} catch (Throwable e) {
			logger.debug(e.getClass() + " " + e.getMessage());
		}
	}

	private final static Logger logger = Logger.getLogger(PaypalOrderBean.class);

	@EJB
	PaypalTokenHolder paypalTokenHolder;

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	public void createPaypalOrder(String order, String payment, String token, String address)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		PaypalOrder o = new PaypalOrder(order, payment, token, address);
		em.persist(o);
	}

	public void cancelPaypal(String token) {
		List<PaypalOrder> results = em
				.createNamedQuery(PaypalOrderTransferObject.GET_BY_TOKEN, PaypalOrder.class)
				.setParameter("token", token).getResultList();
		if (results.size() == 1) {
			em.remove(results.get(0));
			logger.debug("Cancelled order removed from database");
		} else {
			logger.debug("Order not found in database");
		}

	}

	public String executePaypal(String token, String payerId) throws InternalException,
			StockLevelException {
		try {
			List<PaypalOrder> results = em
					.createNamedQuery(PaypalOrderTransferObject.GET_BY_TOKEN, PaypalOrder.class)
					.setParameter("token", token).getResultList();
			if (results.size() == 1) {
				logger.debug("Execute order token: '" + token + "' payerId: '" + payerId + "'");
				logger.debug("Reserve items");
				ObjectMapper mapper = new ObjectMapper();
				Order order = mapper.readValue(results.get(0).getOrder(), Order.class);

				for (OrderItem line : order.order_items) {
					int n = line.quantity;
					ProductType pt = em.find(ProductType.class, line.sku);
					pt.setNumberInStock(pt.getNumberInStock() - n);
					if (pt.getNumberInStock() < 0) {
						logger.debug("Invalid order removed from database");
						em.remove(results.get(0));
						throw new StockLevelException(line.sku);
					}
					pt.setNumberReserved(pt.getNumberReserved() + n);
				}

				JsonNode payment = mapper.readValue(results.get(0).getResponse(), JsonNode.class);
				JsonNode links = payment.get("links");
				String execute = null;
				for (JsonNode link : links) {
					if (link.get("rel").asText().equals("execute")) {
						execute = link.get("href").asText();
					}
				}

				HttpsURLConnection con = (HttpsURLConnection) (new URL(execute)).openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestProperty("Authorization", "Bearer " + paypalTokenHolder.getToken());
				con.setRequestProperty("Content-Type", "application/json");

				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes("{ \"payer_id\" : \"" + payerId + "\" }");
				wr.close();

				int responseCode = con.getResponseCode();
				if (responseCode / 100 != 2) {
					throw new InternalException("Response code " + responseCode);
				}

				mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(con.getInputStream(), JsonNode.class);
				logger.debug("Approval response "
						+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
				String id = rootNode.get("id").asText();
				updatePaypalOrder(id, mapper.writeValueAsString(rootNode), rootNode.get("state")
						.asText());

				PaypalOrder ppOrder = em.find(PaypalOrder.class, id);
				JsonNode payer_info = rootNode.get("payer").get("payer_info");
				ppOrder.setFirst_name(payer_info.get("first_name").asText());
				ppOrder.setLast_name(payer_info.get("last_name").asText());
				ppOrder.setEmail(payer_info.get("email").asText());

				return rootNode.get("id").asText();
			} else {
				logger.debug("Order not found in database");
				throw new InternalException("Order not found in database");
			}
		} catch (StockLevelException e) {
			throw e;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			logger.debug(baos);
			throw new InternalException(e.getClass() + " " + e.getMessage());
		}
	}

	private void updatePaypalOrder(String id, String payment, String state)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(payment, JsonNode.class);
		PaypalOrder ppOrder = em.find(PaypalOrder.class, id);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date createTime = df.parse(rootNode.get("create_time").asText().replace("Z", "GMT"));
		Date updateTime = df.parse(rootNode.get("update_time").asText().replace("Z", "GMT"));
		ppOrder.setState(state);
		ppOrder.setCreateTime(createTime);
		ppOrder.setUpdateTime(updateTime);
		ppOrder.setResponse(payment);
		ppOrder.setToken(null);
		logger.debug(id + " updated create:" + createTime + " update:" + updateTime + " state:"
				+ state);
	}

	public List<PaypalOrderTransferObject> search(String query) throws InternalException {
		List<PaypalOrder> ps = em.createNamedQuery(query, PaypalOrder.class).getResultList();
		List<PaypalOrderTransferObject> results = new ArrayList<>();
		for (PaypalOrder p : ps) {
			try {
				results.add(p.getTransferObject(em));
			} catch (IOException e) {
				throw new InternalException(e.getClass() + " " + e.getMessage());
			}
		}
		return results;
	}

	public void dispatch(String id, String trackNum) throws InternalException {
		try {
			String href = null;
			String method = null;
			JsonNode amount = null;
			PaypalOrder o = em.find(PaypalOrder.class, id);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(o.getResponse(), JsonNode.class);
			for (JsonNode transaction : rootNode.get("transactions")) {
				for (JsonNode relatedResource : transaction.get("related_resources")) {
					JsonNode authorization = relatedResource.get("authorization");
					amount = authorization.get("amount");
					for (JsonNode link : authorization.get("links")) {
						if (link.get("rel").asText().equals("capture")) {
							if (href != null) {
								throw new InternalException("Found more than one capture link");
							}
							href = link.get("href").asText();
							method = link.get("method").asText();
						}
					}
				}
			}
			logger.debug(method + " " + href);

			ObjectNode capture = mapper.createObjectNode();
			ObjectNode newAmount = capture.putObject("amount");
			newAmount.put("currency", amount.get("currency"));
			newAmount.put("total", amount.get("total"));
			capture.put("is_final_capture", true);

			HttpsURLConnection con = (HttpsURLConnection) (new URL(href)).openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + paypalTokenHolder.getToken());
			con.setRequestProperty("Content-Type", "application/json");

			con.setDoOutput(true);
			mapper.writeValue(con.getOutputStream(), capture);

			processError(con);

			rootNode = mapper.readValue(con.getInputStream(), JsonNode.class);

			logger.debug("Capture response "
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

			updatePaypalOrder(id, mapper.writeValueAsString(rootNode),
					"capture-" + rootNode.get("state").asText());

			String order = o.getOrder();
			for (JsonNode item : mapper.readValue(order, JsonNode.class).get("order_items")) {
				long sku = item.get("sku").asLong();
				int quantity = item.get("quantity").asInt();
				ProductType pt = em.find(ProductType.class, sku);
				pt.setNumberReserved(pt.getNumberReserved() - quantity);
				logger.debug("Reduced reserved " + quantity + " of " + sku);
			}
			o.setTrackNum(trackNum);

		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			throw new InternalException(e.getClass() + " " + e.getMessage() + " " + baos.toString());
		}

		// public void processDelivered(OrderSummary orderSummary) {
		// GoogleOrder o = em.find(GoogleOrder.class, orderSummary.getGoogleOrderNumber());
		// ShoppingCart order = orderSummary.getShoppingCart();
		// for (Item line : order.getItems().getItem()) {
		// String mid = line.getMerchantItemId();
		// int n = line.getQuantity();
		// ProductType pt = em.find(ProductType.class, Long.parseLong(mid));
		// pt.setNumberReserved(pt.getNumberReserved() - n);
		// }
		// o.setAction(null);
	}

	private void processError(HttpsURLConnection con) throws IOException, InternalException {
		int responseCode = con.getResponseCode();
		if (responseCode / 100 != 2) {
			ByteArrayOutputStream baos;
			InputStream err = null;
			try {
				err = con.getErrorStream();
				baos = new ByteArrayOutputStream();
				if (err != null) {
					int len;
					byte[] buffer = new byte[1024];
					while ((len = err.read(buffer)) != -1) {
						baos.write(buffer, 0, len);
					}
				}
			} finally {
				if (err != null) {
					err.close();
				}
			}
			throw new InternalException("Response code " + responseCode + " " + baos.toString());
		}

	}

}
