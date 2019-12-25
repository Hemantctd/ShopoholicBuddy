package com.shopoholicbuddy.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.MyFansAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.myfansresponse.MyFansResponse;
import com.shopoholicbuddy.models.myfansresponse.Result;
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
public class ShopperMerchantFragment extends Fragment implements NetworkListener {

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
    private MyFansAdapter fansAdapter;
    private List<Result> fansList;
    private boolean isShopper;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariable();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            hitGetFansListApi();

        }  return view;
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {

        if (getArguments() != null && getArguments().containsKey(Constants.IntentConstant.IS_SHOPPER)) {
            isShopper = getArguments().getBoolean(Constants.IntentConstant.IS_SHOPPER, false);
        }
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mActivity = (AppCompatActivity) getActivity();
        fansList = new ArrayList<>();
        fansAdapter = new MyFansAdapter(mActivity, fansList, isShopper, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {

            }
        });
    }

    /**
     * method to se adapter ain views
     */
    private void setAdapter() {
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(fansAdapter);
    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    count = 0;
                    hitGetFansListApi();

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
                    if (firstVisibleItemPosition + totalVisibleItems >= fansList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                            isPagination = true;
                            hitGetFansListApi();

                        }
                    }
                }
            }

        });

    }

    /**
     * method to hit the category list api
     */
    private void hitGetFansListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_TYPE, isShopper ? "1" : "2");
        Call<ResponseBody> call = apiInterface.hitGetMyFansApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_MY_FANS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_MY_FANS:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                               MyFansResponse buddyListResponse = new Gson().fromJson(response, MyFansResponse.class);
                            if (!isPagination) {
                                fansList.clear();
                            } else {
                                isPagination = false;
                            }
                              fansList.addAll(buddyListResponse.getResult());
                            fansAdapter.notifyDataSetChanged();
                               isMoreData = buddyListResponse.getNext() != -1;
                              if (isMoreData) count = buddyListResponse.getNext();
                            if (fansList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            fansList.clear();
                            fansAdapter.notifyDataSetChanged();
                            if (fansList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
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
            fansAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (fansList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            fansAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (fansList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
