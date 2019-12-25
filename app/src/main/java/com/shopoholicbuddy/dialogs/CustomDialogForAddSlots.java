package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.AddSlotsCallback;

import butterknife.ButterKnife;


/**
 * Dialog for image selection
 */

public class CustomDialogForAddSlots extends Dialog {

    private final int type;
    private Context mContext;
    private AddSlotsCallback addSlotsCallback;

    public CustomDialogForAddSlots(Context mContext, int type, AddSlotsCallback addSlotsCallback) {
        super(mContext);
        this.mContext = mContext;
        this.type = type;
        this.addSlotsCallback = addSlotsCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.row_add_slots);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
    }

}
