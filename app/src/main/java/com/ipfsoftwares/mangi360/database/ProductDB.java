package com.ipfsoftwares.mangi360.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by twalipo on 7/8/17.
 */

@Database(version = ProductDB.VERSION)
public final class ProductDB {

	public static final int VERSION = 1;

	@Table(ProductColumn.class)
	public static final String PRODUCTS = "products";

	@OnCreate
	public static void onCreate(Context context, SQLiteDatabase db) {
	}

	@OnUpgrade
	public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
								 int newVersion) {
	}

	@OnConfigure
	public static void onConfigure(SQLiteDatabase db) {
	}
}
