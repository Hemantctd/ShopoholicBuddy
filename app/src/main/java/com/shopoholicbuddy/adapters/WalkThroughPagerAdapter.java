package com.shopoholicbuddy.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopoholicbuddy.R;


/**
 * Extending FragmentStatePagerAdapter
 */

public class WalkThroughPagerAdapter extends PagerAdapter {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private int size;

    public WalkThroughPagerAdapter(Context context, int size) {
        this.size = size;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.activity_tutorial_screen, container, false);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvContent = itemView.findViewById(R.id.tv_content);
        ImageView ivImage = itemView.findViewById(R.id.iv_image);
        switch (position){
            case 0:
                tvTitle.setText(context.getResources().getString(R.string.walkthrough_page_one_title));
                tvContent.setText(context.getResources().getString(R.string.walkthrough_page_one_content));
                ivImage.setImageResource(R.drawable.ic_tutorial_1_img);
                break;
            case 1:
                tvTitle.setText(context.getResources().getString(R.string.walkthrough_page_two_title));
//                tvContent.setText(context.getResources().getString(R.string.walkthrough_page_two_content));
                tvContent.setText("");
                ivImage.setImageResource(R.drawable.ic_tutorial_2_img);
                break;
            case 2:
                tvTitle.setText(context.getResources().getString(R.string.walkthrough_page_three_title));
//                tvContent.setText(context.getResources().getString(R.string.walkthrough_page_three_content));
                tvContent.setText("");
                ivImage.setImageResource(R.drawable.ic_tutorial_3_img);
                break;
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}