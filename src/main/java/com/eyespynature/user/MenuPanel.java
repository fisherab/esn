package com.eyespynature.user;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.eyespynature.client.service.ProductServiceAsync;
import com.eyespynature.shared.DescriptionMenuAndProductTypes;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, MenuPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final ProductServiceAsync productService = ProductServiceAsync.Util.getInstance();

	private final static Resources resources = GWT.create(Resources.class);
	final private static Logger logger = Logger.getLogger(MenuPanel.class.getName());

	@UiField
	CrumbPanel crumbs;

	@UiField
	VerticalPanel leftMenuPanel;

	@UiField
	VerticalPanel items;

	@UiField
	HTML html;

	public MenuPanel(final String token) {

		initWidget(uiBinder.createAndBindUi(this));

		String[] tokens = token.split(":");
		final List<String> menu = new ArrayList<String>(tokens.length - 1);
		for (int i = 1; i < tokens.length; i++) {
			menu.add(tokens[i]);
		}
		crumbs.setCrumbs(tokens);

		logger.fine("Calling get menu");
		productService.getMenu(menu, new AsyncCallback<DescriptionMenuAndProductTypes>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(DescriptionMenuAndProductTypes menuAndProductTypes) {
				List<String> words = menuAndProductTypes.getMenuItems();

				leftMenuPanel.addStyleName(resources.css().menu());
				leftMenuPanel.add(new HTML("Products<hr/>"));
				html.setHTML(menuAndProductTypes.getHtml());

				List<ProductTypeTransferObject> pttos = menuAndProductTypes.getProductTypes();
				for (ProductTypeTransferObject ptto : pttos) {
					items.add(new Item(ptto));
				}
				if (words.isEmpty()) {
					leftMenuPanel.setVisible(false);
				} else {
					for (final String word : words) {
						Hyperlink hword = new Hyperlink(word, token + ":" + word);
						leftMenuPanel.add(hword);
					}
				}
			}
		});

	}
}
