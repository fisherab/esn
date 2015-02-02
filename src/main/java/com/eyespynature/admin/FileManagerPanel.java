package com.eyespynature.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FileManagerPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, FileManagerPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FileSelectionPanel fsp;

	public FileManagerPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
