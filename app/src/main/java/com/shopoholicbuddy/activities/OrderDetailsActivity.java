package com.shopoholicbuddy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForMakeOffer;
import com.shopoholicbuddy.dialogs.CustomDialogForMessage;
import com.shopoholicbuddy.livetracking.BuddyMapsActivity;
import com.shopoholicbuddy.models.NotificationBean;
import com.shopoholicbuddy.models.orderlistdetailsresponse.OrdersDetailsResponse;
import com.shopoholicbuddy.models.requestresponse.RequestDetailsResponse;
import com.shopoholicbuddy.models.requestresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class OrderDetailsActivity extends AppCompatActivity implements NetworkListener, LocationsCallback {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.iv_icon)
    ImageView ivOrderIcon;
    @BindView(R.id.tv_label)
    AppCompatTextView tvLabel;
    @BindView(R.id.ll_label)
    LinearLayout llLabel;
    @BindView(R.id.tv_name)
    AppCompatTextView tvName;
    @BindView(R.id.tv_delivery_to)
    AppCompatTextView tvDeliveryTo;
    @BindView(R.id.iv_product_pickup)
    ImageView ivProductPickup;
    @BindView(R.id.iv_order_delivery_icon)
    ImageView ivOrderDeliveryIcon;
    @BindView(R.id.ll_label2)
    LinearLayout llLabel2;
    @BindView(R.id.tv_deliver_name)
    AppCompatTextView tvDeliverName;
    @BindView(R.id.tv_delivery_to_address)
    AppCompatTextView tvDeliveryToAddress;
    @BindView(R.id.iv_product_deliver)
    ImageView ivProductDeliver;
    @BindView(R.id.btn_accept)
    CustomButton btnAccept;
    @BindView(R.id.accept_button_loader)
    View acceptButtonLoader;
    @BindView(R.id.accept_button_dot)
    View acceptButtonDot;
    @BindView(R.id.layout_accept_button_loader)
    FrameLayout layoutAcceptButtonLoader;
    @BindView(R.id.btn_reject)
    CustomButton btnReject;
    @BindView(R.id.reject_button_loader)
    View rejectButtonLoader;
    @BindView(R.id.reject_button_dot)
    View rejectButtonDot;
    @BindView(R.id.layout_reject_button_loader)
    FrameLayout layoutRejectButtonLoader;
    @BindView(R.id.ll_accept_reject)
    LinearLayout llAcceptReject;
    @BindView(R.id.tv_status)
    CustomTextView tvStatus;
    @BindView(R.id.tv_commission_to_be_earned)
    CustomTextView tvCommissionToBeEarned;
    @BindView(R.id.tv_commission_lable)
    CustomTextView tvCommissionLable;
    @BindView(R.id.btn_action)
    CustomButton btnNavigate;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.layout_button_loader)
    FrameLayout layoutButtonLoader;
    @BindView(R.id.ll_order_detail)
    LinearLayout llOrderDetail;
    @BindView(R.id.tv_time_slots)
    CustomTextView tvTimeSlots;
    @BindView(R.id.iv_dates)
    ImageView ivDates;
    @BindView(R.id.fbl_time_slots)
    FlexboxLayout fblTimeSlots;
    @BindView(R.id.ll_root_view)
    LinearLayout llRootView;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;

    private Result request;
    private com.shopoholicbuddy.models.orderlistdetailsresponse.Result buddyOrderResponse;
    private boolean isLoading;
    private String requestId = "";
    private String orderId = "";
    private String deliveryDate = "";
    private String deliveryPrice = "";
    private RCLocation location;
    private double latitude = 0.0, longitude  = 0.0;
    private String currencyCode;


    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                NotificationBean notificationBean = (NotificationBean) intent.getExtras().getSerializable(Constants.IntentConstant.NOTIFICATION);
                if (notificationBean != null && buddyOrderResponse != null && notificationBean.getOrderId() != null && buddyOrderResponse.getId() != null) {
                    if (notificationBean.getOrderId().equals(buddyOrderResponse.getId())) {
                        buddyOrderResponse.setOrderStatus(notificationBean.getOrderStatus());
                        setOrderStatus();
                    }
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUpdateReceiver, new IntentFilter(Constants.IntentConstant.NOTIFICATION));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUpdateReceiver);
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        initVariables();
//        initializeLocation();
    }

    /**
     * Method to initialize the values used
     */
    private void initVariables() {

        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getText(R.string.order_details));

        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "").equals(Constants.AppConstant.REQUESTS)) {
                llOrderDetail.setVisibility(View.GONE);
                llAcceptReject.setVisibility(View.VISIBLE);
                request = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
                requestId = getIntent().getExtras().getString(Constants.IntentConstant.ORDER_ID, "");
                tvTitle.setText(getString(R.string.request_details));
            } else if (getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "").equals(Constants.AppConstant.MY_ORDERS)) {
                llOrderDetail.setVisibility(View.VISIBLE);
                llAcceptReject.setVisibility(View.GONE);
                buddyOrderResponse = (com.shopoholicbuddy.models.orderlistdetailsresponse.Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
                orderId = getIntent().getExtras().getString(Constants.IntentConstant.ORDER_ID, "");
                tvTitle.setText(getString(R.string.order_details));
            }
            if (request != null || buddyOrderResponse != null) {
                setData();
            }else {
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (!orderId.equals("")) {
                        hitOrderDetailsApi();
                    } else if (!requestId.equals("")) {
                        hitRequestDetailsApi();
                    }
                } else {
//                    showRetryDialog();
                }
            }

        }


    }


    /**
     * method to initialize the location
     */
    private void initializeLocation() {
        location = new RCLocation();
        location.setActivity(this);
        location.setCallback(this);
        location.startLocation();
    }

    /**
     * This method sets the data to the views
     */
    private void setData() {
        llRootView.setVisibility(View.VISIBLE);
        if (request != null) {
            if (request.getBuddyId().equals(request.getMerchantId())) {
                tvName.setText(TextUtils.concat(request.getFirstName() + " " + request.getLastName()));
            }else {
                tvName.setText(request.getMerchantName());
            }
            String toAddress = request.getMerchantAddress();
            tvDeliveryTo.setText(toAddress == null || toAddress.trim().equals("") ? getString(R.string.na) : toAddress);
            String deliverName = request.getShopperName();
            tvDeliverName.setText(deliverName == null || deliverName.equals("") ? getString(R.string.na) : deliverName);
            String deliveryAddress = request.getDileveryAddress();
            tvDeliveryToAddress.setText(deliveryAddress == null || deliveryAddress.equals("") ? getString(R.string.na) : deliveryAddress);
            if (request.getProductType().equals("1")) {
                tvTimeSlots.setVisibility(View.GONE);
                ivDates.setVisibility(View.GONE);
                fblTimeSlots.setVisibility(View.GONE);
            } else {
                if (request.getSlotArr().size() > 0) {
                    tvTimeSlots.setVisibility(View.VISIBLE);
                    ivDates.setVisibility(View.VISIBLE);
                    fblTimeSlots.setVisibility(View.VISIBLE);
                    for (int i = 0; i < request.getSlotArr().size(); i++) {
//                        String startDate = AppUtils.getInstance().formatDate(request.getSlotArr().get(i).getSlotStartDate(), SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                        String startTime = AppUtils.getInstance().formatDate(request.getSlotArr().get(i).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
//                        String endDate = AppUtils.getInstance().formatDate(request.getSlotArr().get(i).getSlotEndDate(), SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                        String endTime = AppUtils.getInstance().formatDate(request.getSlotArr().get(i).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
                        if (request.getSlotArr().get(i).getAllDays().equals("1")){
//                            fblTimeSlots.addView(timeSlotView(startDate + " - " + endDate + " (" +  (getString(R.string.all_day_available)) + ")"));
                            fblTimeSlots.addView(timeSlotView(getString(R.string.all_day_available)));
                        } else {
//                            fblTimeSlots.addView(timeSlotView(startDate + " " + startTime + " - " + endDate + " " + endTime));
                            fblTimeSlots.addView(timeSlotView(startTime + " - " + endTime));
                        }
                    }
                }
            }
            btnAccept.setText(getString(R.string.accept));
            btnReject.setText(getString(R.string.reject));
        } else if (buddyOrderResponse != null) {
            tvName.setText(TextUtils.concat(buddyOrderResponse.getMerchantName()));
            String toAddress = buddyOrderResponse.getMerchantAddress();
            tvDeliveryTo.setText(toAddress.equals("") ? getString(R.string.na) : toAddress);
            String deliverName = buddyOrderResponse.getShopperName();
            tvDeliverName.setText(deliverName.trim().equals("") ? getString(R.string.na) : deliverName);
            String deliveryAddress = buddyOrderResponse.getDileveryAddress();
            tvDeliveryToAddress.setText(deliveryAddress == null || deliveryAddress.equals("") ? getString(R.string.na) : deliveryAddress);
            setOrderStatus();
            if (buddyOrderResponse.getSlotArr().size() == 0) {
                tvTimeSlots.setVisibility(View.GONE);
                ivDates.setVisibility(View.GONE);
                fblTimeSlots.setVisibility(View.GONE);
            } else {
                if (buddyOrderResponse.getSlotArr().size() > 0) {
                    tvTimeSlots.setVisibility(View.VISIBLE);
                    ivDates.setVisibility(View.VISIBLE);
                    fblTimeSlots.setVisibility(View.VISIBLE);
                    fblTimeSlots.removeAllViews();
                    for (int i = 0; i < buddyOrderResponse.getSlotArr().size(); i++) {
//                        String startDate = AppUtils.getInstance().formatDate(buddyOrderResponse.getSlotArr().get(i).getSlotStartDate(), SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                        String startTime = AppUtils.getInstance().formatDate(buddyOrderResponse.getSlotArr().get(i).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
//                        String endDate = AppUtils.getInstance().formatDate(buddyOrderResponse.getSlotArr().get(i).getSlotEndDate(), SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                        String endTime = AppUtils.getInstance().formatDate(buddyOrderResponse.getSlotArr().get(i).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
                        if (buddyOrderResponse.getSlotArr().get(i).getAllDays().equals("1")){
//                            fblTimeSlots.addView(timeSlotView(startDate + " - " + endDate + " (" +  (getString(R.string.all_day_available)) + ")"));
                            fblTimeSlots.addView(timeSlotView(getString(R.string.all_day_available)));
                        } else {
//                            fblTimeSlots.addView(timeSlotView(startDate + " " + startTime + " - " + endDate + " " + endTime));
                            fblTimeSlots.addView(timeSlotView(startTime + " - " + endTime));
                        }
                    }
                }
            }
            try {
                if (Double.parseDouble(buddyOrderResponse.getCommission()) == 0) {
                    tvCommissionLable.setVisibility(View.GONE);
                    tvCommissionToBeEarned.setVisibility(View.GONE);
                } else {
                    tvCommissionLable.setVisibility(View.VISIBLE);
                    tvCommissionToBeEarned.setVisibility(View.VISIBLE);
                    tvCommissionToBeEarned.setText(TextUtils.concat(buddyOrderResponse.getCommission() + "%"));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method to set the order status
     */
    private void setOrderStatus() {
        switch (buddyOrderResponse.getOrderStatus()) {
            case "1":
                tvStatus.setText(R.string.status_pending);
                layoutButtonLoader.setVisibility(View.VISIBLE);
                btnNavigate.setText(getString(R.string.status_shipped));
                break;
            case "2":
                if (buddyOrderResponse.getProductType().equals("1")) {
                    tvStatus.setText(R.string.status_confirm);
                    layoutButtonLoader.setVisibility(View.VISIBLE);
                    btnNavigate.setText(getString(R.string.status_shipped));
                }else {
                    tvStatus.setText(R.string.status_confirm);
                    layoutButtonLoader.setVisibility(View.VISIBLE);
                    btnNavigate.setText(getString(R.string.navigate));
                }
                break;
            case "3":
                tvStatus.setText(R.string.status_shipped);
                layoutButtonLoader.setVisibility(View.VISIBLE);
                btnNavigate.setText(getString(R.string.status_out_for_delivery));
                break;
            case "4":
                tvStatus.setText(R.string.status_out_for_delivery);
                layoutButtonLoader.setVisibility(View.VISIBLE);
                btnNavigate.setText(getString(R.string.navigate));
                break;
            case "5":
                tvStatus.setText(R.string.status_delivered);
                layoutButtonLoader.setVisibility(View.GONE);
                break;
            case "6":
                tvStatus.setText(R.string.status_cancel);
                layoutButtonLoader.setVisibility(View.GONE);
                break;
            case "7":
                tvStatus.setText(R.string.status_rejected);
                layoutButtonLoader.setVisibility(View.GONE);
                break;
        }
        if (buddyOrderResponse.getUserType().equals("1") && !buddyOrderResponse.getIsShared().equals("1") && !buddyOrderResponse.getOrderStatus().equals("4")) {
            layoutButtonLoader.setVisibility(View.GONE);
        }

    }


    /**
     * method to inflate view for language list
     *
     * @param text
     * @return
     */
    private View timeSlotView(String text) {
        final View view = LayoutInflater.from(this).inflate(R.layout.row_request_slots, null);
        final CustomTextView tvSelection = view.findViewById(R.id.text_view);
        tvSelection.setText(text);
        view.setTag(text);
        return view;
    }

    @OnClick({R.id.iv_back, R.id.btn_accept, R.id.btn_reject, R.id.btn_action, R.id.iv_dates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_accept:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    if (request.getProductType().equals("1") && request.getBuddyId().equals(request.getMerchantId())) {
                        if (deliveryDate.equals("")) {
                            CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(this, 2, "", "",
                                    "", (status, currency, text, type) -> {
                                deliveryDate = AppUtils.getInstance().formatDate(text, Constants.AppConstant.DATE_FORMAT, SERVICE_DATE_FORMAT);
                                hitUpdateRequestApi(2);
                            });
                            customDialogForSelectDate.show();
                        }else {
                            hitUpdateRequestApi(2);
                        }

                    } else if (request.getProductType().equals("1") && request.getHomeDelivery().equals("2")) {
                        if (deliveryPrice.equals("")) {
                            CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(this, 5, request.getCurrencySymbol(),
                                    request.getShopperDeliveryCharge(), request.getShopperDeliveryDate(), (status, currency, text, type) -> {
                                deliveryDate = request.getShopperDeliveryDate();
                                deliveryPrice = text;
                                hitUpdateRequestApi(2);
                            });
                            customDialogForSelectDate.show();
                        }else {
                            hitUpdateRequestApi(2);
                        }

                    } else {
                        hitUpdateRequestApi(2);
                    }
                }
                break;
            case R.id.btn_reject:
                if (currencyCode == null || currencyCode.equals("")) {
                    currencyCode = getUserCurrencyCode();
                }
                if (currencyCode != null && !currencyCode.equals("")) {
                    if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                        hitUpdateRequestApi(7);
                    }
                }else {
                    if (latitude != 0.0 && longitude != 0.0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.unable_to_get_location));
                    }
                }
                break;
            case R.id.iv_dates:
//                createAlertDialog(new ArrayList<String>());
                if (request != null) {
                    createAlertDialog(request.getSelectedDateArr());
                }else if (buddyOrderResponse != null){
                    createAlertDialog(buddyOrderResponse.getSelectedDateArr());
                }
                break;
            case R.id.btn_action:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    if (buddyOrderResponse.getOrderStatus().equals("2") || buddyOrderResponse.getOrderStatus().equals("1")) {
                        if (buddyOrderResponse.getProductType().equals("1")){
                            hitUpdateOrderStatusApi(3);
                        }else {
                            hitUpdateOrderStatusApi(8);
                        }
                    } else if (buddyOrderResponse.getOrderStatus().equals("3")) {
                        hitUpdateOrderStatusApi(4);
                    } else if (buddyOrderResponse.getOrderStatus().equals("4")) {
                        hitUpdateOrderStatusApi(8);
                        break;
                    }
                }
        }
    }

    /**
     * function to get user currency code
     */
    private String getUserCurrencyCode() {
        String latitude = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LATITUDE);
        String longitude = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LONGITUDE);
        if ((latitude.equals("") || latitude.equals("0")) && (longitude.equals("") || longitude.equals("0"))) {
/*
            new AlertDialog.Builder(this, R.style.DatePickerTheme)
                    .setMessage(R.string.provide_address_in_profile)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        // do nothing
                    })
                    .show();
*/
            return request.getCurrencyCode();
        }
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        currencyCode = AppUtils.getInstance().getCurrency(this, this.latitude, this.longitude).get(1);
        if (currencyCode.equals("")) currencyCode = request.getCurrencyCode();
        return currencyCode;
    }

    /**
     *
     * method to create alert dialog
     */
    public void createAlertDialog(List<String> list) {
        if (list == null) {
            AppUtils.getInstance().showToast(this, getString(R.string.no_slots_available));
            return;
        }
        List<String> aList = new ArrayList<>();
        for (String date : list){
            if (!date.equals("0000-00-00")) {
                String formatDate = AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT);
                if (!aList.contains(formatDate)) aList.add(formatDate);
            }
        }
        //First Step: convert ArrayList to an Object array.
        Object[] objNames = aList.toArray();
        //Second Step: convert Object array to String array
        String[] dates = Arrays.copyOf(objNames, objNames.length, String[].class);

        AlertDialog.Builder alertBox = new AlertDialog.Builder(this)
                .setItems(dates, null);
        alertBox.show();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "").equals(Constants.AppConstant.MY_ORDERS)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, buddyOrderResponse);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        super.onBackPressed();
    }

    /**
     * This method hits the api to send the accept or reject request to the server
     *
     * @param status
     */
    private void hitUpdateRequestApi(int status) {
        isLoading = true;
        if (status == 2)
            AppUtils.getInstance().setButtonLoaderAnimation(this, btnAccept, acceptButtonLoader, acceptButtonDot, true);
        if (status == 7)
            AppUtils.getInstance().setButtonLoaderAnimation(this, btnReject, rejectButtonLoader, rejectButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        if (!request.getOrderId().equals("0")) {
            params.put(Constants.NetworkConstant.PARAM_ORDER_ID, request.getOrderId());
        } else if (!request.getHuntId().equals("0")) {
            params.put(Constants.NetworkConstant.PARAM_HUNT_ID, request.getHuntId());
        }
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_DATE, deliveryDate);
        params.put(Constants.NetworkConstant.PARAM_BUDDY_DELIVERY_CHARGE, !request.getIsShared().equals("1") ? deliveryPrice : request.getDeliveryCharge());
        params.put(Constants.NetworkConstant.PARAM_STATUS, String.valueOf(status));
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, request.getCurrencyCode());
        if (status == 7) params.put(Constants.NetworkConstant.PARAM_USER_CURRENCY_CODE, currencyCode);
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        Call<ResponseBody> call = apiInterface.hitUpdateRequestApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST);
    }

    /**
     * This method hits the api to update order status
     *
     * @param status
     */
    private void hitUpdateOrderStatusApi(int status) {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnNavigate, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ORDER_ID, buddyOrderResponse.getId());
        params.put(Constants.NetworkConstant.PARAM_STATUS, String.valueOf(status));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "2");
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        Call<ResponseBody> call = apiInterface.hitUpdateOrderStatusApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_UPDATE_ORDER_REQUEST);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST:
                AppUtils.getInstance().setButtonLoaderAnimation(this, btnReject, rejectButtonLoader, rejectButtonDot, false);
                AppUtils.getInstance().setButtonLoaderAnimation(this, btnAccept, acceptButtonLoader, acceptButtonDot, false);
                if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.ORDER_ID, request.getOrderId());
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (responseCode == Constants.NetworkConstant.EMAIL_NOT_VERIFIED) {
/*
                    new CustomDialogForSendMail(this, new MessageDialogCallback() {
                        @Override
                        public void onSubmitClick() {
                            if (!isLoading && AppUtils.getInstance().isInternetAvailable(OrderDetailsActivity.this)) {
                                hitOrderDetailsApi();
                            }
                        }
                    }).show();
*/

                    String email = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL);
                    new AlertDialog.Builder(this, R.style.DatePickerTheme)
