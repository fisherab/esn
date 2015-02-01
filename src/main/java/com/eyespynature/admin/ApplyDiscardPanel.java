package com.eyespynature.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ApplyDiscardPanel extends Composite {

	private Button apply;
	private Button discard;

	ApplyDiscardPanel() {

		HorizontalPanel panel = new HorizontalPanel();

		panel.setSpacing(10);
		apply = new Button("Apply");
		discard = new Button("Discard");
		panel.add(apply);
		panel.add(discard);

		this.initWidget(panel);
	}

	public Button getApply() {
		return apply;
	}

	public Button getDiscard() {
		return discard;
	}

	public void enable() {
		apply.setEnabled(true);
	}

	public void disable() {
		apply.setEnabled(false);
	}
}
