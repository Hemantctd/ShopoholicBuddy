package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.utils.AppUtils;


public class Main2Activity extends AppCompatActivity {
    ImageView ivBack;
    RelativeLayout btnAction;
    CustomButton btn_next;
   CustomEditText et_DeactivationDate,et_onBoardingDate,et_special_instructions,et_remarks,et_team_member,et_team,et_designation,et_personal_name,et_merchantType,et_website;
   CustomTextView txt_Heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        et_DeactivationDate=(CustomEditText)findViewById(R.id.et_DeactivationDate);
        et_onBoardingDate=(CustomEditText)findViewById(R.id.et_onBoardingDate);
        et_special_instructions=(CustomEditText)findViewById(R.id.et_special_instructions);
        et_remarks=(CustomEditText)findViewById(R.id.et_remarks);
        et_team_member=(CustomEditText)findViewById(R.id.et_team_member);
        et_team=(CustomEditText)findViewById(R.id.et_team);
        et_designation=(CustomEditText)findViewById(R.id.et_designation);
        et_personal_name=(CustomEditText)findViewById(R.id.et_personal_name);
        et_merchantType=(CustomEditText)findViewById(R.id.et_merchantType);
        et_website=(CustomEditText)findViewById(R.id.et_website);
        txt_Heading=(CustomTextView)findViewById(R.id.txt_Heading);


        ivBack =  findViewById(R.id.iv_back);
        AppUtils.getInstance().hideKeyboard(this);

        btnAction =  findViewById(R.id.btn_action);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main2Activity.this, Main4Activity.class));

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
                startActivity(new Intent(Main2Activity.this, Main4Activity.class));
            }
        });

    }




    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
