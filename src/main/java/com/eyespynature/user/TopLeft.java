package com.eyespynature.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TopLeft extends Composite {

	interface MyUiBinder extends UiBinder<Widget, TopLeft> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Image logo;

	@UiHandler("logo")
	void onClickLogo(ClickEvent event) {
		History.newItem("Home");
	}

	public TopLeft() {
		initWidget(uiBinder.createAndBindUi(this));

	}
}
