package com.eyespynature.server;

import java.io.DataOutputStream;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@DependsOn("LoggingConfigurator")
public class PaypalTokenHolder {

	private final static Logger logger = Logger.getLogger(PaypalTokenHolder.class);

	private String token;

	@PostConstruct
	public void init() {
		login();
	}

	private void login() {

		try {
			HttpsURLConnection con = (HttpsURLConnection) (new URL(Properties.getPaypalUrl()
					+ "oauth2/token")).openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Accept-Language", "en_US");
			con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

			String userpass = Properties.getPaypalMerchantId() + ":"
					+ Properties.getPaypalMerchantKey();
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
			con.setRequestProperty("Authorization", basicAuth);

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("grant_type=client_credentials");
			wr.close();

			int responseCode = con.getResponseCode();
			if (responseCode / 100 != 2) {
				throw new Exception("Response code " + responseCode);
			}

			ObjectMapper mapper = new ObjectMapper();

			JsonNode rootNode = mapper.readValue(con.getInputStream(), JsonNode.class);
			token = rootNode.get("access_token").asText();
		} catch (Throwable e) {
			logger.debug("Failed to get token " + e.getClass() + " " + e.getMessage());
		}
		logger.debug("Token " + token);

	}

	@Schedule(hour = "*/1", minute = "21", second = "42")
	public void cleanupWeekData() {
		login();
	}

	public String getToken() {
		return token;
	};

}