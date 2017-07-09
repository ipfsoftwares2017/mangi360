package com.ipfsoftwares.mangi360;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView accountHolderImageView;
        TextView accountHolderTextView;
        TextView messageTextView;

        public TransactionViewHolder(View v) {
            super(v);
            accountHolderImageView = (CircleImageView) itemView.findViewById(R.id.account_holder_image_view);
            accountHolderTextView = (TextView) itemView.findViewById(R.id.account_holder_text_view);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
        }
    }

    private RecyclerView mTransactionRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

       mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
       mTransactionRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);
       mLinearLayoutManager = new LinearLayoutManager(this);
       mLinearLayoutManager.setStackFromEnd(false);
    }

    private String[] getTransactions() {
       return (String[]) new ArrayList<String>().toArray();
    }
}
