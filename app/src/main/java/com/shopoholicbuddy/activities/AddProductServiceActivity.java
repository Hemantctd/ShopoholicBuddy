package com.shopoholicbuddy.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.DealImagesAdapter;
import com.shopoholicbuddy.adapters.PreferredCategoriesAdapter;
import com.shopoholicbuddy.adapters.SubCategoryAdapter;
import com.shopoholicbuddy.adapters.TaxAdapter;
import com.shopoholicbuddy.adapters.TimeSlotsAdapter;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.database.AppDatabase;
import com.shopoholicbuddy.dialogs.CustomDialogForConfirmDeal;
import com.shopoholicbuddy.dialogs.CustomDialogForCurrency;
import com.shopoholicbuddy.dialogs.CustomDialogForSaveDeal;
import com.shopoholicbuddy.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholicbuddy.interfaces.AddressCallback;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.interfaces.UserStatusDialogCallback;
import com.shopoholicbuddy.models.AddProductServiceModel;
import com.shopoholicbuddy.models.AddSlotsModel;
import com.shopoholicbuddy.models.TaxArr;
import com.shopoholicbuddy.models.countrymodel.CountryBean;
import com.shopoholicbuddy.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholicbuddy.models.preferredcategorymodel.Result;
import com.shopoholicbuddy.models.productservicedetailsresponse.ProductServiceDetailsResponse;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholicbuddy.models.subcategoryresponse.SubCategoryResponse;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;
import com.shopoholicbuddy.utils.ReverseGeocoding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class AddProductServiceActivity extends BaseActivity implements NetworkListener, TextWatcher, AmazonCallback, AddressCallback {

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
    @BindView(R.id.tv_select_category)
    CustomTextView tvSelectCategory;
    @BindView(R.id.tv_select_sub_category)
    CustomTextView tvSelectSubCategory;
    @BindView(R.id.et_deal_name)
    CustomEditText etDealName;
    @BindView(R.id.et_product_service_name)
    CustomEditText etProductServiceName;
    @BindView(R.id.view_product_service_name)
    View viewProductServiceName;
    @BindView(R.id.tv_start_timing)
    CustomTextView tvStartTiming;
    @BindView(R.id.tv_end_timing)
    CustomTextView tvEndTiming;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.tv_upload_image)
    CustomTextView tvUploadImage;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.rv_upload_image)
    RecyclerView rvUploadImage;
    @BindView(R.id.et_original_price)
    CustomEditText etOriginalPrice;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.view_price)
    View viewPrice;
    @BindView(R.id.tv_percentage)
    TextView tvPercentage;
    @BindView(R.id.iv_increase_percentage)
    ImageView ivIncreasePercentage;
    @BindView(R.id.iv_decrease_percentage)
    ImageView ivDecreasePercentage;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.tv_Final_Price)
    TextView tvFinalPrice;
    @BindView(R.id.tv_discount_price)
    TextView tvDiscountPrice;
    @BindView(R.id.tv_start_date)
    CustomTextView tvStartDate;
    @BindView(R.id.tv_end_date)
    CustomTextView tvEndDate;
    @BindView(R.id.tv_pricing)
    CustomTextView tvPricing;
    @BindView(R.id.tv_choose_date)
    CustomTextView tvChooseDate;
    @BindView(R.id.tv_add_time_slots)
    CustomTextView tvAddTimeSlots;
    @BindView(R.id.tv_time_slots)
    TextView tvTimeSlots;
    @BindView(R.id.rl_upload_images)
    RelativeLayout rlUploadImages;
    @BindView(R.id.rv_time_slots)
    RecyclerView rvTimeSlots;
    @BindView(R.id.tv_quantity)
    CustomEditText tvQuantity;
    @BindView(R.id.iv_increase_quantity)
    ImageView ivIncreaseQuantity;
    @BindView(R.id.iv_decrease_quantity)
    ImageView ivDecreaseQuantity;
    @BindView(R.id.ll_quantity)
    LinearLayout llQuantity;
    @BindView(R.id.view_quantity)
    View viewQuantity;
    @BindView(R.id.tv_select_currency)
    CustomTextView tvSelectCurrency;
    @BindView(R.id.tv_currency)
    CustomTextView tvCurrency;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.tv_delivery_sign)
    CustomTextView tvDeliverySign;
    @BindView(R.id.et_delivery_charges)
    CustomEditText etDeliveryCharges;
    @BindView(R.id.ll_charges)
    LinearLayout llCharges;
    @BindView(R.id.ll_delivery_charges)
    LinearLayout llDeliveryCharges;
    @BindView(R.id.tv_save_deal)
    TextView tvSaveDeal;
    @BindView(R.id.tv_submit_all)
    TextView tvSubmitAll;
    @BindView(R.id.ll_button)
    LinearLayout llButton;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCategories;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.image_loader)
    ProgressBar imageLoader;
    @BindView(R.id.ll_timings)
    LinearLayout llTimings;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.view_separator)
    View viewSeparator;
    @BindView(R.id.fl_root_view)
    LinearLayout rootView;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rb_online)
    RadioButton rbOnline;
    @BindView(R.id.rb_cod)
    RadioButton rbCod;
    @BindView(R.id.rb_both)
    RadioButton rbBoth;
    @BindView(R.id.ll_payment_mode)
    LinearLayout llPaymentMode;
    @BindView(R.id.view_payment_mode)
    View viewPaymentMode;
    @BindView(R.id.tv_select_mode)
    CustomTextView tvSelectMode;
    @BindView(R.id.tv_commission)
    CustomTextView tvCommission;
    @BindView(R.id.view_select_mode)
    View viewSelectMode;
    @BindView(R.id.ll_select_currency)
    LinearLayout llSelectCurrency;
    @BindView(R.id.view_price_discount)
    View viewPriceDiscount;
    @BindView(R.id.ll_price_discount)
    LinearLayout llPriceDiscount;
    @BindView(R.id.rb_recursive)
    CheckBox rbRecursive;
    @BindView(R.id.tv_add_tax)
    CustomTextView tvAddTax;
    @BindView(R.id.tv_tax)
    CustomTextView tvTax;
    @BindView(R.id.rl_taxes)
    RelativeLayout rlTaxes;
    @BindView(R.id.rv_taxes)
    RecyclerView rvTaxes;


    private PreferredCategoriesAdapter preferredCategoriesAdapter;
    private List<Result> categoryList;
    private List<com.shopoholicbuddy.models.subcategoryresponse.Result> subCategoryList;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private TimeSlotsAdapter addSlotsAdapter;
    private ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> timeSlotList;
    private SubCategoryAdapter subCategoryAdapter;
    private AddProductServiceModel addProductServiceModel;
    private boolean isProduct;
    private int count = 1;
    private Dialog dialog;
    private ArrayList<ImageBean> imagesBeanList;
    private int id = 0;
    private AmazonS3 mAmazonS3;
    private DealImagesAdapter imagesAdapter;
    private TaxAdapter taxAdapter;
    private CustomDialogForConfirmDeal confirmDealDialog;
    private String currency = "";
    private String currencySymbol = "";
    private String currencyCode = "";
    private boolean isEdit;
    private boolean openPlacePicker;
    private ArrayList<String> slotsDayList;
    private ArrayList<TaxArr> taxesArrayList;
    private boolean isDateClick = false;
    private String adminCommission = "0";
    private boolean isClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_service);
        ButterKnife.bind(this);
        initVariables();
        createAndSetAdapters();
        initializeAmazonS3();
        setListenersAndSetData();
        checkProductServiceDeal();
        seekbarProgress();
        rootView.requestFocus();
    }


    /**
     * initialize amazon service
     */
    private void initializeAmazonS3() {
        mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        mAmazonS3.setCallback(this);
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        categoryList = new ArrayList<>();
        subCategoryList = new ArrayList<>();
        timeSlotList = new ArrayList<>();
        imagesBeanList = new ArrayList<>();
        slotsDayList = new ArrayList<>();
        taxesArrayList = new ArrayList<>();
        addProductServiceModel = new AddProductServiceModel();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        dialog = new Dialog(this);
        tvSelectCurrency.setEnabled(false);
        tvSelectCurrency.setVisibility(View.GONE);

        adminCommission = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.ADMIN_COMMISSION);
    }


    /**
     * method to create and set adapter on views
     */
    private void createAndSetAdapters() {

        preferredCategoriesAdapter = new PreferredCategoriesAdapter(this, categoryList, (position, view) ->
                new Handler().postDelayed(() -> {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(0);
                    tvSelectCategory.setText(categoryList.get(position).getCatName());
                    addProductServiceModel.setCategory(categoryList.get(position));
                    subCategoryList.clear();
                    tvSelectSubCategory.setText("");
                    //  hit subcategory
                    if (AppUtils.getInstance().isInternetAvailable(AddProductServiceActivity.this)) {
                        hitSubGetCategoryListApi(categoryList.get(position).getCatId());
                    }
                }, 500));

        subCategoryAdapter = new SubCategoryAdapter(this, subCategoryList, (position, view) ->
                new Handler().postDelayed(() -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(0);
                    tvSelectSubCategory.setText(subCategoryList.get(position).getSubCatName());
                    addProductServiceModel.setSubCategory(subCategoryList.get(position));
                }, 500));
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        imagesAdapter = new DealImagesAdapter(this, imagesBeanList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_remove_image:
                    imagesBeanList.remove(position);
                    imagesAdapter.notifyDataSetChanged();
            }
        });
        rvUploadImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvUploadImage.setAdapter(imagesAdapter);

        taxesArrayList.add(new TaxArr());
        taxAdapter = new TaxAdapter(this, taxesArrayList, (RecyclerCallBack) (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_remove:
                    taxesArrayList.remove(position);
                    taxAdapter.notifyDataSetChanged();
            }
        });
        rvTaxes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTaxes.setAdapter(taxAdapter);

    }

    /**
     * methods to set listener on views
     */
    private void setListenersAndSetData() {
        etOriginalPrice.addTextChangedListener(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            addProductServiceModel = (AddProductServiceModel) getIntent().getExtras().getSerializable(Constants.AppConstant.OFFLINE_DEALS);
            if (addProductServiceModel == null)
                addProductServiceModel = new AddProductServiceModel();
        }
//        etDeliveryCharges.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        tvStartDate.addTextChangedListener(new TextWatcher() {
            private String startDate = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startDate = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tvStartDate.getText().length() != 0 && tvEndDate.getText().length() != 0
                        && !s.toString().equals(startDate)) {
                    tvChooseDate.setText("");
                    if (!isProduct) {
                        slotsDayList.clear();
                        timeSlotList.clear();
                        addSlotsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        tvEndDate.addTextChangedListener(new TextWatcher() {
            private String endDate = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                endDate = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tvStartDate.getText().length() != 0 && tvEndDate.getText().length() != 0
                        && !s.toString().equals(endDate)) {
                    tvChooseDate.setText("");
                    if (!isProduct) {
                        slotsDayList.clear();
                        timeSlotList.clear();
                        addSlotsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * method to set the time slots adapter
     */
    private void setAdapter() {

        addSlotsAdapter = new TimeSlotsAdapter(this, timeSlotList, (position, view) -> {
            switch (view.getId()) {
                case R.id.tv_start_time:
//                        setTime(position, 0);
                    showDateTimePicker(position, 0);
                    break;

                case R.id.tv_end_time:
//                        setTime(position, 1);
                    showDateTimePicker(position, 1);
                    break;


                case R.id.iv_delete_row:
                    timeSlotList.remove(position);
                    addSlotsAdapter.notifyItemRemoved(position);
                    addSlotsAdapter.notifyItemRangeChanged(position, timeSlotList.size());
                    break;


            }
        });
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTimeSlots.setAdapter(addSlotsAdapter);

    }

    // set time slots
    private void setTime(final int position, final int type) {
        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddProductServiceActivity.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                int hour = selectedHour;
                String timeSet;
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String min = "";
                if (selectedMinute < 10)
                    min = "0" + selectedMinute;
                else
                    min = String.valueOf(selectedMinute);

                // Append in a StringBuilder
                String aTime = String.valueOf(hour) + ':' + min + " " + timeSet;

                if (type == 0) {
                    if (!timeSlotList.get(position).getSlotEndTime().equals("")) {
                        if (AppUtils.getInstance().checkTime(AddProductServiceActivity.this, aTime, timeSlotList.get(position).getSlotEndTime())) {
                            timeSlotList.get(position).setSlotStartTime(aTime);
                        }
                    } else {
                        timeSlotList.get(position).setSlotStartTime(aTime);
                    }
                } else {
                    if (!timeSlotList.get(position).getSlotStartTime().equals("")) {
                        if (AppUtils.getInstance().checkTime(AddProductServiceActivity.this, timeSlotList.get(position).getSlotStartTime(), aTime)) {
                            timeSlotList.get(position).setSlotEndTime(aTime);
                        }
                    } else {
                        timeSlotList.get(position).setSlotEndTime(aTime);
                    }
                }

                addSlotsAdapter.notifyDataSetChanged();
            }
        }, hour, minute, false);
        mTimePicker.show();
    }

    /**
     * show date time picker
     */
    public void showDateTimePicker(final int position, final int type) {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        try {
            currentDate.setTimeInMillis(new SimpleDateFormat(DATE_FORMAT).parse(AppUtils.getInstance().getDate()).getTime() + 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AddProductServiceActivity.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
//                        Log.v(TAG, "The choosen one " + date.getTime());
                        // Append in a StringBuilder
//                        String aTime = String.valueOf(hour) + ':' + min + " " + timeSet;
                        String aTime = AppUtils.getInstance().getDate(date.getTimeInMillis(), "dd/MM/yyyy hh:mm a");
                        if (type == 0) {
                            if (!timeSlotList.get(position).getSlotEndTime().equals("")) {
                                if (AppUtils.getInstance().checkTime(AddProductServiceActivity.this, aTime, timeSlotList.get(position).getSlotEndTime())) {
                                    timeSlotList.get(position).setSlotStartTime(aTime);
                                }
                            } else {
                                timeSlotList.get(position).setSlotStartTime(aTime);
                            }
                        } else {
                            if (!timeSlotList.get(position).getSlotStartTime().equals("")) {
                                if (AppUtils.getInstance().checkTime(AddProductServiceActivity.this, timeSlotList.get(position).getSlotStartTime(), aTime)) {
                                    timeSlotList.get(position).setSlotEndTime(aTime);
                                }
                            } else {
                                timeSlotList.get(position).setSlotEndTime(aTime);
                            }
                        }
                        addSlotsAdapter.notifyDataSetChanged();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePicker.getDatePicker().setMaxDate(AppUtils.getInstance().getDateFromString(tvEndDate.getText().toString().trim(), DATE_FORMAT).getTime() + 5000);
        try {
            datePicker.getDatePicker().setMinDate(AppUtils.getInstance().getDateFromString(tvStartDate.getText().toString().trim(), DATE_FORMAT).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        datePicker.show();
    }


    @OnClick({R.id.iv_menu, R.id.tv_address, R.id.tv_select_category, R.id.tv_select_sub_category, R.id.tv_start_timing, R.id.tv_end_timing, R.id.tv_upload,
            R.id.iv_increase_percentage, R.id.iv_decrease_percentage, R.id.tv_start_date, R.id.tv_end_date, R.id.tv_choose_date, R.id.tv_time_slots,
            R.id.iv_increase_quantity, R.id.iv_decrease_quantity, R.id.tv_select_currency, R.id.tv_save_deal, R.id.tv_submit_all,
            R.id.rb_online, R.id.rb_cod, R.id.rb_both, R.id.tv_select_mode, R.id.rb_recursive, R.id.ll_price, R.id.tv_tax})
    public void onViewClicked(View view) {
        Calendar startDate, endDate;
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.tv_select_category:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    rvCategories.setAdapter(preferredCategoriesAdapter);
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
                }
                break;
            case R.id.tv_select_sub_category:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    rvCategories.setAdapter(subCategoryAdapter);
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
                }
                break;
            case R.id.tv_start_timing:
                AppUtils.getInstance().openTimePickerAndSetTime(this, tvStartTiming);
                break;
            case R.id.tv_end_timing:
                AppUtils.getInstance().openTimePickerAndSetTime(this, tvEndTiming);
                break;
            case R.id.tv_address:
                if (AppUtils.getInstance().isInternetAvailable(this) && !openPlacePicker) {
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
            case R.id.tv_upload:
                if (!isClicked) {
                    resetClicked();
                    /*if (checkPermissionForReadExtertalStorage()) {
                    goToImageGallery();
                } else {
                    requestPermissionForReadExtertalStorage();
                }*/
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        if (imagesBeanList.size() < 5) {
                            checkStoragePermission();
                        } else {
                            AppUtils.getInstance().showToast(this, getString(R.string.max_5_images_allowed));
                        }
                    }
                }
                break;

            case R.id.iv_increase_percentage:
                int prog = seekbar.getProgress();
                seekbar.setProgress(++prog);
                break;
            case R.id.iv_decrease_percentage:
                int prog1 = seekbar.getProgress();
                seekbar.setProgress(--prog1);
                break;
            case R.id.tv_start_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
//                tvChooseDate.setText("");
                    endDate = tvEndDate.getText().toString().trim().length() == 0 ? null :
                            AppUtils.getInstance().getCallenderObject(tvEndDate.getText().toString().trim());
                    if (!isProduct) {
                        Calendar date = Calendar.getInstance();
                        if (null == endDate) {
                            startDate = Calendar.getInstance();
                        } else {
                            date.setTime(endDate.getTime());
                            date.add(Calendar.MONTH, -2);
                            startDate = date.before(Calendar.getInstance()) ? Calendar.getInstance() : date;
                        }
                    } else {
                        startDate = Calendar.getInstance();
                    }
                    AppUtils.getInstance().openDatePicker(this, tvStartDate, startDate, endDate, tvStartDate.getText().toString().trim(), 2);
                }
                break;
            case R.id.tv_end_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDateClick = false;
                        }
                    }, 1000);
//                tvChooseDate.setText("");
                    startDate = tvStartDate.getText().toString().trim().length() == 0 ? Calendar.getInstance() :
                            AppUtils.getInstance().getCallenderObject(tvStartDate.getText().toString().trim());
                    if (!isProduct) {
                        Calendar date1 = Calendar.getInstance();
                        if (null == startDate || tvStartDate.getText().toString().length() == 0) {
                            endDate = null;
                        } else {
                            date1.setTime(startDate.getTime());
                            date1.add(Calendar.MONTH, 2);
                            endDate = date1;
                        }
                    } else {
                        endDate = null;
                    }
                    AppUtils.getInstance().openDatePicker(this, tvEndDate, startDate, endDate, tvEndDate.getText().toString().trim(), 2);
                }
                break;
            case R.id.tv_choose_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
                    startDate = Calendar.getInstance();
                    endDate = tvStartDate.getText().toString().trim().length() == 0 ? Calendar.getInstance() :
                            AppUtils.getInstance().getCallenderObject(tvStartDate.getText().toString().trim());
                    AppUtils.getInstance().openDatePicker(this, tvChooseDate, startDate, endDate, tvChooseDate.getText().toString().trim(), 2);
                }
                break;
            case R.id.tv_tax:
                if (taxesArrayList.size() >= 10) {
                    AppUtils.getInstance().showToast(this, getString(R.string.maximum_10_tax_allow));
                }else if (taxesArrayList.size() > 0) {
                    String taxName = taxesArrayList.get(taxesArrayList.size() - 1).getTaxName();
                    String taxPer = taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage();
                    if (taxName == null || taxName.isEmpty()) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_tax_name));
                    }else if (taxPer == null || taxPer.isEmpty()) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_tax_percentage));
                    }else if (Double.parseDouble(taxPer) >= 100) {
                        AppUtils.getInstance().showToast(this, getString(R.string.tax_per_must_less_than_100));
                    }else
                        taxesArrayList.add(new TaxArr());
                }else {
                    taxesArrayList.add(new TaxArr());
                }
                taxAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_time_slots:
                if (!isClicked) {
                    resetClicked();
                    if (tvStartDate.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_start_date));
                    } else if (tvEndDate.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_end_date));
                    } else if (tvAddress.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address_first));
                    } else if (currencyCode.equals("") || currencySymbol.equals("")) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_address));
                    } else {
                        String stDate = AppUtils.getInstance().formatDate(tvStartDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                        String edDate = AppUtils.getInstance().formatDate(tvEndDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                        if (slotsDayList.size() == 0)
                            slotsDayList.addAll(AppUtils.getInstance().getDates(stDate, edDate));
                        if (rbRecursive.isChecked()) {
                            Intent intent = new Intent(this, AddTimeSlotsActivity.class);
                            ArrayList<AddSlotsModel> list = new ArrayList<>();
                            for (int i = 0; i < timeSlotList.size(); i++) {
                                list.add(AddSlotsModel.getSlotModel(timeSlotList.get(i)));
                                list.get(i).setCurrency(currencySymbol);
                                list.get(i).setCurrencyCode(currencyCode);
                            }
                            intent.putExtra(Constants.IntentConstant.CURRENCY, currencySymbol);
                            intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                            intent.putExtra(Constants.IntentConstant.SLOTS, list);
                            intent.putExtra(Constants.IntentConstant.START_DATE, stDate);
                            intent.putExtra(Constants.IntentConstant.END_DATE, edDate);
                            startActivityForResult(intent, Constants.IntentConstant.REQUEST_TIME_SLOTS);

                        } else {
                            Intent intent = new Intent(this, TimeSlotsActivity.class);
                            intent.putStringArrayListExtra(Constants.IntentConstant.DAYS_LIST, slotsDayList);
                            intent.putExtra(Constants.IntentConstant.SLOTS, timeSlotList);
                            intent.putExtra(Constants.IntentConstant.CURRENCY, currencySymbol);
                            intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                            intent.putExtra(Constants.IntentConstant.START_DATE, tvStartDate.getText().toString().trim());
                            intent.putExtra(Constants.IntentConstant.END_DATE, tvEndDate.getText().toString().trim());
                            startActivityForResult(intent, Constants.IntentConstant.REQUEST_SLOTS);
                        }
                    }
                }
                break;
            case R.id.iv_increase_quantity:
                if (tvQuantity.getText().toString().trim().length() == 0) count = 0;
                try {
                    count = Integer.parseInt(tvQuantity.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (count < 999)
                    tvQuantity.setText(String.valueOf(++count));
                tvQuantity.setSelection(tvQuantity.getText().toString().length());
                break;
            case R.id.iv_decrease_quantity:
                if (tvQuantity.getText().toString().trim().length() == 0) count = 0;
                try {
                    count = Integer.parseInt(tvQuantity.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (count > 1 && count < 1000)
                    tvQuantity.setText(String.valueOf(--count));
                else
                    tvQuantity.setText(String.valueOf(count));
                tvQuantity.setSelection(tvQuantity.getText().toString().length());
                break;
            case R.id.tv_select_currency:
                CustomDialogForCurrency customDialogForCurrency = new CustomDialogForCurrency(this, 1, new UserStatusDialogCallback() {
                    @Override
                    public void onSelect(String status, int type) {
                        tvSelectCurrency.setText(status);
                        tvCurrency.setText("(" + status + ")");
                        currencySymbol = status;
                        currency = status;
                        if (etDeliveryCharges.getText().toString().trim().equals(""))
                            etDeliveryCharges.setText("0");
                        price();
                    }
                });
                customDialogForCurrency.show();
                break;
            case R.id.ll_price:
                etOriginalPrice.requestFocus();
                AppUtils.getInstance().showKeyboard(this);
                break;
            case R.id.tv_select_mode:
                CustomDialogForCurrency customDialogForMode = new CustomDialogForCurrency(this, 2, new UserStatusDialogCallback() {
                    @Override
                    public void onSelect(String status, int type) {
                        tvSelectMode.setText(status);
                    }
                });
                customDialogForMode.show();
                break;
            case R.id.tv_save_deal:
                if (isOfflineValidate())
                    showDialogueForSaveDeal();
                break;
            case R.id.tv_submit_all:
                if (isValidate())
                    showDialogueForConfirmDeal();
                break;
            case R.id.rb_recursive:
                if (tvStartDate.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_enter_start_date));
                    rbRecursive.setChecked(!rbRecursive.isChecked());
                } else if (tvEndDate.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_enter_end_date));
                    rbRecursive.setChecked(!rbRecursive.isChecked());
                } else {
                    timeSlotList.clear();
                    addSlotsAdapter.notifyDataSetChanged();
                    slotsDayList.clear();
                    String stDate = AppUtils.getInstance().formatDate(tvStartDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                    String edDate = AppUtils.getInstance().formatDate(tvEndDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                    if (slotsDayList.size() == 0)
                        slotsDayList.addAll(AppUtils.getInstance().getDates(stDate, edDate));
                }
                break;
            case R.id.rb_online:
                rbOnline.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                break;
            case R.id.rb_cod:
                rbCod.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                break;
            case R.id.rb_both:
                rbBoth.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
        }
    }


    /**
     * function to reset clicked
     */
    private void resetClicked() {
        isClicked = true;
        new Handler().postDelayed(() -> isClicked = false, 4000);
    }

    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                // permission already granted
                startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 5 - imagesBeanList.size())
                        , Constants.IntentConstant.REQUEST_GALLERY);
            }
        } else {
            //before marshmallow
            startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                            .putExtra("maxSelection", 5 - imagesBeanList.size())
                    , Constants.IntentConstant.REQUEST_GALLERY);
        }
    }


    /**
     * method to show dialog for save deals
     */
    private void showDialogueForSaveDeal() {
        CustomDialogForSaveDeal dialogueForSaveDeal = new CustomDialogForSaveDeal(this, () -> {
            String dealId = "";
            if (addProductServiceModel != null && addProductServiceModel.getId() != null)
                dealId = addProductServiceModel.getId();
            saveServiceDealData();
            boolean isNew = dealId.equals("");
            dealId = dealId.equals("") ? String.valueOf(100000 + new Random().nextInt(999999)) : dealId;
            addProductServiceModel.setId(dealId);
            addProductServiceModel.setProduct(isProduct);
            AppDatabase.setDealDetails(String.valueOf(dealId), new Gson().toJson(addProductServiceModel), isNew);
            finish();
        });
        dialogueForSaveDeal.show();
    }

    /**
     * method to show confirm deal dialog
     */
    private void showDialogueForConfirmDeal() {
        confirmDealDialog = new CustomDialogForConfirmDeal(this, tvChooseDate.getText().toString(), new PopupItemDialogCallback() {
            @Override
            public void onItemOneClick() {
                saveServiceDealData();
                if (AppUtils.getInstance().isInternetAvailable(AddProductServiceActivity.this)) {
                    hitAddDealApi();
                }
            }

            @Override
            public void onItemTwoClick() {
                confirmDealDialog.dismiss();
            }

            @Override
            public void onItemThreeClick() {
                confirmDealDialog.dismiss();
            }
        });
        confirmDealDialog.show();
    }

    /**
     * save service deal data
     */
    private void saveServiceDealData() {
//        addProductServiceModel.setDealName(String.valueOf(etDealName.getText().toString()));
//        addProductServiceModel.setProductServiceName(String.valueOf(etProductServiceName.getText().toString()));
        addProductServiceModel.setDealName(String.valueOf(etProductServiceName.getText().toString()));
        addProductServiceModel.setDescription(String.valueOf(etDescription.getText().toString()));
        addProductServiceModel.setDealDetailStartTiming(String.valueOf(tvStartDate.getText().toString()));
        addProductServiceModel.setDealDetailEndTiming(String.valueOf(tvEndDate.getText().toString()));
        addProductServiceModel.setValidityStartDate(String.valueOf(tvStartDate.getText().toString()));
        addProductServiceModel.setValidityEndDate(String.valueOf(tvEndDate.getText().toString()));
        addProductServiceModel.setDealPostingDate(String.valueOf(tvChooseDate.getText().toString()));

        ArrayList<String> images = new ArrayList<>();
        for (ImageBean bean : imagesBeanList) {
            images.add(bean.getServerUrl());
        }
        addProductServiceModel.setImagesList(images);
        addProductServiceModel.setTaxArrs(taxesArrayList);

        if (!isProduct) {
            addProductServiceModel.setTimeSlotsBean(timeSlotList);

            addProductServiceModel.setSellingPrice("0.0");
            addProductServiceModel.setOriginalPrice("0.0");
            addProductServiceModel.setDiscountPercentage("0");

            addProductServiceModel.setQuantity("0");
            addProductServiceModel.setDeliveryCharges("0.0");
            addProductServiceModel.setCurrency(tvSelectCurrency.getText().toString().trim().equals(getString(R.string.rupees)) ? "2" :
                    (tvSelectCurrency.getText().toString().trim().equals(getString(R.string.dollar)) ? "1" : "3"));
            addProductServiceModel.setCurrency(tvCurrency.getText().toString().trim().equals("(" + getString(R.string.rupees) + ")") ? "2" :
                    (tvCurrency.getText().toString().trim().equals("(" + getString(R.string.dollar) + ")") ? "1" : "3"));
            addProductServiceModel.setCurrencySymbol(currencySymbol);
            addProductServiceModel.setCurrencyCode(currencyCode);
            addProductServiceModel.setMode(tvSelectMode.getText().toString().trim().equals(getString(R.string.fixed)) ? "1" : "2");
//            addProductServiceModel.setPaymentMode("2");
            addProductServiceModel.setPaymentMode(rbOnline.isChecked() ? "1" : rbCod.isChecked() ? "2" : "3");
            addProductServiceModel.setRecursive(rbRecursive.isChecked());
            addProductServiceModel.setSlotsDayList(slotsDayList);
        } else {
            addProductServiceModel.setSellingPrice(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(etOriginalPrice.getText().toString().trim()) * ((100 - seekbar.getProgress()) / 100.00)));
            addProductServiceModel.setOriginalPrice(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(etOriginalPrice.getText().toString())));
            addProductServiceModel.setDiscountPercentage(String.valueOf(seekbar.getProgress()));

            addProductServiceModel.setQuantity(tvQuantity.getText().toString());
            addProductServiceModel.setDeliveryCharges(etDeliveryCharges.getText().toString());
            addProductServiceModel.setCurrency(tvSelectCurrency.getText().toString().trim().equals(getString(R.string.rupees)) ? "2" :
                    (tvSelectCurrency.getText().toString().trim().equals(getString(R.string.dollar)) ? "1" : "3"));
            addProductServiceModel.setCurrency(tvCurrency.getText().toString().trim().equals("(" + getString(R.string.rupees) + ")") ? "2" :
                    (tvCurrency.getText().toString().trim().equals("(" + getString(R.string.dollar) + ")") ? "1" : "3"));
            addProductServiceModel.setCurrencySymbol(currencySymbol);
            addProductServiceModel.setCurrencyCode(currencyCode);
            addProductServiceModel.setPaymentMode(rbOnline.isChecked() ? "1" : rbCod.isChecked() ? "2" : "3");
        }
        addProductServiceModel.setProduct(isProduct);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, addProductServiceModel.toString());

    }


    private void seekbarProgress() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPercentage.setText(TextUtils.concat(String.valueOf(progress) + "%"));
                price();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.IntentConstant.REQUEST_MULTIPLE_IMAGE_INTENT && data != null) {
            if (data.getStringArrayListExtra("result") != null) {
                ArrayList<String> imagesList = data.getStringArrayListExtra("result");
                for (String filePath : imagesList) {
                    startUpload(filePath);
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                getCurrentLocation(place);
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                ArrayList<String> imagesList = data.getExtras().getStringArrayList("result");
                if (imagesList != null) {
                    for (String filePath : imagesList) {
                        startUpload(filePath);
                    }
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_TIME_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ArrayList<AddSlotsModel> slotList = (ArrayList<AddSlotsModel>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            if (slotList != null) {
                timeSlotList.clear();
                for (int i = 0; i < slotList.size(); i++) {
                    timeSlotList.add(AddSlotsModel.getSlotModel(slotList.get(i)));
                    timeSlotList.get(i).setCurrency(currencySymbol);
                }
                addSlotsAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> slotList = (ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            ArrayList<String> daysList = data.getExtras().getStringArrayList(Constants.IntentConstant.DAYS_LIST);
            if (daysList != null) {
                slotsDayList.clear();
                slotsDayList.addAll(daysList);
            }
            if (slotList != null) {
                timeSlotList.clear();
                timeSlotList.addAll(slotList);
                addSlotsAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Method to get the current location of user
     *
     * @param location
     */
    private void getCurrentLocation(Place location) {
        if (location != null && location.getAddress() != null) {
            String address = "";
            if (location.getName() != null && !location.getName().equals("") && !location.getName().toString().contains("\"")) {
                address += location.getName() + ", ";
            }
            address += location.getAddress().toString();
            currencySymbol = "";
            currencyCode = "";
            if (addProductServiceModel == null)
                addProductServiceModel = new AddProductServiceModel();
            addProductServiceModel.setAddress(address);
            addProductServiceModel.setLatitude(String.valueOf(location.getLatLng().latitude));
            addProductServiceModel.setLongitude(String.valueOf(location.getLatLng().longitude));
            tvAddress.setText(address);
            new ReverseGeocoding(this, location.getLatLng().latitude, location.getLatLng().longitude, this).execute();
//            ArrayList<String> currency = AppUtils.getInstance().getCurrency(this, location.getLatLng().latitude, location.getLatLng().longitude);
//            currencySymbol = currency.get(0);
//            currencyCode = currency.get(1);
//            tvSelectCurrency.setText(currencySymbol);
//            tvCurrency.setText(currencySymbol);
//            price();
        } else {
            AppUtils.getInstance().showToast(this, getString(R.string.unable_to_fetch_location));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        price();
        if (etOriginalPrice.hasFocus()) {
            if (etOriginalPrice.getText().toString().equals("")) {
                tvDiscountPrice.setText(TextUtils.concat(currencySymbol + "0.00"));
            }
        }

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
            finish();
        }
    }

    /**
     * method to hit the category list api
     */
    private void hitGetCategoryListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        Call<ResponseBody> call = apiInterface.hitGetPreferredCategoryListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }


    /**
     * method to hit the sub-category list api
     *
     * @param catId
     */
    private void hitSubGetCategoryListApi(String catId) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, catId);
        Call<ResponseBody> call = apiInterface.hitGetSubCategoryListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SUB_CATEGORY);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CATEGORIES:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                        categoryList.clear();
                        String type = isProduct ? "1" : "2";
                        for (Result category : categoriesResponse.getResult()) {
                            if (category.getCategoryType().equals(type)) {
                                categoryList.add(category);
                            }
                        }
                        preferredCategoriesAdapter.notifyDataSetChanged();
                        break;
                }
                break;

            case Constants.NetworkConstant.REQUEST_SUB_CATEGORY:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        SubCategoryResponse subCategoryResponse = new Gson().fromJson(response, SubCategoryResponse.class);
                        subCategoryList.clear();
                        subCategoryList.addAll(subCategoryResponse.getResult());
                        subCategoryAdapter.notifyDataSetChanged();
                        break;
                }
                break;

            case Constants.NetworkConstant.REQUEST_ADD_DEAL:
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        if (isEdit) {
                            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                            ProductServiceDetailsResponse detailsResponse = new Gson().fromJson(response, ProductServiceDetailsResponse.class);
                            FirebaseDatabaseQueries.getInstance().createProductNode(detailsResponse.getResult());
                        }
                        if (addProductServiceModel.getId() != null && !addProductServiceModel.getId().equals("")) {
                            AppDatabase.removeDealFromDb(addProductServiceModel.getId());
                        }
                        finish();
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_DEAL_DETAILS:
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        rootView.setVisibility(View.VISIBLE);
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
     * mrthod to check the intent data
     */
    private void checkProductServiceDeal() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            isProduct = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_PRODUCT);
            if (isProduct) {
                tvTitle.setText(getString(R.string.add_new_product_deal));
                rlUploadImages.setVisibility(View.GONE);
                rbRecursive.setVisibility(View.GONE);
                rvTimeSlots.setVisibility(View.GONE);
                llTimings.setVisibility(View.GONE);
                etProductServiceName.setHint(getString(R.string.product_name));
                etOriginalPrice.setHint(getString(R.string.original_price));
                llPaymentMode.setVisibility(View.VISIBLE);
                viewPaymentMode.setVisibility(View.VISIBLE);
                tvSelectMode.setVisibility(View.GONE);
                viewSelectMode.setVisibility(View.GONE);

            } else {
                llTimings.setVisibility(View.GONE);
                tvTitle.setText(getString(R.string.add_new_service_deal));
                llQuantity.setVisibility(View.GONE);
                llDeliveryCharges.setVisibility(View.GONE);
                viewQuantity.setVisibility(View.GONE);
                etProductServiceName.setHint(getString(R.string.service_name));
//                etOriginalPrice.setHint(getString(R.string.original_price_per_hour));
                llPaymentMode.setVisibility(View.VISIBLE);
                viewPaymentMode.setVisibility(View.VISIBLE);
                tvSelectMode.setVisibility(View.VISIBLE);
                viewSelectMode.setVisibility(View.VISIBLE);
                rbRecursive.setVisibility(View.VISIBLE);

                llPriceDiscount.setVisibility(View.GONE);
                viewPriceDiscount.setVisibility(View.GONE);
                setAdapter();
            }

            //remove payment option
//            llPaymentMode.setVisibility(View.GONE);
//            viewPaymentMode.setVisibility(View.GONE);

            addProductServiceModel = (AddProductServiceModel) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            if (addProductServiceModel != null) {
                String currency = getString(addProductServiceModel.getCurrency().equals("2") ? R.string.rupees : addProductServiceModel.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                addProductServiceModel.setCurrency(currency);
                setDetails();
            } else {
                addProductServiceModel = new AddProductServiceModel();
            }

            String fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            //hit category
            if (AppUtils.getInstance().isInternetAvailable(this)) {
                hitGetCategoryListApi();
            }
            if (fromClass.equals(Constants.AppConstant.DEAL_PREVIEW)) {
                tvSaveDeal.setVisibility(View.GONE);
                viewSeparator.setVisibility(View.GONE);
                tvTitle.setText(getString(isProduct ? R.string.edit_product_deal : R.string.edit_service_deal));
                //hit category
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    hitGetCategoryListApi();
                }
            }
            if (fromClass.equals(Constants.AppConstant.OFFLINE_DEALS)) {
                tvSaveDeal.setVisibility(View.VISIBLE);
                viewSeparator.setVisibility(View.VISIBLE);
                tvTitle.setText(getString(isProduct ? R.string.edit_product_deal : R.string.edit_service_deal));
                if (addProductServiceModel.getSlotsDayList() != null && addProductServiceModel.getSlotsDayList().size() > 0)
                    slotsDayList.addAll(addProductServiceModel.getSlotsDayList());
                //hit category
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    hitGetCategoryListApi();
                }
            } else if (fromClass.equals(Constants.AppConstant.PROFILE_SKILLS) || fromClass.equals(Constants.AppConstant.POSTED_BY_ME)) {
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    isEdit = true;
                    tvSaveDeal.setVisibility(View.GONE);
                    viewSeparator.setVisibility(View.GONE);
                    tvTitle.setText(getString(isProduct ? R.string.edit_product_deal : R.string.edit_service_deal));
                    hitProductServiceDetailsApi(getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, ""));
                }
            }
        }
        if (TextUtils.isEmpty(tvAddress.getText().toString().trim())) {
            addProductServiceModel.setLatitude(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LATITUDE));
            addProductServiceModel.setLongitude(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LONGITUDE));
            if (!addProductServiceModel.getLatitude().equals("") && !addProductServiceModel.getLongitude().equals("")) {
                tvAddress.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.ADDRESS) + " " +
                        AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.ADDRESS2)).toString().trim());
                addProductServiceModel.setAddress(tvAddress.getText().toString());
                new ReverseGeocoding(this,
                        Double.parseDouble(addProductServiceModel.getLatitude()),
                        Double.parseDouble(addProductServiceModel.getLongitude()),
                        this).execute();
