package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.ProductServicePreviewActivity;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.fragments.PostedByMeFragment;
import com.shopoholicbuddy.models.productdealsresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostedByMeAdapter extends RecyclerView.Adapter<PostedByMeAdapter.PostedByMeViewHolder> {


    private final PostedByMeFragment postedByMeFragment;

    private AppCompatActivity mContext;
    private int i = 0;
    private List<Result> productList;

    public PostedByMeAdapter(AppCompatActivity mContext, List<Result> productList, PostedByMeFragment postedByMeFragment) {
        this.mContext = mContext;
        this.productList = productList;
        this.postedByMeFragment = postedByMeFragment;
    }

    @NonNull
    @Override
    public PostedByMeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new PostedByMeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_posted, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostedByMeViewHolder holder, int position) {
        if (productList.get(position).getProductType().equals("1")) {
            holder.tvDiscount.setText(TextUtils.concat(productList.get(position).getDiscount() + mContext.getString(R.string.off_on) + " " + productList.get(position).getName()));
        }else {
            holder.tvDiscount.setText(productList.get(position).getName());
        }
        holder.tvPostedDate.setText(AppUtils.getInstance().formatDate(productList.get(position).getPublishDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        holder.tvInstructions.setText(productList.get(position).getDescription());
        if (productList.get(position).getDealImage().equals("")){
            holder.tvNoOfItems.setText(TextUtils.concat("0 " + mContext.getString(R.string.txt_images)));
            holder.ivProductImage.setImageResource(R.drawable.ic_placeholder);
        }else {
            holder.tvNoOfItems.setText(TextUtils.concat(productList.get(position).getDealImage().split(",").length + " " + mContext.getString(R.string.txt_images)));
            if (productList.get(position).getDealImage() != null && productList.get(position).getDealImage().split(",").length > 0) {
                AppUtils.getInstance().setImages(mContext, productList.get(position).getDealImage().split(",")[0], holder.ivProductImage, 0, R.drawable.ic_placeholder);
            }
        }
        holder.rlPostRow.setBackgroundColor(AppUtils.getInstance().getProductBackgroundColor(mContext, position % 5));

        holder.ivMenu.setOnClickListener(v -> {
            // showPopupMenu(holder.mIvMenu,position);
            holder.ivMenu.setVisibility(View.INVISIBLE);
            showPopupWindow(holder.ivMenu, holder.getAdapterPosition(), holder.rlPostRow);
        });
        holder.rlPostRow.setOnClickListener(v -> {
            String dealId = productList.get(holder.getAdapterPosition()).getId();
            Intent intent = new Intent(mContext, ProductServicePreviewActivity.class);
            intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
            intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.POSTED_BY_ME);
            if (!productList.get(holder.getAdapterPosition()).getDealImage().equals(""))
                intent.putExtra(Constants.IntentConstant.DEAL_IMAGE, productList.get(holder.getAdapterPosition()).getDealImage().split(",")[0]);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(mContext, holder.ivProductImage, ViewCompat.getTransitionName(holder.ivProductImage));
            mContext.startActivity(intent, options.toBundle());
        });
        if (!productList.get(position).getOrderCount().equals("0")){
            holder.ivMenu.setVisibility(View.GONE);
        }else {
            holder.ivMenu.setVisibility(View.VISIBLE);
        }
        holder.tvExpire.setVisibility(productList.get(position).getIsExpire().equals("1") ? View.VISIBLE : View.GONE);
    }


    /**
     * method toshow te popup window
     * @param mIvImage
     * @param position
     * @param mRlPost
     */
    private void showPopupWindow(final ImageView mIvImage, final int position, final RelativeLayout mRlPost) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_row_appointment_option, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, android.R.color.transparent));
        popupWindow.setOnDismissListener(() -> mIvImage.setVisibility(View.VISIBLE));

//        popupWindow.showAsDropDown(mRlPost, mRlPost.getWidth(), -(mRlPost.getHeight()));

        TextView tvEdit = popupView.findViewById(R.id.tv_edit);
        TextView tvSold = popupView.findViewById(R.id.tv_sold_out);
        tvSold.setText(mContext.getResources().getString(R.string.delete));
        tvEdit.setOnClickListener(v -> {
            postedByMeFragment.onPopupItemClick(productList.get(position), position, 1);
            popupWindow.dismiss();
        });
        tvSold.setOnClickListener(v -> {
            postedByMeFragment.onPopupItemClick(productList.get(position), position, 2);
            popupWindow.dismiss();

        });
        int location[] = new int[2];
        // Get the View's(the one that was clicked in the Fragment) location
        mIvImage.getLocationOnScreen(location);

        float marginX = mContext.getResources().getDimension(R.dimen._70sdp);
        float marginY = mContext.getResources().getDimension(R.dimen._1sdp);


        int locationX = (int) (location[0] - marginX);
        int locationY = (int) (location[1] + marginY);
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(mIvImage, Gravity.NO_GRAVITY, locationX, locationY);
        popupWindow.showAsDropDown(mIvImage);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class PostedByMeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_menu)
        ImageView ivMenu;
        @BindView(R.id.iv_product_pic)
        ImageView ivProductImage;
        @BindView(R.id.tv_expire)
        CustomTextView tvExpire;
        @BindView(R.id.tv_no_of_items)
        TextView tvNoOfItems;
        @BindView(R.id.ll_item)
        LinearLayout llItem;
        @BindView(R.id.tv_discount)
        TextView tvDiscount;
        @BindView(R.id.tv_instructions)
        TextView tvInstructions;
        @BindView(R.id.tv_posted_on)
        TextView tvPostedOn;
        @BindView(R.id.tv_posted_date)
        TextView tvPostedDate;
        @BindView(R.id.rl_post_row)
        RelativeLayout rlPostRow;

        PostedByMeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
