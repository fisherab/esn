import java.net.URL;

import org.junit.Test;

public class TestURL {

	@Test
	public void test1() throws Exception {
		URL url = new URL(
				"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-0TX05652VT023503G");

		String[] pairs = url.getQuery().split("&");
		for (String pair : pairs) {
			int loc = pair.indexOf('=');
			String key = pair.substring(0, loc).trim();
			String value = pair.substring(loc + 1).trim();
			if (key.equals("token")) {
				System.out.println("Token is " + value);
			}
		}

	}
}