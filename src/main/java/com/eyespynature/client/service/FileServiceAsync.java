package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.FileDescription;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileServiceAsync {

	public static final class Util {
		private static FileServiceAsync instance;

		public static final FileServiceAsync getInstance() {
			if (instance == null) {
				instance = (FileServiceAsync) GWT.create(FileService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void getFiles(String dir, AsyncCallback<List<FileDescription>> callback);

	void deleteFile(String name, AsyncCallback<Void> asyncCallback);

	void mkdir(String string, AsyncCallback<Void> asyncCallback);

}
