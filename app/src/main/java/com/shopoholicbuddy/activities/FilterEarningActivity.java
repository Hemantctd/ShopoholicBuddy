package com.shopoholicbuddy.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.PreferredCategoriesAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForTimePeriod;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.interfaces.UserStatusDialogCallback;
import com.shopoholicbuddy.models.EarningFilterModel;
import com.shopoholicbuddy.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholicbuddy.models.preferredcategorymodel.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 10-May-18.
 */
public class FilterEarningActivity extends BaseActivity implements NetworkListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_select_time_period)
    CustomTextView tvSelectTimePeriod;
    @BindView(R.id.tv_start_timing)
    CustomTextView tvStartTiming;
    @BindView(R.id.tv_end_timing)
    CustomTextView tvEndTiming;
    @BindView(R.id.ll_timings)
    LinearLayout llTimings;
    @BindView(R.id.tv_select_category)
    CustomTextView tvSelectCategory;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;


    private String startDate = "";
    private String endDate = "";
    private String month = "";
    private String year = "";
    private EarningFilterModel earningFilterModel;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private PreferredCategoriesAdapter prefferdCategoriesAdapter;
    private List<Result> categoryList;
    private boolean isDateClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_earning);
        ButterKnife.bind(this);
        initVariables();
        createAndSetAdapters();
        getDataFromIntent();
        hitGetCategoryListApi();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        categoryList = new ArrayList<>();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);
        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.filter_statistics));
        btnAction.setText(getString(R.string.apply));
    }

    /**
     * method to create and set adapter on views
     */
    private void createAndSetAdapters() {
        prefferdCategoriesAdapter = new PreferredCategoriesAdapter(this, categoryList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(() -> {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(0);
                    tvSelectCategory.setText(categoryList.get(position).getCatName());
                    earningFilterModel.setCategory(categoryList.get(position));
                }, 500);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(prefferdCategoriesAdapter);
    }

    /**
     * method to get data from Intent
     */
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null){
            earningFilterModel = (EarningFilterModel) getIntent().getExtras().getSerializable(Constants.IntentConstant.EARNING_FILTER_MODEL);
        }
        if (earningFilterModel == null){
            earningFilterModel = new EarningFilterModel();
        }else {
            switch (earningFilterModel.getTimePeriod()) {
                case Constants.AppConstant.WEEKLY:
                    tvSelectTimePeriod.setText(getString(R.string.last_week));
                    break;
                case Constants.AppConstant.MONTHLY:
                    tvSelectTimePeriod.setText(getString(R.string.last_month));
                    break;
                case Constants.AppConstant.YEARLY:
                    tvSelectTimePeriod.setText(getString(R.string.last_year));
                    break;
                default:
                    tvStartTiming.setText(earningFilterModel.getStartDate());
                    tvEndTiming.setText(earningFilterModel.getEndDate());
                    break;
            }
            if (earningFilterModel.getCategory() != null) {
                tvSelectCategory.setText(earningFilterModel.getCategory().getCatName());
            }
        }
    }


    @OnClick({R.id.iv_back,R.id.tv_clear, R.id.tv_select_time_period, R.id.tv_start_timing, R.id.tv_end_timing, R.id.tv_select_category, R.id.btn_action})
    public void onViewClicked(View view) {
        Calendar startTiming, endTiming;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -100);
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_clear:
                new AlertDialog.Builder(this, R.style.DatePickerTheme)
                        .setMessage(getString(R.string.sure_to_clear_filter))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            if (AppUtils.getInstance().isInternetAvailable(this)) {
                                earningFilterModel = new EarningFilterModel();
                                tvSelectTimePeriod.setText("");
                                tvSelectCategory.setText("");
                                tvStartTiming.setText("");
                                tvEndTiming.setText("");
                                btnAction.performClick();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                            // do nothing
                        })
                        .show();



                break;
            case R.id.tv_select_time_period:
                CustomDialogForTimePeriod dialogForTimePeriod = new CustomDialogForTimePeriod(this, new UserStatusDialogCallback() {
                    @Override
                    public void onSelect(String status, int type) {
                        tvStartTiming.setText("");
                        tvEndTiming.setText("");
                        switch (type) {
                            case 1:
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.AppConstant.SERVICE_DATE_FORMAT);
                                Calendar calendar = Calendar.getInstance();
                                endDate = dateFormat.format(calendar.getTime());
                                calendar.add(Calendar.DAY_OF_YEAR, -7);
                                startDate = dateFormat.format(calendar.getTime());
                                tvSelectTimePeriod.setText(getString(R.string.last_week));
                                earningFilterModel.setTimePeriod(Constants.AppConstant.WEEKLY);
                                break;
                            case 2:
                                startDate = "";
                                endDate = "";
                                month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
                                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                                tvSelectTimePeriod.setText(getString(R.string.last_month));
                                earningFilterModel.setTimePeriod(Constants.AppConstant.MONTHLY);
                                break;
                            case 3:
                                startDate = "";
                                endDate = "";
                                month = "";
                                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                                tvSelectTimePeriod.setText(getString(R.string.last_year));
                                earningFilterModel.setTimePeriod(Constants.AppConstant.YEARLY);
                                break;
                        }
                    }
                });
                dialogForTimePeriod.show();
                break;
            case R.id.tv_start_timing:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
                    startDate = "";
                    endDate = "";
                    month = "";
                    tvSelectTimePeriod.setText("");
                    earningFilterModel.setTimePeriod("");
                    endTiming = tvEndTiming.getText().toString().trim().length() == 0 ? Calendar.getInstance() :
                            AppUtils.getInstance().getCallenderObject(tvEndTiming.getText().toString().trim());
                    AppUtils.getInstance().openDatePicker(this, tvStartTiming, calendar, endTiming, tvStartTiming.getText().toString().trim(), 1);
                }
                break;
            case R.id.tv_select_category:
                new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
                /*overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivityForResult(new Intent(this, PreferredCategoriesActivity.class)
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FILTER_EARNING)
                                .putExtra(Constants.IntentConstant.IS_CATEGORY, true)
                        , Constants.IntentConstant.REQUEST_CATEGORIES);*/
                break;
            case R.id.tv_end_timing:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
                    startDate = "";
                    endDate = "";
                    month = "";
                    tvSelectTimePeriod.setText("");
                    earningFilterModel.setTimePeriod("");
                    startTiming = tvStartTiming.getText().toString().trim().length() == 0 ? calendar :
                            AppUtils.getInstance().getCallenderObject(tvStartTiming.getText().toString().trim());
                    AppUtils.getInstance().openDatePicker(this, tvEndTiming, startTiming, Calendar.getInstance(), tvEndTiming.getText().toString().trim(), 1);
                }
                break;
            case R.id.btn_action:
                if (TextUtils.isEmpty(tvSelectTimePeriod.getText().toString().trim())) {
                    startDate = tvStartTiming.getText().toString().trim();
                    endDate = tvEndTiming.getText().toString().trim();
                }else {
                    startDate = "";
                    endDate = "";
                }
