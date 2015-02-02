import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eyespynature.shared.json.Amount;
import com.eyespynature.shared.json.Payer;
import com.eyespynature.shared.json.Payment;
import com.eyespynature.shared.json.RedirectUrls;
import com.eyespynature.shared.json.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestPayment {

	@Test
	public void test1() throws Exception {
		Payment payment = new Payment();
		payment.intent = "sale";
		payment.redirect_urls = new RedirectUrls();
		payment.redirect_urls.cancel_url = "canc";
		payment.redirect_urls.return_url = "retu";
		payment.payer = new Payer();
		payment.payer.payment_method = "paypal";
		payment.transactions.add(new Transaction());
		payment.transactions.get(0).amount = new Amount();
		payment.transactions.get(0).amount.total = "7.47";
		payment.transactions.get(0).amount.currency = "USD";
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(payment);
		payment = mapper.readValue(result, Payment.class);
		assertEquals("sale", payment.intent);
		assertEquals("retu", payment.redirect_urls.return_url);
		assertEquals("7.47", payment.transactions.get(0).amount.total);

		JsonNode rootNode = mapper.readValue(result, JsonNode.class);
		System.out.println(rootNode);

		JsonNode transactions = rootNode.get("transactions");
		if (transactions != null) {
			for (JsonNode transaction : transactions) {
				assertEquals("7.47", transaction.get("amount").get("total").asText());
			}
		}

	}

}