//                ArrayList<String> currency = AppUtils.getInstance().getCurrency(this,
//                        Double.parseDouble(addProductServiceModel.getLatitude()),
//                        Double.parseDouble(addProductServiceModel.getLongitude()));
//                currencySymbol = currency.get(0);
//                currencyCode = currency.get(1);
//                tvSelectCurrency.setText(currencySymbol);
//                tvCurrency.setText(currencySymbol);
//                price();
            }
        }
    }

    /**
     * method to calculate price
     */
    private void price() {
        tvDeliverySign.setText("(" + currencySymbol + ")");
        String originalPrice = "0";
        if (!etOriginalPrice.getText().toString().trim().equals(""))
            originalPrice = etOriginalPrice.getText().toString().trim();
        double fp = Double.parseDouble(originalPrice) * ((100 - seekbar.getProgress()) / 100.00);
        String finalPrice = String.format(Locale.ENGLISH, "%.2f", fp);
        tvDiscountPrice.setText(TextUtils.concat(currencySymbol + finalPrice));
        tvDiscountPrice.setText(TextUtils.concat(currencySymbol + finalPrice));
        if (adminCommission.equals("") || adminCommission.equals("0") || fp == 0) {
            tvCommission.setVisibility(View.GONE);
        } else {
            tvCommission.setVisibility(View.VISIBLE);
            double commission = Double.parseDouble(adminCommission);
            String message = commission
                    + "% "
                    + getString(R.string.admin_commission)
                    + " \n"
                    + getString(R.string.your_earning_will_be)
                    + " "
                    + currencySymbol
                    + String.format(Locale.ENGLISH, "%.2f", (fp - (fp * commission) / 100));
            tvCommission.setText(message);
        }
    }

    /**
     * method for validation
     *
     * @return
     */
    private boolean isValidate() {
        if (tvSelectCategory.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_category));
            return false;
        } else if (tvSelectSubCategory.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_sub_category));
            return false;
