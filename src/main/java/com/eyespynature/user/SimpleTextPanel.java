/**
 * 
 */
package com.eyespynature.user;

import com.eyespynature.client.service.TextServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class SimpleTextPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, SimpleTextPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final TextServiceAsync textService = TextServiceAsync.Util.getInstance();

	@UiField
	HTML html;

	public SimpleTextPanel() {

	}

	public SimpleTextPanel(String identifier) {
		initWidget(uiBinder.createAndBindUi(this));

		textService.get(identifier, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);

			}

			@Override
			public void onSuccess(String text) {
				html.setHTML(text);
			}
		});

	}

}
