package com.eyespynature.user;

import java.util.List;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.client.service.ProductServiceAsync;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.user.events.AddToBasketEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageItemPanel extends Composite {

	private final ProductServiceAsync productService = ProductServiceAsync.Util.getInstance();

	interface MyUiBinder extends UiBinder<Widget, PageItemPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	HTML name;

	@UiField
	HTML description;

	@UiField
	HTML price;

	@UiField
	CrumbPanel crumbs;

	@UiField
	VerticalPanel images;

	@UiField
	HTML longDescription;

	@UiField
	HTML specification;

	@UiField
	HTML advice;

	@UiField
	Button cartButton;

	@UiField
	HTML stock;

	private ProductTypeTransferObject ptto;

	public PageItemPanel(String token) {

		initWidget(uiBinder.createAndBindUi(this));

		long key = 0;
		try {
			key = Long.parseLong(token);
		} catch (NumberFormatException e) {
			Window.alert("Failed: " + e);
		}

		productService.getWithKey(key, new AsyncCallback<ProductTypeTransferObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);
			}

			@Override
			public void onSuccess(ProductTypeTransferObject ptto) {
				int n = ptto.getMenu().size();
				String[] menu = new String[n + 2];
				menu[0] = "Home";
				for (int i = 0; i < n; i++) {
					menu[i + 1] = ptto.getMenu().get(i);
				}
				menu[n + 1] = ptto.getName();

				crumbs.setCrumbs(menu);
				PageItemPanel.this.ptto = ptto;

				List<String> names = ptto.getImages();

				Image image = new Image("download?name=" + names.get(0) + "&width=320");
				images.add(image);

				name.setText(ptto.getName());

				description.setText(ptto.getShortD());

				longDescription.setHTML(ptto.getLongD());

				advice.setHTML(ptto.getNotes());

				specification.setHTML(ptto.getSpec());

				long p = ptto.getPrice();
				int pence = (int) (p % 100);
				int pounds = (int) (p / 100);
				price.setHTML("&pound;" + Long.toString(pounds) + "."
						+ (Long.toString(pence) + "00").substring(0, 2));

				if (ptto.getNumberInStock() == 0) {
					cartButton.setEnabled(false);
					stock.setHTML("None currently in stock - please <a href='#contact'>contact us</a> for more information.");
				}
			}
		});

	}

	@UiHandler("cartButton")
	void handleClick(ClickEvent e) {
		TheEventBus.getInstance().fireEvent(new AddToBasketEvent(ptto, 1, false));
	}

}
