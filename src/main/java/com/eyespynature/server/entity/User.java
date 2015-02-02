package com.eyespynature.server.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQuery(name = "User.Count", query = "SELECT COUNT(u) FROM User u")
@Table(name = "USER_")
public class User {

	// For JPA
	public User() {
	}

	public enum Priv {
		ALL(3), PRODUCTS(2), STOCK(1), REPORTS(0);

		private int level;

		Priv(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}

	};

	@Id
	private String email;

	private String pwd;

	private Priv priv;

	public User(String email, String pwd, Priv priv) {
		this.email = email;
		this.pwd = pwd;
		this.priv = priv;
	}

	public String getEmail() {
		return this.email;
	}

	public Priv getPriv() {
		return this.priv;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPriv(Priv priv) {
		this.priv = priv;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
