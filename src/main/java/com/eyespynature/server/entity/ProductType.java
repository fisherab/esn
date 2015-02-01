package com.eyespynature.server.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.eyespynature.shared.ProductTypeTransferObject;

@Entity
@NamedQueries({
		@NamedQuery(name = "ProductType.Hidden", query = "SELECT p FROM ProductType p WHERE p.hidden = true ORDER BY p.name"),
		@NamedQuery(name = "ProductType.NotHidden", query = "SELECT p FROM ProductType p WHERE p.hidden = false ORDER BY p.name"),
		@NamedQuery(name = "ProductType.Menu0", query = "SELECT DISTINCT p.menu0 FROM ProductType p WHERE p.hidden = false"),
		@NamedQuery(name = "ProductType.Menu1", query = "SELECT DISTINCT p.menu1 FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0"),
		@NamedQuery(name = "ProductType.Menu2", query = "SELECT DISTINCT p.menu2 FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 AND p.menu1 = :menu1"),
		@NamedQuery(name = "ProductType.Menu3", query = "SELECT DISTINCT p.menu3 FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 AND p.menu1 = :menu1 AND p.menu2 = :menu2"),
		// @NamedQuery(name = "ProductType.Menu4", query =
		// "SELECT DISTINCT p.menu4 FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 AND p.menu1 = :menu1 AND p.menu2 = :menu2  AND p.menu3 = :menu3"),
		@NamedQuery(name = "ProductType.Item0", query = "SELECT DISTINCT p FROM ProductType p WHERE p.hidden = false ORDER BY p.popularity DESC"),
		@NamedQuery(name = "ProductType.Item1", query = "SELECT DISTINCT p FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 ORDER BY p.popularity DESC"),
		@NamedQuery(name = "ProductType.Item2", query = "SELECT DISTINCT p FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 AND p.menu1 = :menu1 ORDER BY p.popularity DESC"),
		@NamedQuery(name = "ProductType.Item3", query = "SELECT DISTINCT p FROM ProductType p WHERE p.hidden = false AND p.menu0 = :menu0 AND p.menu1 = :menu1 AND p.menu2 = :menu2 ORDER BY p.popularity DESC"),
		@NamedQuery(name = "ProductType.Name", query = "SELECT p FROM ProductType p WHERE p.name = :name") })
public class ProductType {

	public boolean isHidden() {
		return hidden;
	}

	public List<String> getImages() {
		return images;
	}

	public long getId() {
		return id;
	}

	public String getLongD() {
		return longD;
	}

	public String getMenu0() {
		return menu0;
	}

	public String getMenu1() {
		return menu1;
	}

	public String getMenu2() {
		return menu2;
	}

	public String getMenu3() {
		return menu3;
	}

	public int getPopularity() {
		return popularity;
	}

	public String getShortD() {
		return shortD;
	}

	public String getSpec() {
		return spec;
	}

	public List<String> getVideos() {
		return videos;
	}

	public boolean hidden;

	@ElementCollection
	private List<String> videos = new ArrayList<String>();

	@ElementCollection
	private List<String> images = new ArrayList<String>();

	// For JPA
	public ProductType() {
	}

	@Id
	@GeneratedValue
	private long id;

	@Column(length = 4000)
	private String longD;

	private String menu0;

	private String menu1;

	private String menu2;

	private String menu3;

	private String name;

	private int numberInStock;

	private int numberReserved;
	private long price;
	private int popularity;

	private String shortD;

	@Column(length = 4000)
	private String spec;

	@Column(length = 4000)
	private String notes;

	private String tags;

	private int weight;

	private boolean large;

	public ProductType(ProductTypeTransferObject productTypeTransferObject) {
		this.id = productTypeTransferObject.getId();
		this.name = productTypeTransferObject.getName();
		this.shortD = productTypeTransferObject.getShortD();
		this.longD = new String(productTypeTransferObject.getLongD());
		this.notes = new String(productTypeTransferObject.getNotes());
		this.spec = new String(productTypeTransferObject.getSpec());
		this.popularity = productTypeTransferObject.getPopularity();
		this.price = productTypeTransferObject.getPrice();
		this.images.addAll(productTypeTransferObject.getImages());
		this.videos.addAll(productTypeTransferObject.getVideos());
		this.tags = productTypeTransferObject.getTags();
		this.weight = productTypeTransferObject.getWeight();
		this.large = productTypeTransferObject.getLarge();

		List<String> menu = productTypeTransferObject.getMenu();
		if (menu.size() > 0) {
			this.menu0 = menu.get(0);
		}
		if (menu.size() > 1) {
			this.menu1 = menu.get(1);
		}
		if (menu.size() > 2) {
			this.menu2 = menu.get(2);
		}
		if (menu.size() > 3) {
			this.menu3 = menu.get(3);
		}
		this.numberInStock = productTypeTransferObject.getNumberInStock();
		this.numberReserved = productTypeTransferObject.getNumberReserved();
		this.hidden = productTypeTransferObject.isHidden();
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

	public long getPrice() {
		return this.price;
	}

	public ProductTypeTransferObject getTransferObject() {
		List<String> menu = new ArrayList<String>(4);
		if (this.menu0 != null) {
			menu.add(this.menu0);
		}
		if (this.menu1 != null) {
			menu.add(this.menu1);
		}
		if (this.menu2 != null) {
			menu.add(this.menu2);
		}
		if (this.menu3 != null) {
			menu.add(this.menu3);
		}
		return new ProductTypeTransferObject(this.id, this.name, this.shortD, this.longD,
				this.notes, this.spec, this.price, this.popularity, images, videos, menu,
				this.numberInStock, this.numberReserved, this.hidden, this.tags, this.weight,
				this.large);
	}

	public void setNumberInStock(int numberInStock) {
		this.numberInStock = numberInStock;
	}

	public String getNotes() {
		return notes;
	}

	public void setNumberReserved(int numberReserved) {
		this.numberReserved = numberReserved;
	}

	public int getWeight() {
		return weight;
	}

	public boolean isLarge() {
		return large;
	}

}
