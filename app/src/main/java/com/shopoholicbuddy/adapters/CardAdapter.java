package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {

     private Context mContext;
     private RecyclerCallBack recyclerCallBack;

    public CardAdapter(Context mContext ,RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.recyclerCallBack =recyclerCallBack;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_payments, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_card_payment)
        RelativeLayout rlCardPayment;

        public CardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
