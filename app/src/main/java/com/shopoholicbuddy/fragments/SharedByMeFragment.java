package com.shopoholicbuddy.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.ProductDetailsActivity;
import com.shopoholicbuddy.adapters.SharedByMeAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.productdealsresponse.ProductDealsResponse;
import com.shopoholicbuddy.models.productdealsresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharedByMeFragment extends Fragment implements NetworkListener {

    Unbinder unbinder;
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

    private AppCompatActivity mActivity;
    private List<Result> productList;
    private SharedByMeAdapter sharedByMeAdapter;
    private boolean isLoading;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean isMoreData;
    private int count = 0;
    private boolean isPagination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariables();
        if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading)
            hitSharedByMeApi();
        setListeners();
        return view;
    }

    /**
     * function to initialize the variables
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = (AppCompatActivity) getActivity();
        productList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycleView.setLayoutManager(gridLayoutManager);
        sharedByMeAdapter = new SharedByMeAdapter(mActivity, this, productList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_like:
                    if (AppUtils.getInstance().isLoggedIn(mActivity) && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        AppUtils.getInstance().bounceAnimation(mActivity, view);
                        productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                        sharedByMeAdapter.notifyItemChanged(position);
                        hitBookmarkProductsApi(position);
                    }
                    break;
                case R.id.civ_user_image:
                    break;
                case R.id.iv_product_pic:
                    String dealId = productList.get(position).getId();
                    Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                    intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                    intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.SHARED_BY_ME);
                    intent.putExtra(Constants.IntentConstant.DEAL_IMAGE, productList.get(position).getDealImage().split(",")[0]);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(mActivity, view, ViewCompat.getTransitionName(view));
                    SharedByMeFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL, options.toBundle());
                    break;
            }
        });
        recycleView.setAdapter(sharedByMeAdapter);

    }

    /**
     * method to set listeners in views
     */
    public void setListeners()
    {
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
                            isPagination = true;
                            hitSharedByMeApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                count = 0;
                hitSharedByMeApi();
            }
        });
    }

    /**
     * method to hit shared by my api
     */
    private void hitSharedByMeApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(getActivity(), ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_IS_SHARED, Constants.NetworkConstant.BUDDY_SHARED);
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitPostByMeApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BUDDY);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_BUDDY:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
                            if (isPagination){
                                isPagination = false;
                            }else {
                                productList.clear();
                            }
                            productList.addAll(dealsResponse.getResult());
                            sharedByMeAdapter.notifyDataSetChanged();
                            isMoreData = dealsResponse.getNext() != -1;
                            if (isMoreData) count = dealsResponse.getNext();
                            if (productList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            if (isAdded()) {
                                isLoading = false;
                                isPagination = false;
                                progressBar.setVisibility(View.GONE);
                                if (productList.size() == 0) {
                                    layoutNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoDataFound.setVisibility(View.GONE);
                                }
                                if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                                //AppUtils.getInstance().showToast(mActivity, response);
                            }
                            break;
                    }
                    break;
            }
        }

    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (productList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            AppUtils.getInstance().showToast(mActivity, response);
        }

    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (productList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            //AppUtils.getInstance().showToast(mActivity, response);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.remove(i);
                    sharedByMeAdapter.notifyItemRemoved(i);
                    sharedByMeAdapter.notifyItemRangeChanged(i, productList.size());
                    break;
                }
            }
        }
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_CANCELED && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.get(i).setIsBookmark(data.getExtras().getString(Constants.IntentConstant.IS_BOOKMARK));
                    sharedByMeAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }


    /**
     * Method to hit the like deal api
     *
     * @param position
     */
    public void hitBookmarkProductsApi(final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productList.get(position).getId());
        params.put(Constants.NetworkConstant.PARAM_STATUS, productList.get(position).getIsBookmark());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBookmarkProductsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                sharedByMeAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure() {
                productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                sharedByMeAdapter.notifyItemChanged(position);
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

}
