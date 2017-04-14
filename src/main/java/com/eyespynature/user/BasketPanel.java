package com.eyespynature.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.client.service.BasketServiceAsync;
import com.eyespynature.client.service.ProductServiceAsync;
import com.eyespynature.shared.Address;
import com.eyespynature.shared.BasketTransferItemObject;
import com.eyespynature.shared.DeliveryCharges;
import com.eyespynature.shared.DeliveryCharges.DeliveryMethod;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.shared.Provider;
import com.eyespynature.user.events.AddToBasketEvent;
import com.eyespynature.user.events.AddToBasketEventHandler;
import com.eyespynature.user.events.AddedItemButtonEvent;
import com.eyespynature.user.events.AddedItemButtonEvent.ButtonType;
import com.eyespynature.user.events.AddedItemButtonEventHandler;
import com.eyespynature.user.events.BasketEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BasketPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, BasketPanel> {
	}

	private static final BasketServiceAsync basketService = BasketServiceAsync.Util.getInstance();
	private static final EventBus bus = TheEventBus.getInstance();
	private final static Resources resources = GWT.create(Resources.class);

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label setAddressLabel;

	@UiField
	VerticalPanel basket;

	@UiField
	HTML economy;

	@UiField
	HTML standard;

	@UiField
	HTML pickup;

	@UiField
	VerticalPanel basketFrame;

	@UiField
	VerticalPanel processing;

	@UiField
	HTML processingMessage;

	@UiHandler("standardButton")
	void handleStandardButton(ClickEvent e) {
		updateCart();
	}

	@UiHandler("economyButton")
	void handleEconomyButton(ClickEvent e) {
		updateCart();
	}

	@UiHandler("pickupButton")
	void handlePickupButton(ClickEvent e) {
		updateCart();
	}

	@UiHandler("nextDayButton")
	void handleNextDayButton(ClickEvent e) {
		updateCart();
	}

	@UiHandler("firstClassButton")
	void handleFirstClassButton(ClickEvent e) {
		updateCart();
	}

	@UiField
	RadioButton economyButton;

	@UiField
	RadioButton standardButton;

	@UiField
	RadioButton pickupButton;

	@UiField
	RadioButton nextDayButton;

	@UiField
	RadioButton firstClassButton;

	@UiField
	HTML firstClass;

	@UiField
	HTML nextDay;

	private final ProductServiceAsync productService = ProductServiceAsync.Util.getInstance();

	@UiField
	HTML subtotal;

	private int toRestore;

	@UiField
	HTML warning;

	@UiField
	HTML total;
	private long dCharge;

	private DeliveryMethod dMethod;

	@UiField
	HTML paypalCheckout;

	@UiField
	DialogBox returnBox;

	@UiField
	DialogBox rejectBox;

	@UiField
	DialogBox badPaymentBox;

	@UiField
	DialogBox cancelBox;

	@UiField
	DialogBox confirmedBox;

	@UiField
	DialogBox addressBox;

	private String orderId;

	@UiField
	HTMLPanel changeAddressButton;

	@UiField
	HTML addressHTML;

	@UiField
	TextBox address1;

	@UiField
	TextBox address2;

	@UiField
	TextBox town;

	@UiField
	TextBox county;

	@UiField
	TextBox postcode;

	@UiField
	Label address1Message;

	@UiField
	Label townMessage;

	@UiField
	Label postcodeMessage;

	private Address address;

	@UiHandler("okAddressButton")
	void handleOkAddressButton(ClickEvent e) {
		boolean good = true;
		if (address1.getText().isEmpty()) {
			address1Message.setText("Please provided a value");
			good = false;
		} else {
			address1Message.setText("");
		}

		if (town.getText().isEmpty()) {
			townMessage.setText("Please provided a value");
			good = false;
		} else {
			townMessage.setText("");
			town.setText(town.getText().toUpperCase());
		}
		if (postcode.getText().isEmpty()) {
			postcodeMessage.setText("Please provided a value");
			good = false;
		} else {
			postcodeMessage.setText("");
			postcode.setText(postcode.getText().toUpperCase());
		}

		if (good) {
			loadAddressFromForm();
			displayAddress();
			addressBox.hide();
			updateCart();
		}
	}

	private void loadAddressFromForm() {
		address = new Address(address1.getText(), address2.getText(), town.getText(), county.getText(),
				postcode.getText());
		if (Cookies.getCookie("cookies") != null) {
			Date date = new Date();
			date.setTime(date.getTime() + 3600 * 168 * 1000 * 52);
			Cookies.setCookie("address1", address.getAddress1(), date);
			Cookies.setCookie("address2", address.getAddress2(), date);
			Cookies.setCookie("town", address.getTown(), date);
			Cookies.setCookie("county", address.getCounty(), date);
			Cookies.setCookie("postcode", address.getPostcode(), date);
		}
	}

	@UiHandler("cancelAddressButton")
	void handleCancelAddressButton(ClickEvent e) {
		addressBox.hide();
	}

	@UiHandler("okReturnButton")
	void handleOkReturnButton(ClickEvent e) {
		basketService.confirmOrder(orderId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed: " + caught);

			}

			@Override
			public void onSuccess(Void result) {
				returnBox.hide();
				confirmedBox.center();
			}
		});

	}

	@UiHandler("cancelReturnButton")
	void handleCancelReturnButton(ClickEvent e) {
		returnBox.hide();
	}

	@UiHandler("okConfirmedButton")
	void handleOkConfirmedButton(ClickEvent e) {
		basket.clear();
		updateCart();
		confirmedBox.hide();
		History.newItem("Home");
	}

	@UiHandler("okRejectButton")
	void handleOkRejectButton(ClickEvent e) {
		rejectBox.hide();
	}

	@UiHandler("okBadPaymentButton")
	void handleOkBadPaymentButton(ClickEvent e) {
		badPaymentBox.hide();
	}

	@UiHandler("okCancelButton")
	void handleOkCancelButton(ClickEvent e) {
		cancelBox.hide();
	}

	public BasketPanel() {

		this.initWidget(BasketPanel.uiBinder.createAndBindUi(this));
		processing.setVisible(false);

		rejectBox.center();
		badPaymentBox.center();
		confirmedBox.center();
		returnBox.center();
		cancelBox.center();
		addressBox.center();

		rejectBox.hide();
		badPaymentBox.hide();
		confirmedBox.hide();
		returnBox.hide();
		cancelBox.hide();
		addressBox.hide();

		changeAddressButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addressBox.center();
			}
		}, ClickEvent.getType());

		final PopupPanel dialog = new PopupPanel();
		dialog.setGlassEnabled(true);
		dialog.addStyleName(BasketPanel.resources.css().border());

		String cart = Cookies.getCookie("cart");
		if (cart != null) {
			String[] cartArray = cart.split(" ");
			int n = cartArray.length;
			this.toRestore = n / 2;
			this.warning.setHTML("Cart contents are being restored with " + this.toRestore + " to go");
			for (int i = 0; i < n; i += 2) {
				long key = Long.parseLong(cartArray[i]);
				final int count = Integer.parseInt(cartArray[i + 1]);
				this.productService.getWithKey(key, new AsyncCallback<ProductTypeTransferObject>() {

					@Override
					public void onFailure(Throwable caught) {
						BasketPanel.this.toRestore--;
						if (BasketPanel.this.toRestore == 0) {
							BasketPanel.this.warning.setHTML("");
						}
						Window.alert("Failed: " + caught);
					}

					@Override
					public void onSuccess(ProductTypeTransferObject ptto) {
						BasketPanel.this.toRestore--;
						TheEventBus.getInstance().fireEvent(new AddToBasketEvent(ptto, count, true));
						if (BasketPanel.this.toRestore == 0) {
							BasketPanel.this.warning.setHTML("");
						}
					}
				});
			}
		}

		BasketPanel.bus.addHandler(AddToBasketEvent.TYPE, new AddToBasketEventHandler() {

			@Override
			public void onEvent(AddToBasketEvent event) {
				ProductTypeTransferObject ptto = event.getPtto();
				boolean update = false;
				for (Widget item : BasketPanel.this.basket) {
					BasketItem bItem = (BasketItem) item;
					if (bItem.getPtto().getId() == ptto.getId()) {
						bItem.setCount(bItem.getCount() + 1);
						update = true;
						break;
					}
				}
				if (!update) {
					if (Cookies.getCookie("processing") == null) {
						if (ptto.getNumberInStock() == 0) {
							Window.alert("There are no " + ptto.getName()
									+ " items in stock so none have been restored to to your basket");
						} else {
							int n = event.getCount();
							if (n > ptto.getNumberInStock()) {
								Window.alert("The number of items of type " + ptto.getName()
										+ " in your basket has been reduced to match available stock");
								n = ptto.getNumberInStock();
							}
							BasketPanel.this.basket.add(new BasketItem(BasketPanel.this, ptto, n));
						}
					} else {
						BasketPanel.this.basket.add(new BasketItem(BasketPanel.this, ptto, event.getCount()));
					}
				}
				updateCart();
				if (!event.isSilent()) {
					dialog.clear();
					dialog.add(new AddedItem(ptto));
					dialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
						@Override
						public void setPosition(int offsetWidth, int offsetHeight) {
							int left = (Window.getClientWidth() - offsetWidth) / 2;
							int top = (Window.getClientHeight() - offsetHeight) / 3;
							dialog.setPopupPosition(left, top);
						}
					});
				}
			}

		});

		BasketPanel.bus.addHandler(AddedItemButtonEvent.TYPE, new AddedItemButtonEventHandler() {

			@Override
			public void onEvent(AddedItemButtonEvent addedItemButtonEvent) {
				dialog.hide();
				if (addedItemButtonEvent.getButtonType() == ButtonType.basket) {
					History.newItem("basket");
				}

			}
		});

		if (Cookies.getCookie("address1") != null) {
			address = new Address(Cookies.getCookie("address1"), Cookies.getCookie("address2"),
					Cookies.getCookie("town").toUpperCase(), Cookies.getCookie("county"),
					Cookies.getCookie("postcode").toUpperCase());
		}
		displayAddress();
		if (address != null) {
			address1.setText(address.getAddress1());
			address2.setText(address.getAddress2());
			town.setText(address.getTown());
			county.setText(address.getCounty());
			postcode.setText(address.getPostcode());
		}
	}

	private void displayAddress() {
		if (address != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("<p>");
			sb.append(address.getAddress1() + "<br/>");
			if (!address.getAddress2().isEmpty()) {
				sb.append(address.getAddress2() + "<br/>");
			}
			sb.append(address.getTown() + "<br/>");
			if (!address.getCounty().isEmpty()) {
				sb.append(address.getCounty() + "<br/>");
			}
			sb.append(address.getPostcode() + "<br/>");
			sb.append("</p>");
			addressHTML.setHTML(sb.toString());
		} else {
			addressHTML.setHTML("");
		}
	}

	@UiHandler("paypalCheckout")
	void handlePaypalCheckout(ClickEvent e) {
		handleCheckout(Provider.PAYPAL);
	}

	private void setProcessing(boolean p) {
		processing.setVisible(p);
		basketFrame.setVisible(!p);
	}

	private void handleCheckout(Provider provider) {
		List<BasketTransferItemObject> bis = new ArrayList<BasketTransferItemObject>();
		for (Widget item : this.basket) {
			BasketItem bi = (BasketItem) item;
			ProductTypeTransferObject ptto = bi.getPtto();
			bis.add(new BasketTransferItemObject(ptto.getId(), ptto.getName(), ptto.getShortD(), ptto.getPrice(),
					bi.getCount()));
		}
		setProcessing(true);
		if (Cookies.getCookie("cookies") != null) {
			Date date = new Date();
			date.setTime(date.getTime() + 3600 * 168 * 1000);
			Cookies.setCookie("processing", "", date);
		}
		processingMessage.setHTML(
				"<p>Establishing PayPalconnection - please wait.</p><p>Don't use browser forward back or refresh buttons.</p>");
		BasketPanel.basketService.checkOut(bis, dMethod, provider, address, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				warning.setHTML("");
				Window.alert("Failure " + caught);
			}

			@Override
			public void onSuccess(String result) {
				processingMessage.setHTML(
						"<p>Transferring to PayPal - please wait.</p><p>Don't use browser forward back or refresh buttons.</p>");
				Window.open(result, "_self", "");
			}
		});
	}

	public static String formatPrice(long price) {
		int pence = (int) (price % 100);
		int pounds = (int) (price / 100);
		return "&pound;" + Long.toString(pounds) + "." + (Long.toString(pence) + "00").substring(0, 2);
	}

	public void remove(BasketItem basketItem) {
		this.basket.remove(basketItem);
		this.updateCart();
	}

	private void setDelivery(int weight, long subtotalLong, int countBig) {

		long charge = DeliveryCharges.economyCharge(weight, subtotalLong, countBig);
		this.economy.setHTML(formatPrice(charge));
		if (economyButton.getValue()) {
			dCharge = charge;
			dMethod = DeliveryMethod.ECONOMY;
		}

		charge = DeliveryCharges.standardCharge(weight, subtotalLong, countBig);
		this.standard.setHTML(formatPrice(charge));
		if (standardButton.getValue()) {
			dCharge = charge;
			dMethod = DeliveryMethod.STANDARD;
		}

		charge = DeliveryCharges.pickupCharge(weight, subtotalLong, countBig);
		this.pickup.setHTML(formatPrice(charge));
		if (pickupButton.getValue()) {
			dCharge = charge;
			dMethod = DeliveryMethod.PICK_UP;
		}

		charge = DeliveryCharges.firstClassCharge(weight, countBig);
		this.firstClass.setHTML(formatPrice(charge));
		if (firstClassButton.getValue()) {
			dCharge = charge;
			dMethod = DeliveryMethod.FIRST_CLASS;
		}

		charge = DeliveryCharges.nextDayCharge(weight, countBig);
		this.nextDay.setHTML(formatPrice(charge));
		if (nextDayButton.getValue()) {
			dCharge = charge;
			dMethod = DeliveryMethod.NEXT_DAY;
		}

	}

	void updateCart() {
		int count = 0;
		long subtotalLong = 0;
		int countBig = 0;
		if (this.basket.getWidgetCount() == 0) {
			paypalCheckout.setHTML("");
			Cookies.removeCookie("cart");
			setDelivery(0, 0, 0);
		} else {
			if (address == null) {
				paypalCheckout.setHTML("");
				setAddressLabel.setVisible(true);
			} else {
				paypalCheckout.setHTML(
						"<input type=\"image\" name=\"Pay Pal Checkout\" alt=\"Fast checkout through Google\" src=\"https://www.paypal.com/en_GB/GB/i/btn/btn_xpressCheckout.gif\">");
				setAddressLabel.setVisible(false);
			}
			StringBuilder sb = new StringBuilder();
			int weight = 0;
			for (Widget item : this.basket) {
				BasketItem bi = (BasketItem) item;
				int itemCount = bi.getCount();
				if (itemCount == 0) {
					this.basket.remove(bi);
				} else {
					ProductTypeTransferObject ptto = bi.getPtto();
					sb.append(ptto.getId()).append(" ").append(itemCount).append(" ");
					count += itemCount;
					subtotalLong += ptto.getPrice() * itemCount;
					weight += ptto.getWeight() * itemCount;
					if (ptto.getLarge()) {
						countBig += 1;
					}
				}
			}
			setDelivery(weight, subtotalLong, countBig);
			Date date = new Date();
			date.setTime(date.getTime() + 3600 * 168 * 1000);
			if (Cookies.getCookie("cookies") != null) {
				Cookies.setCookie("cart", sb.toString(), date);
			}
		}

		this.subtotal.setHTML(formatPrice(subtotalLong));
		BasketPanel.bus.fireEvent(new BasketEvent(count, this.subtotal));

		total.setHTML(formatPrice(subtotalLong + dCharge));
	}

	public void setPopup(String popup) {
		setProcessing(false);
		Cookies.removeCookie("processing");
		if (popup.startsWith("return")) {
			orderId = popup.substring(7);
			returnBox.center();
		} else if (popup.equals("reject")) {
			rejectBox.center();
		} else if (popup.equals("badPayment")) {
			badPaymentBox.center();
		} else if (popup.equals("cancel")) {
			cancelBox.center();
		}
	}
}
