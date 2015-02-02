package com.eyespynature.server.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.eyespynature.server.LuceneSingleton;
import com.eyespynature.server.entity.Description;
import com.eyespynature.server.entity.Page;
import com.eyespynature.server.entity.ProductType;
import com.eyespynature.shared.AuthException;
import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.shared.Role;

@Stateless
@DeclareRoles({ "Viewer", "StockManager", "ProductManager" })
public class SecureProductServiceBean {
	final static Logger logger = Logger
			.getLogger(SecureProductServiceBean.class);

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	@Resource
	private SessionContext ctx;

	@Resource
	private EJBContext ejbContext;

	private LuceneSingleton lucene;

	public void createProductType(
			ProductTypeTransferObject productTypeTransferObject)
			throws InternalException, AuthException {

		ProductType entryToPersist = new ProductType(productTypeTransferObject);
		boolean fail = false;

		Query query = em.createNamedQuery("ProductType.Name").setParameter(
				"name", entryToPersist.getName());
		fail = query.getResultList().size() > 0;

		if (fail) {
			throw new InternalException("Object with that name exists");
		}

		em.persist(entryToPersist);
		Long key = entryToPersist.getId();

		lucene.addDocument(key, productTypeTransferObject);
		lucene.commit();

		logger.debug("Created new product id=" + key + " name="
				+ entryToPersist.getName());
		return;
	}

	public void deleteDescriptions(List<Long> ids) {
		for (Long id : ids) {
			em.remove(em.find(Description.class, id));
			logger.debug("Delete description " + id);
		}
	}

	@PreDestroy
	public void exit() {
		if (lucene != null) {
			lucene.close();
		}
	}

	public List<DescriptionTransferObject> getDescriptions() {
		List<DescriptionTransferObject> result = new ArrayList<DescriptionTransferObject>();

		TypedQuery<Description> query = em.createNamedQuery(Description.ALL,
				Description.class);

		for (Description desc : query.getResultList()) {
			result.add(desc.getTransferObject());
		}

		logger.debug("Found " + result.size() + " descriptions for query "
				+ Description.ALL);
		return result;
	}

	public String getPage(String name) throws InternalException {
		try {
			return em.createNamedQuery(Page.NAME, String.class)
					.setParameter("name", name).getSingleResult();
		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}
	}

	public List<String> getPagenames() throws InternalException {
		try {
			return em.createNamedQuery(Page.ALL, String.class).getResultList();
		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}
	}

	public ProductTypeTransferObject getProductType(long id)
			throws InternalException {
		ProductType pt = em.find(ProductType.class, id);
		if (pt == null) {
			throw new InternalException("ProductType " + id + " not found");
		}
		return pt.getTransferObject();
	}

	public List<ProductTypeTransferObject> getProductTypes(String queryName)
			throws InternalException, AuthException {
		// LoginServiceImpl.check(sessionid, Priv.REPORTS);

		List<ProductTypeTransferObject> result = new ArrayList<ProductTypeTransferObject>();

		Query query = em.createNamedQuery("ProductType." + queryName);
		@SuppressWarnings("unchecked")
		List<ProductType> pts = query.getResultList();
		for (ProductType pt : pts) {
			result.add(pt.getTransferObject());
		}

		logger.debug("Found " + result.size() + " products for query "
				+ queryName);
		return result;
	}

	public List<Role> getRoles() {
		List<Role> results = new ArrayList<Role>();
		for (Role role : Role.values()) {
			if (ctx.isCallerInRole(role.name())) {
				results.add(role);
			}
		}
		logger.debug("Roles: " + results);
		return results;
	}

	@PostConstruct
	public void init() {
		lucene = LuceneSingleton.getInstance();
	}

	public void make(Map<Long, Integer> toAdd) throws InternalException,
			AuthException {
		logger.debug("Update stock for " + toAdd.size() + " products");

		for (Entry<Long, Integer> entry : toAdd.entrySet()) {

			Long key = entry.getKey();
			Integer incr = entry.getValue();

			ProductType pt = em.find(ProductType.class, key);
			pt.setNumberInStock(pt.getNumberInStock() + incr);

		}

	}

	public void putDescriptions(List<DescriptionTransferObject> dtos)
			throws InternalException {
		for (DescriptionTransferObject dto : dtos) {
			Description d = new Description(dto);
			if (d.getId() == null) {
				em.persist(d);
				logger.debug("Create description for " + d.getMenu0() + " "
						+ d.getMenu1() + " " + d.getMenu2() + " "
						+ d.getMenu3());
			} else {
				em.merge(d);
				logger.debug("Update description for " + d.getMenu0() + " "
						+ d.getMenu1() + " " + d.getMenu2() + " "
						+ d.getMenu3());
			}
		}
	}

	public void putPage(String name, String text) throws InternalException {
		try {
			Page p = new Page(name, text);
			em.merge(p);
			logger.debug("Stored page " + name);
		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}

	}

	public void reindex() throws InternalException {
		List<ProductType> pts = em.createNamedQuery("ProductType.NotHidden",
				ProductType.class).getResultList();
		lucene.reindex();
		for (ProductType pt : pts) {
			ProductTypeTransferObject ptto = pt.getTransferObject();
			lucene.addDocument(ptto.getId(), ptto);
		}
		lucene.commit();
	}

	public void update(Set<ProductTypeTransferObject> modified)
			throws InternalException, AuthException {
		// LoginServiceImpl.check(sessionid, Priv.PRODUCTS);
		logger.debug("Update " + modified.size() + " products");

		for (ProductTypeTransferObject ptto : modified) {

			ProductType pt = new ProductType(ptto);

			lucene.updateDocument(ptto.getId(), ptto);
			lucene.commit();

			logger.debug("Update " + pt);

			em.merge(pt);

		}
	}
}
