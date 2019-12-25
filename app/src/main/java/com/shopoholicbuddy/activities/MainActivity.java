package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForSelectMerchantType;
import com.shopoholicbuddy.interfaces.SelectCountryDialogCallback;
import com.shopoholicbuddy.models.countrymodel.CountryBean;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity{
    CustomButton btn_next;
    CustomEditText et_email,et_fax,et_contactNo,et_pincode,et_city,et_state,et_Address,et_reg_Under,et_reg_business_name,merchantName;
    CustomTextView txtHeading;

    ImageView ivBack;
    RelativeLayout btnAction;
    @BindView(R.id.tv_select_country)
    CustomTextView tvSelectCountry;
    private List<String> allCountriesList;
    private List<String> selectedCountriesList;
    CountryBean countryBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHeading=(CustomTextView)findViewById(R.id.txtHeading);
        et_email=(CustomEditText) findViewById(R.id.et_email);
        et_fax=(CustomEditText) findViewById(R.id.et_fax);
        et_contactNo=(CustomEditText) findViewById(R.id.et_contactNo);
        et_pincode=(CustomEditText) findViewById(R.id.et_pincode);
        et_city=(CustomEditText) findViewById(R.id.et_city);
        et_state=(CustomEditText) findViewById(R.id.et_state);
        et_Address=(CustomEditText) findViewById(R.id.et_Address);
        et_reg_Under=(CustomEditText) findViewById(R.id.et_reg_Under);
        et_reg_business_name=(CustomEditText) findViewById(R.id.et_reg_business_name);
        merchantName=(CustomEditText) findViewById(R.id.merchantName);

        ivBack =  findViewById(R.id.iv_back);
        AppUtils.getInstance().hideKeyboard(this);
//        tvSelectCountry.addTextChangedListener(this);

        btnAction =  findViewById(R.id.btn_action);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));

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
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        });

    }



    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }



}
