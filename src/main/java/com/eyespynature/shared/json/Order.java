package com.eyespynature.shared.json;

import java.util.ArrayList;
import java.util.List;

import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;

public class Order {
	public DeliveryMethod delivery_method;
	public List<OrderItem> order_items = new ArrayList<>();
	
}