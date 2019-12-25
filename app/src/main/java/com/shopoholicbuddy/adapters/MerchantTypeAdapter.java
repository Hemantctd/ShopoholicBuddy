package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.countrymodel.CountryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class MerchantTypeAdapter extends RecyclerView.Adapter<MerchantTypeAdapter.Holder>{


    private final List<String> selectedCountriesList;
    private final Context mContext;
    private final RecyclerCallBack recyclerCallBack;
    public MerchantTypeAdapter(Context mContext, List<String> selectedCountriesList, RecyclerCallBack recyclerCallBack) {
        this.selectedCountriesList = selectedCountriesList;
        this.mContext = mContext;
        this.recyclerCallBack = recyclerCallBack;
    }

    //view holder to hold item for each row
    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.country_name)
        TextView countryName;
        public Holder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerCallBack.onClick(getAdapterPosition(), itemView);
                }
            });

        }

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_code, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.countryName.setText(selectedCountriesList.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedCountriesList.size();
    }

}
