package com.eyespynature.server;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import com.eyespynature.client.service.SecureProductService;
import com.eyespynature.server.bean.SecureProductServiceBean;
import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.shared.Role;

@SuppressWarnings("serial")
public class SecureProductServiceImpl extends ContextRemoteServiceServlet
		implements SecureProductService {

	@EJB
	private SecureProductServiceBean secureProductServiceBean;

	@Override
	public void createProductType(
			ProductTypeTransferObject productTypeTransferObject)
			throws InternalException, AuthException {
		secureProductServiceBean.createProductType(productTypeTransferObject);
	}

	@Override
	public void deleteDescriptions(List<Long> ids) {
		secureProductServiceBean.deleteDescriptions(ids);

	}

	@Override
	public List<DescriptionTransferObject> getDescriptions() {
		return secureProductServiceBean.getDescriptions();
	}

	@Override
	public String getPage(String name) throws InternalException {
		return secureProductServiceBean.getPage(name);
	}

	@Override
	public List<String> getPagenames() throws InternalException {
		return secureProductServiceBean.getPagenames();

	}

	@Override
	public ProductTypeTransferObject getProductType(long id) throws InternalException {
		return secureProductServiceBean.getProductType(id);
	}

	@Override
	public List<ProductTypeTransferObject> getProductTypes(String qName)
			throws InternalException, AuthException {
		return secureProductServiceBean.getProductTypes(qName);
	}

	@Override
	public List<Role> getRoles() {
		return secureProductServiceBean.getRoles();
	}

	@Override
	public void make(Map<Long, Integer> toAdd) throws InternalException,
			AuthException {
		secureProductServiceBean.make(toAdd);

	}

	@Override
	public void putDescriptions(List<DescriptionTransferObject> dtos)
			throws InternalException {
		secureProductServiceBean.putDescriptions(dtos);

	}

	@Override
	public void putPage(String name, String text) throws InternalException {
		secureProductServiceBean.putPage(name, text);
	}

	@Override
	public void reindex() throws InternalException {
		secureProductServiceBean.reindex();
	}

	@Override
	public void update(Set<ProductTypeTransferObject> modified)
			throws InternalException, AuthException {
		secureProductServiceBean.update(modified);
	}

}
