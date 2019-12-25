package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.MessageDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialogForSaveDeal extends Dialog {

    @BindView(R.id.btn_submit)
    CustomButton btnSubmit;
    @BindView(R.id.tv_dialogue_cancel)
    CustomTextView tvDialogueCancel;
    private Context context;
   private MessageDialogCallback messageDialogCallback;
    public CustomDialogForSaveDeal(@NonNull Context context, MessageDialogCallback messageDialogCallback) {
        super(context);
        this.context = context;
        this.messageDialogCallback=messageDialogCallback;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogue_save_deal);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
    }

    @OnClick({R.id.btn_submit, R.id.tv_dialogue_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                dismiss();
                messageDialogCallback.onSubmitClick();
                break;
            case R.id.tv_dialogue_cancel:
                dismiss();
                break;
        }
    }
}
