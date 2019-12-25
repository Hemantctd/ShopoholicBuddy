package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 30-Mar-18.
 */

public class CardDetailsActivity extends BaseActivity {
    @BindView(R.id.et_account_number)
    CustomEditText etAccountNumber;
    @BindView(R.id.view_account_number)
    View viewAccountNumber;
    @BindView(R.id.et_card_number)
    CustomEditText etCardNumber;
    @BindView(R.id.view_card_number)
    View viewCardNumber;
    @BindView(R.id.tv_month)
    CustomTextView tvMonth;
    @BindView(R.id.view_month)
    View viewMonth;
    @BindView(R.id.tv_year)
    CustomTextView tvYear;
    @BindView(R.id.view_year)
    View viewYear;
    @BindView(R.id.et_cvv)
    CustomEditText etCvv;
    @BindView(R.id.view_cvv)
    View viewCvv;
    @BindView(R.id.btn_save_and_next)
    CustomButton btnSaveAndNext;
    @BindView(R.id.progress_loader)
    ProgressBar progressLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.tv_month, R.id.tv_year, R.id.btn_save_and_next, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_month:
                break;
            case R.id.tv_year:
                break;
            case R.id.btn_save_and_next:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.tv_skip:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }
}
