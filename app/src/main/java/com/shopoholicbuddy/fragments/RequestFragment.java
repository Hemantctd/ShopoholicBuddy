package com.shopoholicbuddy.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.shopoholicbuddy.adapters.RequestsAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.firebasechat.utils.FirebaseConstants;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.NotificationBean;
import com.shopoholicbuddy.models.requestresponse.RequestResponse;
import com.shopoholicbuddy.models.requestresponse.Result;
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
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment implements NetworkListener {
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
    private RequestsAdapter requestsAdapter;
    private List<Result> requestList;
    private AppCompatActivity mActivity;
    private boolean isLoading;
    private boolean isMoreData;
    private int count = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isPagination;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        requestList = new ArrayList<>();
        unbinder = ButterKnife.bind(this, view);
        initViews();
        setAdapter();
        setListeners();
        getNotificationData();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
            hitGetRequestsListApi();
        }
        return view;

    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        requestList = new ArrayList<>();
        mActivity = (AppCompatActivity) getActivity();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
    }

    /**
     * method to set the adapter on views
     */
    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        requestsAdapter = new RequestsAdapter(mActivity, requestList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (requestList.size() > position && position >= 0) {
                    AppUtils.getInstance().hitUpdateCountApi(mActivity, requestList.get(position).getReqId());
                    requestList.get(position).setIsRead("1");
                    requestsAdapter.notifyItemChanged(position);
                    if (!requestList.get(position).getOrderId().equals("0")) {
                        Intent intent = new Intent(mActivity, OrderDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, requestList.get(position));
                        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.REQUESTS);
                        RequestFragment.this.startActivityForResult(intent, 1);
                    } else if (!requestList.get(position).getHuntId().equals("0")) {
                        Intent intent = new Intent(mActivity, CommonActivity.class);
                        intent.putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 2);
                        intent.putExtra(Constants.NetworkConstant.PARAM_HUNT_ID, requestList.get(position).getHuntId());
                        intent.putExtra(Constants.IntentConstant.IS_PRODUCT, requestList.get(position).getProductType().equals("1"));
                        RequestFragment.this.startActivityForResult(intent, 1);
                    }
                }
            }
        });
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(requestsAdapter);
    }

    /**
     * Method to get data on notification click
     */
    private void getNotificationData() {
        if (mActivity instanceof HomeActivity) {
            Intent receivedIntent = ((HomeActivity) mActivity).getIntent();
            if (receivedIntent != null && receivedIntent.getExtras() != null && receivedIntent.hasExtra(FirebaseConstants.NOTIFICATION)) {
                try {
                    final NotificationBean notificationBean = (NotificationBean) receivedIntent.getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                    if (notificationBean != null) {
                        if (notificationBean.getType().equals("3")) {
                            RequestFragment.this.startActivityForResult(new Intent(mActivity, CommonActivity.class)
                                            .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 2)
                                            .putExtra(Constants.NetworkConstant.PARAM_HUNT_ID, notificationBean.getHuntId())
                                    , 1);
                        } else {
                            RequestFragment.this.startActivityForResult(new Intent(mActivity, OrderDetailsActivity.class)
                                    .putExtra(Constants.IntentConstant.ORDER_ID, notificationBean.getReqId())
                                    .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.REQUESTS), 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((HomeActivity) mActivity).setIntent(null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data!=null && data.getExtras()!=null) {
            String orderId =data.getExtras().getString(Constants.IntentConstant.ORDER_ID);
            String huntId =data.getExtras().getString(Constants.IntentConstant.HUNT_ID);
            for (int i=0; i<requestList.size();i++){
                if (orderId != null && !orderId.equals("")) {
                    if (requestList.get(i).getOrderId().equalsIgnoreCase(orderId)) {
                        requestList.remove(i);
                        break;
                    }
                } else if (huntId != null && !huntId.equals("")) {
                    if (requestList.get(i).getHuntId().equalsIgnoreCase(huntId)) {
                        requestList.remove(i);
                        break;
                    }
                }
            }
            requestsAdapter.notifyDataSetChanged();
            if (requestList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                count = 0;
                hitGetRequestsListApi();
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= requestList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitGetRequestsListApi();
                        }
                    }
                }
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to get request list
     */
    private void hitGetRequestsListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitgetRequestApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BUDDY_REQUESTS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_BUDDY_REQUESTS:
                    switch (responseCode) {
                        case Constants.NetworkConstant.VERIFY_EMAIL:
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "0");
                            String email = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL);
                            new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                                    .setCancelable(false)
                                    .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                                    .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                        if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                            hitResendLinkApi();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        // do nothing
                                    })
                                    .show();
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            RequestResponse requestResponse = new Gson().fromJson(response, RequestResponse.class);
                                if (isPagination) {
                                    isPagination = false;
                                } else {
                                    requestList.clear();
                                }
                            if (requestResponse.getResult().size() > 0) {
                                requestList.addAll(requestResponse.getResult());
                                requestsAdapter.notifyDataSetChanged();
                                isMoreData = requestResponse.getNext() != -1;
                                if (isMoreData) count = requestResponse.getNext();
                            }
                            if (requestList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                           isPagination = false;
                           requestList.clear();
                            requestsAdapter.notifyDataSetChanged();
                            if (requestList.size() == 0) {
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


    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, null, 1001);
    }


    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            requestsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (requestList.size() == 0) {
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
            requestsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (requestList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }
}
