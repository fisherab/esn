package com.eyespynature.admin;

import java.util.List;

import com.eyespynature.client.HistoryTokenType;
import com.eyespynature.client.service.SecureProductServiceAsync;
import com.eyespynature.shared.Role;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavigationPanel extends Composite {

	private final static AdminResources resources = GWT.create(AdminResources.class);

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	private VerticalPanel panel;

	public NavigationPanel() {

		panel = new VerticalPanel();
		panel.addStyleName(resources.css().navPanel());
		this.initWidget(panel);

		secureProductService.getRoles(new AsyncCallback<List<Role>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(List<Role> result) {
				init(result);
			}

		});
	}

	private void init(List<Role> roles) {

		if (roles.contains(Role.Viewer)) {
			final VerticalPanel reportsPanel = new VerticalPanel();
			reportsPanel.addStyleName(resources.css().navPanel());
			panel.add(reportsPanel);
		}

		if (roles.contains(Role.StockManager)) {
			final VerticalPanel stockPanel = new VerticalPanel();
			stockPanel.addStyleName(resources.css().navPanel());
			panel.add(stockPanel);
			stockPanel
					.add(new InlineHyperlink("Manage stock", HistoryTokenType.MANAGE_STOCK.name()));
			stockPanel.add(new InlineHyperlink("Manage orders", HistoryTokenType.MANAGE_ORDERS
					.name()));
		}

		if (roles.contains(Role.ProductManager)) {
			final VerticalPanel productsPanel = new VerticalPanel();
			productsPanel.addStyleName(resources.css().navPanel());
			panel.add(productsPanel);
			productsPanel.add(new InlineHyperlink("Add Product", HistoryTokenType.ADD_PRODUCT
					.name()));
			productsPanel.add(new InlineHyperlink("Update product", HistoryTokenType.UPDATE_PRODUCT
					.name()));
			productsPanel.add(new InlineHyperlink("Obsolete product",
					HistoryTokenType.OBSOLETE_PRODUCT.name()));
			productsPanel.add(new InlineHyperlink("Restore product",
					HistoryTokenType.RESTORE_PRODUCT.name()));
			productsPanel.add(new InlineHyperlink("Manage files", HistoryTokenType.MANAGE_FILES
					.name()));
			productsPanel.add(new InlineHyperlink("Manage product html", HistoryTokenType.PRODUCT_HTML
					.name()));
			productsPanel.add(new InlineHyperlink("Manage other html", HistoryTokenType.OTHER_HTML
					.name()));
		}
	}

}
