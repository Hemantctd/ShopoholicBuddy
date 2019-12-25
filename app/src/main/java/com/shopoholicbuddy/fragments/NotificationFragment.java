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
import com.shopoholicbuddy.activities.CommonActivity;
import com.shopoholicbuddy.activities.HomeActivity;
import com.shopoholicbuddy.activities.OrderDetailsActivity;
import com.shopoholicbuddy.activities.ProductDetailsActivity;
import com.shopoholicbuddy.adapters.NotificationAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.notificationresponse.NotificationResponse;
import com.shopoholicbuddy.models.notificationresponse.Result;
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
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class NotificationFragment extends Fragment implements NetworkListener {
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
    Unbinder unbinder;
    private View rootView;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private List<Result> notificationList;
    private AppCompatActivity mActivity;
    private LinearLayoutManager linearLayoutManager;
    private NotificationAdapter notificationAdapter;
    private int count = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariable();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            hitGetNotificationListApi();

        }

        return rootView;
    }

    private void initVariable() {
        mActivity = (AppCompatActivity) getActivity();
        notificationList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        linearLayoutManager = new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false);
        notificationAdapter = new NotificationAdapter(mActivity, notificationList, (position, view) -> {
            switch (notificationList.get(position).getNotificationType()) {
                case "1":
                    //deal details
                    Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                    intent.putExtra(Constants.IntentConstant.DEAL_ID, notificationList.get(position).getDealId());
                    startActivity(intent);
                    break;
                case "2":
                    //order detail
                    String action = notificationList.get(position).getNotificationAction();
                    startActivity(new Intent(mActivity, OrderDetailsActivity.class)
                            .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.MY_ORDERS)
                            .putExtra(Constants.IntentConstant.ORDER_ID, notificationList.get(position).getOrderId()));
                    break;
                case "3":
                case "4":
                    AppUtils.getInstance().hitUpdateCountApi(mActivity, notificationList.get(position).getReqId());
                    if (!notificationList.get(position).getOrderId().equals("0")) {
                        startActivity(new Intent(mActivity, OrderDetailsActivity.class)
                                .putExtra(Constants.IntentConstant.ORDER_ID, notificationList.get(position).getReqId())
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.REQUESTS));
                    }else if (!notificationList.get(position).getHuntId().equals("0")) {
                        startActivity(new Intent(mActivity, CommonActivity.class)
                                        .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 2)
                                        .putExtra(Constants.NetworkConstant.PARAM_HUNT_ID, notificationList.get(position).getHuntId()));
                    }
                    break;
                case "11":
                    Intent homeIntent = new Intent(mActivity, HomeActivity.class);
                    homeIntent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.NOTIFICATION);
                    homeIntent.putExtra(Constants.IntentConstant.TYPE, Integer.parseInt(notificationList.get(position).getNotificationType()));
                    AppUtils.getInstance().openNewActivity(mActivity, homeIntent);
                    break;
            }
        });

    }

    private void setAdapter() {
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(notificationAdapter);
    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                hitGetNotificationListApi();

            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= notificationList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                            isPagination = true;
                            hitGetNotificationListApi();

                        }
                    }
                }
            }

        });

    }

    /**
     * method to hit the category list api
     */
    private void hitGetNotificationListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitGetNotificationListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.NOTIFICATION_LIST);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.NOTIFICATION_LIST:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            NotificationResponse notificationResponse = new Gson().fromJson(response, NotificationResponse.class);
                            if (!isPagination){
                                notificationList.clear();
                            }else {
                                isPagination = false;
                            }
                            notificationList.addAll(notificationResponse.getResult());
                            notificationAdapter.notifyDataSetChanged();
                            isMoreData = notificationResponse.getNext() != -1;
                            if (isMoreData) count = notificationResponse.getNext();
                            if (notificationList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            notificationList.clear();
                            notificationAdapter.notifyDataSetChanged();
                            if (notificationList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            notificationAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (notificationList.size() == 0) {
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
            notificationAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (notificationList.size() == 0) {
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
