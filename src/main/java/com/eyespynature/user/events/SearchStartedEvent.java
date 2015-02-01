package com.eyespynature.user.events;

import com.google.gwt.event.shared.GwtEvent;

public class SearchStartedEvent extends GwtEvent<SearchStartedEventHandler> {
	public static Type<SearchStartedEventHandler> TYPE = new Type<SearchStartedEventHandler>();

	@Override
	public Type<SearchStartedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchStartedEventHandler handler) {
		handler.onEvent(this);

	}

}
