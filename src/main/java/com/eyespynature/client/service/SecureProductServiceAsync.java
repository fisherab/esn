package com.eyespynature.client.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.shared.Role;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SecureProductServiceAsync {

	public static final class Util {
		private static SecureProductServiceAsync instance;

		public static final SecureProductServiceAsync getInstance() {
			if (instance == null) {
				instance = (SecureProductServiceAsync) GWT.create(SecureProductService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void getRoles(AsyncCallback<List<Role>> asyncCallback);

	void getProductTypes(String qName, AsyncCallback<List<ProductTypeTransferObject>> asyncCallback);

	void createProductType(ProductTypeTransferObject p1, AsyncCallback<Void> callback);

	void make(Map<Long, Integer> toAdd, AsyncCallback<Void> callback);

	void update(Set<ProductTypeTransferObject> modified, AsyncCallback<Void> callback);

	void reindex(AsyncCallback<Void> callback);

	void getDescriptions(AsyncCallback<List<DescriptionTransferObject>> asyncCallback);

	void putDescriptions(List<DescriptionTransferObject> dtos, AsyncCallback<Void> asyncCallback);

	void deleteDescriptions(List<Long> ids, AsyncCallback<Void> asyncCallback);

	void getPagenames(AsyncCallback<List<String>> callback);

	void getPage(String name, AsyncCallback<String> asyncCallback);

	void putPage(String name, String text, AsyncCallback<Void> asyncCallback);

	void getProductType(long id,
			AsyncCallback<ProductTypeTransferObject> asyncCallback);

}
