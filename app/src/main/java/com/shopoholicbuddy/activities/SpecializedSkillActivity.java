package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.SpecializedSkillAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
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

public class SpecializedSkillActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
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
    private List<Result> productList;
    private SpecializedSkillAdapter specializedSkillAdapter;
    private boolean isMoreData;
    private int count = 0;
    private boolean isLoading, isPagination;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialized_skill);
        ButterKnife.bind(this);
        initVariable();
        setAdapter();
        setListeners();
    }




    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(SpecializedSkillActivity.this)) {
                    count = 0;
                    hitSpecializedSkillApi();
                }
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(SpecializedSkillActivity.this)) {
                            isPagination = true;
                            hitSpecializedSkillApi();
                        }
                    }
                }
            }

        });

    }

    private void initVariable() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        ivBack.setVisibility(View.VISIBLE);
        productList = new ArrayList<>();
        tvTitle.setText(R.string.sepecialized_skills);
        specializedSkillAdapter = new SpecializedSkillAdapter(this, productList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_chat_dots:
                        AppUtils.getInstance().showMorePopUp(SpecializedSkillActivity.this, view, getString(R.string.edit), getString(R.string.delete), "", 3, new PopupItemDialogCallback() {
                            @Override
                            public void onItemOneClick() {
                                startActivityForResult(new Intent(SpecializedSkillActivity.this, AddProductServiceActivity.class)
                                        .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.PROFILE_SKILLS)
                                        .putExtra(Constants.IntentConstant.DEAL_ID, productList.get(position).getId())
                                        .putExtra(Constants.IntentConstant.IS_PRODUCT, false)
                                        , Constants.IntentConstant.REQUEST_EDIT_DEAL);
                            }

                            @Override
                            public void onItemTwoClick() {
                                if (AppUtils.getInstance().isInternetAvailable(SpecializedSkillActivity.this)){
                                    hitDeleteSpecializedSkillApi(productList.get(position).getId());
                                    productList.remove(position);
                                    specializedSkillAdapter.notifyDataSetChanged();
                                    if (productList.size() == 0) {
                                        layoutNoDataFound.setVisibility(View.VISIBLE);
                                    } else {
                                        layoutNoDataFound.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onItemThreeClick() {

                            }
                        });

                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_EDIT_DEAL && resultCode ==RESULT_OK){
            if (AppUtils.getInstance().isInternetAvailable(this)) {
                count = 0;
                hitSpecializedSkillApi();
            }
        }
    }

    private void setAdapter() {

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(specializedSkillAdapter);

        if (AppUtils.getInstance().isInternetAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            count = 0;
            hitSpecializedSkillApi();
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }

    /**
     * method to hit specialization skills
     */
    private void hitSpecializedSkillApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, Constants.NetworkConstant.SERVICE);
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitBuddyDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.BUDDY_DEAL);
    }

    /**
     * method to delete specialization skills
     * @param id
     */
    private void hitDeleteSpecializedSkillApi(String id) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_SERVICE_ID, id);
        Call<ResponseBody> call = apiInterface.hitDeleteSkillsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_DELETE_DEAL);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        switch (requestCode) {
            case Constants.NetworkConstant.BUDDY_DEAL:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
                        if (isPagination) {
                            isPagination = false;
                        } else {
                            productList.clear();
                        }
                        productList.addAll(dealsResponse.getResult());
                        specializedSkillAdapter.notifyDataSetChanged();
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
                        specializedSkillAdapter.notifyDataSetChanged();
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
                case Constants.NetworkConstant.REQUEST_DELETE_DEAL:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        if (productList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
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
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

    }

}