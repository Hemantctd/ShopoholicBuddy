package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholicbuddy.coachmark.CoachMarksImpl;
import com.dnitinverma.locationlibrary.RCLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.FilterBean;
import com.shopoholicbuddy.adapters.SideMenuAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForPaymentInfo;
import com.shopoholicbuddy.firebasechat.fragments.ChatFragment;
import com.shopoholicbuddy.firebasechat.utils.FirebaseConstants;
import com.shopoholicbuddy.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholicbuddy.firebasechat.utils.FirebaseEventListeners;
import com.shopoholicbuddy.fragments.DedicatedRequestFragment;
import com.shopoholicbuddy.fragments.HomeFragment;
import com.shopoholicbuddy.fragments.LauncherHomeFragment;
import com.shopoholicbuddy.fragments.MyDealsFragment;
import com.shopoholicbuddy.fragments.MyEarningFragment;
import com.shopoholicbuddy.fragments.MyFansFragment;
import com.shopoholicbuddy.fragments.MyOrdersFragment;
import com.shopoholicbuddy.fragments.NotificationFragment;
import com.shopoholicbuddy.fragments.OfflineDealFragment;
import com.shopoholicbuddy.fragments.ProfileFragment;
import com.shopoholicbuddy.fragments.RequestFragment;
import com.shopoholicbuddy.fragments.SettingsFragment;
import com.shopoholicbuddy.fragments.UnderDevelopmentFragment;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.MenuItemModel;
import com.shopoholicbuddy.models.NotificationBean;
import com.shopoholicbuddy.models.profileresponse.ProfileResponse;
import com.shopoholicbuddy.models.unreadcountresponse.UnreadCountResponse;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.fl_home_container)
    FrameLayout flHomeContainer;
    @BindView(R.id.iv_user_image)
    CircleImageView ivUserImage;
    @BindView(R.id.tv_user_name)
    CustomTextView tvUserName;
    @BindView(R.id.iv_verify_email)
    ImageView ivVerifyEmail;
    @BindView(R.id.rv_menu_item_layout)
    RecyclerView rvMenuItemLayout;
    @BindView(R.id.side_panel)
    DrawerLayout sidePanel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.layout_menu_profile)
    RelativeLayout layoutMenuProfile;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.view_profile)
    CustomTextView viewProfile;
    @BindView(R.id.profile_toolbar_view)
    View profileToolbarView;
    @BindView(R.id.fab_product)
    CustomTextView fabProduct;
    @BindView(R.id.fab_service)
    CustomTextView fabService;
    @BindView(R.id.fab_menu)
    FloatingActionButton fabMenu;
    @BindView(R.id.fl_fab_menu)
    FrameLayout flFabMenu;
    @BindView(R.id.ll_fab)
    LinearLayout llFab;
    @BindView(R.id.v_dot)
    View vDot;
    @BindView(R.id.menu_right_count)
    ImageView menuRightCount;
    @BindView(R.id.tv_filter_count)
    CustomTextView tvFilterCount;
    @BindView(R.id.fl_menu_right_home)
    FrameLayout flMenuRightHome;
    @BindView(R.id.fl_menu_right)
    FrameLayout flMenuRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.fl_profile)
    FrameLayout flProfile;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.iv_unread)
    ImageView ivUnread;
    @BindView(R.id.tv_pay)
    CustomTextView tvPay;
    private SideMenuAdapter sideMenuAdapter;
    private boolean doubleBackToExitPressedOnce;
    private ArrayList<MenuItemModel> menuItemsList;
    private int previousPosition = -101;
    private FilterBean filterBean;
    private String queryParams;
    private String fromClass;
    private FirebaseEventListeners unreadListener;
    public int unreadRequest = 0;
    public int unreadChat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppUtils.getInstance().removeNotificationsPopups(this);
        checkLocationPermission();
        ButterKnife.bind(this);
        initializeVariables();
        setAdapters();
        setListeners();
        getDataFromIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
        if (fragment instanceof MyEarningFragment) {
            ((MyEarningFragment)fragment).hitPaymentApi();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
        if (AppUtils.getInstance().isUserLogin(this)) {
            hitGetUnreadCountApi();
            unreadListener = new FirebaseEventListeners() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            unreadChat = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                unreadChat += (long)snapshot.getValue();
                                if (unreadChat > 0) {
                                    break;
                                }
                            }
                            updateUnreadStatus();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        unreadChat = 0;
                        updateUnreadStatus();
                    }
                }
            };
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.UNREAD_COUNT)
                    .child(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID))
                    .addValueEventListener(unreadListener);
        }
    }

    @Override
    protected void onStop() {
        if (unreadListener != null){
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.UNREAD_COUNT)
                    .child(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID))
                    .removeEventListener((ValueEventListener) unreadListener);
        }
        super.onStop();
    }

    /**
     * method to get data from intent
     */
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
        }
        if (getIntent() != null && getIntent().getData() != null) {
            queryParams = getIntent().getData().getQueryParameter("deal_id");
        }
        if (fromClass.equals(Constants.AppConstant.QR_CODE)) {
            previousPosition = 0;
            resetSideMenu(1);
            setFragmentOnContainer(1);
        } else if (fromClass.equals(Constants.AppConstant.EARNING)) {
            previousPosition = 0;
            resetSideMenu(5);
            setFragmentOnContainer(5);
        } else if (fromClass.equals(Constants.AppConstant.NOTIFICATION)) {
            handelNotificationFlow();
        } else if (queryParams != null && !queryParams.equals("")) {
            try {
                String dealId = new String(Base64.decode(queryParams, Base64.DEFAULT), "UTF-8");
                Intent intent = new Intent(this, ProductDetailsActivity.class);
                intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                startActivity(intent);
                resetSideMenu(0);
                setFragmentOnContainer(0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            resetSideMenu(0);
            setFragmentOnContainer(0);
        }
    }


    /**
     * method to handel notification flow
     */
    private void handelNotificationFlow() {
        if (getIntent()!= null && getIntent().getExtras() != null) {
            int type = getIntent().getExtras().getInt(Constants.IntentConstant.TYPE);
//            deal-1
//            order-2
//            hunt-3
            switch (type) {
                case 1:
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                    if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(FirebaseConstants.NOTIFICATION)) {
                        final NotificationBean notificationBean = (NotificationBean) getIntent().getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                        if (notificationBean != null) {
                            Intent intent = new Intent(this, ProductDetailsActivity.class);
                            intent.putExtra(Constants.IntentConstant.DEAL_ID, notificationBean.getDealId());
                            startActivity(intent);
                        }
                    }
                    break;
                case 2:
                    if (previousPosition != 1) {
                        previousPosition = 0;
                        resetSideMenu(1);
                        setFragmentOnContainer(1);
                    }
                    break;
                case 3:
                case 4:
                    if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(FirebaseConstants.NOTIFICATION)) {
                        final NotificationBean notificationBean = (NotificationBean) getIntent().getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                        if (notificationBean != null) {
                            AppUtils.getInstance().hitUpdateCountApi(this, notificationBean.getReqId());
                        }
                    }
                    if (previousPosition != 7) {
                        previousPosition = 0;
                        resetSideMenu(7);
                        setFragmentOnContainer(7);
                    }
                    break;
                case 10:
                    if (previousPosition != 6) {
                        previousPosition = 0;
                        resetSideMenu(6);
                        setFragmentOnContainer(6);
                    }
                    break;
                case 11:
                    if (previousPosition != 8) {
                        previousPosition = 0;
                        resetSideMenu(8);
                        setFragmentOnContainer(8);
                    }
                    break;
                default:
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                    setIntent(null);
            }
        }
    }

    /**
     * method to check the location permission
     */
    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, Constants.IntentConstant.REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_LOCATION:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION));
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
            case RCLocation.FINE_LOCATION_PERMISSIONS:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION));
                break;
        }
    }


    /**
     * Method to set the toolbar
     */
    public void setToolbar() {
//        tvTitle.setText(getString(R.string.home));
        tvUserName.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME) + " " +
                AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME)));

        AppUtils.getInstance().setCircularImages(this, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_IMAGE), ivUserImage, R.drawable.ic_side_menu_user_placeholder);

        if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
            ivVerifyEmail.setVisibility(View.GONE);
        }else {
            ivVerifyEmail.setVisibility(View.VISIBLE);
        }

        if (!AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            tvUserName.setText(getString(R.string.guest_user));
            viewProfile.setText(getString(R.string.login));
            ivVerifyEmail.setVisibility(View.GONE);
        }
    }


    /**
     * method to initialize the views
     */
    private void initializeVariables() {
        progressBar.setVisibility(View.GONE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);

        String[] sideMenuItems = getResources().getStringArray(R.array.side_menu_items);
        final TypedArray menuItemsImage = getResources().obtainTypedArray(R.array.menu_items_image);
        TypedArray menuItemsImageDeselected = getResources().obtainTypedArray(R.array.menu_items_image_deselected);
        menuItemsList = new ArrayList<>();
        for (int i = 0; i < sideMenuItems.length; i++) {
            MenuItemModel menuItemModel = new MenuItemModel();
            menuItemModel.setItemName(sideMenuItems[i]);
            menuItemModel.setSelectedResourceId(menuItemsImage.getResourceId(i, -1));
            menuItemModel.setDeselectedResourceId(menuItemsImageDeselected.getResourceId(i, -1));
            menuItemsList.add(menuItemModel);
        }
        menuItemsImage.recycle();
        menuItemsImageDeselected.recycle();

//        if (AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
//            MenuItemModel menuItemModel = new MenuItemModel();
//            menuItemModel.setItemName("LOGOUT");
//            menuItemModel.setSelectedResourceId(R.drawable.ic_home_buddy_available_circle);
//            menuItemModel.setDeselectedResourceId(R.drawable.ic_home_buddy_available_circle);
//            menuItemsList.add(menuItemModel);
//        }

        menuItemsList.get(0).setSelected(true);
        sideMenuAdapter = new SideMenuAdapter(this, menuItemsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                new Handler().postDelayed(() -> {
                    if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                        sidePanel.closeDrawers();
                    }
                }, 100);
                if (previousPosition != position) {
                    resetSideMenu(position);
                    setFragmentOnContainer(position);
                }
            }
        });
        FirebaseDatabaseQueries.getInstance().updateUserData(this);
    }


    /**
     * Method to set fragment on container
     *
     * @param position
     */
    public void setFragmentOnContainer(int position) {
        filterBean = new FilterBean();
        profileToolbarView.setVisibility(View.GONE);
        layoutToolbar.setBackgroundResource(R.drawable.toolbar_gradient);
        menuRight.setVisibility(View.GONE);
        flMenuRightHome.setVisibility(View.GONE);
        tvFilterCount.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        llFab.setVisibility(View.GONE);
        fabMenu.setVisibility(View.GONE);
        fabProduct.setVisibility(View.GONE);
        fabService.setVisibility(View.GONE);
        vDot.setVisibility(View.GONE);
        tvPay.setVisibility(View.GONE);
        llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        llFab.setClickable(false);
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, fragment, HomeFragment.class.getName(), false);
                if (AppUtils.getInstance().isUserLogin(this)) {
                    tvTitle.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME)
                            + " " + getString(R.string.home)));
                }else {
                    tvTitle.setText(getString(R.string.buddy_home));
                }
                flMenuRightHome.setVisibility(View.VISIBLE);
                menuSecondRight.setVisibility(View.VISIBLE);
                menuThirdRight.setVisibility(View.VISIBLE);
                menuSecondRight.setImageResource(R.drawable.ic_home_buddy_location_map_white);
                menuThirdRight.setImageResource(R.drawable.ic_shopper_home_cards_search);
                llFab.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.setImageResource(R.drawable.ic_home_main_pop_up_close_ic);
                changeFabButton();
                break;
            case 1:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyOrdersFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyOrdersFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_orders));
                    menuRight.setVisibility(View.GONE);
