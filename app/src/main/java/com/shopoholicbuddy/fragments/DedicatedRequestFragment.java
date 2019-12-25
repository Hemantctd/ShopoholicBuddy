package com.shopoholicbuddy.fragments;


import android.content.Intent;
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
import com.shopoholicbuddy.activities.HomeActivity;
import com.shopoholicbuddy.activities.WebViewActivity;
import com.shopoholicbuddy.adapters.DedicatedMerchantAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.dedicatedresponse.DedicatedResponse;
import com.shopoholicbuddy.models.dedicatedresponse.Result;
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
public class DedicatedRequestFragment extends Fragment implements NetworkListener {

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
    private DedicatedMerchantAdapter fansAdapter;
    private List<Result> merchantList;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private LinearLayoutManager linearLayoutManager;
    private String termsUrl = "";


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

        }
        return view;
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mActivity = (AppCompatActivity) getActivity();
        merchantList = new ArrayList<>();
        fansAdapter = new DedicatedMerchantAdapter(mActivity, merchantList, (position, view) -> {
            switch (view.getId()) {
                case R.id.btn_accept:
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        hitAcceptRejectApi(position, "2");
                    }
                    break;
                case R.id.btn_reject:
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        hitAcceptRejectApi(position, "3");
                    }
                    break;
                case R.id.tv_terms_text:
                    startActivity(new Intent(mActivity, WebViewActivity.class).putExtra(Constants.IntentConstant.TERMS, termsUrl));
                    break;
            }
        });

        if (((HomeActivity) mActivity).getIntent() != null)
            ((HomeActivity) mActivity).setIntent(null);
    }

    /**
     * method to set adapter on views
     */
    private void setAdapter() {
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(fansAdapter);
    }
    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                hitGetFansListApi();

            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= merchantList.size() - 4) {
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
        Call<ResponseBody> call = apiInterface.hitMerchantListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_MERCHANT);
    }

    /**
     * method to hit the category list api
     */
    private void hitAcceptRejectApi(int position, String type) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_REQUEST_ID, merchantList.get(position).getRequestId());
        params.put(Constants.NetworkConstant.PARAM_STATUS, type);
        Call<ResponseBody> call = apiInterface.hitAcceptMerchantApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        isLoading = false;
                        isPagination = false;
                        progressBar.setVisibility(View.GONE);
                        merchantList.remove(position);
                        fansAdapter.notifyItemRemoved(position);
                        fansAdapter.notifyItemRangeChanged(position, merchantList.size());
                        if (merchantList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        merchantList.clear();
                        fansAdapter.notifyDataSetChanged();
                        if (merchantList.size() == 0) {
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
            }

            @Override
            public void onError(String response, int requestCode) {
                if (isAdded()) {
                    isLoading = false;
                    isPagination = false;
                    progressBar.setVisibility(View.GONE);
                    fansAdapter.notifyDataSetChanged();
                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    if (merchantList.size() == 0) {
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
                    if (merchantList.size() == 0) {
                        layoutNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoDataFound.setVisibility(View.GONE);
                    }
                }
            }
        }, Constants.NetworkConstant.REQUEST_ACCEPT_MERCHANT);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_MERCHANT:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                               DedicatedResponse dedicatedResponse = new Gson().fromJson(response, DedicatedResponse.class);
                               if (termsUrl.equals("")) termsUrl = dedicatedResponse.getTermconditionlink();
                            if (!isPagination) {
                                merchantList.clear();
                            } else {
                                isPagination = false;
                            }
                              merchantList.addAll(dedicatedResponse.getResult());
                            fansAdapter.notifyDataSetChanged();
                               isMoreData = dedicatedResponse.getNext() != -1;
                              if (isMoreData) count = dedicatedResponse.getNext();
                            if (merchantList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            merchantList.clear();
                            fansAdapter.notifyDataSetChanged();
                            if (merchantList.size() == 0) {
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
            fansAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (merchantList.size() == 0) {
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
            if (merchantList.size() == 0) {
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
