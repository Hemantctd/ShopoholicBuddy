package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.productdealsresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class SpecializedSkillAdapter extends RecyclerView.Adapter<SpecializedSkillAdapter.SpecializedSkillHolder> {

    private Context mContext;
    private RecyclerCallBack recyclerCallBack;
    private List<Result> productList;

    public SpecializedSkillAdapter(Context mContext,List<Result> productList ,RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.productList= productList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public SpecializedSkillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_specialized_skills, parent, false);
        return new SpecializedSkillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecializedSkillHolder holder, int position) {
        holder.tvBuddyLanguage.setText(productList.get(position).getName());
        holder.tvBuddySkills.setText(productList.get(position).getDescription());
        holder.tvBuddyTime.setText(TextUtils.concat(
                AppUtils.getInstance().formatDate(productList.get(position).getDealStartTime(), SERVICE_DATE_FORMAT, DATE_FORMAT) + " " + mContext.getString(R.string.to) + " "
                        + AppUtils.getInstance().formatDate(productList.get(position).getDealEndTime(), SERVICE_DATE_FORMAT, DATE_FORMAT)));
        holder.ivChatDots.setVisibility(productList.get(position).getOrderCount().equals("0") ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class SpecializedSkillHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_buddy_language)
        CustomTextView tvBuddyLanguage;
        @BindView(R.id.iv_chat_dots)
        ImageView ivChatDots;
        @BindView(R.id.tv_buddy_skills)
        CustomTextView tvBuddySkills;
        @BindView(R.id.tv_buddy_time)
        CustomTextView tvBuddyTime;
        SpecializedSkillHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        @OnClick(R.id.iv_chat_dots)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(),ivChatDots);
        }

    }
}
