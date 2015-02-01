package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Address implements IsSerializable {

	public Address() {
		// Needed for RPC stuff
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getTown() {
		return town;
	}

	public String getCounty() {
		return county;
	}

	public String getPostcode() {
		return postcode;
	}

	private String address1;
	private String address2;
	private String town;
	private String county;
	private String postcode;

	public Address(String address1, String address2, String town, String county, String postcode) {

		this.address1 = address1;
		this.address2 = address2;
		this.town = town.toUpperCase();
		this.county = county;
		this.postcode = postcode.toUpperCase();
	}

}