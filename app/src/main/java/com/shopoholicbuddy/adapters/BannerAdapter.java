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
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.launcherhomeresponse.BannerArr;
import com.shopoholicbuddy.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
    private final Context mContext;
    private final List<BannerArr> bannerList;
    private final RecyclerCallBack recyclerCallBack;

    public BannerAdapter(Context mContext, List<BannerArr> bannerList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.bannerList = bannerList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        AppUtils.getInstance().setImages(mContext, bannerList.get(position).getBannerImage(), holder.ivBanner, 5, R.drawable.ic_placeholder);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    class BannerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_banner)
        ImageView ivBanner;
        @BindView(R.id.cv_root_view)
        CardView cvRootView;

        BannerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.iv_banner, R.id.cv_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_banner:
                    break;
                case R.id.cv_root_view:
                    break;
            }
        }
    }
}
