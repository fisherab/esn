package com.eyespynature.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Properties {

	private static File CACHE_BASE_DIR;

	private static File DATA_BASE_DIR;

	private static Exception eKeep;

	private static File FILE_BASE_DIR;

	private static String PAYPAL_MERCHANT_ID;

	private static String PAYPAL_MERCHANT_KEY;

	private static String PAYPAL_URL;

	private static String ESN_URL;

	private static String BASE_URL;

	static {
		try {

			DATA_BASE_DIR = new File(new File("..", "data"), "esn");

			FILE_BASE_DIR = new File(DATA_BASE_DIR, "file");

			CACHE_BASE_DIR = new File(DATA_BASE_DIR, "cache");

			java.util.Properties prop = new java.util.Properties();
			InputStream inStream = new FileInputStream(new File("esn.properties"));
			prop.load(inStream);

			PAYPAL_MERCHANT_ID = prop.getProperty("paypal.merchant.id");
			PAYPAL_MERCHANT_KEY = prop.getProperty("paypal.merchant.key");
			PAYPAL_URL = prop.getProperty("paypal.url");

			BASE_URL = prop.getProperty("base.url");
			ESN_URL = prop.getProperty("esn.url");
		} catch (Exception e) {
			eKeep = e;
		}
	}

	public static void check() throws Exception {
		if (eKeep != null) {
			throw eKeep;
		}
	}

	public static File getCacheBaseDir() {
		return CACHE_BASE_DIR;
	}

	public static File getDataBaseDir() {
		return DATA_BASE_DIR;
	}

	public static Exception geteKeep() {
		return eKeep;
	}

	public static File getFileBaseDir() {
		return FILE_BASE_DIR;
	}

	public static String getPaypalMerchantId() {
		return PAYPAL_MERCHANT_ID;
	}

	public static String getPaypalMerchantKey() {
		return PAYPAL_MERCHANT_KEY;
	}

	public static String getPaypalUrl() {
		return PAYPAL_URL;
	}

	public static String getBaseUrl() {
		return BASE_URL;
	}

	public static String getEsnUrl() {
		return ESN_URL;
	}

}
