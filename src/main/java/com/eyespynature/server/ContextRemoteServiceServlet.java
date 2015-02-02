package com.eyespynature.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

/**
 * ContextRemoteServiceServlet is an extension of RemoteServiceServlet that should work behind
 * reverse proxies
 */
@SuppressWarnings("serial")
public class ContextRemoteServiceServlet extends RemoteServiceServlet {

	final static Logger logger = Logger.getLogger(ContextRemoteServiceServlet.class);

	/**
	 * Attempt to load the RPC serialization policy normally. If it isn't found, try harder ...
	 */
	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request,
			String moduleBaseURL, String strongName) {
		logger.debug("Looking for SerializationPolicy");
		SerializationPolicy policy = super.doGetSerializationPolicy(request, moduleBaseURL,
				strongName);
		if (policy == null) {
			logger.debug("Looking harder for SerializationPolicy");
			return ContextRemoteServiceServlet.loadSerializationPolicy(this, request,
					moduleBaseURL, strongName);
		} else {
			return policy;
		}
	}

	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
			HttpServletRequest request, String moduleBaseURL, String strongName) {

		String modulePath = null;
		if (moduleBaseURL != null) {
			try {
				modulePath = new URL(moduleBaseURL).getPath();
			} catch (MalformedURLException ex) {
				// log the information, we will default
				servlet.log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
			}
		}

		String serializationPolicyFilePath = SerializationPolicyLoader
				.getSerializationPolicyFileName(modulePath + strongName);

		// Open the RPC resource file and read its contents.
		SerializationPolicy serializationPolicy = null;
		try (InputStream is = servlet.getServletContext().getResourceAsStream(
				serializationPolicyFilePath)) {
			if (is != null) {
				try {
					serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
				} catch (ParseException e) {
					logger.error("ERROR: Failed to parse the policy file '"
							+ serializationPolicyFilePath + "'", e);
				} catch (IOException e) {
					logger.error("ERROR: Could not read the policy file '"
							+ serializationPolicyFilePath + "'", e);
				}
			} else {
				String message = "ERROR: The serialization policy file '"
						+ serializationPolicyFilePath
						+ "' was not found; did you forget to include it in this deployment?";
				logger.error(message);
			}
		} catch (IOException e) {
			logger.error(e.getClass() + " " + e.getMessage());
		}
		logger.debug("Returning SerializationPolicy");
		return serializationPolicy;

	}

}