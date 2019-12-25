package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.ProductDetailsActivity;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 09-Apr-18.
 */

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ImagesHolder> {
    private final Context mContext;
    private final List<String> productImagesList;
    private final RecyclerCallBack recyclerCallBack;
    public int selectedPosition = 0;

    public ProductImagesAdapter(Context mContext, List<String> productImagesList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.productImagesList = productImagesList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_product_image, parent, false);
        return new ImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesHolder holder, int position) {
        if (mContext instanceof ProductDetailsActivity && ((ProductDetailsActivity)mContext).videoDrawable != null &&
                productImagesList.get(position).equals(((ProductDetailsActivity)mContext).videoUrl)) {
            holder.ivProductImage.setImageDrawable(((ProductDetailsActivity)mContext).videoDrawable);
        }else {
            AppUtils.getInstance().setImages(mContext, productImagesList.get(position), holder.ivProductImage, 0, R.drawable.ic_placeholder);
        }
        if (selectedPosition == position){
            holder.ivProductImage.setBackgroundResource(R.drawable.round_corner_acent_stroke_grey_bg);
        }else {
            holder.ivProductImage.setBackgroundResource(R.color.colorGrey);
        }
    }

    @Override
    public int getItemCount() {
        return productImagesList.size();
    }

    class ImagesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_product_pic)
        ImageView ivProductImage;
        @BindView(R.id.cv_root_view)
        CardView cvRootView;

        ImagesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick({R.id.iv_product_pic})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_product_pic:
                    selectedPosition = getAdapterPosition();
                    recyclerCallBack.onClick(getAdapterPosition(), ivProductImage);
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
