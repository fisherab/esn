package com.eyespynature.user;

import java.util.List;

import com.eyespynature.client.TheEventBus;
import com.eyespynature.client.service.ProductService.SortType;
import com.eyespynature.client.service.ProductServiceAsync;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.eyespynature.user.events.SearchEndedEvent;
import com.eyespynature.user.events.SearchStartedEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Pager extends Composite {

	interface MyUiBinder extends UiBinder<Widget, Pager> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final EventBus bus = TheEventBus.getInstance();
	private static final ProductServiceAsync productService = ProductServiceAsync.Util
			.getInstance();

	@UiField
	Button previous;

	@UiField
	Button next;

	@UiField
	Button down2;

	@UiField
	Button down1;

	@UiField
	Button here;

	@UiField
	Button up1;

	@UiField
	Button up2;

	@UiField
	Label countBox;

	private int pageNum;
	private int maxPageNum;
	private int count;
	private SortType sortType;
	private String search;

	@UiHandler("next")
	void nextHandler(ClickEvent e) {
		display(pageNum + 1);
	}

	@UiHandler("down2")
	void down2Handler(ClickEvent e) {
		display(pageNum - 2);
	}

	@UiHandler("down1")
	void down1Handler(ClickEvent e) {
		display(pageNum - 1);
	}

	@UiHandler("up1")
	void up1Handler(ClickEvent e) {
		display(pageNum + 1);
	}

	@UiHandler("up2")
	void up2Handler(ClickEvent e) {
		display(pageNum + 2);
	}

	@UiHandler("previous")
	void previousHandler(ClickEvent e) {
		display(pageNum - 1);
	}

	private void display(final int page) {
		bus.fireEvent(new SearchStartedEvent());
		productService.search(search, page * Constants.PAGE_SIZE, Constants.PAGE_SIZE,
				sortType.name(), new AsyncCallback<List<ProductTypeTransferObject>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<ProductTypeTransferObject> pttos) {
						bus.fireEvent(new SearchEndedEvent(pttos, page * Constants.PAGE_SIZE,
								count, search, sortType));
					}
				});
	}

	public Pager() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void setRange(SearchEndedEvent searchEndedEvent) {

		int offset = searchEndedEvent.getOffset();
		sortType = searchEndedEvent.getSortType();
		search = searchEndedEvent.getSearch();
		count = searchEndedEvent.getCount();
		pageNum = offset / Constants.PAGE_SIZE;
		maxPageNum = (count - 1) / Constants.PAGE_SIZE;
		previous.setEnabled(pageNum != 0);
		down2.setVisible(pageNum >= 2);
		down2.setEnabled(true);
		down2.setText(Integer.toString(pageNum - 1));
		down1.setVisible(pageNum >= 1);
		down1.setEnabled(true);
		down1.setText(Integer.toString(pageNum));
		here.setVisible(true);
		here.setEnabled(false);
		here.setText(Integer.toString(pageNum + 1));
		up1.setVisible(pageNum <= maxPageNum - 1);
		up1.setEnabled(true);
		up1.setText(Integer.toString(pageNum + 2));
		up2.setVisible(pageNum <= maxPageNum - 2);
		up2.setEnabled(true);
		up2.setText(Integer.toString(pageNum + 3));
		next.setEnabled(pageNum != maxPageNum);

		if (count > 1) {
			countBox.setText(count + " items were found.");
		} else {
			countBox.setText("1 item was found.");
		}
	}

}
