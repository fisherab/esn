package com.eyespynature.admin;

import java.util.Arrays;

import com.eyespynature.client.HistoryTokenType;
import com.eyespynature.client.service.SecureProductServiceAsync;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class CreateProductTypePanel extends Composite {

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	interface MyUiBinder extends UiBinder<Widget, CreateProductTypePanel> {
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

	private int addTextBox(String caption, String width, String title) {
		int n = ft.getRowCount();
		ft.setText(n, 0, caption);
		TextBox t = new TextBox();
		ft.setWidget(n, 1, t);
		t.setWidth(width);
		t.setTitle(title);
		t.addChangeHandler(somethingChanged);
		return n;
	}

	private int addTextArea(String caption, String width, String height,
			String title) {
		int n = ft.getRowCount();
		ft.setText(n, 0, caption);
		TextArea t = new TextArea();
		ft.setWidget(n, 1, t);
		t.setSize(width, height);
		t.setTitle(title);
		t.addChangeHandler(somethingChanged);
		return n;
	}

	public CreateProductTypePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		adp.disable();
		fsp.setChanges(adp.getApply());

		final int nameRow = addTextBox("Name", "40em",
				"Name - cannot be changed");
		final int shortRow = addTextBox("Short desc", "40em", null);
		final int longRow = addTextArea("Long desc", "40em", "20ex", null);
		final int notesRow = addTextArea("Notes", "40em", "20ex", "Optional");
		final int specRow = addTextArea("Spec", "40em", "20ex", "Optional");
		final int menuRow = addTextArea("Menu levels", "40em", "10ex",
				"Comma separated list");
		final int tagsRow = addTextArea("Tags", "40em", "10ex",
				"Comma separated list");
		final int weightRow = addTextBox("Weight", "10em",
				"Weight as an integral number of grams");

		{
			int n = ft.getRowCount();
			ft.setText(n, 0, "Large");
			CheckBox t = new CheckBox();
			ft.setWidget(n, 1, t);
			t.setTitle("If object is too large to send two together");
			t.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					adp.enable();
				}
			});
		}
		final int largeRow = ft.getRowCount() - 1;

		final int priceRow = addTextBox("Price", "10em", "Price in pounds");

		adp.getDiscard().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				History.newItem(HistoryTokenType.UPDATE_PRODUCT.name());
			}
		});

		adp.getApply().addClickHandler(new ClickHandler() {

			boolean err;

			private void check(int n, boolean condition, String message) {
				if (!condition) {
					ft.setWidget(n, 3, new HTML("<font color=red>" + message
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
				ProductTypeTransferObject ptto = new ProductTypeTransferObject();

				String ts = ((TextBoxBase) ft.getWidget(nameRow, 1)).getValue()
						.trim();
				this.check(nameRow, ts.length() > 0,
						"Please enter the name of the object - ideally unique");
				ptto.setName(ts);

				ts = ((TextBoxBase) ft.getWidget(shortRow, 1)).getValue()
						.trim();
				this.check(shortRow, ts.length() > 0,
						"Please enter a short description");
				ptto.setShortD(ts);

				ts = ((TextBoxBase) ft.getWidget(longRow, 1)).getValue().trim();
				this.check(longRow, ts.length() > 0,
						"Please enter a longer description");
				ptto.setLongD(ts);

				ts = ((TextBoxBase) ft.getWidget(notesRow, 1)).getValue()
						.trim();
				ptto.setNotes(ts);

				ts = ((TextBoxBase) ft.getWidget(specRow, 1)).getValue().trim();
				this.check(specRow, ts.length() > 0,
						"Please enter a specification");
				ptto.setSpec(ts);

				ts = ((TextBoxBase) ft.getWidget(menuRow, 1)).getValue().trim();
				this.check(menuRow, ts.length() > 0,
						"Please enter a comma separated list - normally lower case");
				ptto.getMenu().addAll(Arrays.asList(ts.split("\\s*,\\s*")));

				ts = ((TextBoxBase) ft.getWidget(tagsRow, 1)).getValue().trim();
				ptto.setTags(ts);

				ts = ((TextBoxBase) ft.getWidget(weightRow, 1)).getValue()
						.trim();
				this.check(weightRow, ts.matches("^[1-9][0-9]*$"),
						"Please enter the weight in grams");
				ptto.setWeight(Integer.parseInt(ts));

				boolean large = ((CheckBox) ft.getWidget(largeRow, 1))
						.getValue();
				ptto.setLarge(large);

				ts = ((TextBoxBase) ft.getWidget(priceRow, 1)).getValue()
						.trim();
				this.check(
						priceRow,
						ts.matches("^[0-9]*\\.[0-9][0-9]$"),
						"Please enter the price in pounds and pence e.g. 3.12 for three pounds and twelve pence");
				ptto.setPrice(Long.parseLong(ts.replace(".", "")));

				ptto.getImages().addAll(fsp.getNames());

				if (!this.err) {

					CreateProductTypePanel.this.secureProductService
							.createProductType(ptto, new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed: " + caught);
								}

								@Override
								public void onSuccess(Void result) {
									History.newItem(HistoryTokenType.UPDATE_PRODUCT
											.name());

								}
							});
				}
			}
		});

	}

}