//        } else if (etDealName.getText().toString().length() == 0) {
//            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_deal_name));
//            return false;
        } else if (etProductServiceName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(isProduct ? R.string.please_enter_product_name : R.string.please_enter_service_name));
            return false;
        } else if (etDescription.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_description));
            return false;
        } else if (tvAddress.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
            return false;
        } else if (addProductServiceModel.getLatitude() == null || addProductServiceModel.getLongitude() == null
                || addProductServiceModel.getLatitude().equals("") || addProductServiceModel.getLongitude().equals("")
                || currencySymbol.equals("") || currencyCode.equals("")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_address));
            return false;
        } else if (imagesBeanList.size() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_upload_image));
            return false;
        } else if (isProduct) {
//            if (tvSelectCurrency.getText().toString().trim().length() == 0) {
//                AppUtils.getInstance().showToast(this, getString(R.string.please_select_currency));
//                return false;
//            } else
            if (etOriginalPrice.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_original_price));
                return false;
            }
        }
        if (tvStartDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_start_date));
            return false;
        } else if (tvEndDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_end_date));
            return false;
        } else if (tvChooseDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_date));
            return false;
        } else if (AppUtils.getInstance().checkDate(tvStartDate.getText().toString(), tvChooseDate.getText().toString())) {
            AppUtils.getInstance().showToast(this, getString(R.string.publish_date_check));
            return false;
        } else if (tvQuantity.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_quantity));
            return false;
