package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProductServiceAsync {

	public static final class Util {
		private static ProductServiceAsync instance;

		public static final ProductServiceAsync getInstance() {
			if (instance == null) {
				instance = (ProductServiceAsync) GWT.create(ProductService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void getMenu(java.util.List<java.lang.String> menu,
			AsyncCallback<com.eyespynature.shared.DescriptionMenuAndProductTypes> callback);

	void getWithKey(java.lang.Long key,
			AsyncCallback<com.eyespynature.shared.ProductTypeTransferObject> callback);

	void search(String query, int offset, int count, String sortTypeName,
			AsyncCallback<List<ProductTypeTransferObject>> callback);

	void getCount(String query, AsyncCallback<Integer> callback);

}
