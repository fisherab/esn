package com.eyespynature.admin;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.eyespynature.client.service.PaypalOrderServiceAsync;
import com.eyespynature.shared.Address;
import com.eyespynature.shared.PaypalOrderTransferObject;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class ManageOrdersPanel extends Composite {

	@UiField
	HTML status;

	public class SwitchableButtonCell extends AbstractCell<PaypalOrderTransferObject> {
		SwitchableButtonCell() {
			super("click");
		}

		@Override
		public void render(Context context, PaypalOrderTransferObject oto, SafeHtmlBuilder sb) {
			String value = oto.getState();
			if (value == null) {
				return;
			}
			if (value.equals("confirmed")) {
				sb.appendHtmlConstant("<button type=\"button\">").appendEscaped("Dispatch")
						.appendHtmlConstant("</button>");
			} else {
				sb.appendEscaped(value);
			}
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, PaypalOrderTransferObject oto,
				NativeEvent event, ValueUpdater<PaypalOrderTransferObject> valueUpdater) {
			String value = oto.getState();
			if (value == null) {
				return;
			}

			if (value.equals("confirmed")) {
				final String sn = oto.getId();
				boolean tracked = oto.getDelivery().isTracked();
				String trackingNumber = null;
				if (tracked) {
					trackingNumber = Window.prompt("Please enter tracking number", "");
					if (trackingNumber == null || trackingNumber.isEmpty()) {
						status.setHTML("You must provide a tracking number");
						return;
					}
				}
				orderService.dispatch(sn, trackingNumber, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						status.setHTML("Dispatch of " + sn + " was succesful");
					}
				});

			}

		}
	}

	private class CartCell extends AbstractCell<Map<String, Integer>> {

		@Override
		public void render(Context context, Map<String, Integer> cart, SafeHtmlBuilder sb) {
			if (cart == null) {
				return;
			}

			sb.appendHtmlConstant("<table><tr><td valign=\"top\">");

			for (Entry<String, Integer> e : cart.entrySet()) {
				sb.appendHtmlConstant("<div>").appendEscaped(e.getValue() + " " + e.getKey())
						.appendHtmlConstant("</div>");
			}

			sb.appendHtmlConstant("</td></tr></table>");

		}

	}

	private class AddressCell extends AbstractCell<Address> {

		@Override
		public void render(Context context, Address address, SafeHtmlBuilder sb) {
			if (address == null) {
				return;
			}

			sb.appendHtmlConstant("<table><tr><td valign=\"top\">");

			addLine(sb, address.getAddress1());
			addLine(sb, address.getAddress2());
			addLine(sb, address.getTown());
			addLine(sb, address.getCounty());
			addLine(sb, address.getPostcode());

			sb.appendHtmlConstant("</td></tr></table>");

		}

		private void addLine(SafeHtmlBuilder sb, String line) {
			if (line != null) {
				sb.appendHtmlConstant("<div>").appendEscaped(line).appendHtmlConstant("</div>");
			}
		}

	}

	final private static Logger logger = Logger.getLogger(ManageOrdersPanel.class.getName());

	final DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	interface MyUiBinder extends UiBinder<Widget, ManageOrdersPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final PaypalOrderServiceAsync orderService = PaypalOrderServiceAsync.Util.getInstance();

	final ListDataProvider<PaypalOrderTransferObject> dataProvider = new ListDataProvider<PaypalOrderTransferObject>();

	@UiField
	HeadingElement title;

	@UiField
	SimplePager pager;

	@UiField(provided = true)
	CellTable<PaypalOrderTransferObject> table;

	@UiHandler("refresh")
	void refreshClick(ClickEvent e) {
		refresh();
	}

	public ManageOrdersPanel() {

		logger.fine("Created MOP");

		final ProvidesKey<PaypalOrderTransferObject> keyProvider = new ProvidesKey<PaypalOrderTransferObject>() {
			@Override
			public Object getKey(PaypalOrderTransferObject oto) {
				return oto.getId();
			}
		};

		table = new CellTable<PaypalOrderTransferObject>(keyProvider);
		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);

		initWidget(uiBinder.createAndBindUi(this));

		title.setInnerText("Manage orders");

		// Key
		TextColumn<PaypalOrderTransferObject> keyColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return (oto.getId());
			}
		};
		table.addColumn(keyColumn, "ID");

		// Created
		TextColumn<PaypalOrderTransferObject> createdColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return (dateFormat.format(oto.getCreateTime()));
			}
		};
		table.addColumn(createdColumn, "Created");

		// Updated
		final DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		TextColumn<PaypalOrderTransferObject> updatedColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return (dateFormat.format(oto.getUpdateTime()));
			}
		};
		table.addColumn(updatedColumn, "Updated");

		// State
		Column<PaypalOrderTransferObject, PaypalOrderTransferObject> stateColumn = new Column<PaypalOrderTransferObject, PaypalOrderTransferObject>(
				new SwitchableButtonCell()) {

			@Override
			public PaypalOrderTransferObject getValue(PaypalOrderTransferObject oto) {
				return oto;
			}

		};
		table.addColumn(stateColumn, "Action");

		// Buyer
		Column<PaypalOrderTransferObject, Address> buyerColumn = new Column<PaypalOrderTransferObject, Address>(
				new AddressCell()) {

			@Override
			public Address getValue(PaypalOrderTransferObject oto) {
				return oto.getAddress();
			}

		};
		table.addColumn(buyerColumn, "Buyer");

		// Cart
		Column<PaypalOrderTransferObject, Map<String, Integer>> cartColumn = new Column<PaypalOrderTransferObject, Map<String, Integer>>(
				new CartCell()) {

			@Override
			public Map<String, Integer> getValue(PaypalOrderTransferObject oto) {
				return oto.getCart();
			}

		};
		table.addColumn(cartColumn, "Cart");

		// Delivery
		TextColumn<PaypalOrderTransferObject> deliveryColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return oto.getDelivery().name();
			}
		};
		table.addColumn(deliveryColumn, "Delivery");

		// first_name
		TextColumn<PaypalOrderTransferObject> first_nameColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return oto.getFirstName();
			}
		};
		table.addColumn(first_nameColumn, "First name");

		// last_name
		TextColumn<PaypalOrderTransferObject> last_nameColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return oto.getLastName();
			}
		};
		table.addColumn(last_nameColumn, "Last name");

		// email
		TextColumn<PaypalOrderTransferObject> emailColumn = new TextColumn<PaypalOrderTransferObject>() {
			@Override
			public String getValue(PaypalOrderTransferObject oto) {
				return oto.getEmail();
			}
		};
		table.addColumn(emailColumn, "email");

		pager.setDisplay(table);

		refresh();

	}

	private void refresh() {
		orderService.search(PaypalOrderTransferObject.GET_ACTIVE,
				new AsyncCallback<List<PaypalOrderTransferObject>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.toString());
					}

					@Override
					public void onSuccess(List<PaypalOrderTransferObject> result) {
						List<PaypalOrderTransferObject> l = dataProvider.getList();
						l.clear();
						l.addAll(result);
					}
				});

	}
}
