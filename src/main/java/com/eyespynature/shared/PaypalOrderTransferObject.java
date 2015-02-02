package com.eyespynature.shared;

import java.util.Date;
import java.util.Map;

import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.google.gwt.user.client.rpc.IsSerializable;

public class PaypalOrderTransferObject implements IsSerializable {

	public static String GET_ACTIVE = "PaypalOrder.GetActive";
	public static String GET_BY_TOKEN = "PaypalOrder.GetByToken";
	public static String GET_DEAD = "PaypalOrder.GetDead";
	public static String GET_PENDING = "PaypalOrder.GetPending";
	private String id;

	public String getId() {
		return id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getState() {
		return state;
	}

	public String getPayment() {
		return payment;
	}

	private Date createTime;
	private Date updateTime;
	private String state;
	private Map<String, Integer> cart;
	private String payment;
	private Address address;
	private DeliveryMethod delivery;
	private String firstName;
	private String lastName;
	private String email;
	private String trackNum;

	public PaypalOrderTransferObject() {
		// Needed for RPC stuff
	}

	public PaypalOrderTransferObject(String id, Date createTime, Date updateTime, String state,
			DeliveryMethod delivery, Map<String, Integer> order, String payment, Address address,
			String firstName, String lastName, String email, String trackNum) {
		this.id = id;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.state = state;
		this.delivery = delivery;
		this.cart = order;
		this.payment = payment;
		this.address = address;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.trackNum = trackNum;
	}

	public String getTrackNum() {
		return trackNum;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public Address getAddress() {
		return address;
	}

	public DeliveryMethod getDelivery() {
		return delivery;
	}

	public Map<String, Integer> getCart() {
		return cart;
	}
}
