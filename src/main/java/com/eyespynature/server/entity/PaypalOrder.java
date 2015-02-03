package com.eyespynature.server.entity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import com.eyespynature.shared.Address;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.PaypalOrderTransferObject;
import com.eyespynature.shared.json.Order;
import com.eyespynature.shared.json.OrderItem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@NamedQueries({
		@NamedQuery(name = "PaypalOrder.GetActive", query = "SELECT o FROM PaypalOrder o WHERE o.state NOT IN ('capture-authorized')"),
		@NamedQuery(name = "PaypalOrder.GetDead", query = "SELECT o FROM PaypalOrder o WHERE o.state IN ('created', 'approved')"),
		@NamedQuery(name = "PaypalOrder.GetCompleted", query = "SELECT o FROM PaypalOrder o WHERE o.state IN ('capture-approved')"),
		@NamedQuery(name = "PaypalOrder.GetPending", query = "SELECT o FROM PaypalOrder o WHERE o.state = 'capture-pending'"),
		@NamedQuery(name = "PaypalOrder.GetByToken", query = "SELECT o FROM PaypalOrder o WHERE o.token = :token") })
public class PaypalOrder {

	private final static Logger logger = Logger.getLogger(PaypalOrder.class);

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createTime;

	@Id
	private String id;

	@Column(name = "ORDER_", nullable = false)
	private String order;

	@Column(length = 4096, nullable = false)
	private String response;

	@Column(nullable = false)
	private String state;

	private String token;

	@Column(nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updateTime;

	// For JPA
	public PaypalOrder() {
	}

	public PaypalOrder(String order, String payment, String token, String address)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		this.order = order;
		this.response = payment;
		this.token = token;
		this.address = address;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(payment, JsonNode.class);
		id = rootNode.get("id").asText();
		state = rootNode.get("state").asText();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		createTime = df.parse(rootNode.get("create_time").asText().replace("Z", "GMT"));
		updateTime = df.parse(rootNode.get("update_time").asText().replace("Z", "GMT"));
		logger.debug("Created paypal order " + id + " at " + createTime + " for " + order
				+ "token: " + token);
	}

	public String getAddress() {
		return address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public String getId() {
		return id;
	}

	public String getOrder() {
		return order;
	}

	public String getResponse() {
		return response;
	}

	public String getState() {
		return state;
	}

	public String getToken() {
		return token;
	}

	private String first_name;

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String last_name;
	private String email;

	private String trackNum;

	public PaypalOrderTransferObject getTransferObject(EntityManager em) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Order o = mapper.readValue(order, Order.class);
		DeliveryMethod delivery = o.delivery_method;
		Map<String, Integer> order = new HashMap<>();
		for (OrderItem oi : o.order_items) {
			ProductType pt = em.find(ProductType.class, oi.sku);
			order.put(pt.getName(), oi.quantity);
		}
		return new PaypalOrderTransferObject(id, createTime, updateTime, state, delivery, order,
				response, mapper.readValue(address, Address.class), first_name, last_name, email,
				trackNum);
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setResponse(String payment) {
		this.response = payment;
	}

	public void setState(String state) {
		logger.debug("State of " + id + " changing from " + this.state + " to " + state);
		this.state = state;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setTrackNum(String trackNum) {
		this.trackNum = trackNum;
	}

	public String getTrackNum() {
		return trackNum;
	}

}
