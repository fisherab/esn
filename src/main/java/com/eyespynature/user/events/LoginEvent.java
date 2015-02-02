package com.eyespynature.user.events;

import java.util.logging.Logger;

import com.google.gwt.event.shared.GwtEvent;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	final private static Logger logger = Logger.getLogger(LoginEvent.class.getName());
	private String sessionid;
	private String email;
	private String priv;

	public LoginEvent(String email, String sessionid, String priv) {
		logger.finest("Login event " + sessionid);
		this.email = email;
		this.sessionid = sessionid;
		this.priv = priv;
	}

	public LoginEvent() {
	}

	public String getEmail() {
		return this.email;
	}

	public String getPriv() {
		return this.priv;
	}

	@Override
	public Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getSessionid() {
		return sessionid;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onEvent(this);
	}

}
