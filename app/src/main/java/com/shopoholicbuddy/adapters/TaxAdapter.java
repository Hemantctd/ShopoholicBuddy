package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.AddProductServiceActivity;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.TaxArr;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaxAdapter extends RecyclerView.Adapter<TaxAdapter.TaxHolder> {


    private final ArrayList<TaxArr> taxesArrayList;
    private final RecyclerCallBack recyclerCallBack;
    private final Context mContext;

    public TaxAdapter(Context mContext, ArrayList<TaxArr> taxesArrayList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.taxesArrayList = taxesArrayList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public TaxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_taxes, parent, false);
        return new TaxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxHolder holder, int position) {
        if (holder.getAdapterPosition() == 0) {
            holder.ivRemove.setVisibility(View.INVISIBLE);
        } else {
            holder.ivRemove.setVisibility(View.VISIBLE);
        }
        holder.tvTaxName.setText(taxesArrayList.get(holder.getAdapterPosition()).getTaxName());
        holder.tvTaxPercentage.setText(taxesArrayList.get(holder.getAdapterPosition()).getTaxPercentage());

        if (mContext instanceof AddProductServiceActivity) {
            holder.tvPer.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            holder.tvTaxName.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            holder.tvTaxPercentage.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            holder.tvTaxName.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorHintText));
            holder.tvTaxPercentage.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorHintText));
        }else {
            holder.tvPer.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.tvTaxName.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.tvTaxPercentage.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.tvTaxName.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorGrayish));
            holder.tvTaxPercentage.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorGrayish));
        }

        holder.tvTaxName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                taxesArrayList.get(holder.getAdapterPosition()).setTaxName(holder.tvTaxName.getText().toString());
            }
        });
        holder.tvTaxPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                taxesArrayList.get(holder.getAdapterPosition()).setTaxPercentage(holder.tvTaxPercentage.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taxesArrayList.size();
    }


    public class TaxHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_tax_name)
        CustomEditText tvTaxName;
        @BindView(R.id.tv_tax_percentage)
        CustomEditText tvTaxPercentage;
        @BindView(R.id.tv_per)
        CustomTextView tvPer;
        @BindView(R.id.iv_remove)
        ImageView ivRemove;
        @BindView(R.id.ll_time_slots)
        LinearLayout llTimeSlots;
        public TaxHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.iv_remove})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_remove:
                    recyclerCallBack.onClick(getAdapterPosition(), ivRemove);
                    break;
            }
        }
    }
}
