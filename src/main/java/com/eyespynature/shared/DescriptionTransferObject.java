package com.eyespynature.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DescriptionTransferObject implements IsSerializable {

	private Long id;

	public Long getId() {
		return id;
	}

	private String html;

	private List<String> menu = new ArrayList<String>();

	private Integer max;

	private Order order;

	public DescriptionTransferObject() {
		// Needed for RPC stuff
	}

	public DescriptionTransferObject(Long id, String html, List<String> menu, Integer max,
			Order order) {
		this.id = id;
		this.html = html;
		this.menu = menu;
		this.max = max;
		this.order = order;
	}

	public Integer getMax() {
		return max;
	}

	public Order getOrder() {
		return order;
	}

	public String getHtml() {
		return html;
	}

	public List<String> getMenu() {
		return menu;
	}

}
