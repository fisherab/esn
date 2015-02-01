package com.eyespynature.user.events;

import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.event.shared.GwtEvent;

public class AddToBasketEvent extends GwtEvent<AddToBasketEventHandler> {
	public static Type<AddToBasketEventHandler> TYPE = new Type<AddToBasketEventHandler>();
	private ProductTypeTransferObject ptto;
	private int count;

	public boolean isSilent() {
		return this.silent;
	}

	private boolean silent;

	public AddToBasketEvent(ProductTypeTransferObject ptto, int count, boolean silent) {
		this.ptto = ptto;
		this.count = count;
		this.silent = silent;
	}

	@Override
	public Type<AddToBasketEventHandler> getAssociatedType() {
		return TYPE;
	}

	public ProductTypeTransferObject getPtto() {
		return ptto;
	}

	@Override
	protected void dispatch(AddToBasketEventHandler handler) {
		handler.onEvent(this);
	}

	public int getCount() {
		return count;
	}

}
