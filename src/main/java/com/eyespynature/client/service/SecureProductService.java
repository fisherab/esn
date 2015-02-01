package com.eyespynature.client.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.shared.Role;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("secureproduct")
public interface SecureProductService extends RemoteService {
	void createProductType(ProductTypeTransferObject p1)
			throws InternalException, AuthException;

	void update(Set<ProductTypeTransferObject> modified)
			throws InternalException, AuthException;

	void make(Map<Long, Integer> toAdd) throws InternalException, AuthException;

	List<ProductTypeTransferObject> getProductTypes(String qName)
			throws InternalException, AuthException;

	List<Role> getRoles();

	void reindex() throws InternalException;

	List<DescriptionTransferObject> getDescriptions();

	void putDescriptions(List<DescriptionTransferObject> dtos)
			throws InternalException;

	void deleteDescriptions(List<Long> ids);

	List<String> getPagenames() throws InternalException;

	String getPage(String name) throws InternalException;

	void putPage(String name, String text) throws InternalException;

	ProductTypeTransferObject getProductType(long id) throws InternalException;

}
