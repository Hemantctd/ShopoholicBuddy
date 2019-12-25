package com.shopoholicbuddy.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shopoholicbuddy.fragments.BookmarkedFragment;
import com.shopoholicbuddy.fragments.PostedByMeFragment;
import com.shopoholicbuddy.fragments.SharedByMeFragment;


/**
 * Created by appinventiv on 2/5/18.
 */

public class DealsViewPager extends FragmentPagerAdapter {


    public DealsViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;

                if(position==0){
                    fragment=new PostedByMeFragment();}
               else if(position==1){
                    fragment=new SharedByMeFragment();}
                else if(position==2){
                    fragment=new BookmarkedFragment();}
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
