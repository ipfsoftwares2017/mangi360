package com.ipfsoftwares.mangi360.model;

import android.database.Cursor;
import android.util.Log;

import com.ipfsoftwares.mangi360.database.ProductColumn;

/**
 * Created by twalipo on 7/8/17.
 */

public class Product {
	String name;
	double price;
	int quantity;
	int id;
	boolean status;

	public Product(String name,double price, int id ){
		this.name = name;
		this.price = price;
		this.id = id;
		this.status = false;
		quantity = 0;
	}

	public Product(Cursor data) {
		this.name = data.getString(data.getColumnIndex(ProductColumn.NAME));
		this.price = Double.parseDouble(data.getString(data.getColumnIndex(ProductColumn.PRICE)));
		this.id = data.getInt(data.getColumnIndex(ProductColumn._ID));
		this.status = data.getInt(data.getColumnIndex(ProductColumn.STATUS)) == 1;
		quantity = data.getInt(data.getColumnIndex(ProductColumn.QUANTITY));
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

	public double getTotalAmount(){
		return price * quantity;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
