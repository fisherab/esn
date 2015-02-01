package com.eyespynature.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.eyespynature.client.HistoryTokenType;
import com.eyespynature.client.PopupEditText;
import com.eyespynature.client.TruncatedClickableTextCell;
import com.eyespynature.client.service.SecureProductServiceAsync;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class UpdateProductTypesPanel extends Composite {

	private final static String MT = "---";

	public class PopupFileSelectionPanel extends PopupPanel {

		private FileSelectionPanel fsp;
		private Button pApply;

		public PopupFileSelectionPanel() {

			setGlassEnabled(true);
			VerticalPanel pvp = new VerticalPanel();
			add(pvp);

			fsp = new FileSelectionPanel();
			fsp.setMode("read");

			ScrollPanel scp = new ScrollPanel(fsp);
			scp.setHeight(Integer.toString(Window.getClientHeight() * 4 / 5)
					+ "px");
			scp.setWidth(Integer.toString(Window.getClientWidth() * 4 / 5)
					+ "px");
			pvp.add(scp);

			HorizontalPanel pbs = new HorizontalPanel();
			pbs.setSpacing(10);
			pvp.add(pbs);

			pApply = new Button("Apply");

			pbs.add(pApply);

			Button pDiscard = new Button("Discard");
			pDiscard.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});
			pbs.add(pDiscard);

		}

		public Button getApply() {
			return pApply;
		}

		public List<String> getNames() {
			return fsp.getNames();
		}

		public void setNames(List<String> names) {
			fsp.setNames(names);

		}

	}

	final private static Logger logger = Logger
			.getLogger(UpdateProductTypesPanel.class.getName());

	interface MyUiBinder extends UiBinder<Widget, UpdateProductTypesPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private class CurrencyCell extends AbstractCell<Long> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				Long value, SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			sb.appendHtmlConstant("&pound;" + value / 100 + "." + value % 100);
		}
	}

	private final SecureProductServiceAsync secureProductService = SecureProductServiceAsync.Util
			.getInstance();

	private String query;
	private PopupEditText shortPopup;
	private ProductTypeTransferObject currentRow;
	private PopupEditText longPopup;
	private PopupEditText notesPopup;
	private PopupEditText specPopup;
	private PopupEditText menuPopup;
	private PopupEditText tagsPopup;
	private Set<ProductTypeTransferObject> modified = new HashSet<ProductTypeTransferObject>();
	private final Map<Long, Integer> toAdd = new HashMap<Long, Integer>();

	final ListDataProvider<ProductTypeTransferObject> dataProvider = new ListDataProvider<ProductTypeTransferObject>();

	@UiField
	ApplyDiscardPanel adp;

	@UiField
	HeadingElement title;

	@UiField
	SimplePager pager;

	@UiField(provided = true)
	CellTable<ProductTypeTransferObject> table;

	@UiHandler("reindex")
	public void reindex(ClickEvent event) {
		secureProductService.reindex(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("Reindexing triggered");
			}
		});
	}

	private PopupFileSelectionPanel fspPopup;

	public UpdateProductTypesPanel(final HistoryTokenType type) {

		final ProvidesKey<ProductTypeTransferObject> keyProvider = new ProvidesKey<ProductTypeTransferObject>() {
			@Override
			public Object getKey(ProductTypeTransferObject ptto) {
				return ptto.getId();
			}
		};

		table = new CellTable<ProductTypeTransferObject>(keyProvider);
		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);
		initWidget(uiBinder.createAndBindUi(this));

		Set<String> columns = new HashSet<String>();

		adp.disable();

		ClickHandler applyHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				logger.fine("Updating " + modified.size() + " products");

				for (ProductTypeTransferObject product : modified) {
					if (product.getNotes().equals(MT)) {
						product.setNotes("");
					}
				}
				secureProductService.update(modified,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());

							}

							@Override
							public void onSuccess(Void result) {
								refresh();
							}
						});
			}
		};

		if (type == HistoryTokenType.MANAGE_STOCK) {
			title.setInnerText("Manage stock");
			columns.addAll(Arrays.asList("Key", "Name", "Price",
					"NumberInStock", "Add", "NumberReserved"));
			query = "NotHidden";
			adp.getApply().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					logger.fine("Updating stock for " + toAdd.size()
							+ " products");
					secureProductService.make(toAdd, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							toAdd.clear();
							refresh();
						}
					});
				}
			});
		} else if (type == HistoryTokenType.OBSOLETE_PRODUCT) {
			title.setInnerText("Mark product as obsolete");
			columns.addAll(Arrays.asList("Key", "Name", "NumberInStock",
					"NumberReserved", "Hidden"));
			query = "NotHidden";
			adp.getApply().addClickHandler(applyHandler);
		} else if (type == HistoryTokenType.RESTORE_PRODUCT) {
			title.setInnerText("Restore product");
			columns.addAll(Arrays.asList("Key", "Name", "NumberInStock",
					"NumberReserved", "Hidden"));
			query = "Hidden";
			adp.getApply().addClickHandler(applyHandler);
		} else if (type == HistoryTokenType.UPDATE_PRODUCT) {
			title.setInnerText("Update product");
			columns.addAll(Arrays.asList("Name", "ModShort", "ModLong",
					"ModNotes", "ModSpec", "ModMenu", "ModTags", "ModWeight",
					"ModLarge", "ModPrice", "Images"));
			query = "NotHidden";
			adp.getApply().addClickHandler(applyHandler);
		}

		adp.getDiscard().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}

		});

		// Key
		if (columns.contains("Key")) {
			TextColumn<ProductTypeTransferObject> keyColumn = new TextColumn<ProductTypeTransferObject>() {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return Long.toString(ptto.getId());
				}
			};
			table.addColumn(keyColumn, "Key");
		}

		// Name
		if (columns.contains("Name")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new ClickableTextCell()) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return ptto.getName();
				}
			};
			table.addColumn(column, "Name");
			column.setSortable(true);
			ListHandler<ProductTypeTransferObject> nameSortHandler = new ListHandler<ProductTypeTransferObject>(
					dataProvider.getList());
			nameSortHandler.setComparator(column,
					new Comparator<ProductTypeTransferObject>() {
						public int compare(ProductTypeTransferObject o1,
								ProductTypeTransferObject o2) {
							if (o1 == o2) {
								return 0;
							}

							// Compare the name columns.
							if (o1 != null) {
								return (o2 != null) ? o1.getName().compareTo(
										o2.getName()) : 1;
							}
							return -1;
						}
					});
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					if (modified.isEmpty()) {
						History.newItem(HistoryTokenType.UPDATE_ONE_PRODUCT
								.name() + ":" + object.getId());
					} else {
						Window.alert("You have unsaved changes");
					}

				}
			});
			table.addColumnSortHandler(nameSortHandler);
		}

		// ModShort
		if (columns.contains("ModShort")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new ClickableTextCell()) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return ptto.getShortD();
				}
			};
			table.addColumn(column, "Short desc");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					shortPopup.setValue(value);
					currentRow = object;
					shortPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									shortPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModLong
		if (columns.contains("ModLong")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new TruncatedClickableTextCell(180)) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return ptto.getLongD();
				}
			};
			table.addColumn(column, "Long desc");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					longPopup.setValue(value);
					currentRow = object;
					longPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									longPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModNotes
		if (columns.contains("ModNotes")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new TruncatedClickableTextCell(180)) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					String notes = ptto.getNotes();
					return notes == null || notes.trim().isEmpty() ? MT : notes;

				}
			};
			table.addColumn(column, "Notes");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					notesPopup.setValue(value.equals(MT) ? null : value);
					currentRow = object;
					notesPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									notesPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModSpec
		if (columns.contains("ModSpec")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new TruncatedClickableTextCell(180)) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return ptto.getSpec();
				}
			};
			table.addColumn(column, "Spec");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					specPopup.setValue(value);
					currentRow = object;
					specPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									specPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModMenu
		if (columns.contains("ModMenu")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new ClickableTextCell()) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					StringBuilder sb = new StringBuilder();
					boolean first = true;
					for (String word : ptto.getMenu()) {
						if (first) {
							first = false;
						} else {
							sb.append(", ");
						}
						sb.append(word);
					}
					return sb.toString();
				}
			};
			table.addColumn(column, "Menu");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject ptto,
						String value) {
					menuPopup.setValue(value);
					currentRow = ptto;
					menuPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									menuPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModTags
		if (columns.contains("ModTags")) {
			Column<ProductTypeTransferObject, String> column = new Column<ProductTypeTransferObject, String>(
					new TruncatedClickableTextCell(180)) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					String tags = ptto.getTags();
					return tags == null || tags.trim().isEmpty() ? MT : tags;

				}
			};
			table.addColumn(column, "Tags");
			column.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject object,
						String value) {
					tagsPopup.setValue(value.equals(MT) ? null : value);
					currentRow = object;
					tagsPopup
							.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
								public void setPosition(int offsetWidth,
										int offsetHeight) {
									int left = (Window.getClientWidth() - offsetWidth) / 2;
									int top = (Window.getClientHeight() - offsetHeight) / 3;
									tagsPopup.setPopupPosition(left, top);
								}
							});
				}
			});
		}

		// ModWeight
		if (columns.contains("ModWeight")) {
			final EditTextCell cell = new EditTextCell();
			Column<ProductTypeTransferObject, String> weightColumn = new Column<ProductTypeTransferObject, String>(
					cell) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return Integer.toString(ptto.getWeight());
				}

			};
			table.addColumn(weightColumn, "Weight");
			weightColumn
					.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

						@Override
						public void update(int index,
								ProductTypeTransferObject ptto, String value) {
							try {
								if (!value.matches("^[1-9][0-9]*$")) {
									throw new NumberFormatException();
								}
								ptto.setWeight(Integer.parseInt(value));
								modified.add(ptto);
								adp.enable();
							} catch (NumberFormatException e) {
								cell.clearViewData(keyProvider.getKey(ptto));
							}
							table.redraw();
						}
					});
		}

		// ModLarge
		if (columns.contains("ModLarge")) {
			Column<ProductTypeTransferObject, Boolean> largeColumn = new Column<ProductTypeTransferObject, Boolean>(
					new CheckboxCell()) {

				@Override
				public Boolean getValue(ProductTypeTransferObject ptto) {
					return ptto.getLarge();
				}

			};
			table.addColumn(largeColumn, "Large");
			largeColumn
					.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, Boolean>() {

						@Override
						public void update(int index,
								ProductTypeTransferObject ptto, Boolean value) {
							ptto.setLarge(value);
							modified.add(ptto);
							adp.enable();
							table.redraw();
						}
					});
		}

		// Price
		if (columns.contains("Price")) {
			Column<ProductTypeTransferObject, Long> priceColumn = new Column<ProductTypeTransferObject, Long>(
					new CurrencyCell()) {
				@Override
				public Long getValue(ProductTypeTransferObject ptto) {
					return ptto.getPrice();
				}

			};
			table.addColumn(priceColumn, "Price");
			priceColumn.setSortable(true);
			ListHandler<ProductTypeTransferObject> priceSortHandler = new ListHandler<ProductTypeTransferObject>(
					dataProvider.getList());
			priceSortHandler.setComparator(priceColumn,
					new Comparator<ProductTypeTransferObject>() {
						public int compare(ProductTypeTransferObject o1,
								ProductTypeTransferObject o2) {
							if (o1 == o2) {
								return 0;
							}

							// Compare the price columns.
							if (o1 != null) {
								return (o2 != null) ? o1.getPrice().compareTo(
										o2.getPrice()) : 1;
							}
							return -1;
						}
					});
			table.addColumnSortHandler(priceSortHandler);
		}

		// ModPrice
		if (columns.contains("ModPrice")) {
			final EditTextCell cell = new EditTextCell();
			Column<ProductTypeTransferObject, String> priceColumn = new Column<ProductTypeTransferObject, String>(
					cell) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					Long value = ptto.getPrice();
					return value / 100 + "." + value % 100;
				}

			};
			table.addColumn(priceColumn, "Price");
			priceColumn
					.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

						@Override
						public void update(int index,
								ProductTypeTransferObject ptto, String value) {
							try {
								if (!value.matches("^[0-9]*\\.[0-9][0-9]$")) {
									throw new NumberFormatException();
								}
								ptto.setPrice(Long.parseLong(value.replace(".",
										"")));
								modified.add(ptto);
								adp.enable();
							} catch (NumberFormatException e) {
								cell.clearViewData(keyProvider.getKey(ptto));
							}
							table.redraw();
						}
					});

		}

		// NumberInStock
		if (columns.contains("NumberInStock")) {
			TextColumn<ProductTypeTransferObject> numberInStockColumn = new TextColumn<ProductTypeTransferObject>() {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return Integer.toString(ptto.getNumberInStock());
				}
			};
			table.addColumn(numberInStockColumn, "# in stock");
		}

		// NumberInStock
		if (columns.contains("Add")) {
			final TextInputCell cell = new TextInputCell();
			Column<ProductTypeTransferObject, String> addColumn = new Column<ProductTypeTransferObject, String>(
					cell) {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					Long key = ptto.getId();
					if (toAdd.containsKey(key)) {
						return Integer.toString(toAdd.get(key));
					} else {
						return "";
					}
				}
			};
			table.addColumn(addColumn, "# to make");
			addColumn
					.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

						@Override
						public void update(int index,
								ProductTypeTransferObject ptto, String value) {
							try {
								toAdd.put(ptto.getId(), Integer.parseInt(value));
								adp.enable();
							} catch (NumberFormatException e) {
								cell.clearViewData(keyProvider.getKey(ptto));
							}
							table.redraw();
						}
					});
		}

		// NumberReserved
		if (columns.contains("NumberReserved")) {
			TextColumn<ProductTypeTransferObject> numberReservedColumn = new TextColumn<ProductTypeTransferObject>() {
				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					return Integer.toString(ptto.getNumberReserved());
				}
			};
			table.addColumn(numberReservedColumn, "# reserved");
		}

		// Hidden
		if (columns.contains("Hidden")) {
			Column<ProductTypeTransferObject, Boolean> hiddenColumn = new Column<ProductTypeTransferObject, Boolean>(
					new CheckboxCell()) {
				@Override
				public Boolean getValue(ProductTypeTransferObject ptto) {
					return ptto.isHidden();
				}
			};
			hiddenColumn
					.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, Boolean>() {

						@Override
						public void update(int index,
								ProductTypeTransferObject ptto, Boolean value) {
							ptto.setHidden(value);
							modified.add(ptto);
							adp.enable();
						}

					});
			table.addColumn(hiddenColumn, "Hidden");
		}

		// Images
		if (columns.contains("Images")) {
			List<HasCell<ProductTypeTransferObject, ?>> compositeList = new ArrayList<HasCell<ProductTypeTransferObject, ?>>();

			Column<ProductTypeTransferObject, String> feeda = new Column<ProductTypeTransferObject, String>(
					new ClickableTextCell()) {

				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					List<String> names = ptto.getImages();
					return "" + names.size() + " ";
				}
			};

			feeda.setFieldUpdater(new FieldUpdater<ProductTypeTransferObject, String>() {

				@Override
				public void update(int index, ProductTypeTransferObject ptto,
						String value) {
					fspPopup.setNames(ptto.getImages());
					currentRow = ptto;
					fspPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
						public void setPosition(int offsetWidth,
								int offsetHeight) {
							int left = (Window.getClientWidth() - offsetWidth) / 2;
							int top = (Window.getClientHeight() - offsetHeight) / 3;
							fspPopup.setPopupPosition(left, top);
						}
					});

				}
			});

			compositeList.add(feeda);

			compositeList.add(new Column<ProductTypeTransferObject, String>(
					new ImageLoadingCell()) {

				@Override
				public String getValue(ProductTypeTransferObject ptto) {
					List<String> names = ptto.getImages();
					if (names.size() > 0) {
						return "download?name=" + names.get(0)
								+ "&width=100&height=100";
					} else {
						return null;
					}
				}
			});

			Column<ProductTypeTransferObject, ProductTypeTransferObject> imagesColumn = new Column<ProductTypeTransferObject, ProductTypeTransferObject>(
					new CompositeCell<ProductTypeTransferObject>(compositeList) {
					}) {

				@Override
				public ProductTypeTransferObject getValue(
						ProductTypeTransferObject ptto) {
					return ptto;
				}

			};

			table.addColumn(imagesColumn, "Images");
		}

		pager.setDisplay(table);
		pager.setPageSize(100);

		this.shortPopup = new PopupEditText();
		this.shortPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = shortPopup.getValue().trim();
				if (value.isEmpty()) {
					Window.alert("Short description must contain something");
				} else {
					currentRow.setShortD(value);
					dataProvider.refresh();
					modified.add(currentRow);
					adp.enable();
				}
				shortPopup.hide();
			}
		});

		this.longPopup = new PopupEditText();
		this.longPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = longPopup.getValue().trim();
				if (value.isEmpty()) {
					Window.alert("Long description must contain something");
				} else {
					currentRow.setLongD(value);
					dataProvider.refresh();
					modified.add(currentRow);
					adp.enable();
				}
				longPopup.hide();
			}
		});

		this.notesPopup = new PopupEditText();
		this.notesPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = notesPopup.getValue().trim();
				currentRow.setNotes(value.isEmpty() ? MT : value);
				dataProvider.refresh();
				modified.add(currentRow);
				adp.enable();
				notesPopup.hide();
			}
		});

		this.specPopup = new PopupEditText();
		this.specPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = specPopup.getValue().trim();
				if (value.isEmpty()) {
					Window.alert("Spec must contain something");
				} else {
					currentRow.setSpec(value);
					dataProvider.refresh();
					modified.add(currentRow);
					adp.enable();
				}
				specPopup.hide();
			}
		});

		this.menuPopup = new PopupEditText();
		this.menuPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = menuPopup.getValue().trim();
				if (value.isEmpty()) {
					Window.alert("Menu must contain something");
				} else {
					currentRow.getMenu().clear();
					currentRow.getMenu().addAll(
							Arrays.asList(value.split("\\s*,\\s*")));
					dataProvider.refresh();
					modified.add(currentRow);
					adp.enable();
				}
				menuPopup.hide();
			}
		});

		this.tagsPopup = new PopupEditText();
		this.tagsPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String value = tagsPopup.getValue().trim();
				currentRow.setTags(value.isEmpty() ? MT : value);
				dataProvider.refresh();
				modified.add(currentRow);
				adp.enable();
				tagsPopup.hide();
			}
		});

		this.fspPopup = new PopupFileSelectionPanel();
		this.fspPopup.getApply().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				currentRow.getImages().clear();
				currentRow.getImages().addAll(fspPopup.getNames());
				dataProvider.refresh();
				modified.add(currentRow);
				adp.enable();
				fspPopup.hide();
			}
		});

		refresh();

	}

	private void refresh() {
		adp.disable();
		modified.clear();
		secureProductService.getProductTypes(query,
				new AsyncCallback<List<ProductTypeTransferObject>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.toString());
					}

					@Override
					public void onSuccess(List<ProductTypeTransferObject> result) {
						List<ProductTypeTransferObject> l = dataProvider
								.getList();
						l.clear();
						l.addAll(result);
					}
				});

	}
}
