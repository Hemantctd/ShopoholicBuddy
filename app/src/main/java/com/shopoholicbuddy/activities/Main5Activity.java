package com.shopoholicbuddy.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.utils.AppUtils;


public class Main5Activity extends AppCompatActivity {
    ImageView ivBack;
    RelativeLayout btnAction;
    CustomButton btn_Submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        ivBack =  findViewById(R.id.iv_back);
        AppUtils.getInstance().hideKeyboard(this);

        btnAction =  findViewById(R.id.btn_action);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }




    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
