package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.CountryCodeAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.countrymodel.CountryBean;
import com.shopoholicbuddy.models.deliveryaddressresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class EnterAddressActivity extends BaseActivity implements NetworkListener {

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
    @BindView(R.id.et_first_name)
    CustomEditText etFirstName;
    @BindView(R.id.view_first_name)
    View viewFirstName;
    @BindView(R.id.et_last_name)
    CustomEditText etLastName;
    @BindView(R.id.view_last_name)
    View viewLastName;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.view_address)
    View viewAddress;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.view_country_code)
    View viewCountryCode;
    @BindView(R.id.rl_country_code)
    RelativeLayout rlCountryCode;
    @BindView(R.id.et_phone_no)
    CustomEditText etPhoneNo;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    private boolean isLoading;
    private double latitude;
    private double longitude;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private CountryCodeAdapter countryAdapter;
    private ArrayList<CountryBean> selectedCountriesList;
    private boolean openPlacePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_address);
        ButterKnife.bind(this);
        initVariables();
        setListener();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        selectedCountriesList = new ArrayList<>();
        btnAction.setText(getString(R.string.save));
        tvTitle.setText(getString(R.string.add_address));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);


        countryAdapter = new CountryCodeAdapter(this, selectedCountriesList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setPeekHeight(0);
                        tvCountryCode.setText(selectedCountriesList.get(position).getCountryCode());
                    }
                }, 500);
            }
        });
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        rvCountryCode.setAdapter(countryAdapter);

        etFirstName.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME));
        etLastName.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME));
    }


    @OnClick({R.id.iv_menu, R.id.tv_address, R.id.btn_action, R.id.rl_country_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.rl_country_code:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 500);
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate() && !isLoading) {
                        hitSaveAddressApi();
                    }
                }
                break;
            case R.id.tv_address:
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(this), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * method to set the listener on views
     */
    private void setListener() {
        etFirstName.addTextChangedListener(this);
        etLastName.addTextChangedListener(this);
        tvAddress.addTextChangedListener(this);
        etPhoneNo.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppUtils.getInstance().changeSeparatorViewColor(etFirstName, viewFirstName);
        AppUtils.getInstance().changeSeparatorViewColor(etLastName, viewLastName);
        AppUtils.getInstance().changeSeparatorViewColor(tvAddress, viewAddress);
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNo, viewPhoneNo);
        AppUtils.getInstance().changeSeparatorViewColor(tvCountryCode, viewCountryCode);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etFirstName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_first_name));
            return false;
        } else if (etFirstName.getText().toString().trim().length() < 3) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_3_char_first_name));
            return false;
        } else if (!etFirstName.getText().toString().matches("^[a-zA-Z]+[a-zA-Z0-9 ]*")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_first_name));
            return false;
        } else if (tvAddress.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() < 7) {
            AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
            return false;
        }
        return true;
    }

    /**
     * Method to hit the signup api
     */
    private void hitSaveAddressApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_NAME, etFirstName.getText().toString().trim() + " " + etLastName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_ADDRESS, tvAddress.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, String.valueOf(latitude));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, String.valueOf(longitude));
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, tvCountryCode.getText().toString().trim());

        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, etPhoneNo.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitSaveAddressApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SAVE_ADDRESS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SAVE_ADDRESS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        Result result = new Result();
                        result.setName(etFirstName.getText().toString().trim() + " " + etLastName.getText().toString().trim());
                        result.setLatitude(String.valueOf(latitude));
                        result.setLongitude(String.valueOf(longitude));
                        result.setMobileNo(etPhoneNo.getText().toString().trim());
                        result.setCountryId(tvCountryCode.getText().toString().trim());
                        result.setAddress(tvAddress.getText().toString().trim());
                        Intent intent = new Intent();
                        intent.putExtra(Constants.IntentConstant.DELIVERY_ADDRESS, result);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if (place != null && place.getAddress() != null) {
                    String address = "";
                    if (place.getName()!= null && !place.getName().equals("") && !place.getName().toString().contains("\"")) {
                        address += place.getName() + ", ";
                    }
                    address += place.getAddress().toString();
                    tvAddress.setText(address);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                }
            }
        }
    }
}
