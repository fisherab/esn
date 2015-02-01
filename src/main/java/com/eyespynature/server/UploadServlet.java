package com.eyespynature.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

@WebServlet(name = "upload", urlPatterns = { "/upload" })
@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

	final static Logger logger = Logger.getLogger(UploadServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		ServletFileUpload upload = new ServletFileUpload();
		resp.setContentType("text/plain");

		String filename = null;
		String dirInForm = null;

		File temp = File.createTempFile("esn.", null, Properties.getFileBaseDir());
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();

				if (!item.isFormField()) {
					UploadServlet.logger.debug("Got an uploaded file: " + item.getFieldName()
							+ ", name = " + item.getName());

					byte[] buffer = new byte[1024];
					FileOutputStream fos = new FileOutputStream(temp);
					int n;
					while ((n = stream.read(buffer)) != -1) {
						fos.write(buffer, 0, n);
					}
					fos.close();
				} else {
					String value = Streams.asString(stream);
					String name = item.getFieldName();
					UploadServlet.logger.debug("Got a field: " + name + " = " + value);
					if (name.equals("filename")) {
						filename = value;
					}
					if (name.equals("dirInForm")) {
						dirInForm = value;
					}
				}
				stream.close();
			}

		} catch (FileUploadException e) {
			logger.debug("FileUploadException " + e.getMessage());
			throw new ServletException(e.getMessage());
		}

		try {
			temp.renameTo(new File(Properties.getFileBaseDir(), dirInForm + "/" + filename));
			resp.getWriter().print("File upload succesful");
		} catch (Throwable e) {
			logger.debug(e);
			throw new ServletException(e.getMessage());
		}

	}
}
