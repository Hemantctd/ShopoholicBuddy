package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
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

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.ProductImagesAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForMakeOffer;
import com.shopoholicbuddy.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholicbuddy.interfaces.MakeOfferDialogCallback;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
import com.shopoholicbuddy.models.productservicedetailsresponse.ProductServiceDetailsResponse;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 09-Apr-18.
 */

public class ProductDetailsActivity extends BaseActivity implements NetworkListener {

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
    @BindView(R.id.images_view)
    View imagesView;
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
    @BindView(R.id.label_attributes)
    CustomTextView labelAttributes;
    @BindView(R.id.tv_attributes)
    CustomTextView tvAttributes;
    @BindView(R.id.view_attributes)
    View viewAttributes;
    @BindView(R.id.tv_home_delivery_available)
    CustomTextView tvHomeDeliveryAvailable;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.tv_read_more)
    CustomTextView tvReadMore;
    @BindView(R.id.tv_search_buddy)
    CustomTextView tvSearchBuddy;
    @BindView(R.id.tv_product_availability)
    CustomTextView tvProductAvailability;
    @BindView(R.id.ll_product_availability)
    LinearLayout llProductAvailability;
    @BindView(R.id.tv_deal_posted)
    CustomTextView tvDealPosted;
    @BindView(R.id.tv_label_deal_expired)
    CustomTextView tvLabelDealExpired;
    @BindView(R.id.tv_commission)
    CustomTextView tvCommission;
    @BindView(R.id.tv_deal_expiry)
    CustomTextView tvDealExpiry;
    @BindView(R.id.tv_slot_availability)
    CustomTextView tvSlotAvailability;
    @BindView(R.id.fbl_time_slots)
    FlexboxLayout fblTimeSlots;
    @BindView(R.id.view_time_slots)
    View viewTimeSlots;
    @BindView(R.id.tv_request_sharing)
    CustomTextView tvRequestSharing;
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.layout_retry)
    LinearLayout layoutRetry;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private String dealId = "";
    private ArrayList<String> productImagesList;
    private ProductImagesAdapter productImagesAdapter;
    private Result productDetails;
    private String fromClass = "";
    private String dealImage = "";
    private boolean readMore = false;
    public String videoUrl;
    public BitmapDrawable videoDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_services);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        if (AppUtils.getInstance().isInternetAvailable(this)) hitProductServiceDetailsApi();
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
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        layoutToolbar.setBackgroundResource(R.drawable.gradient_transluscent_overlay);

        productImagesList = new ArrayList<>();
        productImagesAdapter = new ProductImagesAdapter(this, productImagesList, (position, view) -> {
            if (productImagesList.get(position).equals(videoUrl)) {
                ivProductPic.setImageDrawable(videoDrawable);
                ivPlayVideo.setVisibility(View.VISIBLE);
            } else {
                AppUtils.getInstance().setImages(ProductDetailsActivity.this, productImagesList.get(position), ivProductPic, 0, R.drawable.ic_placeholder);
                ivPlayVideo.setVisibility(View.GONE);
            }
        });

        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_home_shopper_more_ic);
        menuThirdRight.setImageResource(R.drawable.ic_home_shopper_share_ic);
        menuRight.setVisibility(View.VISIBLE);
        menuSecondRight.setVisibility(View.VISIBLE);
        menuThirdRight.setVisibility(View.VISIBLE);
        menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_unfilledf);
        if (getIntent() != null && getIntent().getExtras() != null) {
            dealId = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, "");
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            dealImage = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_IMAGE, "");
            AppUtils.getInstance().setImages(this, dealImage, ivProductPic, 0, R.drawable.ic_placeholder);
        }

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


    /**
     * method to show the images
     */
    public void showImages() {
        if (productImagesList != null) {
            ArrayList<String> images = new ArrayList<String>(productImagesList);
            images.remove(videoUrl);
            if (images.size() > 0) {
                Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
                intent.putExtra("imagelist", images);
                intent.putExtra("from", productDetails.getName());
                if (productImagesAdapter != null)
                    intent.putExtra("pos", productImagesAdapter.selectedPosition);
                startActivityForResult(intent, 10001);
            }
        }
    }

    @OnClick({R.id.tv_request_sharing, R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right, R.id.iv_menu, R.id.tv_search_buddy, R.id.iv_play_video, R.id.iv_product_pic, R.id.tv_read_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_buddy:
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
            case R.id.tv_request_sharing:
                if (AppUtils.getInstance().isLoggedIn(this) && (productDetails.getRequestStatus().equals("0") || productDetails.getRequestStatus().equals(""))) {
                    new CustomDialogForMakeOffer(ProductDetailsActivity.this, 4, productDetails.getCurrencySymbol(), "", "", (status, currency, price, type) -> {
                        if (AppUtils.getInstance().isInternetAvailable(ProductDetailsActivity.this)) {
                            hitShareProductApi(price);
                        }
                    }).show();
                }
                break;
            case R.id.menu_second_right:
                if (productDetails != null && productDetails.getIsBookmark() != null) {
                    if (AppUtils.getInstance().isLoggedIn(this) && AppUtils.getInstance().isInternetAvailable(this)) {
                        if (productDetails.getIsBookmark().equals("1")) {
                            productDetails.setIsBookmark("2");
                            menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_unfilledf);
                        } else {
                            productDetails.setIsBookmark("1");
                            menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_filledf);
                        }
                        hitBookmarkProductsApi();
                    }
                }
                break;
            case R.id.menu_right:
                if (productDetails != null) {
                    AppUtils.getInstance().showMorePopUp(this, menuRight, getString(R.string.block), getString(R.string.report), "", 1,
                            new PopupItemDialogCallback() {
                                @Override
                                public void onItemOneClick() {
                                    showBlockDialog();
                                }

                                @Override
                                public void onItemTwoClick() {
                                    CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(
                                            ProductDetailsActivity.this, 3, "", "", "", new MakeOfferDialogCallback() {
                                        @Override
                                        public void onSelect(String status, String currency, String text, int type) {
                                            if (AppUtils.getInstance().isInternetAvailable(ProductDetailsActivity.this)) {
                                                hitReportProductApi(text);
                                            }
                                        }
                                    });
                                    customDialogForSelectDate.show();
                                }

                                @Override
                                public void onItemThreeClick() {
                                }
                            });
                }
                break;
            case R.id.menu_third_right:
                if (productDetails != null) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, productDetails.getName());
                    /*if (productImagesList.size() > 0) {
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(productImagesList.get(0)));
                    }*/
                    String dealUrl = productDetails.getDealUrl();
                    String user = "&user_type=" + AppUtils.getInstance().encryptString("2");
                    dealUrl += user;
                    /*if (!productDetails.getDealUrl().contains("http://tinyurl.com")) {
                        String url = productDetails.getDealUrl();
                        String[] params = productDetails.getDealUrl().split("Dealdetailinfo?");
                        if (params.length > 1) {
                            String queryParams = AppUtils.getInstance().decryptString(params[1]);
                            queryParams += "&user_type=2";
                            String encryptParam = AppUtils.getInstance().encryptString(queryParams);
                            dealUrl = params[0] + "Dealdetailinfo?" + encryptParam;
                        }
                    }*/
                    sendIntent.putExtra(Intent.EXTRA_TEXT, dealUrl);
                    sendIntent.setType("text/uri");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_deal)));
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.iv_product_pic:
                if (productImagesList != null && productImagesList.size() > 0 && productImagesList.size() > productImagesAdapter.selectedPosition) {
                    if (!productImagesList.get(productImagesAdapter.selectedPosition).equals(videoUrl)) {
                        showImages();
                    }
                }
                break;
            case R.id.iv_play_video:
                startActivity(new Intent(this, VideoviewActivity.class)
                        .putExtra(Constants.VIDEO_URL, videoUrl)
                        .putExtra(Constants.VIDEO_URL_THUMB, "")
                );
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * dialog to block user
     */
    private void showBlockDialog() {
        new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(getString(R.string.block_deal))
                .setMessage(getString(R.string.sure_to_block_deal))
                .setPositiveButton(getString(R.string.block), (dialog, which) -> {
                    if (AppUtils.getInstance().isInternetAvailable(ProductDetailsActivity.this)) {
                        hitBlockProductApi();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    // do nothing
                })
                .show();

    }

    /**
     * method to hit share deal api
     * @param price
     */
    private void hitShareProductApi(String price) {
        productDetails.setStatus("2");
        tvRequestSharing.setText(R.string.request_pending);
        productDetails.setRequestStatus("1");
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_CHARGE, price);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitShareDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SHARE_DEAL);
    }

    /**
     * method to hit block deal api
     */
    private void hitBlockProductApi() {
//        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBlockDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_BLOCK_DEAL);
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
        intent.putExtra(Constants.IntentConstant.IS_BOOKMARK, productDetails.getIsBookmark());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * method to hit report deal api
     *
     * @param message
     */
    private void hitReportProductApi(String message) {
//        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_REPORT, message);
        Call<ResponseBody> call = apiInterface.hitReportDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_REPORT_DEAL);
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * Method to hit the deal details api
     */
    private void hitProductServiceDetailsApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.GET_DEAL_DETAILS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitProductServiceDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_DEAL_DETAILS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_DEAL_DETAILS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        rootView.setVisibility(View.VISIBLE);
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        ProductServiceDetailsResponse detailsResponse = new Gson().fromJson(response, ProductServiceDetailsResponse.class);
                        if (detailsResponse.getResult().getDealImages() != null && !detailsResponse.getResult().getDealImages().equals("")) {
                            Collections.addAll(productImagesList, detailsResponse.getResult().getDealImages().split(","));
                        } else {
                            tvProductImages.setVisibility(View.GONE);
                            imagesView.setVisibility(View.GONE);
                        }
                        AppUtils.getInstance().saveImages(this, productImagesList);
                        videoUrl = detailsResponse.getResult().getVideoUrl();
                        if (videoUrl != null && !videoUrl.trim().equals("")) {
                            AsyncTask.execute(() -> {
                                try {
                                    productImagesList.add(videoUrl);
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(videoUrl, new HashMap<>());
                                    Bitmap bitmap = retriever.getFrameAtTime(10000, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
                                    videoDrawable = new BitmapDrawable(getResources(), bitmap);
                                    runOnUiThread(() -> {
                                        if (productImagesList.size() > 0)
                                            productImagesAdapter.notifyDataSetChanged();
                                    });
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        productImagesAdapter.notifyDataSetChanged();
                        setProductDetails(detailsResponse.getResult());
                        break;
                    default:
//                        layoutRetry.setVisibility(View.VISIBLE);
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                break;
            case Constants.NetworkConstant.REQUEST_SHARE_DEAL:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
//        layoutRetry.setVisibility(View.VISIBLE);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
//        layoutRetry.setVisibility(View.VISIBLE);
    }


    /**
     * Method to hit the signup api
     */
    public void hitBookmarkProductsApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        params.put(Constants.NetworkConstant.PARAM_STATUS, productDetails.getIsBookmark());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBookmarkProductsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                if (productDetails.getIsBookmark().equals("1")) {
                    productDetails.setIsBookmark("2");
                    menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_unfilledf);
                } else {
                    productDetails.setIsBookmark("1");
                    menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_filledf);
                }
            }

            @Override
            public void onFailure() {
                if (productDetails.getIsBookmark().equals("1")) {
                    productDetails.setIsBookmark("2");
                    menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_unfilledf);
                } else {
                    productDetails.setIsBookmark("1");
                    menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_filledf);
                }
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    /**
     * method to set product details data
     *
     * @param result
     */
    private void setProductDetails(Result result) {
        productDetails = result;
        String currency = getString(result.getCurrency().equals("2") ? R.string.rupees : result.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        String currencyCode = result.getCurrencyCode();
        String currencySymbol = result.getCurrencySymbol();
        if (dealImage.equals("") && productImagesList.size() > 0) {
            AppUtils.getInstance().setImages(this, productImagesList.get(0), ivProductPic, 0, R.drawable.ic_placeholder);
        }
//        appbar.setExpanded(true);
        tvProductUserName.setText(TextUtils.concat(result.getFirstName() + " " + result.getLastName()));
        AppUtils.getInstance().setCircularImages(this, result.getImage(), civProductUserImage, R.drawable.ic_side_menu_user_placeholder);
        tvOriginalPrice.setText(TextUtils.concat(currencySymbol + result.getOrignalPrice()));
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvFinalPrice.setText(TextUtils.concat(currencySymbol + result.getSellingPrice()));
        tvFlatDiscount.setText(TextUtils.concat(result.getDiscount() + getString(R.string.discount_post_string)));
        tvProductDescription.setText(result.getDescription());
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
        tvRequestSharing.setText(getString(result.getRequestStatus().equals("1") ? R.string.request_pending :
                result.getRequestStatus().equals("2") ? R.string.request_accepted : R.string.request_to_share));
        tvAttributes.setText(result.getCustomAttribute());
        tvDealPosted.setText(AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                Constants.AppConstant.DATE_FORMAT));
        tvProductDetails.setText(productDetails.getName());
        tvProductType.setVisibility(View.VISIBLE);
        tvProductType.setText(TextUtils.concat(result.getSubcategoryName() + " " + getString(R.string.in) + " " + result.getCategoryName()));

        if (fromClass.equals(Constants.AppConstant.SHARED_BY_ME) && !result.getCommissionPercentage().equals(0)) {
            tvCommission.setVisibility(View.VISIBLE);
            tvCommission.setText(TextUtils.concat(getString(R.string.commission) + " " + result.getCommissionPercentage() + "%"));
        }else {
            tvCommission.setVisibility(View.GONE);
        }

        if (result.getProductType().equals("1")) {
//            tvProductType.setVisibility(View.VISIBLE);
//            tvProductType.setText(TextUtils.concat(result.getSubCatName() + " " + getString(R.string.in) + " " + result.getCategoryName()));

            llProductAvailability.setVisibility(View.VISIBLE);
            tvHomeDeliveryAvailable.setText(getString(result.getHomeDelivery().equals("1") ? R.string.yes : R.string.no));
            tvSearchBuddy.setVisibility(result.getHomeDelivery().equals("1") ? View.GONE : View.VISIBLE);
            tvProductAvailability.setText(productDetails.getPaymentMethod().equals("1") ? getString(R.string.online) : (productDetails.getPaymentMethod().equals("2") ? getString(R.string.cash_on_delivery) : getString(R.string.online_cash_on_delivery)));

            tvSlotAvailability.setVisibility(View.GONE);
            fblTimeSlots.setVisibility(View.GONE);
            viewTimeSlots.setVisibility(View.GONE);

            if (result.getDiscount().equals("0")) {
                tvProductName.setText(result.getName());
            } else {
                tvProductName.setText(TextUtils.concat(getString(R.string.flat) + " " + result.getDiscount() + getString(R.string.off_on) + " " + result.getName()));
            }
            tvDealExpiry.setText(AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                    Constants.AppConstant.DATE_FORMAT));
        } else {
            tvProductUserType.setVisibility(View.GONE);
//            tvProductType.setVisibility(View.GONE);
            llProductAvailability.setVisibility(View.VISIBLE);
            tvProductAvailability.setText(productDetails.getPaymentMethod().equals("1") ? getString(R.string.online) : (productDetails.getPaymentMethod().equals("2") ? getString(R.string.cash_on_delivery) : getString(R.string.online_cash_on_delivery)));
            tvSearchBuddy.setVisibility(View.GONE);
            tvSlotAvailability.setVisibility(View.VISIBLE);
            fblTimeSlots.setVisibility(View.VISIBLE);
            viewTimeSlots.setVisibility(View.VISIBLE);
            tvProductName.setText(result.getName());
            for (int i = 0; i < result.getServiceSlot().size(); i++) {
//                String time = result.getServiceSlot().get(i).getSlotStartTime() + " - " + result.getServiceSlot().get(i).getSlotEndTime();
                String time = AppUtils.getInstance().getTimeSlots(result.getServiceSlot().get(i).getSlotStartTime(), result.getServiceSlot().get(i).getSlotEndTime());
                fblTimeSlots.addView(timeSlotView(time));
            }
            if (productDetails.getDealStartTime() != null && !productDetails.getDealStartTime().equals("") &&
                    productDetails.getDealEndTime() != null && !productDetails.getDealEndTime().equals("")) {
                tvLabelDealExpired.setText(getString(R.string.deal_validity));
                tvDealExpiry.setText(TextUtils.concat(
                        AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT) + " "
                                + getString(R.string.to)
                                + " " + AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT)));
            } else {
                tvLabelDealExpired.setVisibility(View.GONE);
                tvDealExpiry.setVisibility(View.GONE);
            }
        }

        if (result.getUserType().equals("1")) {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.merchant) + ")"));
            tvAddress.setText(productDetails.getStoreLocation());
        } else {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.buddy) + ")"));
            tvSearchBuddy.setVisibility(View.GONE);
            tvAddress.setText(productDetails.getBuddyAddress());
        }
        if (productDetails.getIsBookmark().equals("1")) {
            menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_filledf);
        } else {
            menuSecondRight.setImageResource(R.drawable.ic_home_cards_bookmark_unfilledf);
        }
        if (productDetails.getCustomAttribute() == null || productDetails.getCustomAttribute().equals("")) {
            tvAttributes.setVisibility(View.GONE);
            viewAttributes.setVisibility(View.GONE);
            labelAttributes.setVisibility(View.GONE);
        }
        tvSearchBuddy.setVisibility(View.GONE);

        switch (fromClass) {
            case Constants.AppConstant.POSTED_BY_ME:
                tvRequestSharing.setVisibility(View.GONE);
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;
            case Constants.AppConstant.SHARED_BY_ME:
                tvRequestSharing.setVisibility(View.GONE);
                break;
            default:
                tvRequestSharing.setVisibility(View.VISIBLE);
                break;
        }
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
    public void onBackPressed() {
        if (productDetails != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
            intent.putExtra(Constants.IntentConstant.IS_BOOKMARK, productDetails.getIsBookmark());
            setResult(RESULT_CANCELED, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.GONE);
    }
}
