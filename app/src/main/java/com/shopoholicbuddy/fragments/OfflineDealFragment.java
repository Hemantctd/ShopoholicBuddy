package com.shopoholicbuddy.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.AddProductServiceActivity;
import com.shopoholicbuddy.adapters.OfflineDealAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.database.AppDatabase;
import com.shopoholicbuddy.models.AddProductServiceModel;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfflineDealFragment extends Fragment {

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
    private Activity mActivity;
    private OfflineDealAdapter dealAdapter;
    private ArrayList<AddProductServiceModel> dealList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariable();
        setAdapter();
        setDeals();
        return view;
    }

    /**
     * method to initialize variables
     */
    private void initVariable() {
        progressBar.setVisibility(View.GONE);
        dealList = new ArrayList<>();
        swipeRefreshLayout.setEnabled(false);
        mActivity = getActivity();
        dealAdapter = new OfflineDealAdapter(mActivity, dealList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_product_pic:
                case R.id.rl_offline_deal:
//                        Intent intent = new Intent(mActivity, ProductServicePreviewActivity.class);
//                        intent.putExtra(Constants.IntentConstant.PRODUCT_DETAILS, dealList.get(position));
//                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, view, ViewCompat.getTransitionName(view));
//                        OfflineDealFragment.this.startActivityForResult(intent, 10001, options.toBundle());
                    OfflineDealFragment.this.startActivityForResult(new Intent(mActivity, AddProductServiceActivity.class)
                            .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, dealList.get(position))
                            .putExtra(Constants.IntentConstant.IS_PRODUCT, dealList.get(position).isProduct())
                            .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.OFFLINE_DEALS), 10001);

                    break;
                case R.id.iv_delete:
                    new AlertDialog.Builder(mActivity, R.style.DatePickerTheme).setTitle(getString(R.string.delete_deal))
                            .setMessage(getString(R.string.sure_to_delete_deal))
                            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                try {
                                    AppDatabase.removeDealFromDb(dealList.get(position).getId());
                                    dealList.remove(position);
                                    dealAdapter.notifyItemRemoved(position);
                                    dealAdapter.notifyItemRangeChanged(position, dealList.size());
                                    if (dealList.size() == 0) {
                                        layoutNoDataFound.setVisibility(View.VISIBLE);
                                    } else {
                                        layoutNoDataFound.setVisibility(View.GONE);
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                                // do nothing
                            })
                            .show();
                    break;
            }

        });
    }

    /**
     * method to set deals data
     */
    private void setDeals() {
        List<String> dealsDetailsList = AppDatabase.fetchDealDetails();
        dealList.clear();
        dealAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        for (String dealDetails : dealsDetailsList) {
            dealList.add(new Gson().fromJson(dealDetails, AddProductServiceModel.class));
        }
        dealAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        if (dealList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    /**
     * method to set adapter on views
     */
    private void setAdapter() {
        recycleView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recycleView.setAdapter(dealAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001){
            setDeals();
        }
    }
}
