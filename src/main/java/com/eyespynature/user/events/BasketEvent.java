package com.eyespynature.user.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HTML;

public class BasketEvent extends GwtEvent<BasketEventHandler> {
	
	public int getItemCount() {
		return this.itemCount;
	}

	public HTML getSubtotal() {
		return this.subtotal;
	}

	public static Type<BasketEventHandler> TYPE = new Type<BasketEventHandler>();

	private int itemCount;
	private HTML subtotal;

	public BasketEvent(int itemCount, HTML subtotal) {
		this.itemCount = itemCount;
		this.subtotal = subtotal;
	}

	@Override
	public Type<BasketEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BasketEventHandler handler) {
		handler.onEvent(this);
	}

}
