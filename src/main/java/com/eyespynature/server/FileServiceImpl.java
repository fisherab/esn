package com.eyespynature.server;

import java.util.List;

import javax.ejb.EJB;

import com.eyespynature.client.service.FileService;
import com.eyespynature.server.bean.FileServiceBean;
import com.eyespynature.shared.FileDescription;
import com.eyespynature.shared.InternalException;

@SuppressWarnings("serial")
public class FileServiceImpl extends ContextRemoteServiceServlet implements FileService {

	@EJB
	private FileServiceBean fileServiceBean;

	@Override
	public List<FileDescription> getFiles(String dir) throws InternalException {
		return fileServiceBean.getFiles(dir);
	}

	@Override
	public void deleteFile(String name) throws InternalException {
		fileServiceBean.deleteFile(name);
	}

	@Override
	public void mkdir(String dir) throws InternalException {
		fileServiceBean.mkdir(dir);
	}
}
