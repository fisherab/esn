package com.eyespynature.user.events;

import com.google.gwt.event.shared.GwtEvent;

public class AddedItemButtonEvent extends GwtEvent<AddedItemButtonEventHandler> {
	public static Type<AddedItemButtonEventHandler> TYPE = new Type<AddedItemButtonEventHandler>();

	private ButtonType buttonType;

	public enum ButtonType {
		basket, kontinue
	}

	public AddedItemButtonEvent(ButtonType buttonType) {

		this.buttonType = buttonType;
	}

	public ButtonType getButtonType() {
		return buttonType;
	}

	@Override
	public Type<AddedItemButtonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddedItemButtonEventHandler handler) {
		handler.onEvent(this);
	}

}
