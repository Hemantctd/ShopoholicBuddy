package com.shopoholicbuddy.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.HomeActivity;
import com.shopoholicbuddy.adapters.FilterBean;
import com.shopoholicbuddy.adapters.ViewPagerAdapter;
import com.shopoholicbuddy.firebasechat.utils.FirebaseConstants;
import com.shopoholicbuddy.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholicbuddy.firebasechat.utils.FirebaseEventListeners;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class HomeFragment extends Fragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private HomeCategoriesDealsFragment homeCategoryDealsFragment;
    private HomePercentageDealsFragment homePercentageDealsFragment;
    private AppCompatActivity mActivity;
    private Unbinder unbinder;
    private FilterBean filterData;


    private BroadcastReceiver mapGridReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (viewPager.getCurrentItem()){
                case 0:
                    homeCategoryDealsFragment.changeMapGrid();
                    break;
                case 1:
                    homePercentageDealsFragment.changeMapGrid();
                    break;
            }
        }
    };

    /**
     * method to change the favourite icon of other fragment
     * @param type
     * @param id
     * @param isFavourite
     */
    public void changeFavouriteIcon(String type, String id, String isFavourite) {
        if (type.equals(Constants.IntentConstant.CATEGORY)) {
            homePercentageDealsFragment.changeFavouriteIcon(id, isFavourite);
        }else {
            homeCategoryDealsFragment.changeFavouriteIcon(id, isFavourite);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mapGridReceiver, new IntentFilter(Constants.IntentConstant.MAP_GRID));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mapGridReceiver);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setAdapters();
        setUpViewPager();
        resetStatus();
        return rootView;
    }

    /**
     * method to set adapter on views
     */
    private void setAdapters() {
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        homeCategoryDealsFragment = new HomeCategoriesDealsFragment();
//        homePercentageDealsFragment = new HomeCategoriesDealsFragment();
        homePercentageDealsFragment = new HomePercentageDealsFragment();
        if (((HomeActivity) mActivity).getIntent() != null)
            ((HomeActivity) mActivity).setIntent(null);

    }


    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        adapter.addFragment(homeCategoryDealsFragment, getString(R.string.category_based_deals));
        adapter.addFragment(homePercentageDealsFragment, getString(R.string.special_offers));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);//ths is done to make sure that fragment does not load again
        tabLayout.setupWithViewPager(viewPager);
        AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mActivity instanceof HomeActivity){
                    String address = "";
                    double latitude = 0, longitude = 0;
                    filterData = ((HomeActivity)mActivity).getFilterData();
                    if (filterData != null) {
                        address = filterData.getAddress();
                        latitude = filterData.getLatitude();
                        longitude = filterData.getLongitude();

                        filterData = new FilterBean();
                        if (!address.equals("") && latitude != 0 && longitude != 0) {
                            filterData.setLatitude(latitude);
                            filterData.setLongitude(longitude);
                            filterData.setAddress(address);
                            ((HomeActivity) mActivity).setFilterBean(filterData);
                            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Constants.IntentConstant.FILTER_DATA));
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to set user status
     *
     * @param statusDrawable
     * @param status
     */
    public void setUserStatus(final int statusDrawable, final String status) {
        if (homeCategoryDealsFragment != null) {
            homeCategoryDealsFragment.setStatus(statusDrawable, status);
        }
        if (homePercentageDealsFragment != null) {
            homePercentageDealsFragment.setStatus(statusDrawable, status);
        }

        if (!AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID).equals("")) {
            ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
            final HashMap<String, String> params = AppUtils.getInstance().getUserMap(mActivity);
            params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
            params.put(Constants.NetworkConstant.PARAM_IS_AVAILABLE, String.valueOf(statusDrawable));
            Call<ResponseBody> call = apiInterface.hitEditProfileDataApi(AppUtils.getInstance().encryptData(params));
            ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
                @Override
                public void onSuccess(int responseCode, String response, int requestCode) {
                    AppSharedPreference.getInstance().putInt(mActivity, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS, statusDrawable);
                    FirebaseDatabaseQueries.getInstance().setUserStatus(mActivity, statusDrawable);
                }

                @Override
                public void onError(String response, int requestCode) {
                    if (homeCategoryDealsFragment != null) {
                        homeCategoryDealsFragment.resetStatus();
                    }
                    if (homePercentageDealsFragment != null) {
                        homePercentageDealsFragment.resetStatus();
                    }
                }

                @Override
                public void onFailure() {
                    if (homeCategoryDealsFragment != null) {
                        homeCategoryDealsFragment.resetStatus();
                    }
                    if (homePercentageDealsFragment != null) {
                        homePercentageDealsFragment.resetStatus();
                    }
                }
            }, Constants.NetworkConstant.REQUEST_PRODUCTS);
        }
    }



    /**
     * method to set user status
     */
    public void resetStatus() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE)
                .child(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID))
                .child(FirebaseConstants.STATUS_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    int status = 0;
                    if (dataSnapshot.getValue() != null) {
                        status = Integer.parseInt(dataSnapshot.getValue().toString());
                    }
                    AppSharedPreference.getInstance().putInt(mActivity, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS, status);
                    if (homeCategoryDealsFragment != null) {
                        homeCategoryDealsFragment.resetStatus();
                    }
                    if (homePercentageDealsFragment != null) {
                        homePercentageDealsFragment.resetStatus();
                    }
                }
            }
        });
    }


    /**
     * function to set coachmarks
     * @param tvLocation
     * @param ivShowCategory
     */
    public void setCoachMarks(View tvLocation, View ivShowCategory) {
        if (mActivity instanceof HomeActivity) {
            ((HomeActivity)mActivity).setCoachMarks((ViewGroup) tabLayout.getChildAt(0), tvLocation, ivShowCategory);
        }
    }
}
