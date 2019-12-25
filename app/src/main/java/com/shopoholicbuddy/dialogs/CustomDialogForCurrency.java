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

public class CustomDialogForCurrency extends Dialog {

    private final int type;
    @BindView(R.id.tv_active)
    CustomTextView tvRupees;
    @BindView(R.id.tv_away)
    CustomTextView tvDoller;
    @BindView(R.id.tv_do_not_disturb)
    CustomTextView tvSingapourDoller;
    @BindView(R.id.tv_offline)
    CustomTextView tvOffline;
    @BindView(R.id.view_active)
    View viewActive;
    @BindView(R.id.view_away)
    View viewAway;
    @BindView(R.id.view_dnd)
    View viewDnd;
    private Context mContext;
    private UserStatusDialogCallback dialogCallback;

    public CustomDialogForCurrency(Context mContext, int type, UserStatusDialogCallback dialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.type = type;
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
        if (type == 1) {
            tvOffline.setVisibility(View.GONE);
            viewDnd.setVisibility(View.GONE);
            tvRupees.setText(mContext.getString(R.string.rupees));
            tvDoller.setText(mContext.getString(R.string.dollar));
            tvSingapourDoller.setText(mContext.getString(R.string.singapuri_dollar));
        } else if (type == 2) {
            tvOffline.setVisibility(View.GONE);
            viewDnd.setVisibility(View.GONE);
            tvSingapourDoller.setVisibility(View.GONE);
            viewAway.setVisibility(View.GONE);
            tvRupees.setText(mContext.getString(R.string.fixed));
            tvDoller.setText(mContext.getString(R.string.txt_per_hour));
        }

    }

    @OnClick({R.id.tv_active, R.id.tv_away, R.id.tv_do_not_disturb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_active:
                dialogCallback.onSelect(tvRupees.getText().toString(), 1);
                break;
            case R.id.tv_away:
                dialogCallback.onSelect(tvDoller.getText().toString(), 2);
                break;
            case R.id.tv_do_not_disturb:
                dialogCallback.onSelect(tvSingapourDoller.getText().toString(), 3);
                break;
        }
        dismiss();
    }
}
