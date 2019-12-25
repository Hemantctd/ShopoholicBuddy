package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.MessageDialogCallback;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for error
 */

public class CustomDialogForError extends Dialog {
    private MessageDialogCallback messageDialogCallback;

    public CustomDialogForError(Context mContext, MessageDialogCallback messageDialogCallback) {
        super(mContext);
        this.messageDialogCallback = messageDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_error);
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

    @OnClick({R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
                dismiss();
                messageDialogCallback.onSubmitClick();
                break;
        }
    }
}
