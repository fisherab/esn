package com.eyespynature.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DescriptionMenuAndProductTypes implements IsSerializable {

	public List<ProductTypeTransferObject> getProductTypes() {
		return this.productTypes;
	}

	public List<String> getMenuItems() {
		return this.menuItems;
	}

	private List<ProductTypeTransferObject> productTypes;
	private List<String> menuItems;
	private String html;

	public DescriptionMenuAndProductTypes() {
		// Needed for RPC stuff
	}

	public DescriptionMenuAndProductTypes(String html, List<String> menuItems,
			List<ProductTypeTransferObject> productTypes) {
		this.html = html;
		this.menuItems = menuItems;
		this.productTypes = productTypes;
	}

	public String getHtml() {
		return html;
	}

}
