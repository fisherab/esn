package com.eyespynature.server;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.eyespynature.client.service.TextService;
import com.eyespynature.server.entity.Page;
import com.eyespynature.shared.InternalException;

@SuppressWarnings("serial")
public class TextServiceImpl extends ContextRemoteServiceServlet implements TextService {

	final static Logger logger = Logger.getLogger(TextServiceImpl.class);

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	@Override
	public String get(String key) throws InternalException {
		try {
			if (key.contains("/") || key.contains("\\")) {
				return null;
			}
			return em.find(Page.class, key).getHtml();

		} catch (Exception e) {
			throw new InternalException(e.getMessage());
		}
	}

}
