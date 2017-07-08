package com.ipfsoftwares.mangi360.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ipfsoftwares.mangi360.R;
import com.ipfsoftwares.mangi360.model.Product;

import java.util.ArrayList;

/**
 * Created by twalipo on 7/8/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

	private ArrayList<Product> products ;
	private Context context;
	private int lastAnimatedPosition = -1;

	public ProductAdapter(Context context, ArrayList<Product> products) {
		this.context = context;
		this.products = products;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.mechant_item,parent,false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Product product = products.get(position);

		holder.productName.setText(product.getName());
		holder.productPrice.setText(product.getFormattedPrice());
		holder.status.setChecked(product.isStatus());

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
