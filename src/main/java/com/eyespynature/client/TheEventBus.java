package com.eyespynature.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class TheEventBus {

	private static EventBus eventBus;

	public static synchronized EventBus getInstance() {
		if (TheEventBus.eventBus == null) {
			TheEventBus.eventBus = new SimpleEventBus();
		}
		return TheEventBus.eventBus;
	}

}
