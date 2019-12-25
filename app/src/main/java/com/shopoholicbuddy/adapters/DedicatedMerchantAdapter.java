package com.shopoholicbuddy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.customviews.CustomTypefaceSpan;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.dedicatedresponse.Result;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DedicatedMerchantAdapter extends RecyclerView.Adapter<DedicatedMerchantAdapter.MerchantHolder> {

    private Context context;
    private List<Result> merchantList;
    private boolean isShopper;
    private RecyclerCallBack recyclerCallBack;

    public DedicatedMerchantAdapter(Context context, List<Result> merchantList, RecyclerCallBack recyclerCallBack) {
        this.context = context;
        this.merchantList = merchantList;
        this.recyclerCallBack = recyclerCallBack;

    }

    @NonNull
    @Override
    public MerchantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dedicated_merchant, parent, false);
        return new MerchantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantHolder holder, int position) {
        AppUtils.getInstance().setCircularImages(context, merchantList.get(position).getImage(), holder.ivBuddyImage, R.drawable.ic_side_menu_user_placeholder);
        String buddyName = merchantList.get(position).getFirstName() + " " + merchantList.get(position).getLastName();
        holder.tvBuddyName.setText(TextUtils.concat(buddyName.toUpperCase() + " " + context.getString(R.string.request_dedicated)));
        holder.tvPhoneNo.setText(TextUtils.concat(context.getString(R.string.txt_phone_no) + " " + (merchantList.get(position).getMobileNo() == null ?
                context.getString(R.string.na) : merchantList.get(position).getCountryId() + merchantList.get(position).getMobileNo())));
        holder.tvEmail.setText(TextUtils.concat(context.getString(R.string.txt_email_id) + " " + (merchantList.get(position).getEmail() == null ?
                context.getString(R.string.na) : merchantList.get(position).getEmail())));
        holder.tvCommission.setText(TextUtils.concat(context.getString(R.string.txt_commission) + " " + merchantList.get(position).getBuddyCommisionPercentage() + "%"));
        holder.cbTerms.setChecked(false);
        setSpannableString(holder, position);

        holder.btnAccept.setOnClickListener(v -> {
            if (holder.cbTerms.isChecked()) {
                recyclerCallBack.onClick(position, holder.btnAccept);
            }else {
                AppUtils.getInstance().showToast(context, context.getString(R.string.accept_terms));
            }
        });
        holder.btnReject.setOnClickListener(v -> {
            recyclerCallBack.onClick(position, holder.btnReject);
        });

    }

    /**
     * function to set spannable string
     * @param holder
     * @param position
     */
    private void setSpannableString(MerchantHolder holder, int position) {
        SpannableString ss = new SpannableString(context.getString(R.string.dedicated_terms));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                recyclerCallBack.onClick(position, holder.tvTermsText);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        int start, end;
        switch (AppSharedPreference.getInstance().getString(context, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE)) {
            case Constants.AppConstant.CHINES_TRAD:
            case Constants.AppConstant.CHINES_SIMPLE:
                start = 27;
                end = ss.length();
                break;
            default:
                start = 27;
                end = ss.length();
        }
        ss.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(context.getAssets(), context.getString(R.string.orkney_bold))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.white)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvTermsText.setText(ss);
        holder.tvTermsText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvTermsText.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return merchantList.size();
    }

    public class MerchantHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_buddy_image)
        CircleImageView ivBuddyImage;
        @BindView(R.id.tv_buddy_name)
        CustomTextView tvBuddyName;
        @BindView(R.id.tv_phone_no)
        CustomTextView tvPhoneNo;
        @BindView(R.id.tv_email)
        CustomTextView tvEmail;
        @BindView(R.id.tv_commission)
        CustomTextView tvCommission;
        @BindView(R.id.cb_terms)
        CheckBox cbTerms;
        @BindView(R.id.tv_terms_text)
        CustomTextView tvTermsText;
        @BindView(R.id.btn_accept)
        CustomTextView btnAccept;
        @BindView(R.id.btn_reject)
        CustomTextView btnReject;
        @BindView(R.id.rl_buddy_assigned)
        RelativeLayout rlBuddyAssigned;

        public MerchantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
