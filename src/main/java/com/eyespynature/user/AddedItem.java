package com.eyespynature.user;

import java.util.List;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.user.events.AddedItemButtonEvent;
import com.eyespynature.user.events.AddedItemButtonEvent.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddedItem extends Composite {

	interface MyUiBinder extends UiBinder<Widget, AddedItem> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final EventBus bus = TheEventBus.getInstance();

	@UiField
	HTML name;

	@UiField
	VerticalPanel images;

	@UiHandler("kontinue")
	public void continueClicked(ClickEvent event) {
		bus.fireEvent(new AddedItemButtonEvent(ButtonType.kontinue));
	}

	@UiHandler("basket")
	public void basketClicked(ClickEvent event) {
		bus.fireEvent(new AddedItemButtonEvent(ButtonType.basket));
	}

	public AddedItem(ProductTypeTransferObject ptto) {
		initWidget(uiBinder.createAndBindUi(this));

		List<String> names = ptto.getImages();

		Image image = names.isEmpty() ? new Image() : new Image("download?name=" + names.get(0)
				+ "&width=100");
		images.add(image);

		name.setText(ptto.getName());

	}
}
