package com.eyespynature.server.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.Order;

@Entity
@NamedQueries({
		@NamedQuery(name = "Description.All", query = "SELECT DISTINCT p FROM Description p ORDER BY p.menu0, p.menu1, p.menu2, p.menu3"),
		@NamedQuery(name = "Description.Html0", query = "SELECT DISTINCT p FROM Description p WHERE p.menu0 IS NULL"),
		@NamedQuery(name = "Description.Html1", query = "SELECT DISTINCT p FROM Description p WHERE p.menu0 = :menu0 AND p.menu1 IS NULL"),
		@NamedQuery(name = "Description.Html2", query = "SELECT DISTINCT p FROM Description p WHERE p.menu0 = :menu0 AND p.menu1 = :menu1 AND p.menu2 IS NULL"),
		@NamedQuery(name = "Description.Html3", query = "SELECT DISTINCT p FROM Description p WHERE p.menu0 = :menu0 AND p.menu1 = :menu1 AND p.menu2 = :menu2 AND p.menu3 IS NULL"), })
public class Description {

	public static final String ALL = "Description.All";
	public static final String HTML0 = "Description.Html0";
	public static final String HTML1 = "Description.Html1";
	public static final String HTML2 = "Description.Html2";
	public static final String HTML3 = "Description.Html3";

	public Long getId() {
		return id;
	}

	public String getMenu0() {
		return menu0;
	}

	public String getMenu1() {
		return menu1;
	}

	public String getMenu2() {
		return menu2;
	}

	public String getMenu3() {
		return menu3;
	}

	// For JPA
	public Description() {
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 4000)
	private String html;

	private String menu0;

	private String menu1;

	private String menu2;

	private String menu3;

	@Column(name = "MAX_")
	private Integer max;

	@Column(name = "ORDER_")
	private Order order;

	public Description(DescriptionTransferObject descriptionTransferObject) {
		this.id = descriptionTransferObject.getId();

		this.html = descriptionTransferObject.getHtml();

		this.max = descriptionTransferObject.getMax();

		this.order = descriptionTransferObject.getOrder();

		List<String> menu = descriptionTransferObject.getMenu();
		if (menu.size() > 0) {
			this.menu0 = menu.get(0);
		}
		if (menu.size() > 1) {
			this.menu1 = menu.get(1);
		}
		if (menu.size() > 2) {
			this.menu2 = menu.get(2);
		}
		if (menu.size() > 3) {
			this.menu3 = menu.get(3);
		}
	}

	public DescriptionTransferObject getTransferObject() {
		List<String> menu = new ArrayList<String>(4);
		if (this.menu0 != null) {
			menu.add(this.menu0);
		}
		if (this.menu1 != null) {
			menu.add(this.menu1);
		}
		if (this.menu2 != null) {
			menu.add(this.menu2);
		}
		if (this.menu3 != null) {
			menu.add(this.menu3);
		}
		return new DescriptionTransferObject(id, html, menu, max, order);
	}

	public String getHtml() {
		return html;
	}

	public static String getHtml0() {
		return HTML0;
	}

	public static String getHtml1() {
		return HTML1;
	}

	public static String getHtml2() {
		return HTML2;
	}

	public static String getHtml3() {
		return HTML3;
	}

	public Integer getMax() {
		return max;
	}

	public Order getOrder() {
		return order;
	}

}
