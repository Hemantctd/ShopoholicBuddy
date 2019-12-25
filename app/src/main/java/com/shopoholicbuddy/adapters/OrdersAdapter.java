package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.orderlistdetailsresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.BuddyHolder> {

    private Context mContext;
    private List<Result> ordersList;
    private RecyclerCallBack recyclerCallBack;

    public OrdersAdapter(Context mContext, List<Result> ordersList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.ordersList = ordersList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_orders, parent, false);
        return new BuddyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuddyHolder holder, int position) {
        holder.tvShopperOrderId.setText(ordersList.get(position).getOrderNumber());
        if (!ordersList.get(position).getDealName().equals("")) {
            holder.labelDealName.setVisibility(View.VISIBLE);
            holder.tvDealName.setVisibility(View.VISIBLE);
            holder.tvDealName.setText(ordersList.get(position).getDealName());
        } else {
            holder.labelDealName.setVisibility(View.GONE);
            holder.tvDealName.setVisibility(View.GONE);
        }
        holder.tvShoperName.setText(TextUtils.concat(ordersList.get(position).getFirstName() + " " + ordersList.get(position).getLastName()));
        holder.tvShopperAddress.setText(ordersList.get(position).getDileveryAddress());

        if (ordersList.get(position).getHuntId() != null && !ordersList.get(position).getHuntId().equals("0")) {
            holder.tvPrice.setText(TextUtils.concat(ordersList.get(position).getCurrencySymbol() + ordersList.get(position).getPriceStart()
                    + (ordersList.get(position).getProductType().equals("1") ?
                    (" - " + ordersList.get(position).getCurrencySymbol() + ordersList.get(position).getPriceEnd()) : "")));
        }else {
            holder.tvPrice.setText(TextUtils.concat(ordersList.get(position).getCurrencySymbol() + ordersList.get(position).getActualAmount()));
        }

        if (ordersList.get(position).getProductType().equals("1")) {
            holder.labelQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            if (!ordersList.get(position).getDealId().equals("0") && !ordersList.get(position).getDeliveryCharge().equals("0")) {
                holder.labelDeliveryCharges.setVisibility(View.VISIBLE);
                holder.tvDeliveryCharges.setVisibility(View.VISIBLE);
            }else {
                holder.labelDeliveryCharges.setVisibility(View.GONE);
                holder.tvDeliveryCharges.setVisibility(View.GONE);
            }
            holder.tvQuantity.setText(ordersList.get(position).getQuantity());
            holder.tvDeliveryCharges.setText(TextUtils.concat(ordersList.get(position).getCurrencySymbol() + ordersList.get(position).getDeliveryCharge()));
        }else {
            holder.labelQuantity.setVisibility(View.GONE);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.labelDeliveryCharges.setVisibility(View.GONE);
            holder.tvDeliveryCharges.setVisibility(View.GONE);
        }

        switch (ordersList.get(position).getOrderStatus()) {
            case "1":
                holder.tvDealStatus.setText(R.string.status_pending);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.ivOrderIcon.setImageResource(R.color.colorOrange);
                break;
            case "2":
                holder.tvDealStatus.setText(R.string.status_confirm);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.ivOrderIcon.setImageResource(R.color.colorOrange);
                break;
            case "3":
                holder.tvDealStatus.setText(R.string.status_shipped);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.ivOrderIcon.setImageResource(R.color.colorOrange);
                break;
            case "4":
                holder.tvDealStatus.setText(R.string.status_out_for_delivery);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.ivOrderIcon.setImageResource(R.color.colorOrange);
                break;
            case "5":
                holder.tvDealStatus.setText(R.string.status_delivered);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                holder.ivOrderIcon.setImageResource(R.color.colorGreen);
                break;
            case "6":
                holder.tvDealStatus.setText(R.string.status_cancel);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                holder.ivOrderIcon.setImageResource(R.color.colorRed);
                break;
            case "7":
                holder.tvDealStatus.setText(R.string.status_rejected);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                holder.ivOrderIcon.setImageResource(R.color.colorRed);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    class BuddyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        CircleImageView ivOrderIcon;
        @BindView(R.id.label_shopper_order)
        CustomTextView labelShopperOrder;
        @BindView(R.id.tv_order_status)
        CustomTextView tvDealStatus;
        @BindView(R.id.tv_order_id)
        CustomTextView tvShopperOrderId;
        @BindView(R.id.label_deal_name)
        CustomTextView labelDealName;
        @BindView(R.id.tv_deal_name)
        CustomTextView tvDealName;
        @BindView(R.id.label_price)
        CustomTextView labelPrice;
        @BindView(R.id.tv_price)
        CustomTextView tvPrice;
        @BindView(R.id.label_quantity)
        CustomTextView labelQuantity;
        @BindView(R.id.tv_quantity)
        CustomTextView tvQuantity;
        @BindView(R.id.label_delivery_charges)
        CustomTextView labelDeliveryCharges;
        @BindView(R.id.tv_delivery_charges)
        CustomTextView tvDeliveryCharges;
        @BindView(R.id.tv_deliver_to)
        CustomTextView tvDeliverTo;
        @BindView(R.id.tv_shoper_name)
        CustomTextView tvShoperName;
        @BindView(R.id.tv_shopper_address)
        CustomTextView tvShopperAddress;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;

        BuddyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.rl_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), rlRootView);
                    break;
            }
        }
    }
}
