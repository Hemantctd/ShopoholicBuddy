package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.MessageDialogCallback;
import com.shopoholicbuddy.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialogForPaymentInfo extends Dialog {

    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.btn_yes)
    CustomButton btnYes;
    @BindView(R.id.accept_button_loader)
    View acceptButtonLoader;
    @BindView(R.id.accept_button_dot)
    View acceptButtonDot;
    @BindView(R.id.layout_accept_button_loader)
    FrameLayout layoutAcceptButtonLoader;
    @BindView(R.id.btn_no)
    CustomButton btnNo;
    @BindView(R.id.reject_button_loader)
    View rejectButtonLoader;
    @BindView(R.id.reject_button_dot)
    View rejectButtonDot;
    @BindView(R.id.layout_reject_button_loader)
    FrameLayout layoutRejectButtonLoader;
    @BindView(R.id.ll_accept_reject)
    LinearLayout llAcceptReject;

    private Context context;
    private String message;
    private MessageDialogCallback dialogCallback;
    private int responseCode;

    public CustomDialogForPaymentInfo(@NonNull Context context, String message, int responseCode, MessageDialogCallback dialogCallback) {
        super(context);
        this.context = context;
        this.message = message;
        this.responseCode = responseCode;
        this.dialogCallback = dialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }

        btnYes.setText(context.getString(R.string.pay));
        btnNo.setText(context.getString(R.string.cancel));

        tvMessageTitle.setText(message);

        if (responseCode == 315) {
            layoutRejectButtonLoader.setVisibility(View.VISIBLE);
        } else if (responseCode == 317) {
            layoutRejectButtonLoader.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_yes, R.id.btn_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                dialogCallback.onSubmitClick();
                dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
        }
    }
}
