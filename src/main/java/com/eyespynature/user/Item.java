package com.eyespynature.user;

import java.util.List;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.user.events.AddToBasketEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Item extends Composite {

	interface MyUiBinder extends UiBinder<Widget, Item> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Hyperlink name;

	@UiField
	HTML description;

	@UiField
	HTML price;

	@UiField
	Button cartButton;

	@UiField
	HTML stock;

	@UiHandler("cartButton")
	void handleClick(ClickEvent e) {
		TheEventBus.getInstance().fireEvent(new AddToBasketEvent(ptto, 1, false));
	}

	@UiHandler("more")
	void handleMoreButton(ClickEvent e) {
		History.newItem("item:" + ptto.getId());
	}

	@UiField
	Image image;

	private ProductTypeTransferObject ptto;

	public Item(ProductTypeTransferObject ptto) {
		initWidget(uiBinder.createAndBindUi(this));

		this.ptto = ptto;

		List<String> names = ptto.getImages();
		if (names.size() > 0) {
			image.setUrl("download?name=" + names.get(0) + "&width=200");
		}

		name.setText(ptto.getName());
		name.setTargetHistoryToken("item:" + ptto.getId());

		description.setText(ptto.getShortD());

		if (ptto.getNumberInStock() == 0) {
			stock.setHTML("None currently in stock - please <a href='#contact'>contact us</a> for more information.");
			stock.setVisible(true);
			cartButton.setEnabled(false);
		} else if (ptto.getNumberInStock() < Constants.STOCK_LEVEL_WARNING) {
			int m = ptto.getNumberInStock();
			stock.setHTML("There "
					+ (m == 1 ? "is" : "are")
					+ " only "
					+ m
					+ " currently in stock - if you need more please <a href='#contact'>contact us</a> for more information.");
			stock.setVisible(true);
			cartButton.setEnabled(true);
		} else {
			cartButton.setEnabled(true);
		}

		price.setHTML(BasketPanel.formatPrice(ptto.getPrice()));

	}
}
