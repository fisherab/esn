package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BasketTransferItemObject implements IsSerializable {

	BasketTransferItemObject() {
		// Needed for RPC stuff
	}

	private long id;
	private String name;
	private String description;
	private long pence;
	private int count;

	public BasketTransferItemObject(long id, String name, String description, long pence, int count) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.pence = pence;
		this.count = count;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public long getPence() {
		return this.pence;
	}

	public int getCount() {
		return this.count;
	}

	public long getId() {
		return id;
	}

}
