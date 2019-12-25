package com.shopoholicbuddy.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.fragments.BlockDealsFragment;
import com.shopoholicbuddy.fragments.MyBuddyFragment;
import com.shopoholicbuddy.fragments.ProductFragment;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonActivity extends BaseActivity {

    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    private String huntId;
    private boolean isProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        ButterKnife.bind(this);
        ivMenu.setImageResource(R.drawable.ic_back);
        getDataFromIntent();
    }


    /**
     * method to get the data from intent
     */
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null){
            int fragmentType = getIntent().getExtras().getInt(Constants.IntentConstant.FRAGMENT_TYPE, 0);
            huntId = getIntent().getExtras().getString(Constants.NetworkConstant.PARAM_HUNT_ID, "");
            isProduct = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_PRODUCT, true);
            setFragmentOnContainer(fragmentType);
        }
    }


    /**
     * Method to set fragment on container
     *
     * @param fragmentType\
     */
    public void setFragmentOnContainer(int fragmentType) {
        Fragment fragment;
        switch (fragmentType) {
            case 0:
                fragment = new MyBuddyFragment();
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, MyBuddyFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.my_buddy));
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;
            case 1:
                fragment = new BlockDealsFragment();
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, BlockDealsFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.blocked_deals));
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;
            case 2:
                fragment = new ProductFragment();
                Bundle productBundle = new Bundle();
                productBundle.putBoolean(Constants.IntentConstant.IS_PRODUCT, isProduct);
                productBundle.putString(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
                fragment.setArguments(productBundle);
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, ProductFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.hunt_details));
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_menu, R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.menu_right:
                break;
            case R.id.menu_second_right:
                break;
            case R.id.menu_third_right:
                break;
        }
    }
}
