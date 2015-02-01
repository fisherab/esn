package com.eyespynature.server.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eyespynature.server.entity.User.Priv;

@Entity
@NamedQuery(name = "Session.AfterDate", query = "SELECT s FROM Session s WHERE s.expiryDate < :expiryDate")
public class Session {
	
	// For JPA
	public Session() {
	}

	private String email;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@Id
	@Column(name = "KEY_")
	private String key;

	private Priv priv;

	public Session(String email, Priv priv) {
		this.key = UUID.randomUUID().toString();
		this.email = email;
		this.priv = priv;
		this.expiryDate = new Date();
		this.expiryDate.setTime(this.expiryDate.getTime() + 3600 * 4 * 10); // .04
																			// hours
	}

	public String getEmail() {
		return this.email;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public String getKey() {
		return this.key;
	}

	public Priv getPriv() {
		return this.priv;
	}

}
