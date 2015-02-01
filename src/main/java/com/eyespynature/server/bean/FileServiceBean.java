package com.eyespynature.server.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.eyespynature.server.Properties;
import com.eyespynature.shared.FileDescription;
import com.eyespynature.shared.InternalException;

@Stateless
public class FileServiceBean {

	final static Logger logger = Logger.getLogger(FileServiceBean.class);

	public List<FileDescription> getFiles(String dir) throws InternalException {

		List<FileDescription> result = new ArrayList<FileDescription>();
		File directory = new File(Properties.getFileBaseDir(), dir);
		File[] fs = directory.listFiles();
		Arrays.sort(fs);
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				result.add(new FileDescription(f.getName(), true));
			}
		}
		for (File f : directory.listFiles()) {
			if (!f.isDirectory()) {
				result.add(new FileDescription(f.getName(), false));
			}
		}
		FileServiceBean.logger.debug("Found " + result.size() + " entries in " + dir);
		return result;
	}

	public void deleteFile(String name) throws InternalException {
		File f = new File(Properties.getFileBaseDir(), name);
		if (f.delete()) {
			FileServiceBean.logger.debug("Deleted " + name);
		} else {
			FileServiceBean.logger.debug("Failed to delete " + name + "(" + f.getAbsolutePath() + ")");
			throw new InternalException("Failed to delete " + name);
		}
	}

	public void mkdir(String dir) throws InternalException {
		File f = new File(Properties.getFileBaseDir(), dir);
		if (f.mkdir()) {
			FileServiceBean.logger.debug("Created directory " + dir);
		} else {
			FileServiceBean.logger.debug("Failed to create directory " + dir + "(" + f.getAbsolutePath() + ")");
			throw new InternalException("Failed to create directory " + dir);
		}
	}

}
