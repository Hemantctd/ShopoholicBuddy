package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.fragments.HomeCategoriesDealsFragment;
import com.shopoholicbuddy.fragments.HomePercentageDealsFragment;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.productdealsresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class SharedByMeAdapter extends RecyclerView.Adapter<SharedByMeAdapter.ProductHolder> {
    private final Context mContext;
    private final List<Result> productList;
    private final RecyclerCallBack recyclerCallBack;
    private final Fragment mFragment;

    public SharedByMeAdapter(Context mContext, Fragment mFragment, List<Result> productList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.productList = productList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        boolean isMapShow = false;
        if (mFragment instanceof HomeCategoriesDealsFragment) {
            isMapShow = ((HomeCategoriesDealsFragment) mFragment).isMapShow;
        } else if (mFragment instanceof HomePercentageDealsFragment) {
            isMapShow = ((HomePercentageDealsFragment) mFragment).isMapShow;
        }
        if (isMapShow) {
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.rlRootView.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = (int) mContext.getResources().getDimension(R.dimen._120sdp);
            holder.rlRootView.setLayoutParams(layoutParams);
        } else {
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.rlRootView.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            holder.rlRootView.setLayoutParams(layoutParams);
        }
        if (productList.get(position).getDealImage() != null && productList.get(position).getDealImage().split(",").length > 0) {
            AppUtils.getInstance().setImages(mContext, productList.get(position).getDealImage().split(",")[0], holder.ivProductPic, 0, R.drawable.ic_placeholder);
        }
        AppUtils.getInstance().setCircularImages(mContext, productList.get(position).getImage(), holder.civUserImage, R.drawable.ic_side_menu_user_placeholder);
        holder.tvUserName.setText(TextUtils.concat(productList.get(position).getFirstName() + " " + productList.get(position).getLastName()));
        holder.tvDetails.setText(productList.get(position).getName());
        holder.llBottomView.setBackgroundColor(AppUtils.getInstance().getProductRandomColor(mContext, position));
        holder.ivServiceType.setImageResource(productList.get(position).getProductType()
                .equals(Constants.NetworkConstant.PRODUCT) ? R.drawable.ic_home_cards_bag : R.drawable.ic_home_buddy_service_selected);
        holder.ivLike.setImageResource(productList.get(position).getIsBookmark().equals("1") ? R.drawable.ic_home_cards_bookmark_filledf : R.drawable.ic_home_cards_bookmark_unfilledf);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_product_pic)
        ImageView ivProductPic;
        @BindView(R.id.iv_like)
        ImageView ivLike;
        @BindView(R.id.tv_details)
        CustomTextView tvDetails;
        @BindView(R.id.civ_user_image)
        CircleImageView civUserImage;
        @BindView(R.id.fl_user_pic)
        FrameLayout flUserPic;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.iv_service_type)
        ImageView ivServiceType;
        @BindView(R.id.ll_bottom_view)
        LinearLayout llBottomView;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;

        ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick({R.id.iv_like, R.id.civ_user_image, R.id.rl_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_like:
                    recyclerCallBack.onClick(getAdapterPosition(), ivLike);
                    break;
                case R.id.civ_user_image:
                    recyclerCallBack.onClick(getAdapterPosition(), civUserImage);
                    break;
                case R.id.rl_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), ivProductPic);
                    break;
            }
        }
    }
}
