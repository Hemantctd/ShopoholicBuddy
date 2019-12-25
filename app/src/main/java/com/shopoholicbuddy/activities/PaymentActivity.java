package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.CardAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.orderresponse.OrderResponse;
import com.shopoholicbuddy.models.productservicedetailsresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class PaymentActivity extends BaseActivity implements NetworkListener {
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.rv_card_payment)
    RecyclerView rvCardPayment;
    @BindView(R.id.tv_add_new_card)
    CustomTextView tvAddNewCard;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_product_price)
    CustomTextView tvProductPrice;
    private CardAdapter cardAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading;
    private Result productDetails;
    private com.shopoholicbuddy.models.deliveryaddressresponse.Result deliveryAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        initVariables();
        setAdapter();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        btnAction.setText(getString(R.string.request_for_cod));
        tvTitle.setText(getString(R.string.payment));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        cardAdapter = new CardAdapter(this, (position, view) -> {

        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(0, -1);
        if (getIntent() != null && getIntent().getExtras() != null) {
            productDetails = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            deliveryAddress = (com.shopoholicbuddy.models.deliveryaddressresponse.Result) getIntent().getExtras()
                    .getSerializable(Constants.IntentConstant.DELIVERY_ADDRESS);
            String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
            tvProductPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getSellingPrice()) * Integer.parseInt(productDetails.getQuantity()))));
        }
    }


    /**
     * method to set adapter in views
     */
    private void setAdapter() {
        rvCardPayment.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvCardPayment);
        rvCardPayment.setAdapter(cardAdapter);
    }

    @OnClick({R.id.iv_menu, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                finish();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (!isLoading) hitBuyProductServiceAPi();
                }
                break;
        }
    }


    /**
     * Method to hit the signup api
     */
    private void hitBuyProductServiceAPi() {
        AppUtils.getInstance().setButtonLoaderAnimation(PaymentActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, productDetails.getQuantity());
        params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, productDetails.getSellingPrice());
        params.put(Constants.NetworkConstant.PARAM_DELIVERED_BY, productDetails.getUserType());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_ADDRESS, deliveryAddress.getAddress());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, deliveryAddress.getLatitude());
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, deliveryAddress.getLongitude());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, productDetails.getDiscount());
        Call<ResponseBody> call = apiInterface.hitCreateOrderApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SIGN_UP);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SIGN_UP:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        OrderResponse orderResponse = new Gson().fromJson(response, OrderResponse.class);
                        AppUtils.getInstance().showToast(this, orderResponse.getMsg());
                        Intent intent = new Intent(this, QRCodeActivity.class);
                        intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, orderResponse.getResult());
                        startActivity(intent);

                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);

    }
}
