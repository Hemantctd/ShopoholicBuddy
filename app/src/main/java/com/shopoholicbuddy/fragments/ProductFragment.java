package com.shopoholicbuddy.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.HuntTimeSlotsActivity;
import com.shopoholicbuddy.adapters.ProductImagesAdapter;
import com.shopoholicbuddy.adapters.SlotsAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForAcceptHunt;
import com.shopoholicbuddy.dialogs.CustomDialogForMessage;
import com.shopoholicbuddy.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholicbuddy.models.huntdetailresponse.HuntDetailResponse;
import com.shopoholicbuddy.models.huntdetailresponse.ImageArr;
import com.shopoholicbuddy.models.huntdetailresponse.Result;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholicbuddy.models.productservicedetailsresponse.SlotSelectedDate;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class ProductFragment extends Fragment implements NetworkListener {


    @BindView(R.id.tv_select_category)
    CustomTextView tvSelectCategory;
    @BindView(R.id.tv_select_sub_category)
    CustomTextView tvSelectSubCategory;
    @BindView(R.id.tv_price_range)
    CustomTextView tvPriceRange;
    @BindView(R.id.tv_upload_image)
    CustomTextView tvUploadImage;
    @BindView(R.id.rv_upload_image)
    RecyclerView rvUploadImage;
    @BindView(R.id.tv_description)
    CustomTextView tvDescription;
    @BindView(R.id.tv_hunt_name)
    CustomTextView tvHuntName;
    @BindView(R.id.add_description)
    CustomEditText addDescription;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.tv_expected_date)
    CustomTextView tvExpectedDate;
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
    @BindView(R.id.ll_root_view)
    LinearLayout llRootView;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.label_target_area)
    CustomTextView labelTargetArea;
    @BindView(R.id.tv_target_area)
    CustomTextView tvTargetArea;
    @BindView(R.id.view_target_area)
    View viewTargetArea;
    @BindView(R.id.rl_slots)
    RelativeLayout rlSlots;
    @BindView(R.id.iv_dates)
    ImageView ivDates;
    @BindView(R.id.rv_time_slots)
    RecyclerView rvTimeSlots;
    @BindView(R.id.label_price)
    TextView labelPrice;
    @BindView(R.id.view_price)
    View viewPrice;
    @BindView(R.id.label_expected_date)
    CustomTextView labelExpectedDate;
    @BindView(R.id.view_expected_date)
    View viewExpectedDate;
    @BindView(R.id.view_time_slots)
    View viewTimeSlots;
    @BindView(R.id.expected_date)
    View expectedDate;
    @BindView(R.id.view_price_sep)
    View viewPriceSep;
    @BindView(R.id.tv_bidding_image)
    CustomTextView tvBiddingImage;
    @BindView(R.id.rv_bidding_image)
    RecyclerView rvBiddingImage;
    @BindView(R.id.tv_bidding_price)
    CustomTextView tvBiddingPrice;
    @BindView(R.id.label_bidding_date)
    CustomTextView labelBiddingDate;
    @BindView(R.id.tv_bidding_date)
    CustomTextView tvBiddingDate;
    @BindView(R.id.bidding_date)
    View biddingDate;
    @BindView(R.id.view_bidding_date)
    View viewBiddingDate;
    @BindView(R.id.ll_bidding_details)
    LinearLayout llBiddingDetails;
    @BindView(R.id.tv_closed)
    CustomTextView tvClosed;

    private boolean isProduct;
    private AppCompatActivity mActivity;
    private ProductImagesAdapter productImagesAdapter;
    private ProductImagesAdapter biddingImagesAdapter;
    private Unbinder unbinder;
    private String huntId = "";
    private List<String> productImagesList;
    private List<String> biddingImagesList;
    private Result huntData;
    private boolean isLoading;
    private SlotsAdapter slotsAdapter;
    private ArrayList<ServiceSlot> selectedSlotsArray;
    private ArrayList<ServiceSlot> slotsArray;
    private ArrayList<String> slotsDayList;
    public String currencySymbol = "";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariables();
        createAndSetAdapters();
        return view;
    }


    @OnClick({R.id.btn_accept, R.id.btn_reject, R.id.iv_dates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    /*CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(mActivity, 2, "", new MakeOfferDialogCallback() {
                        @Override
                        public void onSelect(String status, String currency, String text, int type) {
                            String date = AppUtils.getInstance().formatDate(text, Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT);
                            hitUpdateRequestApi(2, date);
                        }
                    });
                    customDialogForSelectDate.show();*/
                    if (!isProduct) {
                        StringBuilder selectedSlots = new StringBuilder();
                        if (AppUtils.getInstance().isLoggedIn(mActivity)) {
                            for (int i = 0; i < selectedSlotsArray.size(); i++) {
                                if (i != 0) selectedSlots.append(",");
                                selectedSlots.append(selectedSlotsArray.get(i).getId());
                            }
                            Collections.sort(slotsDayList, new StringDateComparator());
                            if (!selectedSlots.toString().equals("")) {
                                huntData.setSelectedSlots(selectedSlots.toString());
                                huntData.setSelectedDates(slotsDayList);
                            }
                        }
                        if (selectedSlots.toString().equals("")) {
                            AppUtils.getInstance().showToast(mActivity, getString(R.string.Please_enter_slots));
                            return;
                        }
                    }
                    openHuntDialog();
                }
                break;
            case R.id.btn_reject:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    hitUpdateRequestApi(7, "");
                }
                break;
            case R.id.iv_dates:
               /* Intent intent = new Intent(mActivity, HuntTimeSlotsActivity.class);
                intent.putExtra(Constants.IntentConstant.DAYS_LIST, huntData.getSlotSelectedDate());
                intent.putStringArrayListExtra(Constants.IntentConstant.SELECTED_DAYS_LIST, slotsDayList);
                intent.putExtra(Constants.IntentConstant.SLOTS, slotsArray);
                intent.putExtra(Constants.IntentConstant.CURRENCY, huntData.getCurrencySymbol());
                if (huntData.getServiceSlot() != null && huntData.getServiceSlot().size() > 0) {
                    intent.putExtra(Constants.IntentConstant.START_DATE, huntData.getServiceSlot().get(0).getSlotStartDate());
                    intent.putExtra(Constants.IntentConstant.END_DATE, huntData.getServiceSlot().get(0).getSlotEndDate());
                }
                if (huntData.getBiddingStatus().equals("2")) {
                    intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.IntentConstant.BUDDY);
                }
                ProductFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_SLOTS);*/
                if (huntData != null) {
                    createAlertDialog(huntData.getSlotSelectedDate());
                }
                break;
        }
    }


    /**
     *
     * method to create alert dialog
     * @param list
     */
    public void createAlertDialog(ArrayList<SlotSelectedDate> list) {
        if (list == null) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.no_slots_available));
            return;
        }
        List<String> aList = new ArrayList<>();
        for (SlotSelectedDate slotDate : list){
            String formatDate = AppUtils.getInstance().formatDate(slotDate.getSelectedDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
            if (!aList.contains(formatDate)) aList.add(formatDate);
        }
        //First Step: convert ArrayList to an Object array.
        Object[] objNames = aList.toArray();
        //Second Step: convert Object array to String array
        String[] dates = Arrays.copyOf(objNames, objNames.length, String[].class);

        AlertDialog.Builder alertBox = new AlertDialog.Builder(mActivity)
                .setItems(dates, null);
        alertBox.show();
    }

    /**
     * function to open hunt dialog
     */
    private void openHuntDialog() {
        CustomDialogForAcceptHunt customDialogForAcceptHunt = CustomDialogForAcceptHunt.newInstance(huntData.getId(), huntData);
        customDialogForAcceptHunt.show(getChildFragmentManager(), getString(R.string.hunt_request));
    }

    /**
     * initialize the variables
     */

    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        btnAccept.setText(getString(R.string.accept));
        btnReject.setText(getString(R.string.reject));

        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);

        productImagesList = new ArrayList<>();
        biddingImagesList = new ArrayList<>();
        selectedSlotsArray = new ArrayList<>();
        slotsArray = new ArrayList<>();
        slotsDayList = new ArrayList<>();

        if (getArguments() != null && getArguments().containsKey(Constants.IntentConstant.IS_PRODUCT)) {
            isProduct = getArguments().getBoolean(Constants.IntentConstant.IS_PRODUCT, false);
            huntId = getArguments().getString(Constants.NetworkConstant.PARAM_HUNT_ID, "");
        }
        if (!isProduct) {
            rvUploadImage.setVisibility(View.GONE);
            tvUploadImage.setVisibility(View.GONE);
        }
        if (!huntId.equals("") && AppUtils.getInstance().isInternetAvailable(mActivity)) {
            hitGetHuntDataApi();
        }
        addDescription.setEnabled(false);

    }

    /**
     * method to create and set adapter on views
     */
    private void createAndSetAdapters() {
        productImagesAdapter = new ProductImagesAdapter(mActivity, productImagesList, (position, view) -> {
            showImages(productImagesList, productImagesAdapter);
        });
        rvUploadImage.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvUploadImage.setAdapter(productImagesAdapter);

        biddingImagesAdapter = new ProductImagesAdapter(mActivity, biddingImagesList, (position, view) -> {
            showImages(biddingImagesList, biddingImagesAdapter);
        });
        rvBiddingImage.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvBiddingImage.setAdapter(biddingImagesAdapter);

        slotsAdapter = new SlotsAdapter(mActivity, this, selectedSlotsArray, (position, view) -> {
            switch (view.getId()) {
                case R.id.fl_root_view:
//                        slotsArray.get(position).setSelected(!slotsArray.get(position).isSelected());
//                        slotsAdapter.notifyItemChanged(position);
                    break;
            }
        });
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvTimeSlots.setAdapter(slotsAdapter);

    }

    /**
     * method to show the images
     * @param imagesList
     * @param imagesAdapter
     */
    public void showImages(List<String> imagesList, ProductImagesAdapter imagesAdapter) {
        if (imagesList != null) {
            ArrayList<String> images = new ArrayList<String>(imagesList);
            if (images.size() > 0) {
                Intent intent = new Intent(mActivity, FullScreenImageSliderActivity.class);
                intent.putExtra("imagelist", images);
                intent.putExtra("from", getString(R.string.images));
                if (imagesAdapter != null)
                    intent.putExtra("pos", imagesAdapter.selectedPosition);
                startActivityForResult(intent, 10001);
            }
        }
    }

    /**
     * method to hit the category list api
     */
    private void hitGetHuntDataApi() {
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
        Call<ResponseBody> call = apiInterface.hitHuntDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PRODUCTS);
    }

    /**
     * This method hits the api to send the accept or reject request to the server
     *
     * @param status
     * @param date
     */
    private void hitUpdateRequestApi(int status, String date) {
        isLoading = true;
        if (status == 2)
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAccept, acceptButtonLoader, acceptButtonDot, true);
        if (status == 7)
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnReject, rejectButtonLoader, rejectButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntData.getId());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_DATE, date);
        params.put(Constants.NetworkConstant.PARAM_STATUS, String.valueOf(status));
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        Call<ResponseBody> call = apiInterface.hitUpdateRequestApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            isLoading = false;
            if (isAdded()) {
                AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAccept, acceptButtonLoader, acceptButtonDot, false);
                AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnReject, rejectButtonLoader, rejectButtonDot, false);
                switch (requestCode) {
                    case Constants.NetworkConstant.REQUEST_PRODUCTS:
                        switch (responseCode) {
                            case Constants.NetworkConstant.SUCCESS_CODE:
                                HuntDetailResponse huntDetailResponse = new Gson().fromJson(response, HuntDetailResponse.class);
                                huntData = huntDetailResponse.getResult();
                                getDataAndSetValue();
                                break;
                            default:
                                try {
                                    AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                        break;
                    case Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST:
                        switch (responseCode) {
                            case Constants.NetworkConstant.SUCCESS_CODE:
                                Intent intent = new Intent();
                                intent.putExtra(Constants.IntentConstant.HUNT_ID, huntId);
                                mActivity.setResult(RESULT_OK, intent);
                                if (!mActivity.isFinishing() || !mActivity.isDestroyed())
                                    mActivity.finish();
                                break;
                            default:
                                AppUtils.getInstance().showToast(mActivity, response);
                                try {
                                    AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                        break;

                }
            }
        }
    }


    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAccept, acceptButtonLoader, acceptButtonDot, false);
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnReject, rejectButtonLoader, rejectButtonDot, false);
            AppUtils.getInstance().showToast(mActivity, response);
            if (huntData == null) showRetryDialog();
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAccept, acceptButtonLoader, acceptButtonDot, false);
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnReject, rejectButtonLoader, rejectButtonDot, false);
            if (huntData == null) showRetryDialog();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * method to get data and set values in views
     */
    private void getDataAndSetValue() {
        if (huntData != null) {
            llRootView.setVisibility(View.VISIBLE);
            isProduct = huntData.getProductType().equals("1");
            tvSelectCategory.setText(huntData.getCategoryName());
            tvSelectSubCategory.setText(huntData.getSubcategoryName());
            tvHuntName.setText(huntData.getHuntTitle());
            addDescription.setText(huntData.getDescription());
            currencySymbol = huntData.getCurrencySymbol();

            for (ImageArr image : huntData.getImageArr()) {
                productImagesList.add(image.getImagePath());
            }
            if (productImagesList.size() > 0) {
                rvUploadImage.setVisibility(View.VISIBLE);
                tvUploadImage.setVisibility(View.VISIBLE);
                tvUploadImage.setText(getString(R.string.uploaded_images));
            }else {
                rvUploadImage.setVisibility(View.GONE);
                tvUploadImage.setVisibility(View.GONE);
            }
            productImagesAdapter.notifyDataSetChanged();

            if (!huntData.getLatitude().equals("") && !huntData.getLongitude().equals("")) {
                tvAddress.setText(huntData.getAddress());
            }

            if (isProduct) {
                rlSlots.setVisibility(View.GONE);

                labelExpectedDate.setVisibility(View.VISIBLE);
                viewExpectedDate.setVisibility(View.VISIBLE);
                tvExpectedDate.setVisibility(View.VISIBLE);
                expectedDate.setVisibility(View.VISIBLE);
                labelPrice.setVisibility(View.VISIBLE);
                viewPrice.setVisibility(View.VISIBLE);

                viewPriceSep.setVisibility(View.VISIBLE);
                tvPriceRange.setVisibility(View.VISIBLE);
                tvPriceRange.setText(TextUtils.concat(huntData.getCurrencySymbol() + huntData.getPriceStart() + " - " + huntData.getCurrencySymbol() + huntData.getPriceEnd()));
                tvExpectedDate.setText(AppUtils.getInstance().formatDate(huntData.getExpectedDeliveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                if (huntData.getTargetArea() != null && !huntData.getTargetArea().equals("")) {
                    labelTargetArea.setVisibility(View.VISIBLE);
                    tvTargetArea.setVisibility(View.VISIBLE);
                    viewTargetArea.setVisibility(View.VISIBLE);
                    tvTargetArea.setText(huntData.getTargetArea());
                } else {
                    labelTargetArea.setVisibility(View.GONE);
                    tvTargetArea.setVisibility(View.GONE);
                    viewTargetArea.setVisibility(View.GONE);
                }
            } else {
                rlSlots.setVisibility(View.VISIBLE);

                labelExpectedDate.setVisibility(View.GONE);
                viewExpectedDate.setVisibility(View.GONE);
                tvExpectedDate.setVisibility(View.GONE);
                expectedDate.setVisibility(View.GONE);

                tvTargetArea.setVisibility(View.GONE);
                viewTargetArea.setVisibility(View.GONE);
                labelTargetArea.setVisibility(View.GONE);
                /*if (huntData.getServiceSlot() != null) {
                    slotsArray.addAll(huntData.getServiceSlot());
                }*/
//                ivDates.setText(getString(R.string.selected_dates));
                if (huntData.getServiceSlot() != null && huntData.getServiceSlot().size() > 0) {
                    slotsDayList.clear();
                    slotsArray.clear();
                    /*for (ServiceSlot slot : huntData.getServiceSlot()) {
                        for (SlotSelectedDate selectedDate : huntData.getSlotSelectedDate()) {
                            slotsDayList.add(selectedDate.getSelectedDate());
                            if (selectedDate.getSlotId().equals(slot.getId())
                                    && !slotsArray.contains(slot)) {
                                slotsArray.add(slot);
                            }
                        }
                    }*/
                    selectedSlotsArray.addAll(huntData.getServiceSlot());
                    slotsAdapter.notifyDataSetChanged();
                }

                labelPrice.setVisibility(View.VISIBLE);
                viewPrice.setVisibility(View.VISIBLE);
                tvPriceRange.setVisibility(View.VISIBLE);
                double price = 0.0;
                try {
                    price = Double.parseDouble(huntData.getPriceStart()) * huntData.getSlotSelectedDate().size();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                tvPriceRange.setText(TextUtils.concat(huntData.getCurrencySymbol()
                        + String.format(Locale.ENGLISH, "%.2f", price)));
                viewPriceSep.setVisibility(View.VISIBLE);

            }
            if (huntData.getBiddingStatus().equals("2")) {
                llBiddingDetails.setVisibility(View.VISIBLE);
                llAcceptReject.setVisibility(View.GONE);
                if (isProduct) {
                    labelBiddingDate.setVisibility(View.VISIBLE);
                    viewBiddingDate.setVisibility(View.VISIBLE);
                    tvBiddingDate.setVisibility(View.VISIBLE);
                    biddingDate.setVisibility(View.VISIBLE);
                    tvBiddingDate.setText(AppUtils.getInstance().formatDate(huntData.getBiddingDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                } else {
                    labelBiddingDate.setVisibility(View.GONE);
                    viewBiddingDate.setVisibility(View.GONE);
                    tvBiddingDate.setVisibility(View.GONE);
                    biddingDate.setVisibility(View.GONE);
                    /*ivDates.setText(getString(R.string.selected_dates));
                    if (huntData.getServiceSlot() != null && huntData.getServiceSlot().size() > 0) {
                        slotsDayList.clear();
                        slotsArray.clear();
                        for (ServiceSlot slot : huntData.getServiceSlot()) {
                            for (SlotSelectedDate selectedDate : huntData.getSlotSelectedDate()) {
                                slotsDayList.add(selectedDate.getSelectedDate());
                                if (selectedDate.getSlotId().equals(slot.getId())
                                        && !slotsArray.contains(slot)) {
                                    slotsArray.add(slot);
                                }
                            }
                        }
                        selectedSlotsArray.addAll(slotsArray);
                    }*/
                }
                tvBiddingPrice.setText(TextUtils.concat(huntData.getCurrencySymbol() + huntData.getBiddingPrice()));
                for (ImageArr image : huntData.getBiddingImageArr()) {
                    biddingImagesList.add(image.getImagePath());
                }
                biddingImagesAdapter.notifyDataSetChanged();
            } else {
                llBiddingDetails.setVisibility(View.GONE);
                llAcceptReject.setVisibility(View.VISIBLE);
            }
            if (huntData.getStatus().equals("2") || huntData.getIsHuntClose().equals("1")) {
                tvClosed.setVisibility(View.VISIBLE);
                llAcceptReject.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method to show success dialog after signup complete
     */
    private void showRetryDialog() {
        CustomDialogForMessage messageDialog = new CustomDialogForMessage(mActivity, getString(R.string.error), getString(R.string.network_issue),
                getString(R.string.retry), false, () -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                hitGetHuntDataApi();
            } else {
                showRetryDialog();
            }
        });
        messageDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.GONE);
        if (requestCode == Constants.IntentConstant.REQUEST_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ArrayList<ServiceSlot> slotList = (ArrayList<ServiceSlot>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            ArrayList<String> daysList = data.getExtras().getStringArrayList(Constants.IntentConstant.SELECTED_DAYS_LIST);
            setSlotsInModel(slotList, daysList);
        }
    }

    /**
     * function to set slots in model
     *
     * @param slotList
     * @param daysList
     */
    private void setSlotsInModel(ArrayList<ServiceSlot> slotList, ArrayList<String> daysList) {
        slotsDayList.clear();
        slotsArray.clear();
        if (daysList != null) slotsDayList.addAll(daysList);
        if (slotList != null) {
            slotsArray.addAll(slotList);
            selectedSlotsArray.clear();
            for (ServiceSlot slot : slotsArray) {
                if (slot.isSelected()) {
                    selectedSlotsArray.add(slot);
                }
            }
            slotsAdapter.notifyDataSetChanged();
            if (AppUtils.getInstance().isLoggedIn(mActivity)) {
                StringBuilder selectedSlots = new StringBuilder();
                double price = 0.0;
                for (int i = 0; i < selectedSlotsArray.size(); i++) {
                    if (i != 0) selectedSlots.append(",");
                    selectedSlots.append(selectedSlotsArray.get(i).getId());
                    price += Double.parseDouble(selectedSlotsArray.get(i).getPrice());
                }
                Collections.sort(slotsDayList, new StringDateComparator());
                if (!selectedSlots.toString().equals("")) {
                    if (slotsDayList != null) price = price * slotsDayList.size();
                    if (price == 0.0) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.amount_cant_be_zero));
                        return;
                    }
                    tvPriceRange.setText(String.valueOf(price));
                }
            }
        }
        slotsAdapter.notifyDataSetChanged();
    }


    /**
     * inner class for sort date array
     */
    class StringDateComparator implements Comparator<String> {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);

        public int compare(String lhs, String rhs) {
            try {
                return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