//                if ((!startDate.equals("") && endDate.equals("")) || (startDate.equals("") && !endDate.equals(""))) {
//                    AppUtils.getInstance().showToast(FilterEarningActivity.this, getString(R.string.please_fill_timing));
//                } else {
//                earningFilterModel.setTimePeriod(tvSelectTimePeriod.getText().toString().trim());
                    earningFilterModel.setStartDate(startDate);
                    earningFilterModel.setEndDate(endDate);
                    earningFilterModel.setMonth(month);
                    earningFilterModel.setYear(year);
                    int count = 0;
                    if (!startDate.equals("") || !endDate.equals("")) count++;
                    if (earningFilterModel.getCategory() != null) count++;
                    if (!tvSelectTimePeriod.getText().toString().equals("")) count++;
                    earningFilterModel.setCount(count);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.EARNING_FILTER_MODEL, earningFilterModel);
                    setResult(RESULT_OK, intent);
                    finish();
//                }
                break;
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
            super.onBackPressed();
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

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CATEGORIES:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                        categoryList.clear();
                        categoryList.addAll(categoriesResponse.getResult());
                        prefferdCategoriesAdapter.notifyDataSetChanged();
                        break;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
              if (requestCode == Constants.IntentConstant.REQUEST_CATEGORIES) {
                if (data != null && data.getExtras() != null) {
                    Result category = (Result) data.getExtras().getSerializable(Constants.IntentConstant.CATEGORY);
                    if (category != null) {
                        tvSelectCategory.setText(category.getCatName());
                        earningFilterModel.setCategory(category);
                    }
                }
            }
        }
    }
}
