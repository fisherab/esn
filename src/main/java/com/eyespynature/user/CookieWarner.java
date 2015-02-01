package com.eyespynature.user;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.user.events.AcceptCookieEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CookieWarner extends Composite {

	interface MyUiBinder extends UiBinder<Widget, CookieWarner> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiHandler("button")
	void handleClick(ClickEvent e) {
		TheEventBus.getInstance().fireEvent(new AcceptCookieEvent());
	}

	public CookieWarner() {

		initWidget(uiBinder.createAndBindUi(this));
	}

}
