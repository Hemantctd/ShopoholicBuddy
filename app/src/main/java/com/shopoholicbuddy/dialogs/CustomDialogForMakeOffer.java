package com.shopoholicbuddy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.MakeOfferDialogCallback;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


/**
 * Dialog for image selection
 */

public class CustomDialogForMakeOffer extends Dialog {

    private final int status;
    private final String currency;
    private final String price;
    private final String deliveryDate;
    @BindView(R.id.tv_message_title)
    TextView tvMessageTitle;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private Context mContext;
    private MakeOfferDialogCallback makeOfferDialogCallback;

    public CustomDialogForMakeOffer(Context mContext, int status, String currency, String price, String deliveryDate, MakeOfferDialogCallback makeOfferDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.status = status;
        this.currency = currency;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.makeOfferDialogCallback = makeOfferDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_make_offer);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        InputFilter[] filterArray = new InputFilter[1];
        if (status == 1) {
            etMessage.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.GONE);
//            etMessage.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMessage.setMaxLines(1);
            filterArray[0] = new InputFilter.LengthFilter(15);
            tvMessageTitle.setText(TextUtils.concat(mContext.getString(R.string.counter_offer) + "(" + currency + ")"));
        }else if (status == 4) {
            etMessage.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.GONE);
//            etMessage.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMessage.setMaxLines(1);
            filterArray[0] = new InputFilter.LengthFilter(15);
            tvMessageTitle.setText(TextUtils.concat(mContext.getString(R.string.delivery_charges) + "(" + currency + ")"));
            etMessage.setHint(R.string.enter_delivery_charges);
        }else if (status == 2){
            etMessage.setVisibility(View.GONE);
            tvDate.setVisibility(View.VISIBLE);
            etMessage.setInputType(InputType.TYPE_CLASS_TEXT);
            tvMessageTitle.setText(mContext.getString(R.string.enter_delivery_date));
        }else if (status == 5) {
            etMessage.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.GONE);
            etMessage.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMessage.setMaxLines(1);
            filterArray[0] = new InputFilter.LengthFilter(15);
            String message = "";
            message += TextUtils.concat(mContext.getString(R.string.delivery_charges) + "(" + currency + ")");
            message += "\n" + mContext.getString(R.string.max_delivery_charge_is) + " " + currency + price + ". ";
            message += mContext.getString(R.string.expected_delivery_date_is) + " " + AppUtils.getInstance().formatDate(deliveryDate, SERVICE_DATE_FORMAT, DATE_FORMAT) + ".";
            tvMessageTitle.setText(message);
            etMessage.setHint(R.string.enter_delivery_charges);
        }else {
            etMessage.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.GONE);
            etMessage.setHint(R.string.enter_your_text_here);
            etMessage.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            tvMessageTitle.setText(mContext.getString(R.string.why_report_deal));
            etMessage.setMaxLines(3);
            filterArray[0] = new InputFilter.LengthFilter(150);
            btnSubmit.setText(mContext.getString(R.string.report));
        }
        etMessage.setFilters(filterArray);
    }

    @OnClick({R.id.btn_submit, R.id.tv_date, R.id.tv_cancel})
    public void onViewClicked(View view) {
        AppUtils.getInstance().hideKeyboardOfEditText(mContext, etMessage);
        switch (view.getId()) {
            case R.id.btn_submit:
                if (status == 1 || status == 3 || status == 4) {
                    if (etMessage.getText().toString().trim().length() > 0) {
                        dismiss();
//                        String currency = mContext.getString(this.currency.equals("2") ? R.string.rupees : this.currency.equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                        String message = mContext.getString(R.string.i_would_like_to_buy_it_for) + " " + currency + etMessage.getText().toString().trim()
                                + ". " + mContext.getString(R.string.offer_limit);
                        makeOfferDialogCallback.onSelect(message, currency, etMessage.getText().toString().trim(), 0);
                    }else {
                        AppUtils.getInstance().showToast(mContext,
                                mContext.getString(status == 1 ? R.string.please_enter_offer : status == 4 ? R.string.please_enter_delivery_charges : R.string.please_enter_message));
                    }
                } else if (status == 5) {
                    if (etMessage.getText().toString().trim().length() == 0) {

                    }else if (!price.equals("") && Double.parseDouble(price) < Double.parseDouble(etMessage.getText().toString().trim())) {
                        AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.max_delivery_charge_is) + " " + currency + price);
                    }else {
                        makeOfferDialogCallback.onSelect("", currency, etMessage.getText().toString().trim(), 0);
                        dismiss();
                    }
                }else {
                    if (tvDate.getText().toString().trim().length() > 0) {
                        dismiss();
                        makeOfferDialogCallback.onSelect(tvDate.getText().toString().trim(),currency, tvDate.getText().toString().trim(),  0);
                    }else {
                        AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.please_enter_delivery_date));
                    }
                }
                break;
            case R.id.tv_date:
                AppUtils.getInstance().openDatePicker(mContext, tvDate, Calendar.getInstance(), null, tvDate.getText().toString().trim(), 1);
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
