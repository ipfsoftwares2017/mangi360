package com.ipfsoftwares.mangi360.model;

/**
 * Created by twalipo on 7/8/17.
 */

public class Product {
	String name;
	double price;
	int id;
	boolean status;

	public Product(String name,double price, int id ,boolean status ){
		this.name = name;
		this.price = price;
		this.id = id;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public String getFormattedPrice(){
		return "Tsh ".concat(String.valueOf(price));
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
