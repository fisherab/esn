package com.eyespynature.client.service;

import java.util.List;

import com.eyespynature.shared.FileDescription;
import com.eyespynature.shared.InternalException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("file")
public interface FileService extends RemoteService {

	List<FileDescription> getFiles(String dir) throws InternalException;

	void deleteFile(String name) throws InternalException;

	void mkdir(String string) throws InternalException;
}
