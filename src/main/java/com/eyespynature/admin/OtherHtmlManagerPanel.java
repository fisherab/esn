package com.eyespynature.admin;

import java.util.List;

import com.eyespynature.client.service.SecureProductServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class OtherHtmlManagerPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, OtherHtmlManagerPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Button apply_button;

	@UiField
	Button discard_button;

	@UiField
	TextArea html;

	private boolean modified;

	@UiField
	ListBox page;

	@UiField
	HTML preview;

	@UiField
	Button preview_button;

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	@UiField
	HTML status;

	public OtherHtmlManagerPanel() {

		initWidget(uiBinder.createAndBindUi(this));

		setEnabled(false);

		secureProductService.getPagenames(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(List<String> items) {
				for (String item : items) {
					page.addItem(item);
				}
			}
		});

	}

	@UiHandler("apply_button")
	void applyButtonHandler(ClickEvent event) {
		String name = page.getValue(page.getSelectedIndex());
		secureProductService.putPage(name, html.getText(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				status.setHTML("Page stored");
				setEnabled(false);
			}
		});

	}

	@UiHandler("discard_button")
	void discardButtonHandler(ClickEvent event) {
		refresh();
	}

	@UiHandler("html")
	void htmlHandler(KeyPressEvent event) {
		if (!modified) {
			setEnabled(true);
		}
	}

	@UiHandler("page")
	void pageHandler(ClickEvent event) {
		refresh();
	}

	@UiHandler("preview_button")
	void previewButtonHandler(ClickEvent event) {
		preview.setHTML(html.getText());
	}

	private void refresh() {
		String name = page.getValue(page.getSelectedIndex());
		secureProductService.getPage(name, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(String html) {
				OtherHtmlManagerPanel.this.html.setText(html);
				preview.setHTML(html);
			}
		});
		setEnabled(false);
		status.setHTML("");
	}

	private void setEnabled(boolean flag) {
		modified = flag;
		apply_button.setEnabled(flag);
		preview_button.setEnabled(flag);
		discard_button.setEnabled(flag);
		page.setEnabled(!flag);
	}
};
