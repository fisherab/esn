package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FileDescription implements IsSerializable {

	// Needed for RPC stuff
	public FileDescription() {
	}

	private String name;
	private boolean directory;

	public FileDescription(String name, boolean directory) {
		this.name = name;
		this.directory = directory;
	}

	public String getName() {
		return name;
	}

	public boolean isDirectory() {
		return directory;
	}

	@Override
	public String toString() {
		return name + " " + (directory ? "D" : "F");
	}

}
