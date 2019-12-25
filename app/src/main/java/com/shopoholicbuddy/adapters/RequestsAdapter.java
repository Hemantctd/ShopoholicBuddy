package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.requestresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsHolder> {
    private final RecyclerCallBack recyclerCallBack;
    private Context context;
    private List<Result> requestsList;

    public RequestsAdapter(AppCompatActivity mActivity, List<Result> requestsList, RecyclerCallBack recyclerCallBack) {
        context = mActivity;
        this.requestsList = requestsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public RequestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_requests, parent, false);
        return new RequestsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsHolder holder, int position) {
        holder.tvName.setText(requestsList.get(position).getShopperName());
        holder.tvDealName.setText(requestsList.get(position).getDealName());
        holder.tvQuantity.setText(TextUtils.concat(context.getString(R.string.txt_quantity) + " "
                + requestsList.get(position).getQuantity()));
        String address = requestsList.get(position).getDileveryAddress();
        holder.tvDeliveryTo.setText(address==null || address.equals("") ? context.getString(R.string.na) : address);
        holder.tvTime.setText(AppUtils.getInstance().formatDate(requestsList.get(position).getCreateDate(),
                "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy"));

        if (position % 2 == 0) {
            holder.rlRequest.setBackgroundResource(R.color.colorWhiteTransparent);
        } else {
            holder.rlRequest.setBackgroundResource(android.R.color.transparent);
        }
        if (requestsList.get(position).getOrderId() != null && !requestsList.get(position).getOrderId().equals("0")) {
            holder.tvLabel.setText(TextUtils.concat(context.getString(R.string.new_delivery_request) +
                    (requestsList.get(position).getDileveryDate() != null && !requestsList.get(position).getDileveryDate().equals("") ? "\n"
                            + context.getString(R.string.estimate_delivery_date) + " "
                            + AppUtils.getInstance().formatDate(requestsList.get(position).getDileveryDate(),
                            Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT) : "")));
            holder.tvPrice.setText(TextUtils.concat(context.getString(R.string.txt_price) + " "
                    + requestsList.get(position).getCurrencySymbol() + requestsList.get(position).getActualAmount()));
            holder.tvDealName.setVisibility(View.VISIBLE);
            if (requestsList.get(position).getProductType().equals("1")) {
                holder.tvQuantity.setVisibility(View.VISIBLE);
                holder.tvDeliveryCharges.setVisibility(View.VISIBLE);
                if (requestsList.get(position).getHomeDelivery().equals("2") && !requestsList.get(position).getIsShared().equals("1")) {
                    holder.tvDeliveryCharges.setText(TextUtils.concat(context.getString(R.string.expected_delivery_charges) + " : "
                            + requestsList.get(position).getCurrencySymbol() + requestsList.get(position).getShopperDeliveryCharge()));
                }else {
                    holder.tvDeliveryCharges.setText(TextUtils.concat(context.getString(R.string.delivery_charges) + " : "
                            + requestsList.get(position).getCurrencySymbol() + requestsList.get(position).getDeliveryCharge()));
                }
            }else {
                holder.tvQuantity.setVisibility(View.GONE);
                holder.tvDeliveryCharges.setVisibility(View.GONE);
            }
        }
        if (requestsList.get(position).getHuntId() != null && !requestsList.get(position).getHuntId().equals("0")) {
            holder.tvLabel.setText(context.getString(R.string.new_hunt_request));
            holder.tvPrice.setText(TextUtils.concat(context.getString(R.string.txt_price) + " "
                    + requestsList.get(position).getCurrencySymbol() + requestsList.get(position).getPriceStart()
                    + (requestsList.get(position).getProductType().equals("1") ?
                    (" - " + requestsList.get(position).getCurrencySymbol() + requestsList.get(position).getPriceEnd()) : "")));
            holder.tvDealName.setVisibility(View.GONE);
            holder.tvDeliveryCharges.setVisibility(View.GONE);
            if (requestsList.get(position).getProductType().equals("1")) {
                holder.tvQuantity.setVisibility(View.VISIBLE);
            }else {
                holder.tvQuantity.setVisibility(View.GONE);
            }
        }
        if (requestsList.get(position).getIsRead().equals("1")) {
            holder.ivOrderIcon.setVisibility(View.INVISIBLE);
        }else {
            holder.ivOrderIcon.setVisibility(View.VISIBLE);
        }
        if (requestsList.get(position).getStatus().equals("2") || requestsList.get(position).getIsHuntClose().equals("1")){
            holder.tvClosed.setVisibility(View.VISIBLE);
        }else {
            holder.tvClosed.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }


    public class RequestsHolder extends ViewHolder {
        @BindView(R.id.tv_label)
        CustomTextView tvLabel;
        @BindView(R.id.tv_time)
        CustomTextView tvTime;
        @BindView(R.id.iv_icon)
        ImageView ivOrderIcon;
        @BindView(R.id.tv_name)
        CustomTextView tvName;
        @BindView(R.id.tv_deal_name)
        CustomTextView tvDealName;
        @BindView(R.id.tv_price)
        CustomTextView tvPrice;
        @BindView(R.id.tv_quantity)
        CustomTextView tvQuantity;
        @BindView(R.id.tv_delivery_charges)
        CustomTextView tvDeliveryCharges;
        @BindView(R.id.tv_delivery_to)
        CustomTextView tvDeliveryTo;
        @BindView(R.id.rl_request)
        RelativeLayout rlRequest;
        @BindView(R.id.tv_closed)
        CustomTextView tvClosed;

        public RequestsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_request)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), rlRequest);

        }

    }
}
