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
import com.shopoholicbuddy.models.earninglistresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyEarningsAdapter extends RecyclerView.Adapter<MyEarningsAdapter.EarningsHolder> {

    private Context mContext;
    private List<Result> myEarningsList;
    private RecyclerCallBack recyclerCallBack;

    public MyEarningsAdapter(Context mContext, List<Result> myEarningsList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.myEarningsList = myEarningsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public EarningsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_my_earnings, parent, false);
        return new EarningsHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EarningsHolder holder, int position) {
//        String currency = mContext.getResources().getString(myEarningsList.get(position).getCurrency().equals("2") ? R.string.rupees
//                : myEarningsList.get(position).getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        String currency = myEarningsList.get(position).getCurrencySymbol();
        holder.tvOrderNumber.setText(myEarningsList.get(position).getOrderNumber());
        holder.tvOrderName.setText(myEarningsList.get(position).getName());
        holder.tvCategory.setText(myEarningsList.get(position).getCatName());
//        holder.tvEarning.setText(TextUtils.concat(currency + myEarningsList.get(position).getTotalEarning()));
        holder.tvEarning.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(myEarningsList.get(position).getTotalEarning()))));
        holder.tvDate.setText(AppUtils.getInstance().formatDate(myEarningsList.get(position).getOrderDate(),
                Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        if(position % 2 ==0)
        {
            holder.rlRootView.setBackgroundResource(R.color.colorWhiteTransparent);
        }
        else
        {
            holder.rlRootView.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return myEarningsList.size();
    }


    class EarningsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        CustomTextView tvDate;
        @BindView(R.id.tv_order_number)
        CustomTextView tvOrderNumber;
        @BindView(R.id.tv_order_name)
        CustomTextView tvOrderName;
        @BindView(R.id.tv_category)
        CustomTextView tvCategory;
        @BindView(R.id.tv_earning)
        CustomTextView tvEarning;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;

        EarningsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
