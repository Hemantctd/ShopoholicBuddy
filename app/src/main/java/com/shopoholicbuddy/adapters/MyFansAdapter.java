package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.myfansresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyFansAdapter extends RecyclerView.Adapter<MyFansAdapter.MyFansHolder> {

    private Context context;
    private List<Result> myFansList;
    private boolean isShopper;
    private RecyclerCallBack recyclerCallBack;

    public MyFansAdapter(Context context, List<Result> myFansList, boolean isShopper, RecyclerCallBack recyclerCallBack) {
        this.context = context;
        this.myFansList = myFansList;
        this.isShopper = isShopper;
        this.recyclerCallBack = recyclerCallBack;

    }

    @NonNull
    @Override
    public MyFansHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shopper_merchant, parent, false);
        return new MyFansHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFansHolder holder, int position) {
        if (isShopper) {
            holder.rbBuddyRating.setVisibility(View.GONE);
        }else {
            holder.rbBuddyRating.setVisibility(View.VISIBLE);
            holder.rbBuddyRating.setCount((int)(Float.parseFloat(myFansList.get(position).getRating()) + 0.5));
        }
        holder.tvBuddyName.setText(TextUtils.concat(myFansList.get(position).getFirstName() + " "
                + myFansList.get(position).getLastName()));
        holder.tvBuddyAddress.setText(myFansList.get(position).getAddress());
        AppUtils.getInstance().setCircularImages(context, myFansList.get(position).getImage(), holder.ivBuddyImage, R.drawable.ic_side_menu_user_placeholder);
        if (myFansList.get(position).getAddress().equals("")){
            holder.tvBuddyAddress.setVisibility(View.GONE);
        }else {
            holder.tvBuddyAddress.setVisibility(View.VISIBLE);
            holder.tvBuddyAddress.setText(myFansList.get(position).getAddress());
        }

    }

    @Override
    public int getItemCount() {
        return myFansList.size();
    }


    public class MyFansHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_buddy_image)
        CircleImageView ivBuddyImage;
        @BindView(R.id.tv_buddy_name)
        CustomTextView tvBuddyName;
        @BindView(R.id.rb_buddy_rating)
        RatingBar rbBuddyRating;
        @BindView(R.id.tv_buddy_address)
        CustomTextView tvBuddyAddress;
        @BindView(R.id.rl_buddy_assigned)
        RelativeLayout rlBuddyAssigned;

        public MyFansHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_buddy_assigned)
        public void onViewClicked() {

            recyclerCallBack.onClick(getAdapterPosition(), rlBuddyAssigned);
        }

    }
}
