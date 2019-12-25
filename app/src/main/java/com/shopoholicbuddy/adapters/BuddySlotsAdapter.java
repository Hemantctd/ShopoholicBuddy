package com.shopoholicbuddy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.ScheduleActivity;
import com.shopoholicbuddy.activities.TimeSlotsActivity;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.buddyscheduleresponse.Result;
import com.shopoholicbuddy.models.orderlistdetailsresponse.SlotArr;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;


import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuddySlotsAdapter extends RecyclerView.Adapter<BuddySlotsAdapter.BuddySlotsHolder> {

    private Context mContext;
    private List<Result> buddyScheduleList;
    private RecyclerCallBack recyclerCallBack;

    public BuddySlotsAdapter(Context mContext, List<Result> buddyScheduleList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.buddyScheduleList = buddyScheduleList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddySlotsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buddy_slots, parent, false);
        return new BuddySlotsHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull BuddySlotsHolder holder, int position) {
        if (mContext instanceof ScheduleActivity) {
            String startTime = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
            String endTime = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
            String startDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
            String endDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
            if (buddyScheduleList.get(position).isAllDay()) {
//                holder.textView.setText(TextUtils.concat(startDate + " - " + endDate + " (" +  (mContext.getString(R.string.all_day_available)) + ")"));
                holder.textView.setText(mContext.getString(R.string.all_day_available));
            }else {
//                holder.textView.setText(TextUtils.concat(startDate + " " + startTime + " - " + endDate + " " + endTime));
                holder.textView.setText(TextUtils.concat(startTime + " - " + endTime));
            }
            holder.tvEdit.setVisibility(View.GONE);
            holder.ivOptions.setVisibility(buddyScheduleList.get(position).getOrderCount().equals("0") ? View.VISIBLE : View.GONE);
        }else if (mContext instanceof TimeSlotsActivity) {
            String startTime = buddyScheduleList.get(position).getSlotStartTime();
            String endTime = buddyScheduleList.get(position).getSlotEndTime();
            String startDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
            String endDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
            if (buddyScheduleList.get(position).isAllDay()) {
                holder.textView.setText(mContext.getString(R.string.all_day_available));
            }else {
                holder.textView.setText(TextUtils.concat(startTime + " - " + endTime));
            }
            holder.tvEdit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return buddyScheduleList.size();
    }


    class BuddySlotsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view)
        CustomTextView textView;
        @BindView(R.id.iv_options)
        ImageView ivOptions;
        @BindView(R.id.tv_edit)
        TextView tvEdit;

        BuddySlotsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.iv_options, R.id.text_view, R.id.tv_edit})
        public void onViewClicked(View view) {
            switch (view.getId()){
                case R.id.text_view:
                    tvEdit.setVisibility(View.GONE);
                    break;
                case R.id.iv_options:
                    tvEdit.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_edit:
                    tvEdit.setVisibility(View.GONE);
                    recyclerCallBack.onClick(getAdapterPosition(), tvEdit);
                    break;
            }
        }

    }
}
