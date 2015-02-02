package com.eyespynature.admin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eyespynature.client.HistoryTokenType;
import com.eyespynature.client.service.SecureProductServiceAsync;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class UpdateProductTypePanel extends Composite {

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	interface MyUiBinder extends UiBinder<Widget, UpdateProductTypePanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlexTable ft;

	@UiField
	ApplyDiscardPanel adp;

	@UiField
	FileSelectionPanel fsp;

	ChangeHandler somethingChanged = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			adp.enable();
		}
	};

	public UpdateProductTypePanel(long id) {
		initWidget(uiBinder.createAndBindUi(this));
		adp.disable();

		UpdateProductTypePanel.this.secureProductService.getProductType(id,
				new AsyncCallback<ProductTypeTransferObject>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed: " + caught);
					}

					@Override
					public void onSuccess(final ProductTypeTransferObject ptto) {

						int n = 0;

						ft.setText(n, 0, "Name");
						ft.setWidget(n, 1, new Label());
						((Label) ft.getWidget(n, 1)).setWidth("40em");
						((Label) ft.getWidget(n, 1))
								.setTitle("Name - cannot be changed");
						((Label) ft.getWidget(n, 1)).setText(ptto.getName());
						n++;

						ft.setText(n, 0, "Short desc");
						ft.setWidget(n, 1, new TextBox());
						((TextBox) ft.getWidget(n, 1)).setWidth("40em");
						((TextBox) ft.getWidget(n, 1)).setText(ptto.getShortD());
						((TextBox) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int shortRow = n++;

						ft.setText(n, 0, "Long desc");
						ft.setWidget(n, 1, new TextArea());
						((TextArea) ft.getWidget(n, 1)).setSize("40em", "20ex");
						((TextArea) ft.getWidget(n, 1)).setText(ptto.getLongD());
						((TextArea) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int longRow = n++;

						ft.setText(n, 0, "Notes");
						ft.setWidget(n, 1, new TextArea());
						((TextArea) ft.getWidget(n, 1)).setSize("40em", "20ex");
						((TextArea) ft.getWidget(n, 1)).setTitle("Optional");
						((TextArea) ft.getWidget(n, 1)).setText(ptto.getNotes());
						((TextArea) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int notesRow = n++;

						ft.setText(n, 0, "Spec");
						ft.setWidget(n, 1, new TextArea());
						((TextArea) ft.getWidget(n, 1)).setSize("40em", "20ex");
						((TextArea) ft.getWidget(n, 1)).setText(ptto.getSpec());
						((TextArea) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int specRow = n++;

						ft.setText(n, 0, "Menu levels");
						ft.setWidget(n, 1, new TextArea());
						((TextArea) ft.getWidget(n, 1)).setSize("40em", "10ex");
						((TextArea) ft.getWidget(n, 1))
								.setTitle("Comma separated list");
						List<String> texts = ptto.getMenu();
						String text = "";
						for (String t : texts) {
							if (!text.isEmpty()) {
								text = text + ",";
							}
							text = text + t;
						}
						((TextArea) ft.getWidget(n, 1)).setText(text);
						((TextArea) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int menuRow = n++;

						ft.setText(n, 0, "Tags");
						ft.setWidget(n, 1, new TextArea());
						((TextArea) ft.getWidget(n, 1)).setSize("40em", "10ex");
						((TextArea) ft.getWidget(n, 1))
								.setTitle("Optional space or comma separated list");
						((TextArea) ft.getWidget(n, 1)).setText(ptto.getTags());
						((TextArea) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int tagsRow = n++;

						ft.setText(n, 0, "Weight");
						ft.setWidget(n, 1, new TextBox());
						((TextBox) ft.getWidget(n, 1))
								.setTitle("Weight as an integral number of grams");
						((TextBox) ft.getWidget(n, 1)).setText(Integer
								.toString(ptto.getWeight()));
						((TextBox) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int weightRow = n++;

						ft.setText(n, 0, "Large");
						ft.setWidget(n, 1, new CheckBox());
						((CheckBox) ft.getWidget(n, 1))
								.setTitle("If object is two large to send two together");
						((CheckBox) ft.getWidget(n, 1)).setValue(ptto
								.getLarge());
						((CheckBox) ft.getWidget(n, 1))
								.addClickHandler(new ClickHandler() {

									@Override
									public void onClick(ClickEvent event) {
										adp.enable();
									}
								});
						final int largeRow = n++;

						ft.setText(n, 0, "Price");
						ft.setWidget(n, 1, new TextBox());
						NumberFormat numFormat = NumberFormat.getFormat("0.00");
						((TextBox) ft.getWidget(n, 1)).setText(numFormat
								.format(ptto.getPrice().doubleValue() / 100.));
						((TextBox) ft.getWidget(n, 1))
								.addChangeHandler(somethingChanged);
						final int priceRow = n++;

						fsp.setNames(ptto.getImages());
						fsp.setChanges(adp.getApply());

						adp.getDiscard().addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								History.newItem(HistoryTokenType.UPDATE_PRODUCT
										.name());
							}
						});

						adp.getApply().addClickHandler(new ClickHandler() {

							boolean err;

							private void check(int n, boolean condition,
									String message) {
								if (!condition) {
									ft.setWidget(n, 3, new HTML(
											"<font color=red>" + message
													+ "</font>"));
									this.err = true;
								} else {
									if (ft.getCellCount(n) > 3) {
										ft.clearCell(n, 3);
									}
								}
							}

							@Override
							public void onClick(ClickEvent event) {
								this.err = false;

								String ts = ((TextBoxBase) ft.getWidget(
										shortRow, 1)).getValue().trim();
								this.check(shortRow, ts.length() > 0,
										"Please enter a short description");
								ptto.setShortD(ts);

								ts = ((TextBoxBase) ft.getWidget(longRow, 1))
										.getValue().trim();
								this.check(longRow, ts.length() > 0,
										"Please enter a longer description");
								ptto.setLongD(ts);

								ts = ((TextBoxBase) ft.getWidget(notesRow, 1))
										.getValue().trim();
								ptto.setNotes(ts);

								ts = ((TextBoxBase) ft.getWidget(specRow, 1))
										.getValue().trim();
								this.check(specRow, ts.length() > 0,
										"Please enter a specification");
								ptto.setSpec(ts);

								ts = ((TextBoxBase) ft.getWidget(menuRow, 1))
										.getValue().trim();
								this.check(menuRow, ts.length() > 0,
										"Please enter a comma separated list - normally lower case");
								ptto.getMenu().clear();
								ptto.getMenu().addAll(
										Arrays.asList(ts.split("\\s*,\\s*")));

								ts = ((TextBoxBase) ft.getWidget(tagsRow, 1))
										.getValue().trim();
								ptto.setTags(ts);

								ts = ((TextBoxBase) ft.getWidget(weightRow, 1))
										.getValue().trim();
								this.check(weightRow,
										ts.matches("^[1-9][0-9]*$"),
										"Please enter the weight in grams");
								ptto.setWeight(Integer.parseInt(ts));

								boolean large = ((CheckBox) ft.getWidget(
										largeRow, 1)).getValue();
								ptto.setLarge(large);

								ts = ((TextBoxBase) ft.getWidget(priceRow, 1))
										.getValue().trim();
								this.check(
										priceRow,
										ts.matches("^[0-9]*\\.[0-9][0-9]$"),
										"Please enter the price in pounds and pence e.g. 3.12 for three pounds and twelve pence");
								ptto.setPrice(Long.parseLong(ts
										.replace(".", "")));
								ptto.getImages().clear();
								ptto.getImages().addAll(fsp.getNames());

								if (!this.err) {

									Set<ProductTypeTransferObject> modified = new HashSet<>();
									modified.add(ptto);

									UpdateProductTypePanel.this.secureProductService
											.update(modified,
													new AsyncCallback<Void>() {

														@Override
														public void onFailure(
																Throwable caught) {
															Window.alert("Failed: "
																	+ caught);
														}

														@Override
														public void onSuccess(
																Void result) {
															History.newItem(HistoryTokenType.UPDATE_PRODUCT
																	.name());
														}
													});
								}
							}
						});

					}
				});

	}

}
