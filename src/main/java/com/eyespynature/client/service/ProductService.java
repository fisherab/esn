package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.DescriptionMenuAndProductTypes;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("product")
public interface ProductService extends RemoteService {

	enum SortType {
		RELEVANCE, PRICEL2H, PRICEH2L
	}

	DescriptionMenuAndProductTypes getMenu(List<String> menu) throws InternalException;

	ProductTypeTransferObject getWithKey(Long key) throws InternalException;

	List<ProductTypeTransferObject> search(String query, int offset, int count, String sortTypeName)
			throws InternalException;

	int getCount(String query) throws InternalException;
}