//                    menuRight.setImageResource(R.drawable.ic_home_cards_filter);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 2:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyDealsFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyDealsFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_deals));
//                    menuRight.setVisibility(View.VISIBLE);
//                    menuRight.setImageResource(R.drawable.ic_shppers_order_reverse);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 3:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new OfflineDealFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, OfflineDealFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.offline_deals));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 4:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyFansFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyFansFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_fans));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 5:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyEarningFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyEarningFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_earnings));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 6:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new ChatFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, ChatFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.chat));
                    menuRight.setVisibility(View.GONE);
//                    menuRight.setImageResource(R.drawable.ic_chat_list_add);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 7:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new RequestFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, RequestFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.requests));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 8:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new DedicatedRequestFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, DedicatedRequestFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.dedicated_requests));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 9:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new NotificationFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, NotificationFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.notifications));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 10:
                AppUtils.getInstance().logoutUser(this);
                finish();
                break;

           /* default:
                fragment = new LauncherHomeFragment();
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, fragment, LauncherHomeFragment.class.getName(), false);
                if (AppUtils.getInstance().isUserLogin(this)) {
                    tvTitle.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME)
                            + " " + getString(R.string.home)));
                }else {
                    tvTitle.setText(getString(R.string.buddy_home));
                }
                break;*/
        }
    }

    /**
     * method to set adapter on views
     */
    private void setAdapters() {
        rvMenuItemLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMenuItemLayout.setAdapter(sideMenuAdapter);
    }

    /**
     * method to set listeners on views
     */
    private void setListeners() {
        sidePanel.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (AppUtils.getInstance().isUserLogin(HomeActivity.this)) {
                    hitGetUnreadCountApi();
                }
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }


    /**
     * method to change menu icon
     *
     * @param isMenu
     */
    public void changeMenuIcon(boolean isMenu) {
        if (isMenu) {
            ivMenu.setImageResource(R.drawable.ic_home_cards_menu);
        } else {
            ivMenu.setImageResource(R.drawable.ic_back);
        }
    }


    /**
     * function to set the coachMarks
     * @param viewGroup
     * @param tvLocation
     * @param ivShowCategory
     */
    public void setCoachMarks(ViewGroup viewGroup, View tvLocation, View ivShowCategory) {
        if (!AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_HOME_SEEN)) {
            CoachMarksImpl.getInstance().setHomeScreenCoachMarks(this, ivMenu, menuThirdRight, menuSecondRight, flMenuRightHome,
                    viewGroup.getChildAt(0), viewGroup.getChildAt(1), tvLocation, ivShowCategory, flFabMenu);
        }
    }

    /**
     * method to get that load fragment is main
     *
     * @return
     */
    private boolean isMainFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
        return fragment instanceof HomeFragment || fragment instanceof MyOrdersFragment ||
                fragment instanceof UnderDevelopmentFragment || fragment instanceof OfflineDealFragment || fragment instanceof ChatFragment
                || fragment instanceof MyDealsFragment || fragment instanceof MyFansFragment || fragment instanceof MyEarningFragment
                || fragment instanceof RequestFragment || fragment instanceof SettingsFragment || fragment instanceof ProfileFragment ||
                fragment instanceof NotificationFragment || fragment instanceof LauncherHomeFragment || fragment instanceof DedicatedRequestFragment;
    }

    @OnClick({R.id.tv_user_name, R.id.fab_product, R.id.fab_service, R.id.fab_menu, R.id.layout_menu_profile, R.id.iv_setting, R.id.fl_menu, R.id.iv_menu, R.id.menu_right, R.id.fl_menu_right_home, R.id.menu_second_right, R.id.menu_third_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                llFab.setClickable(false);
                new Handler().postDelayed(() -> {
                    if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                        sidePanel.closeDrawers();
                    }
                }, 100);

                menuRight.setVisibility(View.GONE);
                flMenuRightHome.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                profileToolbarView.setVisibility(View.GONE);
                llFab.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                fabProduct.setVisibility(View.GONE);
                fabService.setVisibility(View.GONE);
                vDot.setVisibility(View.GONE);
                layoutToolbar.setBackgroundResource(R.drawable.toolbar_gradient);

                Fragment settingsFragment = new SettingsFragment();
                tvTitle.setText(getString(R.string.settings));
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, settingsFragment, SettingsFragment.class.getName(), false);
                resetSideMenu(-1);
                break;
            case R.id.layout_menu_profile:
                llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                llFab.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                            sidePanel.closeDrawers();
                        }
                    }
                }, 100);

                menuRight.setVisibility(View.GONE);
                flMenuRightHome.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                llFab.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                fabProduct.setVisibility(View.GONE);
                fabService.setVisibility(View.GONE);
                vDot.setVisibility(View.GONE);
                if (AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
                    profileToolbarView.setVisibility(View.VISIBLE);
                    layoutToolbar.setBackgroundResource(android.R.color.transparent);
                    Fragment fragment = new ProfileFragment();
                    tvTitle.setText(getString(R.string.my_profile));
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, ProfileFragment.class.getName(), false);
                    resetSideMenu(-10);
                } else {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    AppUtils.getInstance().openNewActivity(this, loginIntent);
//                    finish();
                }
                break;
            case R.id.fl_menu:
            case R.id.iv_menu:
                llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                llFab.setClickable(false);
                if (isMainFragment()) {
                    sidePanel.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.fl_menu_right_home:
            case R.id.menu_right:
                llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                llFab.setClickable(false);
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
                Intent intent;
                if (fragment instanceof HomeFragment) {
//                    intent = new Intent(this, ProductFilterActivity.class);
                    intent = new Intent(this, ProductFilterActivityTest.class);
                    intent.putExtra(Constants.IntentConstant.FILTER_DATA, filterBean);
                    startActivityForResult(intent, Constants.IntentConstant.REQUEST_FILTER);
                }
                break;
            case R.id.menu_second_right:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.MAP_GRID));
                break;
            case R.id.menu_third_right:
                llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                llFab.setClickable(false);
                Fragment homeFragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
                Intent menuIntent;
                if (homeFragment instanceof HomeFragment) {
                    menuIntent = new Intent(this, SearchProductsActivity.class);
                    startActivityForResult(menuIntent, Constants.IntentConstant.REQUEST_SEARCH);
                }
                break;
            case R.id.fab_product:
                changeFabButton();
                Intent productIntent = new Intent(HomeActivity.this, AddProductServiceActivity.class);
                productIntent.putExtra(Constants.IntentConstant.IS_PRODUCT, true);
                startActivity(productIntent);
                break;
            case R.id.fab_service:
                changeFabButton();
                Intent serviceIntent = new Intent(HomeActivity.this, AddProductServiceActivity.class);
                serviceIntent.putExtra(Constants.IntentConstant.IS_PRODUCT, false);
                startActivity(serviceIntent);
                break;
            case R.id.fab_menu:
                if (AppUtils.getInstance().isLoggedIn(this)) {
//                    if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    llFab.setBackgroundColor(ContextCompat.getColor(this, fabProduct.getVisibility() == View.VISIBLE ? android.R.color.transparent : R.color.colorDialogBackground));
                    llFab.setClickable(fabProduct.getVisibility() != View.VISIBLE);
                    float fromDegree = fabProduct.getVisibility() == View.VISIBLE ? 45f : 0f;
                    float toDegree = fabProduct.getVisibility() == View.VISIBLE ? 0f : 45f;
                    AppUtils.getInstance().rotateView(this, fabMenu, fromDegree, toDegree);
                    fabProduct.setVisibility(fabProduct.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    fabService.setVisibility(fabService.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    vDot.setVisibility(fabProduct.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
//                    } else {
//                        hitEmailValidateApi();
//                    }

                    new Handler().postDelayed(() -> {
                        if (!AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_HOME_SEEN) && fabProduct.isShown() && fabService.isShown()) {
                            CoachMarksImpl.getInstance().setAddProductOptionCoachMarks(this, fabProduct, fabService);
                        }
                    }, 200);

                }
                break;
        }
    }

    /**
     * method to change fab button
     */
    private void changeFabButton() {
        llFab.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        llFab.setClickable(false);
        AppUtils.getInstance().rotateView(this, fabMenu, 45f, 0f);
        fabProduct.setVisibility(View.GONE);
        fabService.setVisibility(View.GONE);
        vDot.setVisibility(View.GONE);
    }

    /**
     * method to reset side menu
     *
     * @param position
     */
    public void resetSideMenu(int position) {
        if (previousPosition >= 0) menuItemsList.get(previousPosition).setSelected(false);
        sideMenuAdapter.notifyItemChanged(previousPosition);
        if (position >= 0) menuItemsList.get(position).setSelected(true);
        previousPosition = position;
        sideMenuAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        if (isMainFragment()) {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            AppUtils.getInstance().showToast(this, getString(R.string.s_click_back_again_msg));

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.AppConstant.TIME_OUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RCLocation.REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION).putExtra(Constants.IntentConstant.LOCATION, false));
        }
        else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            data.setAction(Constants.IntentConstant.MAP_LOCATION);
            data.putExtra(Constants.IntentConstant.MAP_LOCATION, resultCode == RESULT_OK);
            LocalBroadcastManager.getInstance(this).sendBroadcast(data);
        }
        else if (requestCode == Constants.IntentConstant.REQUEST_FILTER && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            filterBean = (FilterBean) data.getExtras().getSerializable(Constants.IntentConstant.FILTER_DATA);
            if (filterBean != null && filterBean.getCount() > 0) {
                tvFilterCount.setVisibility(View.VISIBLE);
                tvFilterCount.setText(String.valueOf(filterBean.getCount()));
            } else {
                tvFilterCount.setVisibility(View.GONE);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.FILTER_DATA));
        }
        else if (requestCode == Constants.NetworkConstant.REQUEST_CATEGORIES && resultCode == RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
            if (fragment instanceof ProfileFragment) {
                ((ProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * method to get the filter data
     *
     * @return
     */
    public FilterBean getFilterData() {
        return filterBean == null ? new FilterBean() : filterBean;
    }

    /**
     * method to set the product filter data
     *
     * @return
     */
    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
        if (filterBean != null && filterBean.getCount() > 0){
            tvFilterCount.setVisibility(View.VISIBLE);
            tvFilterCount.setText(String.valueOf(filterBean.getCount()));
        }else {
            tvFilterCount.setVisibility(View.GONE);
        }
    }


    /**
     * Method to hit the signup api
     *
     * @param
     */
    public void hitEmailValidateApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitEmailValidateApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppSharedPreference.getInstance().putString(HomeActivity.this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "1");
                        llFab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, fabProduct.getVisibility() == View.VISIBLE ? android.R.color.transparent : R.color.colorDialogBackground));
                        llFab.setClickable(fabProduct.getVisibility() != View.VISIBLE);
                        float fromDegree = fabProduct.getVisibility() == View.VISIBLE ? 45f : 0f;
                        float toDegree = fabProduct.getVisibility() == View.VISIBLE ? 0f : 45f;
                        AppUtils.getInstance().rotateView(HomeActivity.this, fabMenu, fromDegree, toDegree);
                        fabProduct.setVisibility(fabProduct.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        fabService.setVisibility(fabService.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        vDot.setVisibility(fabProduct.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(HomeActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(HomeActivity.this, response);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1);
    }


    /**
     * function to hit unread count api
     */
    private void hitGetUnreadCountApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "2");
        Call<ResponseBody> call = apiInterface.hitGetUnreadCountApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        UnreadCountResponse unreadCountResponse = new Gson().fromJson(response, UnreadCountResponse.class);
                        unreadRequest = unreadCountResponse.getResult().getUnreadRequestCount();
                        updateUnreadStatus();
                        break;
                }
            }

            @Override
            public void onError(String response, int requestCode) {
//                progressBar.setVisibility(View.GONE);
//                AppUtils.getInstance().showToast(HomeActivity.this, response);
            }

            @Override
            public void onFailure() {
//                progressBar.setVisibility(View.GONE);
            }
        }, 1);

        if (!AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
            ApiInterface apiIntf = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
            final HashMap<String, String> par = new HashMap<>();
            par.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
            Call<ResponseBody> calls = apiIntf.hitProfileDataApi(AppUtils.getInstance().encryptData(par));
            ApiCall.getInstance().hitService(this, calls, new NetworkListener() {
                @Override
                public void onSuccess(int responseCode, String response, int requestCode) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                        ProfileResponse profileResponse = new Gson().fromJson(response, ProfileResponse.class);
                        AppSharedPreference.getInstance().putString(HomeActivity.this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, profileResponse.getResult().getIsEmailVerified());
                        setToolbar();
                    }
                }
                @Override
                public void onError(String response, int requestCode) {}
                @Override
                public void onFailure() {}
            }, Constants.NetworkConstant.REQUEST_PROFILE);
        }
    }

    /**
     * function to update side menu dot
     */
    private void updateUnreadStatus() {
        if (unreadRequest + unreadChat > 0) {
            //show menu dot
            ivUnread.setVisibility(View.VISIBLE);
        }else {
            //hide menu dot
            ivUnread.setVisibility(View.GONE);
        }
        sideMenuAdapter.notifyDataSetChanged();
    }

    /**
     * function to show pay option
     * @param isShow
     */
    public void showPayOption(boolean isShow) {
        tvPay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * function to show pay option
     * @param message
     */
    public void showPayDialog(String message, int responseCode) {
        new CustomDialogForPaymentInfo(this, message, responseCode, () -> {
            if (previousPosition != 5) {
                resetSideMenu(5);
                setFragmentOnContainer(5);
            }
        }).show();
    }

}
