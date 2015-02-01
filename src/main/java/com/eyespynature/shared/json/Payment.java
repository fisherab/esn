package com.eyespynature.shared.json;

import java.util.ArrayList;
import java.util.List;

public class Payment {

	public String intent;

	public RedirectUrls redirect_urls;

	public Payer payer;

	public List<Transaction> transactions = new ArrayList<>();
}
