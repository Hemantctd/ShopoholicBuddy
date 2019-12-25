package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.AddressAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholicbuddy.models.deliveryaddressresponse.DeliveryAddressResponse;
import com.shopoholicbuddy.models.productservicedetailsresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class PurchaseSummaryActivity extends BaseActivity implements NetworkListener {

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
    @BindView(R.id.iv_purchase_product)
    ImageView ivPurchaseProduct;
    @BindView(R.id.tv_purchase_product_name)
    CustomTextView tvPurchaseProductName;
    @BindView(R.id.tv_purchase_product_price)
    CustomTextView tvPurchaseProductPrice;
    @BindView(R.id.tv_purchase_product_quantity)
    CustomTextView tvPurchaseProductQuantity;
    @BindView(R.id.et_product_quantity)
    CustomEditText etProductQuantity;
    @BindView(R.id.tv_murchant_Name)
    CustomTextView tvMerchantName;
    @BindView(R.id.tv_add)
    CustomTextView tvAdd;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.rv_address_list)
    RecyclerView rvAddressList;
    private Result productDetails;
    private List<com.shopoholicbuddy.models.deliveryaddressresponse.Result> addressList;
    private AddressAdapter addressAdapter;
    private int previousPosition = 0;
    private int totalQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_summary);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        if (AppUtils.getInstance().isInternetAvailable(this)) hitGetAddressesApi();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        addressList = new ArrayList<>();
        btnAction.setText(getString(R.string.proceed_to_pay));
        tvTitle.setText(getString(R.string.purchase_summary));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        if (getIntent() != null && getIntent().getExtras() != null) {
            productDetails = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            setProductDetailsData();
        }
        addressAdapter = new AddressAdapter(this, addressList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
               addressList.get(previousPosition).setSelected(false);
               addressAdapter.notifyItemChanged(previousPosition);
               addressList.get(position).setSelected(true);
               addressAdapter.notifyItemChanged(position);
               previousPosition = position;
            }

            @Override
            public void onLongClick(int position, View clickedView) {

            }
        });
    }


    /**
     * method to set listener on views
     */
    private void setAdapters() {
        rvAddressList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAddressList.setAdapter(addressAdapter);
    }

    /**
     * method to set product details
     */
    private void setProductDetailsData() {
        totalQuantity = Integer.parseInt(productDetails.getQuantity());
        tvPurchaseProductName.setText(productDetails.getName());
        String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        tvPurchaseProductPrice.setText(TextUtils.concat(currency + productDetails.getSellingPrice()));
        tvPurchaseProductQuantity.setText(TextUtils.concat(getString(R.string.quantity)));
        tvMerchantName.setText(TextUtils.concat(productDetails.getFirstName() + " " + productDetails.getLastName() + " ("
                + getString(productDetails.getUserType().equals("1") ? R.string.merchant : R.string.buddy) + ")"));

        if (productDetails.getDealImages().split(",").length > 0)
            AppUtils.getInstance().setImages(this, productDetails.getDealImages().split(",")[0], ivPurchaseProduct, 0, R.drawable.ic_placeholder);

    }

    @OnClick({R.id.iv_menu, R.id.tv_add, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                finish();
                break;
            case R.id.tv_add:
                startActivityForResult(new Intent(this, EnterAddressActivity.class), Constants.IntentConstant.REQUEST_ADDRESS);
                break;
            case R.id.btn_action:
                if (etProductQuantity.getText().toString().trim().length() == 0){
                    AppUtils.getInstance().showToast(this, getString(R.string.please_enter_quantity));
                } else if (addressList.size() == 0){
                    AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
                } else {
                    productDetails.setQuantity(etProductQuantity.getText().toString().trim());
                    if (Integer.parseInt(productDetails.getQuantity()) <= totalQuantity) {
                        startActivity(new Intent(this, PaymentActivity.class)
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails)
                                .putExtra(Constants.IntentConstant.DELIVERY_ADDRESS, addressList.get(previousPosition)));
                    } else {
                        AppUtils.getInstance().showToast(this, getString(R.string.sorry_we_have) + " "
                                + totalQuantity +  " " + getString(R.string.txt_available));
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_ADDRESS) {
            if (data != null && data.getExtras() != null && data.hasExtra(Constants.IntentConstant.DELIVERY_ADDRESS)) {
                com.shopoholicbuddy.models.deliveryaddressresponse.Result addressResponse =
                        (com.shopoholicbuddy.models.deliveryaddressresponse.Result) data.getExtras().getSerializable(Constants.IntentConstant.DELIVERY_ADDRESS);
                if (addressList.size() > 0) {
                    addressList.get(previousPosition).setSelected(false);
                }
                if (addressList.size() == 3) {
                    addressList.remove(2);
                }
                addressList.add(0, addressResponse);
                addressList.get(0).setSelected(true);
                previousPosition = 0;
                addressAdapter.notifyDataSetChanged();
            }
        }
    }



    /**
     * Method to hit the reset password api
     */
    private void hitGetAddressesApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitGetAddressesApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_GET_ADDRESS);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(PurchaseSummaryActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_GET_ADDRESS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        DeliveryAddressResponse addressResponse = new Gson().fromJson(response, DeliveryAddressResponse.class);
                        addressList.clear();
                        addressList.addAll(addressResponse.getResult());
                        addressList.get(0).setSelected(true);
                        previousPosition = 0;
                        addressAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {

    }
}
