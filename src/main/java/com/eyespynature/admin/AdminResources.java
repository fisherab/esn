package com.eyespynature.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface AdminResources extends ClientBundle {
	public static final AdminResources INSTANCE = GWT.create(AdminResources.class);

	@Source("Admin.css")
	EyeCss css();

	interface EyeCss extends CssResource {
		String link();

		String menu();

		String h1();

		String h2();

		String navPanel();

	}

}