//                            .setTitle(getString(R.string.cancel_order))
                            .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                            .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!isLoading && AppUtils.getInstance().isInternetAvailable(OrderDetailsActivity.this)) {
                                        hitResendLinkApi();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                // do nothing
                            })
                            .show();
                } else {
                    AppUtils.getInstance().showToast(this, response);
                    try {
                        AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Constants.NetworkConstant.REQUEST_UPDATE_ORDER_REQUEST:
                AppUtils.getInstance().setButtonLoaderAnimation(this, btnNavigate, viewButtonLoader, viewButtonDot, false);
                if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                    if (buddyOrderResponse.getOrderStatus().equals("2") || buddyOrderResponse.getOrderStatus().equals("1")) {
                        if (buddyOrderResponse.getProductType().equals("1")) {
                            buddyOrderResponse.setOrderStatus("3");
                        }else {
                            checkLocationPermission();
                        }
                    } else if (buddyOrderResponse.getOrderStatus().equals("3")) {
                        buddyOrderResponse.setOrderStatus("4");
                    } else if (buddyOrderResponse.getOrderStatus().equals("4")) {
                        checkLocationPermission();
                    }
                    setData();
                } else {
                    AppUtils.getInstance().showToast(this, response);
                    try {
                        AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.NetworkConstant.REQUEST_ORDER:
                switch (responseCode) {
                    case Constants.NetworkConstant.VERIFY_EMAIL:
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        OrdersDetailsResponse ordersDetailsResponse = new Gson().fromJson(response, OrdersDetailsResponse.class);
                        buddyOrderResponse = ordersDetailsResponse.getResult();
                        if (buddyOrderResponse != null) {
                            setData();
                        }
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
            case Constants.NetworkConstant.REQUEST_BUDDY_REQUESTS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        RequestDetailsResponse ordersDetailsResponse = new Gson().fromJson(response, RequestDetailsResponse.class);
                        request = ordersDetailsResponse.getResult();
                        if (request != null) {
                            setData();
                        }
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
        progressBar.setVisibility(View.GONE);
        isLoading = false;
        AppUtils.getInstance().showToast(this, response);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnNavigate, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnReject, rejectButtonLoader, rejectButtonDot, false);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAccept, acceptButtonLoader, acceptButtonDot, false);
        AppUtils.getInstance().showToast(this, response);

    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnNavigate, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnReject, rejectButtonLoader, rejectButtonDot, false);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAccept, acceptButtonLoader, acceptButtonDot, false);
