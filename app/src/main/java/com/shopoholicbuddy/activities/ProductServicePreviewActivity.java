package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.ProductImagesAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.database.AppDatabase;
import com.shopoholicbuddy.dialogs.CustomDialogForConfirmDeal;
import com.shopoholicbuddy.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.AddProductServiceModel;
import com.shopoholicbuddy.models.preferredcategorymodel.Result;
import com.shopoholicbuddy.models.productservicedetailsresponse.ProductServiceDetailsResponse;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Class created by Sachin on 09-Apr-18.
 */

public class ProductServicePreviewActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_product_pic)
    ImageView ivProductPic;
    @BindView(R.id.iv_play_video)
    ImageView ivPlayVideo;
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
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_product_images)
    CustomTextView tvProductImages;
    @BindView(R.id.rv_product_images)
    RecyclerView rvProductImages;
    @BindView(R.id.view_product_images)
    View viewProductImages;
    @BindView(R.id.civ_product_user_image)
    CircleImageView civProductUserImage;
    @BindView(R.id.fl_profile)
    FrameLayout flProfile;
    @BindView(R.id.tv_product_user_name)
    CustomTextView tvProductUserName;
    @BindView(R.id.tv_product_user_type)
    CustomTextView tvProductUserType;
    @BindView(R.id.tv_product_type)
    CustomTextView tvProductType;
    @BindView(R.id.tv_product_name)
    CustomTextView tvProductName;
    @BindView(R.id.tv_product_description)
    CustomTextView tvProductDescription;
    @BindView(R.id.tv_final_price)
    CustomTextView tvFinalPrice;
    @BindView(R.id.tv_original_price)
    CustomTextView tvOriginalPrice;
    @BindView(R.id.tv_flat_discount)
    CustomTextView tvFlatDiscount;
    @BindView(R.id.tv_product_details)
    CustomTextView tvProductDetails;
    @BindView(R.id.tv_quantity)
    CustomTextView tvQuantity;
    @BindView(R.id.tv_currency)
    CustomTextView tvCurrency;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.tv_read_more)
    CustomTextView tvReadMore;
    @BindView(R.id.ll_product)
    LinearLayout llProduct;
    @BindView(R.id.tv_deal_posted)
    CustomTextView tvDealPosted;
    @BindView(R.id.tv_deal_validity)
    CustomTextView tvDealValidity;
    @BindView(R.id.label_delivery_charges)
    CustomTextView labelDeliveryCharges;
    @BindView(R.id.tv_delivery_charges)
    CustomTextView tvDeliveryCharges;
    @BindView(R.id.view_delivery_charges)
    View viewDeliveryCharges;
    @BindView(R.id.tv_slot_availability)
    CustomTextView tvSlotAvailability;
    @BindView(R.id.fbl_time_slots)
    FlexboxLayout fblTimeSlots;
    @BindView(R.id.view_time_slots)
    View viewTimeSlots;
    @BindView(R.id.tv_edit_deal)
    CustomTextView tvEditDeal;
    @BindView(R.id.view_separator)
    View viewSeparator;
    @BindView(R.id.tv_confirm_and_post_deal)
    CustomTextView tvConfirmAndPostDeal;
    @BindView(R.id.layout_action)
    LinearLayout layoutAction;
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.rl_price)
    RelativeLayout rlPrice;
    @BindView(R.id.iv_dates)
    ImageView ivDates;
    private ArrayList<String> productImagesList;
    private ProductImagesAdapter productImagesAdapter;
    private AddProductServiceModel productDetails;
    private CustomDialogForConfirmDeal confirmDealDialog;
    private String dealId, fromClass;
    private String dealImage = "";
    private boolean readMore = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products_services_preview);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productDetails != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        productImagesList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        if (getIntent() != null && getIntent().getExtras() != null) {
            productDetails = (AddProductServiceModel) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            dealId = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, "");
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            dealImage = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_IMAGE, "");
            AppUtils.getInstance().setImages(this, dealImage, ivProductPic, 0, R.drawable.ic_placeholder);
            if (fromClass.equals(Constants.AppConstant.POSTED_BY_ME)) {
                if (dealId != null) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        layoutAction.setVisibility(View.GONE);
                        tvEditDeal.setVisibility(View.GONE);
                        viewSeparator.setVisibility(View.GONE);
                        tvConfirmAndPostDeal.setVisibility(View.GONE);
//                        tvConfirmAndPostDeal.setText(getString(R.string.delete));
                        hitProductServiceDetailsApi(getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, ""));
                    }
                }
            } else {
                if (productDetails != null) {
                    setProductDetails();
                }
            }
        }
        layoutToolbar.setBackgroundResource(R.drawable.gradient_transluscent_overlay);

        productImagesAdapter = new ProductImagesAdapter(this, productImagesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                AppUtils.getInstance().setImages(ProductServicePreviewActivity.this, productImagesList.get(position), ivProductPic,
                        0, R.drawable.ic_placeholder);
            }
        });

        ivMenu.setImageResource(R.drawable.ic_back);

    }

    /**
     * method to set the adapters in views
     */
    private void setAdapters() {
        rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProductImages.setAdapter(productImagesAdapter);

        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float offset = Math.abs(verticalOffset);
            float totalScrollRange = appBarLayout.getTotalScrollRange();
            layoutToolbar.setBackgroundResource(offset == totalScrollRange ? R.drawable.toolbar_gradient : R.drawable.gradient_transluscent_overlay);
        });

    }


    @OnClick({R.id.iv_menu, R.id.tv_confirm_and_post_deal, R.id.tv_edit_deal, R.id.iv_product_pic, R.id.tv_read_more, R.id.iv_dates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                finish();
                break;
            case R.id.tv_read_more:
                if (!readMore) {
                    readMore = true;
                    tvProductDescription.setMaxLines(100);
                    tvReadMore.setText(getString(R.string.read_less));
                }else {
                    readMore = false;
                    tvProductDescription.setMaxLines(2);
                    tvReadMore.setText(getString(R.string.read_more));
                }
                break;
            case R.id.tv_confirm_and_post_deal:
                if (fromClass.equals(Constants.AppConstant.POSTED_BY_ME)) {

                } else {
                    showDialogueForConfirmDeal();
                }
                break;
            case R.id.tv_edit_deal:
                startActivityForResult(new Intent(this, AddProductServiceActivity.class)
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.DEAL_PREVIEW)
                                .putExtra(Constants.IntentConstant.DEAL_ID, productDetails.getId())
                                .putExtra(Constants.IntentConstant.IS_PRODUCT, productDetails.isProduct())
                        , Constants.IntentConstant.REQUEST_EDIT_DEAL);
                finish();
                break;
            case R.id.iv_product_pic:
                showImages();
                break;
            case R.id.iv_dates:
                if (productDetails != null) {
                    createAlertDialog(productDetails.getSlotsDayList());
                }
                break;
        }
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
            String formatDate = AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT);
            if (!aList.contains(formatDate)) aList.add(formatDate);
        }
        //First Step: convert ArrayList to an Object array.
        Object[] objNames = aList.toArray();
        //Second Step: convert Object array to String array
        String[] dates = Arrays.copyOf(objNames, objNames.length, String[].class);

        AlertDialog.Builder alertBox = new AlertDialog.Builder(this)
                .setItems(dates, null);
        alertBox.show();
    }

    /**
     * method to show the images
     */
    public void showImages() {
        if (productImagesList != null && productImagesList.size() > 0) {
            Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
            intent.putExtra("imagelist", productImagesList);
            intent.putExtra("from", productDetails.getDealName());
            if (productImagesAdapter != null)
                intent.putExtra("pos", productImagesAdapter.selectedPosition);
            startActivityForResult(intent, 10001);
        }
    }

    /**
     * method to show confirm deal dialog
     */
    private void showDialogueForConfirmDeal() {
        confirmDealDialog = new CustomDialogForConfirmDeal(this, productDetails.getDealPostingDate(), new PopupItemDialogCallback() {
            @Override
            public void onItemOneClick() {
                if (AppUtils.getInstance().isInternetAvailable(ProductServicePreviewActivity.this)) {
                    hitAddDealApi();
                }
            }

            @Override
            public void onItemTwoClick() {
                confirmDealDialog.dismiss();
                startActivity(new Intent(ProductServicePreviewActivity.this, AddProductServiceActivity.class)
                        .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails)
                        .putExtra(Constants.IntentConstant.IS_PRODUCT, productDetails.isProduct())
                        .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.DEAL_PREVIEW));
                finish();
            }

            @Override
            public void onItemThreeClick() {
                confirmDealDialog.dismiss();
            }
        });
        confirmDealDialog.show();
    }


    /**
     * method to hit the add deal api
     */
    private void hitAddDealApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, productDetails.getCategory().getCatId());
        params.put(Constants.NetworkConstant.PARAM_SUB_CATEGORY_ID, productDetails.getSubCategory().getSubCatId());
        params.put(Constants.NetworkConstant.PARAM_DEAL_NAME, productDetails.getDealName());
        params.put(Constants.NetworkConstant.PARAM_SERVICE_NAME, productDetails.getDealName());
        params.put(Constants.NetworkConstant.PARAM_DEAL_START_DATE, AppUtils.getInstance().formatDate(productDetails.getValidityStartDate(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_DEAL_END_DATE, AppUtils.getInstance().formatDate(productDetails.getValidityEndDate(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_ORIGINAL_PRICE, productDetails.getOriginalPrice());
        params.put(Constants.NetworkConstant.PARAM_PUBLISH_DATE, AppUtils.getInstance().formatDate(productDetails.getDealPostingDate(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, productDetails.isProduct() ? Constants.NetworkConstant.PRODUCT : Constants.NetworkConstant.SERVICE);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY, productDetails.getCurrency());
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, productDetails.getCurrencyCode());
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_SYMBOL, productDetails.getCurrencySymbol());
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, productDetails.getQuantity());
        params.put(Constants.NetworkConstant.PARAM_SELLING_PRICE, productDetails.getSellingPrice());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, productDetails.getDiscountPercentage());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT_VALIDATION_START, AppUtils.getInstance().formatDate(productDetails.getValidityStartDate(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT_VALIDATION_END, AppUtils.getInstance().formatDate(productDetails.getValidityEndDate(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_IS_SAVE_DEAL, "1");
        params.put(Constants.NetworkConstant.PARAM_DESCRIPTION, productDetails.getDescription());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_CHARGES, productDetails.getDeliveryCharges());
        params.put(Constants.NetworkConstant.PARAM_IMAGE_ARRAY, new Gson().toJson(getImagesArray()));
        params.put(Constants.NetworkConstant.PARAM_TIME_SLOT_ARRAY, new Gson().toJson(getTimeSlotsArray()));
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_METHOD, productDetails.getPaymentMode());
        params.put(Constants.NetworkConstant.PARAM_SERVICE_TYPE, productDetails.getMode());
        params.put(Constants.NetworkConstant.PARAM_IS_RECURSIVE, productDetails.isRecursive() ? "1" : "0");
        params.put(Constants.NetworkConstant.PARAM_SLOT_SELECTED_DATE, new Gson().toJson(productDetails.getSlotsDayList()));
        Call<ResponseBody> call = apiInterface.hitAddDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ADD_DEAL);
    }

    /**
     * method to get time slot array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getTimeSlotsArray() {
        ArrayList<HashMap<String, String>> slotsList = new ArrayList<>();
        if (productDetails.getTimeSlotsBean() != null && productDetails.getTimeSlotsBean().size() > 0) {
            for (com.shopoholicbuddy.models.buddyscheduleresponse.Result slotsBean : productDetails.getTimeSlotsBean()) {
                HashMap<String, String> timeSlotMap = new HashMap<>();
                String slotStartTime = AppUtils.getInstance().formatDate(slotsBean.getSlotStartTime(), "hh:mm a", "HH:mm:ss");
                String slotEndTime = AppUtils.getInstance().formatDate(slotsBean.getSlotEndTime(), "hh:mm a", "HH:mm:ss");
                timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_DATE, slotsBean.getSlotStartDate());
                timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_DATE, slotsBean.getSlotEndDate());
                timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_TIME, slotsBean.isAllDay() ? "00:00:00" : slotStartTime);
                timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_TIME, slotsBean.isAllDay() ? "01:00:00" : slotEndTime);
                timeSlotMap.put(Constants.NetworkConstant.PARAM_DEAL_ID, slotsBean.getId());
                timeSlotMap.put(Constants.NetworkConstant.PARAM_ALL_DAYS, slotsBean.isAllDay() ? "1" : "0");
//            timeSlotMap.put(Constants.NetworkConstant.PARAM_HOURS, "");
//            timeSlotMap.put(Constants.NetworkConstant.PARAM_MIN, "");
                timeSlotMap.put(Constants.NetworkConstant.PARAM_PRICE, slotsBean.getPrice());
                timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, slotsBean.getId());
                slotsList.add(timeSlotMap);
            }
        }
        return slotsList;
    }

    /**
     * method to get images array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getImagesArray() {
        ArrayList<HashMap<String, String>> imagesList = new ArrayList<>();
        for (int i = 0; i < productDetails.getImagesList().size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_IMAGE_PATH, productDetails.getImagesList().get(i));
            imageMap.put(Constants.NetworkConstant.PARAM_MEDIA_TYPE, "1");
            imageMap.put(Constants.NetworkConstant.PARAM_DEFAULT_IMAGE, i == 0 ? "1" : "0");
            imagesList.add(imageMap);
        }
        return imagesList;
    }

    /**
     * method to set product details data
     */
    private void setProductDetails() {
        rootView.setVisibility(View.VISIBLE);
        String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        String currencyCode = productDetails.getCurrencyCode();
        String currencySymbol = productDetails.getCurrencySymbol();
        productDetails.setOriginalPrice(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getOriginalPrice())));
        productDetails.setSellingPrice(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getSellingPrice())));
        productImagesList.addAll(productDetails.getImagesList());
        if (dealImage.equals("") && productImagesList.size() > 0) {
            AppUtils.getInstance().setImages(this, productImagesList.get(0), ivProductPic, 0, R.drawable.ic_placeholder);
        } else {
            tvProductImages.setVisibility(View.GONE);
            viewProductImages.setVisibility(View.GONE);
//            ivProductPic.setImageResource(R.drawable.ic_placeholder);
        }
