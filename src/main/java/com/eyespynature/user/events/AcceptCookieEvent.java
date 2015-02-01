package com.eyespynature.user.events;

import com.google.gwt.event.shared.GwtEvent;

public class AcceptCookieEvent extends GwtEvent<AcceptCookieEventHandler> {
	public static Type<AcceptCookieEventHandler> TYPE = new Type<AcceptCookieEventHandler>();

	@Override
	public Type<AcceptCookieEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AcceptCookieEventHandler handler) {
		handler.onEvent(this);

	}

}
