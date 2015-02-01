package com.eyespynature.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TextServiceAsync {
	public static final class Util {
		private static TextServiceAsync instance;

		public static final TextServiceAsync getInstance() {
			if (instance == null) {
				instance = (TextServiceAsync) GWT.create(TextService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void get(String key, AsyncCallback<String> callback);

}
