package com.ipfsoftwares.mangi360.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.ipfsoftwares.mangi360.R;
import com.ipfsoftwares.mangi360.database.ProductColumn;
import com.ipfsoftwares.mangi360.database.ProductProvider;
import com.ipfsoftwares.mangi360.model.Product;

import java.util.ArrayList;

/**
 * Created by twalipo on 7/9/17.
 */

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

	private ArrayList<Product> products ;
	private Context context;
	private int lastAnimatedPosition = -1;
	public static final String[] PROJECTION = new String[]{
		ProductColumn._ID, ProductColumn.NAME,ProductColumn.PRICE,ProductColumn.STATUS, ProductColumn.QUANTITY
	};

	public ListingAdapter(Context context, ArrayList<Product> products) {
		this.context = context;
		this.products = products;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.listing_item,parent,false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Product product = products.get(position);

		holder.listName.setText(product.getName());
		holder.listPrice.setText(product.getFormattedPrice());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					final View productView = LayoutInflater.from(context).inflate(R.layout.add_product_dialog,null);
					final EditText name = (EditText) productView.findViewById(R.id.name);
					final EditText price = (EditText) productView.findViewById(R.id.amount);
					name.setText(product.getName());
					price.setText(String.valueOf(product.getPrice()));

					new AlertDialog.Builder(context).
						setView(productView).
						setCancelable(false).
						setTitle(R.string.edit_delete_product).
						setMessage(R.string.edit_desc).
						setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								if(!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(price.getText().toString())){
									new Thread(new Runnable() {
										@Override
										public void run() {
											//setup update data
											ContentValues values = new ContentValues();
											values.put(ProductColumn.NAME,name.getText().toString());
											values.put(ProductColumn.PRICE,price.getText().toString());

											//update local database
											context.getContentResolver().update(ProductProvider.Products.CONTENT_URI,
												values,
												ProductColumn._ID + " = ?",
												new String[] {String.valueOf(product.getId())});
											Snackbar.make(holder.itemView, R.string.update_success,Snackbar.LENGTH_SHORT).show();
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
					}).setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									context.getContentResolver().delete(ProductProvider.Products.CONTENT_URI,
										ProductColumn._ID + " = ?",
									new String[] {String.valueOf(product.getId())});
								}
							}).start();
							dialogInterface.dismiss();
							Snackbar.make(holder.itemView, R.string.delete_success,Snackbar.LENGTH_SHORT).show();
						}
					}).show();
			}
		});

		animateView(holder.itemView,position);
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	private void animateView(View view, int position){
		if(position > lastAnimatedPosition){
			lastAnimatedPosition = position;
			view.setTranslationY(300);
			view.animate()
				.translationY(0.0f)
				.setInterpolator(new DecelerateInterpolator(3.0f))
				.setDuration(700)
				.start();
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView listName;
		TextView listPrice;

		public ViewHolder(View itemView) {
			super(itemView);
			listName = (TextView) itemView.findViewById(R.id.list_name);
			listPrice = (TextView) itemView.findViewById(R.id.list_price);
		}
	}
}
