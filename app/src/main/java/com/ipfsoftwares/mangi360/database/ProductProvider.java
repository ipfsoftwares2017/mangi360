package com.ipfsoftwares.mangi360.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by twalipo on 7/8/17.
 */

@ContentProvider(authority = ProductProvider.AUTHORITY,
	database = ProductDB.class)
public final class ProductProvider {

	public static final String AUTHORITY = "com.ipfsoftwares.mangi360.ProductProvider";
	static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	interface PATH {
		String PRODUCTS = "products";
	}
	private static Uri buildUri(String... paths){
		Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
		for(String path: paths){
			builder.appendPath(path);
		}
		return builder.build();
	}

	@TableEndpoint(table = ProductDB.PRODUCTS)
	public static final class Products {

		@ContentUri(path = PATH.PRODUCTS,
		type = "vnd.android.cursor.dir/product",
		defaultSort = ProductColumn.NAME + "ASC")
		public static final Uri PRODUCTS = buildUri(PATH.PRODUCTS);



	}
}
