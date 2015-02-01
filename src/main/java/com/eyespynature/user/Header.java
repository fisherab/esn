package com.eyespynature.user;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.user.events.SearchRequestedEvent;
import com.eyespynature.user.events.SearchRequestedEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

	interface MyUiBinder extends UiBinder<Widget, Header> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final EventBus bus = TheEventBus.getInstance();

	@UiField
	TextBox search;

	@UiField
	BasketInHeader basketInHeader;

	@UiHandler("searchButton")
	void handleSearchClick(ClickEvent event) {
		bus.fireEvent(new SearchRequestedEvent(search.getText().trim()));
	}

	@UiHandler("home")
	void onClickHome(ClickEvent event) {
		History.newItem("Home");
	}

	@UiHandler("nestboxes")
	void onClickNestboxes(ClickEvent event) {
		History.newItem("Home:Nestboxes");
	}

	@UiHandler("cameras")
	void onClickCameras(ClickEvent event) {
		History.newItem("Home:Cameras");
	}

	@UiHandler("tools")
	void onClickTools(ClickEvent event) {
		History.newItem("Home:Tools");
	}

	@UiHandler("faq")
	void onClickContactFaq(ClickEvent event) {
		History.newItem("faq");
	}

	@UiHandler("gettingStarted")
	void onClickContactGettingStarted(ClickEvent event) {
		History.newItem("starting");
	}

	@UiHandler("contactUs")
	void onClickContactUs(ClickEvent event) {
		History.newItem("contact");
	}

	@UiHandler("aboutUs")
	void onClickAboutUs(ClickEvent event) {
		History.newItem("about");
	}

	@UiHandler("search")
	void handleEnter(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
			bus.fireEvent(new SearchRequestedEvent(search.getText().trim()));
		}
	}

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		bus.addHandler(SearchRequestedEvent.TYPE, new SearchRequestedEventHandler() {
			@Override
			public void onEvent(SearchRequestedEvent searchRequestedEvent) {
				search.setText(searchRequestedEvent.getQuery());
			}
		});

	}
}
