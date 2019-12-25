package com.shopoholicbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.shopoholicbuddy.activities.BuddyProfileActivity;
import com.shopoholicbuddy.adapters.BuddyAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.buddylistresponse.BuddyListResponse;
import com.shopoholicbuddy.models.buddylistresponse.Result;
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
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyBuddyFragment extends Fragment implements NetworkListener {
    @BindView(R.id.recycle_view)
    RecyclerView rvBuddies;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private AppCompatActivity mActivity;
    Unbinder unbinder;
    private View rootView;
    private BuddyAdapter buddyAdapter;
    private List<Result> buddyList;
    private boolean isMoreData;
    private boolean isLoading;
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private boolean isPagination;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = (AppCompatActivity) getActivity();
        initVariable();
        setAdapters();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitGetBuddyListApi();
        }

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initVariable() {
        buddyList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        buddyAdapter = new BuddyAdapter(mActivity, buddyList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.rl_buddy_assigned:
                        Intent buddyProfileIntent = new Intent(mActivity, BuddyProfileActivity.class);
                        buddyProfileIntent.putExtra(Constants.IntentConstant.BUDDY_ID, buddyList.get(position).getUserId());
                        startActivity(buddyProfileIntent);
                        break;
                }
            }
        });
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                    count = 0;
                    hitGetBuddyListApi();
                }
            }
        });
        rvBuddies.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= buddyList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                            isPagination = true;
                            hitGetBuddyListApi();
                        }
                    }
                }
            }

        });

    }

    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvBuddies.setLayoutManager(linearLayoutManager);
        rvBuddies.setAdapter(buddyAdapter);
    }

    private void hitGetBuddyListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitBuddyListApi(AppUtils.getInstance().encryptData(params));
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
                            BuddyListResponse buddyListResponse = new Gson().fromJson(response, BuddyListResponse.class);
                            if (isPagination){
                                isPagination = false;
                            }else {
                                buddyList.clear();
                            }
                            buddyList.addAll(buddyListResponse.getResult());
                            buddyAdapter.notifyDataSetChanged();
                            isMoreData = buddyListResponse.getNext() != -1;
                            if (isMoreData) count = buddyListResponse.getNext();
                            if (buddyList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            buddyList.clear();
                            buddyAdapter.notifyDataSetChanged();
                            if (buddyList.size() == 0) {
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
            buddyAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (buddyList.size() == 0) {
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
            buddyAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (buddyList.size() == 0) {
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
