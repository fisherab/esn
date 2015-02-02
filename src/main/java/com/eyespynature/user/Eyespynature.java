package com.eyespynature.user;

import java.util.Date;
import java.util.logging.Logger;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.user.events.AcceptCookieEvent;
import com.eyespynature.user.events.AcceptCookieEventHandler;
import com.eyespynature.user.events.LoginEvent;
import com.eyespynature.user.events.LoginEventHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Eyespynature implements EntryPoint {

	private static final EventBus bus = TheEventBus.getInstance();
	final private static Logger logger = Logger.getLogger(Eyespynature.class
			.getName());
	private Widget body = new Widget();

	private DockLayoutPanel main;
	private String sessionid;
	private CookieWarner cookieWarner;

	private void displayPanel(Widget panel) {
		this.main.remove(this.body);
		this.body = new ScrollPanel(panel);
		this.main.add(this.body);
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		Resources.INSTANCE.css().ensureInjected();

		/* Initialise history */
		String initToken = History.getToken();
		if (initToken.length() == 0) {
			History.newItem("Home");
		}

		this.main = new DockLayoutPanel(Unit.EM);
		RootLayoutPanel.get().add(this.main);

		if (Cookies.getCookie("cookies") == null) {
			cookieWarner = new CookieWarner();
			main.addNorth(cookieWarner, 8);
		}

		Node loading = DOM.getElementById("loading");
		RootPanel.getBodyElement().removeChild(loading);

		Eyespynature.bus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onEvent(LoginEvent loginEvent) {
				sessionid = loginEvent.getSessionid();
				Eyespynature.logger.finest("sessionid is " + sessionid);
			}
		});

		bus.addHandler(AcceptCookieEvent.TYPE, new AcceptCookieEventHandler() {

			@Override
			public void onEvent(AcceptCookieEvent event) {
				main.clear();
				main.add(body);
				Cookies.setCookie("cookies", null,
						new Date(System.currentTimeMillis() + 100L * 365 * 24
								* 3600 * 1000));
			}
		});

		final BasketPanel basketPanel = new BasketPanel();
		final LoginPanel loginPanel = new LoginPanel();
		final SearchPanel searchPanel = new SearchPanel();
		/* Add history listener */
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				logger.fine("Token is " + token);

				if (token.startsWith("Home")) {
					Eyespynature.this.displayPanel(new MenuPanel(token));
				} else if (token.startsWith("item:")) {
					Eyespynature.this.displayPanel(new PageItemPanel(token
							.substring(5)));
				} else if (token.equals("login")) {
					Eyespynature.this.displayPanel(loginPanel);
				} else if (token.equals("basket")) {
					Eyespynature.this.displayPanel(basketPanel);
				} else if (token.equals("search")) {
					Eyespynature.this.displayPanel(searchPanel);
				} else if (token.equals("cancel") || token.startsWith("return")
						|| token.equals("reject")) {
					basketPanel.setPopup(token);
					Eyespynature.this.displayPanel(basketPanel);
				} else {
					Eyespynature.this.displayPanel(new SimpleTextPanel(token));
				}
			}
		});
		logger.fine("Firing current history");
		History.fireCurrentHistoryState();

	}
}
