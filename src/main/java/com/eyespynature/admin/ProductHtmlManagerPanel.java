package com.eyespynature.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.eyespynature.client.service.SecureProductServiceAsync;
import com.eyespynature.shared.DescriptionTransferObject;
import com.eyespynature.shared.Order;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ProductHtmlManagerPanel extends Composite {

	final private static Logger logger = Logger.getLogger(ProductHtmlManagerPanel.class.getName());

	interface MyUiBinder extends UiBinder<Widget, ProductHtmlManagerPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	final ListDataProvider<ProductTypeTransferObject> dataProvider = new ListDataProvider<ProductTypeTransferObject>();

	@UiField
	Button apply_button;

	@UiField
	FlexTable node_list;

	@UiField
	HTML preview;

	@UiField
	TextArea html;

	@UiField
	HTML status;

	@UiHandler("create_button")
	void createButtonHandler(ClickEvent event) {
		int i = node_list.getRowCount();
		logger.fine("Prepare empty row " + i);
		node_list.setWidget(i, 0, new TextBox());
		node_list.setWidget(i, 1, new TextBox());
		node_list.setWidget(i, 2, new TextBox());
		node_list.setWidget(i, 3, new TextBox());
		ListBox lb = new ListBox();
		lb.addItem("POPULARITY");
		lb.addItem("RANDOM");
		node_list.setWidget(i, 4, lb);
		TextBox tb = new TextBox();
		tb.setWidth("6em");
		node_list.setWidget(i, 5, tb);
		node_list.setWidget(i, 6, new CheckBox());
	}

	@UiHandler("preview_button")
	void previewButtonHandler(ClickEvent event) {
		preview.setHTML(html.getText());
	}

	@UiHandler("discard_button")
	void discardButtonHandler(ClickEvent event) {
		refresh();
	}

	@UiHandler("apply_button")
	void applyButtonHandler(ClickEvent event) {
		status.setHTML("");
		final List<Long> deletes = new ArrayList<Long>();
		final List<DescriptionTransferObject> dtos = new ArrayList<DescriptionTransferObject>();
		logger.fine("Update " + updated);
		if (currentRow != 0) {
			htmlMap.put(currentRow, html.getText());
		}
		for (int i : updated) {
			Long id = idMap.get(i);
			Boolean deleted = ((CheckBox) node_list.getWidget(i, 6)).getValue();
			if (deleted) {
				if (id != null) {
					deletes.add(id);
				}
			} else {
				String text = htmlMap.get(i);
				List<String> menu = new ArrayList<String>();
				String val = ((TextBox) node_list.getWidget(i, 0)).getText().trim();
				if (!val.isEmpty()) {
					menu.add(val);
					val = ((TextBox) node_list.getWidget(i, 1)).getText().trim();
					if (!val.isEmpty()) {
						menu.add(val);
						val = ((TextBox) node_list.getWidget(i, 2)).getText().trim();
						if (!val.isEmpty()) {
							menu.add(val);
							val = ((TextBox) node_list.getWidget(i, 3)).getText().trim();
							if (!val.isEmpty()) {
								menu.add(val);
							}
						}
					}
				}
				ListBox lb = (ListBox) node_list.getWidget(i, 4);
				Order order = Order.valueOf(lb.getItemText(lb.getSelectedIndex()));
				Integer max = null;
				String maxString = ((TextBox) node_list.getWidget(i, 5)).getText().trim();
				if (!maxString.isEmpty()) {
					try {
						max = Integer.parseInt(maxString);
						if (max < 0) {
							Window.alert("Max must be empty or a non-negative integer");
							return;
						}
					} catch (NumberFormatException e) {
						Window.alert("Max must be empty or a non-negative integer");
						return;
					}
				}
				dtos.add(new DescriptionTransferObject(id, text, menu, max, order));
			}
		}
		secureProductService.putDescriptions(dtos, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				secureProductService.deleteDescriptions(deletes, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Caught " + caught);

					}

					@Override
					public void onSuccess(Void result) {
						status.setHTML(dtos.size() + " added or updated and " + deletes.size()
								+ " deleted");
						currentRow = 0;
						refresh();

					}
				});

			}
		});
	}

	private Set<Integer> updated = new HashSet<Integer>();

	private void refresh() {
		logger.fine("About to refresh");

		secureProductService.getDescriptions(new AsyncCallback<List<DescriptionTransferObject>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Caught " + caught);
			}

			@Override
			public void onSuccess(List<DescriptionTransferObject> dtos) {
				node_list.removeAllRows();

				int i = 0;
				node_list.setWidget(i, 0, new HTML("<b>0</b>"));
				node_list.setWidget(i, 1, new HTML("<b>1</b>"));
				node_list.setWidget(i, 2, new HTML("<b>2</b>"));
				node_list.setWidget(i, 3, new HTML("<b>3</b>"));
				node_list.setWidget(i, 4, new HTML("<b>Order</b>"));
				node_list.setWidget(i, 5, new HTML("<b>Max</b>"));
				node_list.setWidget(i, 6, new HTML("<b>Delete</b>"));

				for (DescriptionTransferObject dto : dtos) {
					i++;
					idMap.put(i, dto.getId());
					htmlMap.put(i, dto.getHtml());
					List<String> menu = dto.getMenu();

					TextBox tb = new TextBox();
					if (menu.size() > 0) {
						tb.setText(menu.get(0));
					}
					node_list.setWidget(i, 0, tb);

					tb = new TextBox();
					if (menu.size() > 1) {
						tb.setText(menu.get(1));
					}
					node_list.setWidget(i, 1, tb);

					tb = new TextBox();
					if (menu.size() > 2) {
						tb.setText(menu.get(2));
					}
					node_list.setWidget(i, 2, tb);

					tb = new TextBox();
					if (menu.size() > 3) {
						tb.setText(menu.get(3));
					}
					node_list.setWidget(i, 3, tb);

					ListBox lb = new ListBox();
					String value = dto.getOrder().name();
					lb.addItem("POPULARITY");
					lb.addItem("RANDOM");
					for (int j = 0; j < lb.getItemCount(); j++) {
						if (lb.getItemText(j).equalsIgnoreCase(value)) {
							lb.setSelectedIndex(j);
						}
					}
					node_list.setWidget(i, 4, lb);

					tb = new TextBox();
					tb.setWidth("6em");
					Integer max = dto.getMax();
					if (max != null) {
						tb.setText(Integer.toString(dto.getMax()));
					}
					node_list.setWidget(i, 5, tb);

					node_list.setWidget(i, 6, new CheckBox());

					if (currentRow != 0) {
						node_list.setWidget(currentRow, 7, sel);
						String text = htmlMap.get(currentRow);
						html.setText(text);
						preview.setHTML(text);
						updated.clear();
						updated.add(currentRow);
					}
					apply_button.setEnabled(false);
				}
			}

		});

	}

	Map<Integer, String> htmlMap = new HashMap<Integer, String>();
	Map<Integer, Long> idMap = new HashMap<Integer, Long>();
	int currentRow;

	final HTML sel = new HTML("<b>Selected</b>");

	public ProductHtmlManagerPanel() {

		initWidget(uiBinder.createAndBindUi(this));

		apply_button.setEnabled(false);

		ClickHandler userRowCheck = new ClickHandler() {
			public void onClick(ClickEvent event) {
				Cell src = node_list.getCellForEvent(event);
				if (src != null) {
					if (currentRow != 0) {
						htmlMap.put(currentRow, html.getText());
						node_list.clearCell(currentRow, 7);
					}
					currentRow = src.getRowIndex();
					node_list.setWidget(currentRow, 7, sel);
					String text = htmlMap.get(currentRow);
					html.setText(text);
					preview.setHTML(text);
					apply_button.setEnabled(true);
					updated.add(currentRow);
				}
			}
		};
		node_list.addClickHandler(userRowCheck);

		refresh();
	}
};
