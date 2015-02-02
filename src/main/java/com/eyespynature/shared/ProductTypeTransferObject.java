package com.eyespynature.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductTypeTransferObject implements IsSerializable {

	private List<String> images = new ArrayList<String>();
	private List<String> videos = new ArrayList<String>();

	public String getShortD() {
		return this.shortD;
	}

	public String getLongD() {
		return this.longD;
	}

	public List<String> getVideos() {
		return videos;
	}

	public String getSpec() {
		return this.spec;
	}

	private String name;
	private int numberInStock;
	private int numberReserved;

	private Long price;
	private int popularity;

	private long id;

	public long getId() {
		return id;
	}

	private boolean hidden;

	private String shortD;

	private String longD;

	private String notes;

	private String spec;

	private List<String> menu = new ArrayList<String>();

	private String tags;

	private int weight;

	private boolean large;

	public ProductTypeTransferObject() {
		// Needed for RPC stuff
	}

	public ProductTypeTransferObject(Long id, String name, String shortD, String longD, String notes, String spec,
			long price, int popularity, List<String> images, List<String> videos, List<String> menu, int numberInStock,
			int numberReserved, boolean hidden, String tags, int weight, boolean large) {
		this.id = id;
		this.name = name;
		this.shortD = shortD;
		this.longD = longD;
		this.notes = notes;
		this.spec = spec;
		this.price = price;
		this.popularity = popularity;
		this.images.addAll(images);
		this.videos.addAll(videos);
		this.menu = menu;
		this.numberInStock = numberInStock;
		this.numberReserved = numberReserved;
		this.hidden = hidden;
		this.tags = tags;
		this.weight = weight;
		this.large = large;
	}

	public String getNotes() {
		return this.notes;
	}

	public List<String> getMenu() {
		return this.menu;
	}

	public void setShortD(String shortD) {
		this.shortD = shortD;
	}

	public void setLongD(String longD) {
		this.longD = longD;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberInStock() {
		return this.numberInStock;
	}

	public int getNumberReserved() {
		return this.numberReserved;
	}

	public Long getPrice() {
		return this.price;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public int getPopularity() {
		return popularity;
	}

	public List<String> getImages() {
		return images;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean getLarge() {
		return this.large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}

}