//                tvChooseDate.setText("");
        } else if (tvQuantity.getText().toString().equals("0") || tvQuantity.getText().toString().equals("00") || tvQuantity.getText().toString().equals("000")) {
            AppUtils.getInstance().showToast(this, getString(R.string.quantity_greater_0));
            return false;
        } else if (taxesArrayList.size() == 0 || taxesArrayList.get(taxesArrayList.size() - 1).getTaxName().equals("") ||
                taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_taxes));
            return false;
        } else if (taxesArrayList.size() != 0 && !taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            try {
                for (TaxArr taxArr: taxesArrayList) {
                    if (Double.parseDouble(taxArr.getTaxPercentage()) >= 100) {
                        AppUtils.getInstance().showToast(this, getString(R.string.tax_per_must_less_than_100));
                        return false;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                AppUtils.getInstance().showToast(this, getString(R.string.tax_per_must_less_than_100));
                return false;
            }
        } else if (imageLoader.getVisibility() == View.VISIBLE) {
            AppUtils.getInstance().showToast(this, getString(R.string.images_loading_please_wait));
            return false;
        } else if (!isProduct) {
//            if (tvStartTiming.getText().toString().length() == 0) {
//                AppUtils.getInstance().showToast(this, getString(R.string.please_select_start_date));
//                return false;
//            } else if (tvEndTiming.getText().toString().length() == 0) {
//                AppUtils.getInstance().showToast(this, getString(R.string.please_select_end_date));
//                return false;
//            } else
            if (timeSlotList.size() == 0 || timeSlotList.get(timeSlotList.size() - 1).getSlotStartTime().equals("") || timeSlotList.get(timeSlotList.size() - 1).getSlotEndTime().equals("")) {
                AppUtils.getInstance().showToast(this, getString(R.string.Please_enter_slots));
                return false;
            }
        }

        return true;
    }


    /**
     * method for validation
     *
     * @return
     */
    private boolean isOfflineValidate() {
        if (etProductServiceName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(isProduct ? R.string.please_enter_product_name : R.string.please_enter_service_name));
            return false;
        } else if (etDescription.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_description));
            return false;
        } else if (isProduct) {
            if (etOriginalPrice.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_original_price));
                return false;
            }
        }
        if (tvStartDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_start_date));
            return false;
        } else if (tvEndDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_end_date));
            return false;
        } else if (tvChooseDate.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_choose_date));
            return false;
        } else if (tvQuantity.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_quantity));
            return false;
        } else if (tvQuantity.getText().toString().equals("0") || tvQuantity.getText().toString().equals("00") || tvQuantity.getText().toString().equals("000")) {
            AppUtils.getInstance().showToast(this, getString(R.string.quantity_greater_0));
            return false;
        } else if (taxesArrayList.size() == 0 || taxesArrayList.get(taxesArrayList.size() - 1).getTaxName().equals("") || taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_taxes));
            return false;
        } else if (taxesArrayList.size() != 0 && !taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            try {
                for (TaxArr taxArr: taxesArrayList) {
                    if (Double.parseDouble(taxArr.getTaxPercentage()) >= 100) {
                        AppUtils.getInstance().showToast(this, getString(R.string.tax_per_must_less_than_100));
                        return false;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                AppUtils.getInstance().showToast(this, getString(R.string.tax_per_must_less_than_100));
                return false;
            }
        } else if (!isProduct) {
            if (timeSlotList.size() == 0 || timeSlotList.get(timeSlotList.size() - 1).getSlotStartTime().equals("") || timeSlotList.get(timeSlotList.size() - 1).getSlotEndTime().equals("")) {
                AppUtils.getInstance().showToast(this, getString(R.string.Please_enter_slots));
                return false;
            }
        }
        return true;
    }

    /**
     * upload file in S3
     *
     * @param path
     */
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path, ++id);
        imagesBeanList.add(bean);
        imagesAdapter.notifyDataSetChanged();
        mAmazonS3.uploadImage(bean);
        showImageProgress();
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path, int id) {
        ImageBean bean = new ImageBean();
        bean.setId(String.valueOf(id));
        bean.setName("pic");
        bean.setImagePath(path);
        bean.setLoading(true);
        return bean;
    }


    @Override
    public void uploadSuccess(ImageBean imageBean) {
        imageBean.setLoading(false);
        updateImageBeanList(imageBean, 1);

    }

    @Override
    public void uploadFailed(ImageBean bean) {
        AppUtils.getInstance().showToast(this, getString(R.string.image_upload_fail));
        updateImageBeanList(bean, 2);
    }

    @Override
    public void uploadProgress(ImageBean bean) {
        AppUtils.getInstance().printLogMessage(this.getCallingPackage(), "Uploaded " + bean.getProgress() + " %");

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
    }


    /**
     * method to update image bean list
     *
     * @param imageBean
     * @param status
     */
    private void updateImageBeanList(ImageBean imageBean, int status) {
        for (int i = 0; i < imagesBeanList.size(); i++) {
            if (imageBean.getId().equals(imagesBeanList.get(i).getId())) {
                switch (status) {
                    case 1:
                        imagesBeanList.set(i, imageBean);
                        break;
                    case 2:
                        imagesBeanList.remove(imageBean);
                        break;
                }
                break;
            }
        }
        showImageProgress();
        imagesAdapter.notifyDataSetChanged();
    }

    /**
     * method to change loader state
     */
    private void showImageProgress() {
        boolean check = false;
        for (ImageBean bean : imagesBeanList) {
            if (bean.isLoading())
                check = true;
        }
        imageLoader.setVisibility(check ? View.VISIBLE : View.GONE);
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
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, addProductServiceModel.getCategory().getCatId());
        params.put(Constants.NetworkConstant.PARAM_SUB_CATEGORY_ID, addProductServiceModel.getSubCategory().getSubCatId());
        params.put(Constants.NetworkConstant.PARAM_DEAL_NAME, addProductServiceModel.getDealName());
        params.put(Constants.NetworkConstant.PARAM_SERVICE_NAME, addProductServiceModel.getDealName());
        params.put(Constants.NetworkConstant.PARAM_DEAL_START_DATE, AppUtils.getInstance().formatDate(addProductServiceModel.getValidityStartDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
//        params.put(Constants.NetworkConstant.PARAM_DEAL_START_DATE, "2019-02-02");
        params.put(Constants.NetworkConstant.PARAM_DEAL_END_DATE, AppUtils.getInstance().formatDate(addProductServiceModel.getValidityEndDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_ORIGINAL_PRICE, addProductServiceModel.getOriginalPrice());
        params.put(Constants.NetworkConstant.PARAM_PUBLISH_DATE, AppUtils.getInstance().formatDate(addProductServiceModel.getDealPostingDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, addProductServiceModel.isProduct() ? Constants.NetworkConstant.PRODUCT : Constants.NetworkConstant.SERVICE);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY, addProductServiceModel.getCurrency());
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, /*currencyCode*/currencyCode);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_SYMBOL, currencySymbol);
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, addProductServiceModel.getQuantity());
        params.put(Constants.NetworkConstant.PARAM_SELLING_PRICE, addProductServiceModel.getSellingPrice());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, addProductServiceModel.getDiscountPercentage());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT_VALIDATION_START, AppUtils.getInstance().formatDate(addProductServiceModel.getValidityStartDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
//        params.put(Constants.NetworkConstant.PARAM_DISCOUNT_VALIDATION_START,  "2019-02-02");
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT_VALIDATION_END, AppUtils.getInstance().formatDate(addProductServiceModel.getValidityEndDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_IS_SAVE_DEAL, "1");
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ADDRESS, addProductServiceModel.getAddress());
        params.put(Constants.NetworkConstant.PARAM_BUDDY_LATITUDE, addProductServiceModel.getLatitude());
        params.put(Constants.NetworkConstant.PARAM_BUDDY_LONGITUDE, addProductServiceModel.getLongitude());
        params.put(Constants.NetworkConstant.PARAM_DESCRIPTION, addProductServiceModel.getDescription());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_CHARGES, addProductServiceModel.getDeliveryCharges());
        params.put(Constants.NetworkConstant.PARAM_IMAGE_ARRAY, new Gson().toJson(getImagesArray()));
        params.put(Constants.NetworkConstant.PARAM_TIME_SLOT_ARRAY, new Gson().toJson(getTimeSlotsArray()));
        params.put(Constants.NetworkConstant.PARAM_TAX_ARR, new Gson().toJson(getTaxArray()));

        //remove payment option
