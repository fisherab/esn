package com.eyespynature.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("Eyespynature.css")
	EyeCss css();

	@Source("images/logo.png")
	ImageResource logo();

	interface EyeCss extends CssResource {
		
		String price();
		
		String buy();

		String border();

		String link();

		String menu();

		String h1();

		String h2();

		String click();
	}
}