package com.shopoholicbuddy.fragments;


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
import com.shopoholicbuddy.activities.AddProductServiceActivity;
import com.shopoholicbuddy.adapters.PostedByMeAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.dealspostbymeresponse.Stories;
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
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostedByMeFragment extends Fragment implements NetworkListener {


    @BindView(R.id.rv_posted)
    RecyclerView rvPosted;
    Unbinder unbinder;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.srl_deal_post)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;

    private boolean isLoading;
    private ArrayList<Stories> mStoriesArrayList;
    private List<Result> productList;
    private PostedByMeAdapter postedByMeAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isMoreData;
    private int count = 0;
    private AppCompatActivity mActivity;
    private boolean isPagination;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posted_by_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariables();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading)
            hitPostDataApi();

        return view;
    }

    /**
     * This method initializes the variables used
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = (AppCompatActivity) getActivity();
        mStoriesArrayList = new ArrayList<>();
        productList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * This method sets the adapter for the recycler view
     */
    private void setAdapter() {
        postedByMeAdapter = new PostedByMeAdapter(mActivity, productList, this);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvPosted.setLayoutManager(linearLayoutManager);
        rvPosted.setAdapter(postedByMeAdapter);
    }

    /**
     * method to set listeners in views
     */
    public void setListeners() {
        rvPosted.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= mStoriesArrayList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
                            isPagination = true;
                            hitPostDataApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                count = 0;
                hitPostDataApi();
            }
        });
    }

    /**
     * method to get post deals
     */
    private void hitPostDataApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(getActivity(), ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "2");
        Call<ResponseBody> call = apiInterface.hitPostByMeApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_POSTED);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_POSTED:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                productList.clear();
                            }
                            productList.addAll(dealsResponse.getResult());
                            postedByMeAdapter.notifyDataSetChanged();
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
                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                //AppUtils.getInstance().showToast(mActivity, response);
                            }
                            break;
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method for popup item click action
     * @param result
     * @param position
     * @param i
     */
    public void onPopupItemClick(Result result, final int position, int i) {
        switch (i){
            case 1:
                startActivityForResult(new Intent(mActivity, AddProductServiceActivity.class)
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.POSTED_BY_ME)
                                .putExtra(Constants.IntentConstant.DEAL_ID, result.getId())
                                .putExtra(Constants.IntentConstant.IS_PRODUCT, result.getProductType().equals("1"))
                        , Constants.IntentConstant.REQUEST_EDIT_DEAL);

                break;
            case 2:
                new AlertDialog.Builder(mActivity, R.style.DatePickerTheme).setTitle(getString(R.string.delete_deal))
                    .setMessage(getString(R.string.sure_to_delete_deal))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                            hitDeleteSpecializedSkillApi(productList.get(position).getId());
                            productList.remove(position);
                            postedByMeAdapter.notifyDataSetChanged();
                            if (productList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                        // do nothing
                    })
                    .show();
                break;
        }
    }


    /**
     * method to delete specialization skills
     * @param id
     */
    private void hitDeleteSpecializedSkillApi(String id) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_SERVICE_ID, id);
        Call<ResponseBody> call = apiInterface.hitDeleteSkillsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_DELETE_DEAL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_EDIT_DEAL && resultCode == RESULT_OK){
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                hitPostDataApi();
            }
        }
    }
}
