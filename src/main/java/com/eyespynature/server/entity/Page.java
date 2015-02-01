package com.eyespynature.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "Page.All", query = "SELECT p.name FROM Page p ORDER BY p.name"),
		@NamedQuery(name = "Page.Name", query = "SELECT p.html FROM Page p WHERE p.name = :name") })
public class Page {

	public static final String ALL = "Page.All";

	public static final String NAME = "Page.Name";

	@Column(length = 12000)
	private String html;

	@Id
	private String name;

	// For JPA
	public Page() {
	}

	public Page(String name, String html) {
		this.name = name;
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public String getName() {
		return name;
	}

}
