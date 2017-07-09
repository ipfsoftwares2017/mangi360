package com.ipfsoftwares.mangi360;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.ipfsoftwares.mangi360.adapter.TransactionAdapter;
import com.ipfsoftwares.mangi360.model.Transaction;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    private ArrayList<Transaction> mTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        RecyclerView mTransactionRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);

        mockTransactions();

        TransactionAdapter mTransactionAdapter = new TransactionAdapter(this, mTransactions);
        mTransactionRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTransactionRecyclerView.setAdapter(mTransactionAdapter);
    }

    private void mockTransactions() {
        mTransactions = new ArrayList<>();
        mTransactions.add(new Transaction("688089928", "Bought goods worth TZS.100 at 2017-07-09T08:10:08.989Z"));
        mTransactions.add(new Transaction("688089699", "Bought goods worth TZS.100 at 2017-07-09T08:11:56.684Z"));
        mTransactions.add(new Transaction("688089928", "Bought goods worth TZS.100 at 2017-07-09T08:22:09.456Z"));
    }
}
