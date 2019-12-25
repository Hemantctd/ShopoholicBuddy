package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.AddProductServiceModel;
import com.shopoholicbuddy.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfflineDealAdapter extends RecyclerView.Adapter<OfflineDealAdapter.OfflineDealHolder> {

    private Context mContext;
    private RecyclerCallBack recyclerCallBack;
    private ArrayList<AddProductServiceModel> serviceModelList;

    public OfflineDealAdapter(Context mContext, ArrayList<AddProductServiceModel> serviceModelList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.serviceModelList = serviceModelList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public OfflineDealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_offline_deal, parent, false);
        return new OfflineDealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineDealHolder holder, int position) {
        holder.tvNumberOfImages.setText(TextUtils.concat(
                (serviceModelList.get(position).getImagesList() != null ? serviceModelList.get(position).getImagesList().size() : "0")
                        + " " + mContext.getResources().getString(R.string.images)));
        if (serviceModelList.get(position).getImagesList().size() > 0)
            AppUtils.getInstance().setImages(mContext, serviceModelList.get(position).getImagesList().get(0), holder.ivDealImages, 0, R.drawable.ic_placeholder);
        holder.tvDealDescription.setText(serviceModelList.get(position).getDescription());
        holder.tvPostedDate.setText(serviceModelList.get(position).getDealPostingDate());
        holder.tvDealOffers.setText(TextUtils.concat((serviceModelList.get(position).getDiscountPercentage().equals("0") ? "" :
                (serviceModelList.get(position).getDiscountPercentage() + mContext.getString(R.string.off_on) + " "))
                        + serviceModelList.get(position).getDealName()));

        holder.ivDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class OfflineDealHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_product_pic)
        ImageView ivDealImages;
        @BindView(R.id.tv_number_of_images)
        CustomTextView tvNumberOfImages;
        @BindView(R.id.tv_deal_offers)
        CustomTextView tvDealOffers;
        @BindView(R.id.tv_deal_description)
        CustomTextView tvDealDescription;
        @BindView(R.id.tv_posted_date)
        CustomTextView tvPostedDate;
        @BindView(R.id.rl_offline_deal)
        RelativeLayout rlOfflineDeal;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        OfflineDealHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.rl_offline_deal, R.id.iv_delete})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_offline_deal:
                    recyclerCallBack.onClick(getAdapterPosition(), rlOfflineDeal);
                    break;
                case R.id.iv_deal_image:
                    recyclerCallBack.onClick(getAdapterPosition(), ivDealImages);
                    break;
                case R.id.iv_delete:
                    recyclerCallBack.onClick(getAdapterPosition(), ivDelete);
                    break;
            }
        }

    }
}
