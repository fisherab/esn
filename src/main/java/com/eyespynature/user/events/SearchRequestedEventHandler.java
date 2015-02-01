package com.eyespynature.user.events;

import com.google.gwt.event.shared.EventHandler;

public interface SearchRequestedEventHandler extends EventHandler {

	void onEvent(SearchRequestedEvent searchRequestedEvent);

}