//        appbar.setExpanded(true);
        tvProductUserName.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME) + " "
                + AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME)));
        AppUtils.getInstance().setCircularImages(this, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_IMAGE), civProductUserImage, R.drawable.ic_side_menu_user_placeholder);
        tvFlatDiscount.setText(TextUtils.concat(productDetails.getDiscountPercentage() + getString(R.string.discount_post_string)));
        tvProductDescription.setText(productDetails.getDescription());
        tvProductDescription.post(() -> {
            int lineCount = tvProductDescription.getLineCount();
            runOnUiThread(() -> {
                if (lineCount > 2) {
                    tvReadMore.setVisibility(View.VISIBLE);
                    tvProductDescription.setMaxLines(2);
                }else {
                    tvReadMore.setVisibility(View.GONE);
                    tvProductDescription.setMaxLines(2);
                }
            });
        });
        tvDealPosted.setText(productDetails.getDealPostingDate());
        tvProductDetails.setText(productDetails.getDealName());
        tvDealValidity.setText(TextUtils.concat(productDetails.getValidityStartDate() + " " + getString(R.string.to) + " " + productDetails.getValidityEndDate()));

        tvProductType.setVisibility(View.VISIBLE);
        tvProductType.setText(TextUtils.concat(productDetails.getSubCategory().getSubCatName() + " " + getString(R.string.in) + " " + productDetails.getCategory().getCatName()));

        if (productDetails.isProduct()) {
            tvOriginalPrice.setText(TextUtils.concat(currencySymbol + productDetails.getOriginalPrice()));
            tvFinalPrice.setText(TextUtils.concat(currencySymbol + productDetails.getSellingPrice()));
//            tvProductType.setVisibility(View.VISIBLE);
//            tvProductType.setText(TextUtils.concat(productDetails.getSubCategory().getSubCatName() + " " + getString(R.string.in) + " " + productDetails.getCategory().getCatName()));

            llProduct.setVisibility(View.VISIBLE);
            tvQuantity.setText(productDetails.getQuantity());
//            tvCurrency.setText(getString(productDetails.getCurrency().equals("2") ? R.string.txt_rupees : productDetails.getCurrency().equals("1") ? R.string.txt_dollar : R.string.txt_singapur_dollar));
            tvCurrency.setText(currencySymbol);

            tvSlotAvailability.setVisibility(View.GONE);
            fblTimeSlots.setVisibility(View.GONE);
            viewTimeSlots.setVisibility(View.GONE);

            tvDeliveryCharges.setText(TextUtils.concat(currencySymbol + productDetails.getDeliveryCharges()));
            if (productDetails.getDiscountPercentage().equals("0")) {
                tvProductName.setText(productDetails.getDealName());
            } else {
                tvProductName.setText(TextUtils.concat(getString(R.string.flat) + " " + productDetails.getDiscountPercentage() + getString(R.string.off_on) + " " + productDetails.getDealName()));
            }
        } else {
            rlPrice.setVisibility(View.GONE);
//            tvOriginalPrice.setText(TextUtils.concat(currency + productDetails.getOriginalPrice() + getString(R.string.per_hour)));
//            tvFinalPrice.setText(TextUtils.concat(currency + productDetails.getSellingPrice() + getString(R.string.per_hour)));
            tvOriginalPrice.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getOriginalPrice())) + (productDetails.getMode().equals("1") ? "" : getString(R.string.per_hour))));
            tvFinalPrice.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getSellingPrice())) + (productDetails.getMode().equals("1") ? "" : getString(R.string.per_hour))));

            tvProductUserType.setVisibility(View.GONE);
