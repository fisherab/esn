package com.eyespynature.user;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.user.events.BasketEvent;
import com.eyespynature.user.events.BasketEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class BasketInHeader extends Composite {

	interface MyUiBinder extends UiBinder<Widget, BasketInHeader> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final EventBus bus = TheEventBus.getInstance();
	private static String contentsString;

	@UiField
	HTML contents;

	public BasketInHeader() {
		initWidget(uiBinder.createAndBindUi(this));
		contents.setHTML(contentsString);

		bus.addHandler(BasketEvent.TYPE, new BasketEventHandler() {
			@Override
			public void onEvent(BasketEvent basket) {
				contentsString = basket.getItemCount() + " items = "
						+ basket.getSubtotal().getText();
				contents.setHTML(contentsString);
			}
		});

	}

}
