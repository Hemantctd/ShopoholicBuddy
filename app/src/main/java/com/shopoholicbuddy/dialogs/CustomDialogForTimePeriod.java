package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.UserStatusDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForTimePeriod extends Dialog {

    @BindView(R.id.tv_active)
    CustomTextView tvWeekly;
    @BindView(R.id.tv_away)
    CustomTextView tvMonthly;
    @BindView(R.id.tv_do_not_disturb)
    CustomTextView tvYearly;
    @BindView(R.id.tv_offline)
    CustomTextView tvOffline;
    private Context mContext;
    private UserStatusDialogCallback dialogCallback;

    public CustomDialogForTimePeriod(Context mContext, UserStatusDialogCallback dialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.dialogCallback = dialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_status);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
        }
        tvOffline.setVisibility(View.GONE);
        tvWeekly.setText(mContext.getString(R.string.last_week));
        tvMonthly.setText(mContext.getString(R.string.last_month));
        tvYearly.setText(mContext.getString(R.string.last_year));

    }

    @OnClick({R.id.tv_active, R.id.tv_away, R.id.tv_do_not_disturb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_active:
                dialogCallback.onSelect(tvWeekly.getText().toString(), 1);
                break;
            case R.id.tv_away:
                dialogCallback.onSelect(tvMonthly.getText().toString(), 2);
                break;
            case R.id.tv_do_not_disturb:
                dialogCallback.onSelect(tvYearly.getText().toString(), 3);
                break;
        }
        dismiss();
    }
}
