package com.eyespynature.user;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.client.service.ProductService;
import com.eyespynature.client.service.ProductServiceAsync;
import com.eyespynature.client.service.ProductService.SortType;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.user.events.SearchEndedEvent;
import com.eyespynature.user.events.SearchEndedEventHandler;
import com.eyespynature.user.events.SearchRequestedEvent;
import com.eyespynature.user.events.SearchRequestedEventHandler;
import com.eyespynature.user.events.SearchStartedEvent;
import com.eyespynature.user.events.SearchStartedEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite {

	interface MyUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final EventBus bus = TheEventBus.getInstance();
	private static final ProductServiceAsync productService = ProductServiceAsync.Util
			.getInstance();
	private static final List<ProductTypeTransferObject> nopttos = Collections.emptyList();
	final private static Logger logger = Logger.getLogger(SearchPanel.class.getName());

	private String query;

	@UiField
	HTML message;

	@UiField
	VerticalPanel items;

	@UiField
	Pager pager;

	@UiField
	ListBox sort;

	@UiHandler("sort")
	void handleSortSelect(ChangeEvent event) {
		search();
	}

	@SuppressWarnings("serial")
	class LRUCache<K, V> extends LinkedHashMap<K, V> {

		private int maxEntries;

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxEntries;
		}

		LRUCache(int maxEntries) {
			super(16, (float) 0.75, true);
			this.maxEntries = maxEntries;
		}
	};

	public SearchPanel() {

		initWidget(uiBinder.createAndBindUi(this));

		bus.addHandler(SearchStartedEvent.TYPE, new SearchStartedEventHandler() {

			@Override
			public void onEvent(SearchStartedEvent searchStartedEvent) {
				message.setText("Search in progress");
				message.setVisible(true);
				items.clear();
				pager.setVisible(false);
			}
		});

		bus.addHandler(SearchEndedEvent.TYPE, new SearchEndedEventHandler() {

			@Override
			public void onEvent(SearchEndedEvent searchEndedEvent) {
				List<ProductTypeTransferObject> pttos = searchEndedEvent
						.getProductTypeTransferObjects();
				if (pttos.isEmpty()) {
					message.setText("No items were found matching your search. If you have used the 'AND' keyword consider removing it.");
					message.setVisible(true);
				} else {
					message.setVisible(false);
					for (ProductTypeTransferObject ptto : pttos) {
						items.add(new Item(ptto));
					}
					pager.setRange(searchEndedEvent);
					pager.setVisible(true);
				}

			}
		});

		bus.addHandler(SearchRequestedEvent.TYPE, new SearchRequestedEventHandler() {
			@Override
			public void onEvent(SearchRequestedEvent searchRequestedEvent) {
				query = searchRequestedEvent.getQuery();
				search();
			}
		});

	}

	Map<String, Integer> counts = new LRUCache<String, Integer>(20);

	private void search() {
		if (query.isEmpty()) {
			Window.alert("Please enter something in the search box");
		} else {
			History.newItem("search");
			bus.fireEvent(new SearchStartedEvent());
			Integer count = counts.get(query);
			if (count == null) {
				productService.getCount(query, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						counts.put(query, result);
						getRows(result);
					}
				});
			} else {
				getRows(count);
			}
		}
	}

	private void getRows(final int count) {

		if (count == 0) {
			bus.fireEvent(new SearchEndedEvent(nopttos, 0, count, query, SortType.RELEVANCE));
		} else {
			logger.fine("Index is " + sort.getSelectedIndex() + " "
					+ sort.getValue(sort.getSelectedIndex()));
			SortType sortType = null;
			String typeString = sort.getValue(sort.getSelectedIndex());
			if (typeString.equals("Relevance")) {
				sortType = ProductService.SortType.RELEVANCE;
			} else if (typeString.equals("Price: Low to High")) {
				sortType = ProductService.SortType.PRICEL2H;
			} else if (typeString.equals("Price: High to Low")) {
				sortType = ProductService.SortType.PRICEH2L;
			} else {
				Window.alert("Illegal value in list box");
			}
			final SortType fSortType = sortType;

			productService.search(query, 0, Constants.PAGE_SIZE, sortType.name(),
					new AsyncCallback<List<ProductTypeTransferObject>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(List<ProductTypeTransferObject> pttos) {
							bus.fireEvent(new SearchEndedEvent(pttos, 0, count, query, fSortType));
						}
					});
		}
	}

}
