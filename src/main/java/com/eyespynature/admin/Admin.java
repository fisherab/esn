package com.eyespynature.admin;

import java.util.logging.Logger;

import com.eyespynature.client.HistoryTokenType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class Admin implements EntryPoint {

	final private static Logger logger = Logger
			.getLogger(Admin.class.getName());
	private Widget body = new Widget();

	private DockLayoutPanel main;

	private NavigationPanel navigationPanel;

	private void displayPanel(Widget panel) {
		this.main.remove(this.body);
		this.body = new ScrollPanel(panel);
		this.main.add(this.body);
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		AdminResources.INSTANCE.css().ensureInjected();
		logger.fine("Module loading");

		this.main = new DockLayoutPanel(Unit.EM);
		RootLayoutPanel.get().add(this.main);

		this.navigationPanel = new NavigationPanel();
		this.main.addWest(navigationPanel, 10);

		Node loading = DOM.getElementById("loading");
		RootPanel.getBodyElement().removeChild(loading);

		/* Add history listener */
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();

				if (token.equals(HistoryTokenType.ADD_PRODUCT.name())) {
					Admin.this.displayPanel(new CreateProductTypePanel());
				} else if (token.startsWith(HistoryTokenType.UPDATE_ONE_PRODUCT
						.name())) {
					int tl = HistoryTokenType.UPDATE_ONE_PRODUCT.name()
							.length();
					long id = Long.parseLong(token.substring(tl + 1));
					Admin.this.displayPanel(new UpdateProductTypePanel(id));
				} else if (token.equals(HistoryTokenType.MANAGE_STOCK.name())) {
					Admin.this.displayPanel(new UpdateProductTypesPanel(
							HistoryTokenType.MANAGE_STOCK));
				} else if (token.equals(HistoryTokenType.MANAGE_ORDERS.name())) {
					Admin.this.displayPanel(new ManageOrdersPanel());
				} else if (token.equals(HistoryTokenType.UPDATE_PRODUCT.name())) {
					Admin.this.displayPanel(new UpdateProductTypesPanel(
							HistoryTokenType.UPDATE_PRODUCT));
				} else if (token.equals(HistoryTokenType.OBSOLETE_PRODUCT
						.name())) {
					Admin.this.displayPanel(new UpdateProductTypesPanel(
							HistoryTokenType.OBSOLETE_PRODUCT));
				} else if (token.equals(HistoryTokenType.RESTORE_PRODUCT.name())) {
					Admin.this.displayPanel(new UpdateProductTypesPanel(
							HistoryTokenType.RESTORE_PRODUCT));
				} else if (token.equals(HistoryTokenType.MANAGE_FILES.name())) {
					Admin.this.displayPanel(new FileManagerPanel());
				} else if (token.equals(HistoryTokenType.PRODUCT_HTML.name())) {
					Admin.this.displayPanel(new ProductHtmlManagerPanel());
				} else if (token.equals(HistoryTokenType.OTHER_HTML.name())) {
					Admin.this.displayPanel(new OtherHtmlManagerPanel());
				} else {
					Admin.logger.severe("Current token is unexpected "
							+ event.getValue());
				}
			}
		});

		String initToken = History.getToken();
		if (initToken.length() != 0) {
			logger.fine("Firing current history");
			History.fireCurrentHistoryState();
		} else {
			logger.fine("No history to fire...");
		}
	}

}
