package com.eyespynature.user.events;

import com.google.gwt.event.shared.GwtEvent;

public class SearchRequestedEvent extends GwtEvent<SearchRequestedEventHandler> {
	public static Type<SearchRequestedEventHandler> TYPE = new Type<SearchRequestedEventHandler>();
	private String query;

	public SearchRequestedEvent(String query) {
		this.query = query;
	}

	@Override
	public Type<SearchRequestedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchRequestedEventHandler handler) {
		handler.onEvent(this);

	}

	public String getQuery() {
		return query;
	}

}
