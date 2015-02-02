package com.eyespynature.server.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.stream.JsonGenerator;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.eyespynature.server.entity.ProductType;
import com.eyespynature.shared.InternalException;

@Stateless
@Path("/")
public class RestProductType {
	@Context
	private UriInfo context;

	private static Logger logger = Logger
			.getLogger(ThrowableExceptionMapper.class);

	@PersistenceContext(unitName = "esn")
	private EntityManager em;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("product/{id}")
	public String getProduct(@PathParam("id") String id) {

		ProductType pt = em.find(ProductType.class, Long.parseLong(id));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (JsonGenerator gen = Json.createGenerator(baos)) {
			gen.writeStartObject().write("name", pt.getName())
					.write("shortDescription", pt.getShortD())
					.write("longDescription", pt.getLongD())
					.write("notes", pt.getNotes()).write("spec", pt.getSpec())
					.write("popularity", pt.getPopularity())
					.write("large", pt.isLarge())
					.write("weight", pt.getWeight())
					.write("price", pt.getPrice())
					.write("numberInStock", pt.getNumberInStock())
					.write("numberReserved", pt.getNumberReserved());

			gen.writeStartArray("videos");
			for (String video : pt.getVideos()) {
				gen.write(video);
			}
			gen.writeEnd();

			gen.writeStartArray("images");
			for (String image : pt.getImages()) {
				gen.write(image);
			}
			gen.writeEnd();

			gen.writeStartArray("breadcrumbs");
			if (pt.getMenu0() != null) {
				gen.write(pt.getMenu0());
			}
			if (pt.getMenu1() != null) {
				gen.write(pt.getMenu1());
			}
			if (pt.getMenu2() != null) {
				gen.write(pt.getMenu2());
			}
			if (pt.getMenu3() != null) {
				gen.write(pt.getMenu3());
			}
			gen.writeEnd();
			gen.writeEnd();
		}
		logger.debug("Returning product " + baos.toString());
		return baos.toString();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("products")
	public String getProducts(
			@QueryParam("menuComponents") String menuComponents)
			throws InternalException {
		JsonArray items = Json.createReader(
				new ByteArrayInputStream(menuComponents.getBytes()))
				.readArray();
		TypedQuery<ProductType> query;
		if (items.size() == 0) {
			query = em.createNamedQuery(ProductType.Item0, ProductType.class);
		} else if (items.size() == 1) {
			query = em.createNamedQuery(ProductType.Item1, ProductType.class)
					.setParameter("menu0", items.getString(0));
		} else if (items.size() == 2) {
			query = em.createNamedQuery(ProductType.Item2, ProductType.class)
					.setParameter("menu0", items.getString(0))
					.setParameter("menu1", items.getString(1));
		} else if (items.size() == 3) {
			query = em.createNamedQuery(ProductType.Item3, ProductType.class)
					.setParameter("menu0", items.getString(0))
					.setParameter("menu1", items.getString(1))
					.setParameter("menu2", items.getString(2));
		} else {
			throw new InternalException("Too far ...");
		}
		List<ProductType> pts = query.getResultList();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (JsonGenerator gen = Json.createGenerator(baos)) {
			gen.writeStartArray();
			for (ProductType pt : pts) {
				gen.writeStartObject().write("key", pt.getId())
						.write("name", pt.getName())
						.write("shortDescription", pt.getShortD())
						.write("price", pt.getPrice());

				if (!pt.getImages().isEmpty()) {
					gen.write("image", pt.getImages().get(0));
				}

				gen.writeStartArray("breadcrumbs");
				if (pt.getMenu0() != null) {
					gen.write(pt.getMenu0());
				}
				if (pt.getMenu1() != null) {
					gen.write(pt.getMenu1());
				}
				if (pt.getMenu2() != null) {
					gen.write(pt.getMenu2());
				}
				if (pt.getMenu3() != null) {
					gen.write(pt.getMenu3());
				}
				gen.writeEnd();
				gen.writeEnd();
			}
			gen.writeEnd();
		}
		logger.debug("Returning products " + baos.toString());
		return baos.toString();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("menus")
	public String getMenus(@QueryParam("menuComponents") String menuComponents)
			throws InternalException {
		JsonArray items = Json.createReader(
				new ByteArrayInputStream(menuComponents.getBytes()))
				.readArray();
		TypedQuery<String> query;
		if (items.size() == 0) {
			query = em.createNamedQuery(ProductType.Menu0, String.class);
		} else if (items.size() == 1) {
			query = em.createNamedQuery(ProductType.Menu1, String.class)
					.setParameter("menu0", items.getString(0));
		} else if (items.size() == 2) {
			query = em.createNamedQuery(ProductType.Menu2, String.class)
					.setParameter("menu0", items.getString(0))
					.setParameter("menu1", items.getString(1));
		} else if (items.size() == 3) {
			query = em.createNamedQuery(ProductType.Menu3, String.class)
					.setParameter("menu0", items.getString(0))
					.setParameter("menu1", items.getString(1))
					.setParameter("menu2", items.getString(2));
		} else {
			throw new InternalException("Too far ...");
		}
		List<String> menus = query.getResultList();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (JsonGenerator gen = Json.createGenerator(baos)) {
			gen.writeStartArray();
			for (String menu : menus) {
				if (menu != null) {
					gen.write(menu);
				}
			}
			gen.writeEnd();
		}
		logger.debug("Returning menus " + baos.toString());
		return baos.toString();
	}
}