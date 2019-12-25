package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.MessageDialogCallback;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialogForConfirmDeal extends Dialog {
    @BindView(R.id.tv_message)
    CustomTextView tvMessage;
    @BindView(R.id.btn_yes_post)
    CustomButton btnYesPost;
    @BindView(R.id.btn_edit_deal)
    CustomButton btnEditDeal;
    @BindView(R.id.tv_dialogue_cancel)
    CustomTextView tvDialogueCancel;
    private Context context;
    private PopupItemDialogCallback dialogCallback;
    private String postedDate;

    public CustomDialogForConfirmDeal(@NonNull Context context, String postedDate, PopupItemDialogCallback dialogCallback) {
        super(context);
        this.context = context;
        this.dialogCallback = dialogCallback;
        this.postedDate= postedDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogue_confirm_deal);
        ButterKnife.bind(this);
        if(getWindow()!=null)
        {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        tvMessage.setText(TextUtils.concat(context.getString(R.string.your_deal_will_be_posted_on) + " " + postedDate + " " + context.getString(R.string.sure_want_to_post_deal)));

    }

    @OnClick({R.id.btn_yes_post, R.id.btn_edit_deal, R.id.tv_dialogue_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_yes_post:
                dismiss();
                dialogCallback.onItemOneClick();
                break;
            case R.id.btn_edit_deal:
//                dismiss();
                dialogCallback.onItemTwoClick();
                break;
            case R.id.tv_dialogue_cancel:
//                dismiss();
                dialogCallback.onItemThreeClick();
                break;
        }
    }
}
