package com.eyespynature.server;

import java.util.List;

import javax.ejb.EJB;

import com.eyespynature.client.service.ProductService;
import com.eyespynature.server.bean.ProductServiceBean;
import com.eyespynature.shared.DescriptionMenuAndProductTypes;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.ProductTypeTransferObject;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProductServiceImpl extends ContextRemoteServiceServlet implements ProductService {

	@EJB
	private ProductServiceBean productServiceBean;

	@Override
	public DescriptionMenuAndProductTypes getMenu(List<String> menu) throws InternalException {
		return productServiceBean.getMenu(menu);
	}

	@Override
	public ProductTypeTransferObject getWithKey(Long key) throws InternalException {
		return productServiceBean.getWithKey(key);
	}

	@Override
	public List<ProductTypeTransferObject> search(String query, int offset, int count,
			String sortTypeName) throws InternalException {
		return productServiceBean.search(query, offset, count,
				SortType.valueOf(SortType.class, sortTypeName));
	}

	@Override
	public int getCount(String query) throws InternalException {
		return productServiceBean.getCount(query);
	}

}
