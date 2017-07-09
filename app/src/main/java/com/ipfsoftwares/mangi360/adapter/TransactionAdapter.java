package com.ipfsoftwares.mangi360.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.ipfsoftwares.mangi360.R;
import com.ipfsoftwares.mangi360.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;
    private int lastAnimatedPosition = -1;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.displayName.setText(transaction.getDisplayName());
        holder.message.setText(transaction.getMessage());
        holder.photoUrl.setImageResource(R.drawable.ic_account_circle_black_36dp);

        animateView(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
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
        TextView displayName;
        TextView message;
        ImageView photoUrl;

        ViewHolder(View itemView) {
            super(itemView);

            photoUrl = (ImageView) itemView.findViewById(R.id.account_holder_image_view);
            displayName= (TextView) itemView.findViewById(R.id.account_holder_text_view);
            message = (TextView) itemView.findViewById(R.id.message_text_view);
        }
    }
}

