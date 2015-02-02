package com.eyespynature.client.service;

import com.eyespynature.shared.InternalException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("text")
public interface TextService extends RemoteService {

	String get(String key) throws InternalException;

}
