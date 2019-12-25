package com.shopoholicbuddy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.ViewPagerAdapter;
import com.shopoholicbuddy.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyDealsFragment extends Fragment {


    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private Unbinder unbinder;
    private ViewPagerAdapter adapter;
    private PostedByMeFragment postedByMeFragment;
    private SharedByMeFragment sharedByMeFragment;
    private BookmarkedFragment bookmarkedFragment;
    private AppCompatActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launcher_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setUpViewPager();
        return rootView;
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        progressBar.setVisibility(View.GONE);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        postedByMeFragment = new PostedByMeFragment();
        sharedByMeFragment = new SharedByMeFragment();
        bookmarkedFragment = new BookmarkedFragment();
    }


    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        adapter.addFragment(postedByMeFragment, getString(R.string.posted_by_me));
        adapter.addFragment(sharedByMeFragment, getString(R.string.shared_by_me));
        adapter.addFragment(bookmarkedFragment, getString(R.string.bookmarked));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//ths is done to make sure that fragment does not load again
        tabLayout.setupWithViewPager(viewPager);
        AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
