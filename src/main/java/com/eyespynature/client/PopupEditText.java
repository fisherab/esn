package com.eyespynature.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupEditText extends PopupPanel {

	private TextArea input;
	private Button pApply;

	public PopupEditText() {

		setGlassEnabled(true);
		VerticalPanel pvp = new VerticalPanel();
		add(pvp);

		input = new TextArea();
		this.input.setHeight("10em");
		this.input.setWidth("40em");
		pvp.add(this.input);

		HorizontalPanel pbs = new HorizontalPanel();
		pbs.setSpacing(10);
		pvp.add(pbs);

		pApply = new Button("Apply");

		pbs.add(pApply);

		Button pDiscard = new Button("Discard");
		pDiscard.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		pbs.add(pDiscard);

	}

	public Button getApply() {
		return pApply;
	}

	public void setValue(String value) {
		this.input.setValue(value);
	}

	public String getValue() {
		return this.input.getValue();
	}
}
