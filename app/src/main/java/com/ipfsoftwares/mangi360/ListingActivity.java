package com.ipfsoftwares.mangi360;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipfsoftwares.mangi360.adapter.ListingAdapter;
import com.ipfsoftwares.mangi360.database.ProductColumn;
import com.ipfsoftwares.mangi360.database.ProductProvider;
import com.ipfsoftwares.mangi360.model.Product;

import java.util.ArrayList;

/**
 * Created by twalipo on 7/9/17.
 */

public class ListingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private RecyclerView mMessageRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private ProgressBar mProgressBar;
	private FloatingActionButton addFab;
	private TextView empty;

	// Product
	private ListingAdapter productAdapter;
	private ArrayList<Product> products = new ArrayList<>();
	private final int PRODUCT_LOADER_ID = 123;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listing_activity);

		empty = (TextView) findViewById(R.id.empty);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
		mLinearLayoutManager = new LinearLayoutManager(this);
		mProgressBar.setVisibility(View.VISIBLE);

		productAdapter = new ListingAdapter(this,products);
		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessageRecyclerView.setAdapter(productAdapter);

		addFab = (FloatingActionButton) findViewById(R.id.add_fab);
		addFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final View productView = LayoutInflater.from(ListingActivity.this).inflate(R.layout.add_product_dialog,null);
				new AlertDialog.Builder(ListingActivity.this).
					setView(productView).
					setCancelable(false).
					setTitle(R.string.add_product).
					setMessage(R.string.add_product_desc).
					setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							EditText name = (EditText) productView.findViewById(R.id.name);
							EditText price = (EditText) productView.findViewById(R.id.amount);

							if(!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(price.getText().toString())){
								final ContentValues values = new ContentValues();
								values.put(ProductColumn.NAME,name.getText().toString());
								values.put(ProductColumn.PRICE,price.getText().toString());
								values.put(ProductColumn.STATUS,0);
								values.put(ProductColumn.QUANTITY,0);

								new Thread(new Runnable() {
									@Override
									public void run() {
										ListingActivity.this.getContentResolver().insert(ProductProvider.Products.CONTENT_URI,values);
										Snackbar.make(addFab,"Detail Added successfully",Snackbar.LENGTH_SHORT).show();

									}
								}).start();
								dialogInterface.dismiss();
							}
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
					}
				}).show();
			}
		});

		loadProducts();

	}

	private void loadProducts(){
		getLoaderManager().initLoader(PRODUCT_LOADER_ID,null,this);
	}

	private void notifyDataChange(){
		productAdapter.notifyDataSetChanged();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
			ProductProvider.Products.CONTENT_URI,
			ListingAdapter.PROJECTION,
			null,null,null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mProgressBar.setVisibility(View.GONE);
		if(cursor != null && cursor.getCount() > 0){
			products.clear();
			while(cursor.moveToNext()){
				products.add(new Product(cursor));
			}

			if(empty.getVisibility() == View.VISIBLE)
				empty.setVisibility(View.GONE);

		}else if(products.size() == 0){
			empty.setVisibility(View.VISIBLE);
		}

		notifyDataChange();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
