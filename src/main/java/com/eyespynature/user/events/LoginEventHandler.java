package com.eyespynature.user.events;

import com.google.gwt.event.shared.EventHandler;

public interface LoginEventHandler extends EventHandler {
	public void onEvent(LoginEvent loginEvent);
}
