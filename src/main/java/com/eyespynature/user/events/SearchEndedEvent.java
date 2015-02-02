package com.eyespynature.user.events;

import java.util.List;

import com.eyespynature.client.service.ProductService.SortType;
import com.eyespynature.shared.ProductTypeTransferObject;
import com.google.gwt.event.shared.GwtEvent;

public class SearchEndedEvent extends GwtEvent<SearchEndedEventHandler> {

	public static Type<SearchEndedEventHandler> TYPE = new Type<SearchEndedEventHandler>();
	private List<ProductTypeTransferObject> productTypeTransferObjects;
	private int offset;
	private int count;
	private String search;
	private SortType sortType;

	public SearchEndedEvent(List<ProductTypeTransferObject> productTypeTransferObjects, int offset, int count, String search, SortType sortType) {
		this.productTypeTransferObjects = productTypeTransferObjects;
		this.offset = offset;
		this.count = count;
		this.search = search;
		this.sortType = sortType;
	}

	public SortType getSortType() {
		return this.sortType;
	}

	public static Type<SearchEndedEventHandler> getTYPE() {
		return TYPE;
	}

	public String getSearch() {
		return this.search;
	}

	@Override
	public Type<SearchEndedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchEndedEventHandler handler) {
		handler.onEvent(this);
	}

	public List<ProductTypeTransferObject> getProductTypeTransferObjects() {
		return productTypeTransferObjects;
	}

	public int getOffset() {
		return offset;
	}

	public int getCount() {
		return count;
	}

}
