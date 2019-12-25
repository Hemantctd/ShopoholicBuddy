package com.shopoholicbuddy.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.ProductsAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.productdealsresponse.ProductDealsResponse;
import com.shopoholicbuddy.models.productdealsresponse.Result;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class BuddySharePostDealActivity extends AppCompatActivity implements NetworkListener {

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
    @BindView(R.id.rv_buddy_shared_posted)
    RecyclerView rvBuddySharedPosted;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    private boolean isPosted;
    private ProductsAdapter productsAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<Result> productList;
    private boolean isMoreData;
    private String buddyId;
    private boolean isLoading;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_share_post_deal);
        ButterKnife.bind(this);
        initVariable();
        setAdapter();
        getDataFromIntent();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitBuddyDealApi();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID);
            isPosted = getIntent().getExtras().getBoolean(Constants.IntentConstant.BUDDY_DEAL);
            if (isPosted) {
                tvTitle.setText(R.string.deals_posted);
            } else {
                tvTitle.setText(R.string.deals_shared);
            }
        }
    }

    private void initVariable() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        productList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(this, 2);
        productsAdapter = new ProductsAdapter(this, null, productList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {

            }
        });
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
    }

    private void setAdapter() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                count = 0;
                if (AppUtils.getInstance().isInternetAvailable(BuddySharePostDealActivity.this)) {
                    productList.clear();
                    hitBuddyDealApi();
                }
            }
        });

        rvBuddySharedPosted.setLayoutManager(gridLayoutManager);
        rvBuddySharedPosted.setAdapter(productsAdapter);
    }

    private void hitBuddyDealApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_IS_SHARED, isPosted ? "0" : "1");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitBuddyDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.BUDDY_DEAL);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.BUDDY_DEAL:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
                        int previousPosition = productList.size();
                        productList.addAll(dealsResponse.getResult());
                        productsAdapter.notifyItemRangeChanged(previousPosition, dealsResponse.getResult().size());
                        isMoreData = dealsResponse.getNext() != -1;
                        if (isMoreData) count = dealsResponse.getNext();
                        if (productList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        productList.clear();
                        productsAdapter.notifyDataSetChanged();
                        if (productList.size() > 0) {
                            layoutNoDataFound.setVisibility(View.GONE);
                        } else {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
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
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        productsAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (productList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        productsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (productList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
        }
    }
}
