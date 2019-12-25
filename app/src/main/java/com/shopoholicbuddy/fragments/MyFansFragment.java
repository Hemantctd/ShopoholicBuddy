package com.shopoholicbuddy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.ViewPagerAdapter;
import com.shopoholicbuddy.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyFansFragment extends Fragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    private View rootView;

    private ViewPagerAdapter viewPagerAdapter;
    private ShopperMerchantFragment shopperFragment;
    private ShopperMerchantFragment merchantFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_fans, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariable();
        setViewPagerAdapter();
        return rootView;
    }

    private void initVariable() {
        merchantFragment = new ShopperMerchantFragment();
        shopperFragment = new ShopperMerchantFragment();
        Bundle shopperBundle = new Bundle();
        shopperBundle.putBoolean(Constants.IntentConstant.IS_SHOPPER, true);
        shopperFragment.setArguments(shopperBundle);
          viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }


    private void setViewPagerAdapter() {

        viewPagerAdapter.addFragment(shopperFragment,getString(R.string.Shoppers));
        viewPagerAdapter.addFragment(merchantFragment,getString(R.string.Merchants));
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