//            tvProductType.setVisibility(View.GONE);
            llProduct.setVisibility(View.GONE);
            labelDeliveryCharges.setVisibility(View.GONE);
            tvDeliveryCharges.setVisibility(View.GONE);
            viewDeliveryCharges.setVisibility(View.GONE);
            tvSlotAvailability.setVisibility(View.VISIBLE);
            fblTimeSlots.setVisibility(View.VISIBLE);
            viewTimeSlots.setVisibility(View.VISIBLE);
            tvProductName.setText(productDetails.getDealName());
            for (int i = 0; i < productDetails.getTimeSlotsBean().size(); i++) {
                String startDate = AppUtils.getInstance().formatDate(productDetails.getTimeSlotsBean().get(i).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                String endDate = AppUtils.getInstance().formatDate(productDetails.getTimeSlotsBean().get(i).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                if (productDetails.getTimeSlotsBean().get(i).isAllDay()){
//                    String time = startDate + " - " + endDate + " (" + getString(R.string.all_day_available)/* + ")"*/;
                    String time = getString(R.string.all_day_available);
                    fblTimeSlots.addView(timeSlotView(time + " (" + productDetails.getCurrencySymbol() +
                           productDetails.getTimeSlotsBean().get(i).getPrice() + ")"));
                }else {
//                    String time = startDate + " " + productDetails.getTimeSlotsBean().get(i).getSlotStartTime()+ " - " + endDate + " " + productDetails.getTimeSlotsBean().get(i).getSlotEndTime();
                    String time = productDetails.getTimeSlotsBean().get(i).getSlotStartTime()+ " - " + productDetails.getTimeSlotsBean().get(i).getSlotEndTime();
                    fblTimeSlots.addView(timeSlotView(time + " (" + productDetails.getCurrencySymbol() +
                            productDetails.getTimeSlotsBean().get(i).getPrice() + ")"));
                }
            }
            if (productDetails.getSlotsDayList() != null && productDetails.getSlotsDayList().size() > 0) {
                ivDates.setVisibility(View.VISIBLE);
            }
        }
        tvAddress.setText(productDetails.getAddress());
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvProductUserType.setVisibility(View.VISIBLE);
        tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.buddy) + ")"));
    }

    /**
     * method to inflate view for language list
     *
     * @param text
     * @return
     */
    private View timeSlotView(String text) {
        final View view = LayoutInflater.from(this).inflate(R.layout.item_selection_view_white, null);
        final CustomTextView tvSelection = view.findViewById(R.id.text_view);
        tvSelection.setText(text);
        view.setTag(text);
        return view;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_ADD_DEAL:
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppDatabase.removeDealFromDb(productDetails.getId());
                        setResult(RESULT_OK);
                        finish();
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_DEAL_DETAILS:
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
//                        rootView.setVisibility(View.VISIBLE);
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        ProductServiceDetailsResponse detailsResponse = new Gson().fromJson(response, ProductServiceDetailsResponse.class);
                        setProductDetailsData(detailsResponse.getResult());
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);

    }


    /**
     * Method to hit the deal details api
     */
    private void hitProductServiceDetailsApi(String dealId) {
        rootView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.GET_DEAL_DETAILS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitProductServiceDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_DEAL_DETAILS);
    }


    /**
     * method to open edit screen
     *
     * @param result
     */
    private void setProductDetailsData(com.shopoholicbuddy.models.productservicedetailsresponse.Result result) {
        productDetails = new AddProductServiceModel();

        productDetails.setId(result.getId());
        productDetails.setDealName(result.getName());
        productDetails.setDescription(result.getDescription());
        productDetails.setOriginalPrice(result.getOrignalPrice());
        productDetails.setDealDetailStartTiming(result.getDealStartTime());
        productDetails.setDealDetailEndTiming(result.getDealEndTime());
        productDetails.setValidityStartDate(AppUtils.getInstance().formatDate(result.getDealStartTime(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        productDetails.setValidityEndDate(AppUtils.getInstance().formatDate(result.getDealEndTime(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        productDetails.setDealPostingDate(AppUtils.getInstance().formatDate(result.getPublishDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        productDetails.setDiscountPercentage(result.getDiscount());
        if (!result.getDealImages().equals(""))
            productDetails.setImagesList(new ArrayList<>(Arrays.asList(result.getDealImages().split(","))));
        productDetails.setSellingPrice(result.getSellingPrice());
        productDetails.setAddress(result.getBuddyAddress());
        productDetails.setLatitude(result.getBuddyLatitude());
        productDetails.setLongitude(result.getBuddyLongitude());

        ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> slotsBeanArrayList = new ArrayList<>();
        for (ServiceSlot slot : result.getServiceSlot()) {
            com.shopoholicbuddy.models.buddyscheduleresponse.Result slotsBean = new com.shopoholicbuddy.models.buddyscheduleresponse.Result();
            String startTime = AppUtils.getInstance().formatDate(slot.getSlotStartTime(), "HH:mm:ss", "hh:mm a");
            String endTime = AppUtils.getInstance().formatDate(slot.getSlotEndTime(), "HH:mm:ss", "hh:mm a");
            String startDate = slot.getSlotStartDate();
            String endDate = slot.getSlotEndDate();
            slotsBean.setSlotStartDate(startDate);
            slotsBean.setSlotEndDate(endDate);
            slotsBean.setSlotStartTime(startTime);
            slotsBean.setSlotEndTime(endTime);
            slotsBean.setAllDay(slot.getAllDays().equals("1"));
            slotsBean.setPrice(slot.getPrice());
            slotsBeanArrayList.add(slotsBean);
        }
        if (productDetails.getSlotsDayList() == null) productDetails.setSlotsDayList(new ArrayList<>());
        for (SlotSelectedDate slotDate : result.getSlotSelectedDate()) {
            productDetails.getSlotsDayList().add(slotDate.getSelectedDate());
        }
        productDetails.setTimeSlotsBean(slotsBeanArrayList);
        productDetails.setQuantity(result.getQuantity());
        productDetails.setDeliveryCharges(result.getDileveryCharge());
        productDetails.setCurrency(result.getCurrency());
        productDetails.setCurrencyCode(result.getCurrencyCode());
        productDetails.setCurrencySymbol(result.getCurrencySymbol());
        productDetails.setProduct(result.getProductType().equals("1"));
        productDetails.setMode(result.getServiceType());

        Result cat = new Result();
        cat.setCatId(result.getCatId());
        cat.setCatName(result.getCategoryName());
        productDetails.setCategory(cat);

        com.shopoholicbuddy.models.subcategoryresponse.Result subCat = new com.shopoholicbuddy.models.subcategoryresponse.Result();
        subCat.setSubCatId(result.getSubcatId());
        subCat.setSubCatName(result.getSubcategoryName());
        productDetails.setSubCategory(subCat);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, productDetails.toString());
        setProductDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.GONE);
    }
}
