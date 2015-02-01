package com.eyespynature.user;

import java.util.List;

import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BasketItem extends Composite {

	interface MyUiBinder extends UiBinder<Widget, BasketItem> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	HTML name;

	@UiField
	HTML price;

	@UiField
	HTML total;

	@UiField
	TextBox quantity;

	@UiField
	Button quantityButton;

	@UiField
	Image image;

	private int count;

	@UiHandler("remove")
	void handleClick(ClickEvent e) {
		basketPanel.remove(this);
	}

	@UiHandler("quantityButton")
	void handleQuantityButton(ClickEvent e) {
		if (quantityButton.getText().equals("Remove")) {
			basketPanel.remove(this);
		} else {
			try {
				int n = Integer.parseInt(quantity.getText());
				if (n > ptto.getNumberInStock()) {
					int m = ptto.getNumberInStock();
					Window.alert("There " + (m == 1 ? "is" : "are") + " currently only " + m
							+ " item" + (m == 1 ? "" : "s") + " in stock");
				} else {
					count = n;
					long totalPence = ptto.getPrice() * count;
					total.setHTML(BasketPanel.formatPrice(totalPence));
					basketPanel.updateCart();
					quantityButton.setVisible(false);
				}
			} catch (NumberFormatException e1) {
				Window.alert("Quantity must be a non-negative integer");
			}
		}
	}

	@UiHandler("quantity")
	void handleChangeEvent(KeyUpEvent e) {
		String value = quantity.getValue();
		int ivalue = -1;
		try {
			ivalue = Integer.parseInt(value);
		} catch (NumberFormatException e1) {
		}
		if (ivalue == 0) {
			quantityButton.setText("Remove");
			quantityButton.setVisible(true);
		} else if (ivalue != count) {
			quantityButton.setText("Update");
			quantityButton.setVisible(true);
		} else {
			quantityButton.setVisible(false);
		}

	}

	private BasketPanel basketPanel;

	private ProductTypeTransferObject ptto;

	public BasketItem(BasketPanel basketPanel, ProductTypeTransferObject ptto, int count) {
		initWidget(uiBinder.createAndBindUi(this));
		this.basketPanel = basketPanel;

		this.ptto = ptto;

		this.count = count;

		List<String> imageUrls = ptto.getImages();
		if (imageUrls.size() > 0) {
			image.setUrl("download?name=" + imageUrls.get(0) + "&width=100");
		}

		name.setText(ptto.getName());
		long pence = ptto.getPrice();

		price.setHTML(BasketPanel.formatPrice(pence));

		quantity.setText(Integer.toString(count));

		total.setHTML(BasketPanel.formatPrice(pence * count));

	}

	public ProductTypeTransferObject getPtto() {
		return ptto;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
		quantity.setText(Integer.toString(count));
		long totalPence = ptto.getPrice() * count;
		total.setHTML(BasketPanel.formatPrice(totalPence));
	}

}
