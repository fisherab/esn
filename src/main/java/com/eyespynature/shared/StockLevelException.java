package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class StockLevelException extends Exception implements IsSerializable {

	// Needed for RPC stuff
	public StockLevelException() {
	}

	public StockLevelException(long sku) {
		super("There are now insufficient " + sku + " items to fulfil the order");
	}

}
