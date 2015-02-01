package com.eyespynature.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Footer extends Composite {

	interface MyUiBinder extends UiBinder<Widget, Footer> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

	}
}
