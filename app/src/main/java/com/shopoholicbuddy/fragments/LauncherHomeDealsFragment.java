package com.shopoholicbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.HomeActivity;
import com.shopoholicbuddy.activities.ProductDetailsActivity;
import com.shopoholicbuddy.adapters.BannerAdapter;
import com.shopoholicbuddy.adapters.ProductsAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.launcherhomeresponse.BannerArr;
import com.shopoholicbuddy.models.productdealsresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class LauncherHomeDealsFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.rv_banners)
    RecyclerView rvBanners;
    @BindView(R.id.tv_view_all)
    CustomTextView tvViewAll;
    @BindView(R.id.rv_popular_deals)
    RecyclerView rvPopularDeals;
    @BindView(R.id.fl_popular_deals_title)
    FrameLayout flPopularDealsTitle;
    private View rootView;
    private AppCompatActivity mActivity;
    private List<Result> productList;
    private ProductsAdapter productsAdapter;
    private BannerAdapter bannerAdapter;
    private List<BannerArr> bannerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_launcher_home_deals, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setAdapters();
        return rootView;
    }

    /**
     * method to u=initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        productList = new ArrayList<>();
        bannerList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(mActivity, this, productList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_like:
                        if (AppUtils.getInstance().isLoggedIn(mActivity) && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            hitLikeProductsApi(position);
                            AppUtils.getInstance().bounceAnimation(mActivity, view);
                            productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                            productsAdapter.notifyItemChanged(position);
                        }
                        break;
                    case R.id.civ_user_image:
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        break;
                    case R.id.rl_root_view:
//                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        String dealId = productList.get(position).getId();
                        Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        startActivity(intent);
                        break;
                }
            }
        });
        bannerAdapter = new BannerAdapter(mActivity, bannerList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.rl_root_view:
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        break;
                }
            }
        });
    }


    /**
     * Method to hit the signup api
     *
     * @param position
     */
    public void hitLikeProductsApi(final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        Call<ResponseBody> call = apiInterface.hitCategoriesListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure() {
                productList.get(position).setIsBookmark(productList.get(position).getIsBookmark().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    /**
     * method to set adapters in views
     */
    private void setAdapters() {
        rvBanners.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvBanners.setAdapter(bannerAdapter);
        rvPopularDeals.setLayoutManager(new GridLayoutManager(mActivity, 2));
        rvPopularDeals.setAdapter(productsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_view_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_view_all:
                ((HomeActivity) mActivity).resetSideMenu(0);
                ((HomeActivity) mActivity).setFragmentOnContainer(0);
                break;
        }
    }

    /**
     * method to get data from api
     *
     * @param bannerList
     * @param productList
     */
    public void getPageData(List<Result> productList, List<BannerArr> bannerList) {
        this.productList.addAll(productList);
        productsAdapter.notifyDataSetChanged();
        this.bannerList.addAll(bannerList);
        bannerAdapter.notifyDataSetChanged();
        flPopularDealsTitle.setVisibility(productList.size() == 0 ? View.GONE : View.VISIBLE);
    }
}