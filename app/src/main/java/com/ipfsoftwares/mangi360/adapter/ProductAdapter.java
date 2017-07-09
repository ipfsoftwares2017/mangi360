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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ipfsoftwares.mangi360.ListingActivity;
import com.ipfsoftwares.mangi360.R;
import com.ipfsoftwares.mangi360.database.ProductColumn;
import com.ipfsoftwares.mangi360.database.ProductProvider;
import com.ipfsoftwares.mangi360.model.Product;

import java.util.ArrayList;

/**
 * Created by twalipo on 7/8/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

	private ArrayList<Product> products ;
	private Context context;
	private int lastAnimatedPosition = -1;
	private ProductDelegate productDelegate;
	public static final String[] PROJECTION = new String[]{
		ProductColumn._ID, ProductColumn.NAME,ProductColumn.PRICE,ProductColumn.STATUS, ProductColumn.QUANTITY
	};

	public ProductAdapter(Context context, ArrayList<Product> products) {
		this.context = context;
		this.products = products;
	}

	public interface ProductDelegate{
		public void productAdded(Product product);
	}

	public void setDelegate(ProductDelegate delegate){
		productDelegate = delegate;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.mechant_item,parent,false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Product product = products.get(position);

		holder.productName.setText(product.getName());
		holder.productPrice.setText(product.getFormattedPrice());
		holder.status.setChecked(product.isStatus());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!product.isStatus()){
					final View productView = LayoutInflater.from(context).inflate(R.layout.add_to_cat,null);
					new AlertDialog.Builder(context).
						setView(productView).
						setCancelable(false).
						setTitle("Add To Cat").
						setMessage("Please enter quantity below to ad product to listing").
						setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								EditText quantity = (EditText) productView.findViewById(R.id.quantity);

								if(!TextUtils.isEmpty(quantity.getText().toString()) && !quantity.getText().toString().equals("0")){
									final ContentValues values = new ContentValues();
									values.put(ProductColumn.QUANTITY,quantity.getText().toString());
									values.put(ProductColumn.STATUS,1);
									if(productDelegate != null) {
										product.setQuantity(Integer.parseInt(quantity.getText().toString()));
										productDelegate.productAdded(product);
									}
//
//									new Thread(new Runnable() {
//										@Override
//										public void run() {
//											context.getContentResolver().update(ProductProvider.Products.CONTENT_URI,
//												values,
//												ProductColumn._ID + " = ? ",
//												new String[]{String.valueOf(product.getId())});
//
//											Snackbar.make(holder.itemView, product.getName() + " Added to Cat",Snackbar.LENGTH_SHORT).show();
//
//										}
//									}).start();
//									dialogInterface.dismiss();
								}
							}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					}).show();

				} else {
					new AlertDialog.Builder(context).
						setCancelable(false).
						setTitle(R.string.remove_from_cat).
						setMessage("Remove " + product.getName() + " fro Cat? " ).
						setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								final ContentValues values = new ContentValues();
								values.put(ProductColumn.QUANTITY,0);
								values.put(ProductColumn.STATUS,0);

								new Thread(new Runnable() {
									@Override
									public void run() {
										context.getContentResolver().update(ProductProvider.Products.CONTENT_URI,
											values,
											ProductColumn._ID + " = ? ",
											new String[]{String.valueOf(product.getId())});

										Snackbar.make(holder.itemView, product.getName() + " product removed successfully",Snackbar.LENGTH_SHORT).show();

									}
								}).start();
								dialogInterface.dismiss();
							}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					}).show();
				}
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

		TextView productName;
		TextView productPrice;
		CheckBox status;

		public ViewHolder(View itemView) {
			super(itemView);
			productName = (TextView) itemView.findViewById(R.id.product_name);
			productPrice = (TextView) itemView.findViewById(R.id.product_price);
			status = (CheckBox) itemView.findViewById(R.id.status);
		}
	}
}
