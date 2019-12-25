package com.shopoholicbuddy.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.MyEarningsAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.interfaces.SnackBarItemClick;
import com.shopoholicbuddy.models.EarningFilterModel;
import com.shopoholicbuddy.models.earninglistresponse.EarningListResponse;
import com.shopoholicbuddy.models.earninglistresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
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
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.NetworkConstant.REQUEST_EARNING;

/**
 * Class to select preferred categories
 */

public class EarningListActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right_count)
    ImageView menuRightCount;
    @BindView(R.id.tv_filter_count)
    CustomTextView tvFilterCount;
    @BindView(R.id.fl_menu_right_home)
    FrameLayout flMenuRightHome;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.fl_menu_right)
    FrameLayout flMenuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private List<Result> myEarningsList;
    private MyEarningsAdapter myEarningsAdapter;
    private String fromClass = "";
    private boolean isLoading;
    private String categoryId = "";
    private String timePeriod = "";
    private String startDate = "";
    private String endDate = "";
    private LinearLayoutManager layoutManager;
    private boolean isMoreData;
    private boolean isPagination;
    private EarningFilterModel earningFilterModel;
    private int count = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_list);
        ButterKnife.bind(this);
        initVariables();
        setListener();
        setToolbar();
        setAdapters();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            swipeRefreshLayout.setRefreshing(false);
            isPagination = false;
            hitGetEarningListApi();
        }
    }

    /**
     * method to set toolbar
     */
    private void setToolbar() {
        tvTitle.setText(getString(R.string.my_earnings));
        ivMenu.setImageResource(R.drawable.ic_back);
        flMenuRightHome.setVisibility(View.VISIBLE);
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        myEarningsList = new ArrayList<Result>();
        earningFilterModel = new EarningFilterModel();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myEarningsAdapter = new MyEarningsAdapter(this, myEarningsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
            }
        });
    }


    @OnClick({R.id.iv_menu, R.id.fl_menu_right_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.fl_menu_right_home:
                startActivityForResult(new Intent(this, FilterEarningActivity.class)
                                .putExtra(Constants.IntentConstant.EARNING_FILTER_MODEL, earningFilterModel)
                        , Constants.IntentConstant.REQUEST_FILTER);

                break;
        }
    }

    /**
     * method to set listener on views
     */
    private void setListener() {
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = layoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= myEarningsList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(EarningListActivity.this)) {
                            isPagination = true;
                            hitGetEarningListApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(EarningListActivity.this)) {
                    isPagination = false;
                    count = 0;
                    hitGetEarningListApi();

                }
            }
        });


    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(myEarningsAdapter);
    }

    /**
     * method to hit login api
     */
    private void hitGetEarningListApi() {
        isLoading = true;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        switch (earningFilterModel.getTimePeriod()) {
            case Constants.AppConstant.WEEKLY:
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = dateFormat.format(calendar.getTime());
                endDate = AppUtils.getInstance().getDate();
                break;
            case Constants.AppConstant.MONTHLY:
                calendar.add(Calendar.MONTH, -1);
                startDate = dateFormat.format(calendar.getTime());
                endDate = AppUtils.getInstance().getDate();
                break;
            case Constants.AppConstant.YEARLY:
                calendar.add(Calendar.YEAR, -1);
                startDate = dateFormat.format(calendar.getTime());
                endDate = AppUtils.getInstance().getDate();
                break;
            default:
                startDate = earningFilterModel.getStartDate();
                endDate = earningFilterModel.getEndDate();
                break;
        }

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, categoryId);
        params.put(Constants.NetworkConstant.PARAM_TIME_PEROID, timePeriod);
        params.put(Constants.NetworkConstant.PARAM_START_DATE, AppUtils.getInstance().formatDate(startDate, DATE_FORMAT, SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_END_DATE, AppUtils.getInstance().formatDate(endDate, DATE_FORMAT, SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitBuddyEarningListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, REQUEST_EARNING);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case REQUEST_EARNING:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        EarningListResponse earningListResponse = new Gson().fromJson(response, EarningListResponse.class);
                        if (isPagination) {
                            isPagination = false;
                        } else {
                            myEarningsList.clear();
                        }
                        myEarningsList.addAll(earningListResponse.getResult());
                        isMoreData = earningListResponse.getNext() != -1;
                        if (isMoreData) count = earningListResponse.getNext();
                        myEarningsAdapter.notifyDataSetChanged();
                        if (myEarningsList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;

                    case Constants.NetworkConstant.NO_DATA:
                        myEarningsList.clear();
                        myEarningsAdapter.notifyDataSetChanged();
                        if (myEarningsList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().showToast(this, response);
        if (isPagination) {
            isPagination = false;
        } else {
            myEarningsList.clear();
            showErrorSnackBar();
        }
    }

    @Override
    public void onFailure() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (isPagination) {
            isPagination = false;
        } else {
            myEarningsList.clear();
            showErrorSnackBar();
        }
    }


    /**
     * method to show snackbar to show error
     */
    private void showErrorSnackBar() {
        AppUtils.getInstance().showSnackBar(EarningListActivity.this, getString(R.string.network_issue), new SnackBarItemClick() {
            @Override
            public void onSnackBarItemClick() {
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(EarningListActivity.this)) {
                    hitGetEarningListApi();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_FILTER && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                earningFilterModel = (EarningFilterModel) data.getExtras().getSerializable(Constants.IntentConstant.EARNING_FILTER_MODEL);
                if (earningFilterModel != null) {
                    if (earningFilterModel.getCategory() != null)
                        categoryId = earningFilterModel.getCategory().getCatId();
                    else
                        categoryId = "";
                    timePeriod = earningFilterModel.getTimePeriod();
                    startDate = earningFilterModel.getStartDate();
                    endDate = earningFilterModel.getEndDate();
                    tvFilterCount.setVisibility(earningFilterModel.getCount() == 0 ? View.GONE : View.VISIBLE);
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        count = 0;
                        progressBar.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        isPagination = false;
                        layoutNoDataFound.setVisibility(View.GONE);
                        hitGetEarningListApi();
                    }
                }
            }
        }
    }
}
