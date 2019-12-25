package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.utils.AppUtils;

public class Main4Activity extends BaseActivity {
    ImageView ivBack;
    RelativeLayout btnAction;
    CustomButton btn_next;
    CustomTextView txt_copy_of_adhaar,txt_copy_of_gstCertificate,txt_copy_of_cancelledCheck,txt_copy_of_pan,txt_particular,txt_4,txt_3,txt_2,txt_1,txt_SlNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        txt_copy_of_adhaar=(CustomTextView)findViewById(R.id.txt_copy_of_adhaar);
        txt_copy_of_gstCertificate=(CustomTextView)findViewById(R.id.txt_copy_of_gstCertificate);
        txt_copy_of_cancelledCheck=(CustomTextView)findViewById(R.id.txt_copy_of_cancelledCheck);
        txt_copy_of_pan=(CustomTextView)findViewById(R.id.txt_copy_of_pan);
        txt_particular=(CustomTextView)findViewById(R.id.txt_particular);
        txt_copy_of_adhaar=(CustomTextView)findViewById(R.id.txt_copy_of_adhaar);
        txt_4=(CustomTextView)findViewById(R.id.txt_4);
        txt_3=(CustomTextView)findViewById(R.id.txt_3);
        txt_2=(CustomTextView)findViewById(R.id.txt_2);
        txt_1=(CustomTextView)findViewById(R.id.txt_1);
        txt_SlNo=(CustomTextView)findViewById(R.id.txt_SlNo);

        ivBack =  findViewById(R.id.iv_back);
        AppUtils.getInstance().hideKeyboard(this);

        btnAction =  findViewById(R.id.btn_action);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main4Activity.this, Main5Activity.class));

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        btn_next=(CustomButton)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Main4Activity.this,Main5Activity.class));
            }
        });

    }




    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