//        params.put(Constants.NetworkConstant.PARAM_PAYMENT_METHOD, "2");
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_METHOD, addProductServiceModel.getCurrencyCode().equals("INR") ? addProductServiceModel.getPaymentMode() : "2");

        params.put(Constants.NetworkConstant.PARAM_SERVICE_TYPE, addProductServiceModel.getMode());
        params.put(Constants.NetworkConstant.PARAM_IS_RECURSIVE, addProductServiceModel.isRecursive() ? "1" : "0");
        params.put(Constants.NetworkConstant.PARAM_SLOT_SELECTED_DATE, new Gson().toJson(addProductServiceModel.getSlotsDayList()));
        Call<ResponseBody> call;
        if (isEdit) {
            setResult(RESULT_OK);
            params.put(Constants.NetworkConstant.PARAM_DEAL_ID, addProductServiceModel.getId());
            call = apiInterface.hitEditDealApi(AppUtils.getInstance().encryptData(params));
        } else {
            call = apiInterface.hitAddDealApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ADD_DEAL);
    }

    /**
     * method to get images array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getImagesArray() {
        ArrayList<HashMap<String, String>> imagesList = new ArrayList<>();
        for (int i = 0; i < imagesBeanList.size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_IMAGE_PATH, imagesBeanList.get(i).getServerUrl());
            imageMap.put(Constants.NetworkConstant.PARAM_MEDIA_TYPE, "1");
            imageMap.put(Constants.NetworkConstant.PARAM_DEFAULT_IMAGE, i == 0 ? "1" : "0");
            imagesList.add(imageMap);
        }
        return imagesList;
    }

    /**
     * method to get images array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getTaxArray() {
        ArrayList<HashMap<String, String>> taxList = new ArrayList<>();
        for (int i = 0; i < taxesArrayList.size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_TAX_NAME, taxesArrayList.get(i).getTaxName());
            imageMap.put(Constants.NetworkConstant.PARAM_TAX_VALUE, taxesArrayList.get(i).getTaxPercentage());
            taxList.add(imageMap);
        }
        return taxList;
    }


    /**
     * method to get time slot array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getTimeSlotsArray() {
        ArrayList<HashMap<String, String>> slotsList = new ArrayList<>();
        for (com.shopoholicbuddy.models.buddyscheduleresponse.Result slotsBean : timeSlotList) {
            HashMap<String, String> timeSlotMap = new HashMap<>();
            String slotStartTime = AppUtils.getInstance().formatDate(slotsBean.getSlotStartTime(), "hh:mm a", "HH:mm:ss");
            String slotEndTime = AppUtils.getInstance().formatDate(slotsBean.getSlotEndTime(), "hh:mm a", "HH:mm:ss");
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_DATE, slotsBean.getSlotStartDate());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_DATE, slotsBean.getSlotEndDate());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_TIME, slotsBean.isAllDay() ? "00:00:00" : slotStartTime);
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_TIME, slotsBean.isAllDay() ? "01:00:00" : slotEndTime);
            timeSlotMap.put(Constants.NetworkConstant.PARAM_DEAL_ID, addProductServiceModel.getId());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_ALL_DAYS, slotsBean.isAllDay() ? "1" : "0");
//            timeSlotMap.put(Constants.NetworkConstant.PARAM_HOURS, "");
//            timeSlotMap.put(Constants.NetworkConstant.PARAM_MIN, "");
            timeSlotMap.put(Constants.NetworkConstant.PARAM_PRICE, slotsBean.getPrice());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, slotsBean.getId());
            slotsList.add(timeSlotMap);
        }
        return slotsList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_GALLERY:
                boolean isRationalGalleryStorage = false;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        isRationalGalleryStorage = true;
                    }
                }
                boolean permissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
                if (grantResults.length > 0 && permissionsGranted) {
                    checkStoragePermission();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_storage_permission));
                }
                break;

        }

    }

    /**
     * method to set product details data
     */
    private void setDetails() {
//        String currency = getString(addProductServiceModel.getCurrency().equals("2") ? R.string.rupees : addProductServiceModel.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);

        List<String> list = new ArrayList<>(slotsDayList);
        tvSelectCategory.setText(addProductServiceModel.getCategory().getCatName());
        tvSelectSubCategory.setText(addProductServiceModel.getSubCategory().getSubCatName());
        etProductServiceName.setText(addProductServiceModel.getDealName());
        etDescription.setText(addProductServiceModel.getDescription());
        if (!addProductServiceModel.getLatitude().equals("") && !addProductServiceModel.getLongitude().equals(""))
            tvAddress.setText(addProductServiceModel.getAddress());

        currency = addProductServiceModel.getCurrency();
        currencyCode = addProductServiceModel.getCurrencyCode();

        if (currencyCode.equals("INR")) {
            llPaymentMode.setVisibility(View.VISIBLE);
            viewPaymentMode.setVisibility(View.VISIBLE);
        } else {
            llPaymentMode.setVisibility(View.GONE);
            viewPaymentMode.setVisibility(View.GONE);
        }

        //remove payment option
//        llPaymentMode.setVisibility(View.GONE);
//        viewPaymentMode.setVisibility(View.GONE);

        currencySymbol = addProductServiceModel.getCurrencySymbol();

        if (!addProductServiceModel.getLatitude().equals("") && !addProductServiceModel.getLongitude().equals("") && (currencyCode.equals("") || currencySymbol.equals(""))) {
            new ReverseGeocoding(this,
                    Double.parseDouble(addProductServiceModel.getLatitude()),
                    Double.parseDouble(addProductServiceModel.getLongitude()),
                    this).execute();
//            ArrayList<String> currency = AppUtils.getInstance().getCurrency(this,
//                    Double.parseDouble(addProductServiceModel.getLatitude()),
//                    Double.parseDouble(addProductServiceModel.getLongitude()));
//            currencySymbol = currency.get(0);
//            currencyCode = currency.get(1);
//            addProductServiceModel.setCurrencySymbol(currencySymbol);
//            addProductServiceModel.setCurrencyCode(currencyCode);
        }
//        tvSelectCurrency.setText(currencySymbol);
        etOriginalPrice.setText(addProductServiceModel.getOriginalPrice());
        tvDiscountPrice.setText(TextUtils.concat(addProductServiceModel.getCurrencySymbol() + " " + addProductServiceModel.getSellingPrice()));
        tvPercentage.setText(TextUtils.concat(addProductServiceModel.getDiscountPercentage() + "%"));
        seekbar.setProgress(Integer.parseInt(addProductServiceModel.getDiscountPercentage()));

        rbRecursive.setChecked(addProductServiceModel.isRecursive());

        tvStartDate.setText(addProductServiceModel.getValidityStartDate());
        tvEndDate.setText(addProductServiceModel.getValidityEndDate());
        tvChooseDate.setText(addProductServiceModel.getDealPostingDate());

        switch (addProductServiceModel.getPaymentMode()) {
            case "1":
                rbOnline.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                break;
            case "2":
                rbCod.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                break;
            default:
                rbBoth.setChecked(true);
                rbOnline.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbCod.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
                rbBoth.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
        }

        for (String image : addProductServiceModel.getImagesList()) {
            ImageBean imageBean = addDataInBean(image, ++id);
            imageBean.setLoading(false);
            imageBean.setServerUrl(image);
            imagesBeanList.add(imageBean);
        }
        imagesAdapter.notifyDataSetChanged();

        taxesArrayList.clear();
        taxesArrayList.addAll(addProductServiceModel.getTaxArrs());
        taxAdapter.notifyDataSetChanged();

        if (addProductServiceModel.isProduct()) {
            tvQuantity.setText(addProductServiceModel.getQuantity());
            tvSelectCurrency.setText(addProductServiceModel.getCurrencySymbol());
            tvCurrency.setText("(" + addProductServiceModel.getCurrencySymbol() + ")");
            tvDeliverySign.setText("(" + addProductServiceModel.getCurrencySymbol() + ")");
            etDeliveryCharges.setText(addProductServiceModel.getDeliveryCharges());


        } else {
            tvSelectMode.setText(addProductServiceModel.getMode().equals("1") ? getString(R.string.fixed) : getString(R.string.txt_per_hour));
            tvSelectCurrency.setText(addProductServiceModel.getCurrencySymbol());
            tvCurrency.setText("(" + addProductServiceModel.getCurrencySymbol() + ")");
            timeSlotList.addAll(addProductServiceModel.getTimeSlotsBean());
            addSlotsAdapter.notifyDataSetChanged();

        }
        slotsDayList.addAll(list);

        checkDealValidity();
    }


    /**
     * method to check the deal validity
     * and update date views
     */
    private void checkDealValidity() {
        Date endDate = AppUtils.getInstance().getDateFromString(addProductServiceModel.getValidityEndDate(), DATE_FORMAT);
        Date currentDate = AppUtils.getInstance().getDateFromString(AppUtils.getInstance().getDate(), DATE_FORMAT);
        if (currentDate.after(endDate)) {
            tvStartDate.setText("");
            tvEndDate.setText("");
            tvChooseDate.setText("");
            slotsDayList.clear();
            timeSlotList.clear();
            if (addSlotsAdapter != null) addSlotsAdapter.notifyDataSetChanged();
        }
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

        //hit category
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            hitGetCategoryListApi();
        }
    }


    /**
     * method to open edit screen
     *
     * @param result
     */
    private void setProductDetailsData(com.shopoholicbuddy.models.productservicedetailsresponse.Result result) {
        addProductServiceModel = new AddProductServiceModel();

        addProductServiceModel.setId(result.getId());
        addProductServiceModel.setDealName(result.getName());
        addProductServiceModel.setDescription(result.getDescription());
        addProductServiceModel.setOriginalPrice(result.getOrignalPrice());
        addProductServiceModel.setDealDetailStartTiming(result.getDealStartTime());
        addProductServiceModel.setDealDetailEndTiming(result.getDealEndTime());
        addProductServiceModel.setValidityStartDate(AppUtils.getInstance().formatDate(result.getDealStartTime(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        addProductServiceModel.setValidityEndDate(AppUtils.getInstance().formatDate(result.getDealEndTime(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        addProductServiceModel.setDealPostingDate(AppUtils.getInstance().formatDate(result.getPublishDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        addProductServiceModel.setDiscountPercentage(result.getDiscount());
        addProductServiceModel.setImagesList(new ArrayList<>(Arrays.asList(result.getDealImages().split(","))));
        addProductServiceModel.setSellingPrice(result.getSellingPrice());
        addProductServiceModel.setAddress(result.getBuddyAddress());
        addProductServiceModel.setLatitude(result.getBuddyLatitude());
        addProductServiceModel.setLongitude(result.getBuddyLongitude());
        addProductServiceModel.setPaymentMode(result.getPaymentMethod());
        addProductServiceModel.setMode(result.getServiceType());

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
            addProductServiceModel.setRecursive(slot.getIsRecursive().equals("1"));
        }
        addProductServiceModel.setTimeSlotsBean(slotsBeanArrayList);
        addProductServiceModel.setQuantity(result.getQuantity());
        addProductServiceModel.setDeliveryCharges(result.getDileveryCharge());
        String currency = getString(result.getCurrency().equals("2") ? R.string.rupees : result.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        addProductServiceModel.setCurrency(currency);
        addProductServiceModel.setCurrencySymbol(result.getCurrencySymbol());
        addProductServiceModel.setCurrencyCode(result.getCurrencyCode());
        addProductServiceModel.setProduct(result.getProductType().equals("1"));

        Result cat = new Result();
        cat.setCatId(result.getCatId());
        cat.setCatName(result.getCategoryName());
        addProductServiceModel.setCategory(cat);

        addProductServiceModel.setTaxArrs(result.getTaxArr());

        com.shopoholicbuddy.models.subcategoryresponse.Result subCat = new com.shopoholicbuddy.models.subcategoryresponse.Result();
        subCat.setSubCatId(result.getSubcatId());
        subCat.setSubCatName(result.getSubcategoryName());
        addProductServiceModel.setSubCategory(subCat);
        for (int i = 0; i < result.getSlotSelectedDate().size(); i++) {
            if (!slotsDayList.contains(result.getSlotSelectedDate().get(i).getSelectedDate())) {
                slotsDayList.add(result.getSlotSelectedDate().get(i).getSelectedDate());
            }
        }
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, addProductServiceModel.toString());
        setDetails();
    }

    @Override
    public void setAddress(Address address) {
        if (address != null) {
            String countryCode = address.getCountryCode();
            ArrayList<CountryBean> countries = AppUtils.getInstance().getAllCountries(this);
            for (CountryBean country : countries) {
                if (country.getISOCode().equals(countryCode)) {
                    currencySymbol = country.getCurrency();
                    currencyCode = country.getCurrencyCode();
                    if (currencyCode.equals("INR")) {
                        llPaymentMode.setVisibility(View.VISIBLE);
                    } else {
                        llPaymentMode.setVisibility(View.GONE);
                    }

                    //remove payment option
//                    llPaymentMode.setVisibility(View.GONE);
//                    viewPaymentMode.setVisibility(View.GONE);
                    break;
                }
            }
            tvSelectCurrency.setText(currencySymbol);
            tvCurrency.setText("(" + currencySymbol + ")");
            price();
        }
    }
}