//        showRetryDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_MAP && resultCode == RESULT_OK) {
            if (buddyOrderResponse != null) buddyOrderResponse.setOrderStatus("5");
            setData();
        }
    }


    /**
     * method to check the location permission
     */
    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, Constants.IntentConstant.REQUEST_LOCATION);
            } else {
                openMapActivity();
            }
        } else {
            openMapActivity();
        }
    }

    /**
     * method to open map activity
     */
    private void openMapActivity() {
        LatLng latLng;
        if (buddyOrderResponse.getLatitude().equals("") && buddyOrderResponse.getLongitude().equals("")) {
            latLng = new LatLng(0.0, 0.0);
        } else {
            latLng = new LatLng(Double.parseDouble(buddyOrderResponse.getLatitude()), Double.parseDouble(buddyOrderResponse.getLongitude()));
        }
        LatLng latLngMerchant = null;
        if (!buddyOrderResponse.getMerchantLatitude().equals("") && !buddyOrderResponse.getMerchantLongitude().equals("")) {
            latLngMerchant = new LatLng(Double.parseDouble(buddyOrderResponse.getMerchantLatitude()), Double.parseDouble(buddyOrderResponse.getMerchantLongitude()));
        }
        startActivityForResult(new Intent(this, BuddyMapsActivity.class)
                        .putExtra(Constants.IntentConstant.MERCHANT_ID, buddyOrderResponse.getId())
                        .putExtra(Constants.IntentConstant.BUDDY_ID, buddyOrderResponse.getBuddyId())
                        .putExtra(Constants.IntentConstant.ORDER_ID, buddyOrderResponse.getId())
                        .putExtra(Constants.IntentConstant.USER_TYPE, buddyOrderResponse.getUserType())
                        .putExtra(Constants.IntentConstant.LOCATION, latLng)
                        .putExtra(Constants.IntentConstant.LOCATION_MERCHANT, latLngMerchant)
                        .putExtra(Constants.IntentConstant.SHOPPER_NAME, buddyOrderResponse.getShopperName())
                        .putExtra(Constants.IntentConstant.SHOPPER_ADDRESS, buddyOrderResponse.getDileveryAddress())
                        .putExtra(Constants.IntentConstant.SHOPPER_NUNBER, buddyOrderResponse.getShopperCountryId() + buddyOrderResponse.getShopperNumber())
                , Constants.IntentConstant.REQUEST_MAP);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_LOCATION:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMapActivity();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
        }
    }

    /**
     * method to hit order data api
     */
    private void hitRequestDetailsApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_REQ_ID, requestId);
        Call<ResponseBody> call = apiInterface.hitRequestDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_BUDDY_REQUESTS);
        AppUtils.getInstance().hitUpdateCountApi(this, requestId);
    }

    /**
     * method to hit order data api
     */
    private void hitOrderDetailsApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ORDER_ID, orderId);
        Call<ResponseBody> call = apiInterface.hitOrderDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ORDER);
    }

    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, null, 1001);
    }



    @Override
    public void setLocation(Location mCurrentLocation) {
        location.disconnect();
        if (mCurrentLocation == null)
            return;
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
        location.getAddress();
    }

    @Override
    public void setAddress(Address address) {
        location.disconnect();
        currencyCode = address.getCountryCode();
    }
    @Override
    public void setLatAndLong(Address location) {}
    @Override
    public void disconnect() {}
}
