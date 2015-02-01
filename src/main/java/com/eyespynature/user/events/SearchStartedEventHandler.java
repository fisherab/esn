package com.eyespynature.user.events;

import com.google.gwt.event.shared.EventHandler;

public interface SearchStartedEventHandler extends EventHandler {

	void onEvent(SearchStartedEvent searchStartedEvent);

}
