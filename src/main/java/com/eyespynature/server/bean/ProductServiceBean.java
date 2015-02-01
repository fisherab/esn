package com.eyespynature.server.bean;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.eyespynature.client.service.ProductService.SortType;
import com.eyespynature.server.LuceneSingleton;
import com.eyespynature.server.entity.Description;

import com.eyespynature.server.entity.ProductType;
import com.eyespynature.shared.InternalException;
import com.eyespynature.shared.DescriptionMenuAndProductTypes;
import com.eyespynature.shared.Order;
import com.eyespynature.shared.ProductTypeTransferObject;

@Stateless
public class ProductServiceBean {
	final static Logger logger = Logger.getLogger(ProductServiceBean.class);

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	private LuceneSingleton lucene;

	@PostConstruct
	public void init() {
		lucene = LuceneSingleton.getInstance();
	}

	@PreDestroy
	public void exit() {
		if (lucene != null) {
			lucene.close();
		}
	}

	public DescriptionMenuAndProductTypes getMenu(List<String> menu) throws InternalException {
		logger.debug("Entered get menu for " + menu);
		if (menu.size() > 3) {
			throw new InternalException("Menu is currently limited to 4 levels");
		}

		Set<String> result = new HashSet<String>();

		List<String> words = null;
		if (menu.size() == 0) {
			words = em.createNamedQuery("ProductType.Menu0", String.class).getResultList();
		} else if (menu.size() == 1) {
			words = em.createNamedQuery("ProductType.Menu1", String.class)
					.setParameter("menu0", menu.get(0)).getResultList();
		} else if (menu.size() == 2) {
			words = em.createNamedQuery("ProductType.Menu2", String.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1))
					.getResultList();
		} else if (menu.size() == 3) {
			words = em.createNamedQuery("ProductType.Menu3", String.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1))
					.setParameter("menu2", menu.get(2)).getResultList();
			// } else if (menu.size() == 4) {
			// words =
			// em.createNamedQuery("ProductType.Menu4").setParameter("menu0",
			// menu.get(0))
			// .setParameter("menu1", menu.get(1)).setParameter("menu2",
			// menu.get(2))
			// .setParameter("menu3", menu.get(3)).getResultList();
		}
		for (String word : words) {
			if (word != null) {
				result.add(word);
			}
		}

		List<String> l = new ArrayList<String>(result);
		Collections.sort(l);
		ProductServiceBean.logger.debug("Found " + result.size() + " menu items below " + menu);

		List<ProductTypeTransferObject> ptos = new ArrayList<ProductTypeTransferObject>();

		TypedQuery<ProductType> ptquery = null;
		TypedQuery<Description> htmlquery = null;
		if (menu.size() == 0) {
			ptquery = em.createNamedQuery("ProductType.Item0", ProductType.class);
			htmlquery = em.createNamedQuery(Description.HTML0, Description.class);
		} else if (menu.size() == 1) {
			ptquery = em.createNamedQuery("ProductType.Item1", ProductType.class).setParameter(
					"menu0", menu.get(0));
			htmlquery = em.createNamedQuery(Description.HTML1, Description.class).setParameter(
					"menu0", menu.get(0));
		} else if (menu.size() == 2) {
			ptquery = em.createNamedQuery("ProductType.Item2", ProductType.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1));
			htmlquery = em.createNamedQuery(Description.HTML2, Description.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1));
		} else if (menu.size() == 3) {
			ptquery = em.createNamedQuery("ProductType.Item3", ProductType.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1))
					.setParameter("menu2", menu.get(2));
			htmlquery = em.createNamedQuery(Description.HTML3, Description.class)
					.setParameter("menu0", menu.get(0)).setParameter("menu1", menu.get(1))
					.setParameter("menu2", menu.get(2));
		}

		List<Description> htmls = htmlquery.getResultList();
		String html = null;
		Order order = Order.POPULARITY;
		int max = -1;
		if (htmls.size() == 1) {
			Description html0 = htmls.get(0);
			html = html0.getHtml();
			order = html0.getOrder();
			max = html0.getMax() == null ? -1 : html0.getMax();
			if (order != Order.RANDOM && max != -1) {
				ptquery.setMaxResults(max);
			}
		}

		if (max != 0) {
			List<ProductType> pts = ptquery.getResultList();
			if (order == Order.RANDOM) {
				Collections.shuffle(pts);
			}

			if (order == Order.RANDOM && max != -1) {
				pts.subList(max, pts.size()).clear();
			}

			for (ProductType pt : pts) {
				ptos.add(pt.getTransferObject());
			}
		}

		return new DescriptionMenuAndProductTypes(html, l, ptos);
	}

	public ProductTypeTransferObject getWithKey(Long key) throws InternalException {

		ProductTypeTransferObject result = null;

		ProductType pt = em.find(ProductType.class, key);
		result = (pt == null) ? null : pt.getTransferObject();

		if (result == null) {
			throw new InternalException("Requested product not found");
		} else {
			return result;
		}
	}

	public List<ProductTypeTransferObject> search(String query, int offset, int count,
			SortType sortType) throws InternalException {
		List<Long> ids = lucene.search(query, offset, count, sortType);
		logger.debug("Lucene provided " + ids.size() + " replies to query " + query + " (offset="
				+ offset + " count=" + count + " type=" + sortType + ")");
		try {
			List<ProductTypeTransferObject> results = new ArrayList<ProductTypeTransferObject>();
			for (Long id : ids) {
				ProductType product = em.find(ProductType.class, id);
				if (product == null) {
					logger.debug("ProductType with id " + id + " from lucene not found.");
				} else {
					results.add(em.find(ProductType.class, id).getTransferObject());
				}
			}
			return results;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			String msg = e.getClass() + " reports " + e.getMessage();
			logger.debug(msg + baos.toString());
			throw new InternalException(msg);
		}
	}

	public int getCount(String query) throws InternalException {
		int count = lucene.getCount(query);
		logger.debug("Lucene will provide " + count + " replies to query " + query);
		return count;

	}

}
