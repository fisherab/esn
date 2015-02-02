package com.eyespynature.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class CrumbPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, CrumbPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	HorizontalPanel main;

	public CrumbPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		main.setSpacing(5);
	}

	public void setCrumbs(String[] strings) {
		int n = strings.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n - 1; i++) {
			main.add(new Hyperlink(strings[i], sb.toString() + strings[i]));
			main.add(new HTML(">"));
			sb.append(strings[i]).append(':');
		}
		main.add(new HTML(strings[n - 1]));
	}

}
