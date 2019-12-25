package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.PreferredCategoriesAdapter;
import com.shopoholicbuddy.adapters.SubCategoriesAdapter;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholicbuddy.models.preferredcategorymodel.Result;
import com.shopoholicbuddy.models.subcategoryresponse.SubCategoryResponse;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholicbuddy.utils.Constants.AppConstant.FILTER_EARNING;

/**
 * Class to select preferred categories
 */

public class PreferredCategoriesActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView rvCategories;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private List<Result> preferredCategoriesList;
    private PreferredCategoriesAdapter preferredCategoriesAdapter;
    private List<Result> selectedCategories;
    public String fromClass = "";
    private String categoryIds = "";
    private List<String> categories;
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private SubCategoriesAdapter subCategoriesAdapter;
    private List<com.shopoholicbuddy.models.subcategoryresponse.Result> subCategoriesList;
    private Menu menu;
    private boolean isCategory;
    private String catId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_categories);
        ButterKnife.bind(this);
        initVariables();
        setToolbar();
        setAdapters();
        setListeners();
        progressBar.setVisibility(View.VISIBLE);
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            hitGetCategoryListApi();
        } else {
            AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
        }
    }

    // Initiating Menu XML file (menu.xml)
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.right_menu_text).setVisible(true);
        menu.findItem(R.id.right_menu_text).setTitle(getString(R.string.save));
        if (fromClass.equals(Constants.AppConstant.FILTER)) {
            menu.findItem(R.id.right_menu_text).setVisible(false);
        }
        return true;
    }*/


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(PreferredCategoriesActivity.this)) {
                    count = 0;
                    hitGetCategoryListApi();
                }
            }
        });
        rvCategories.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= preferredCategoriesList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(PreferredCategoriesActivity.this)) {
                            isPagination = true;
                            hitGetCategoryListApi();
                        }
                    }
                }
            }

        });

    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     */
 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.right_menu_text:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    StringBuilder categoryIds = new StringBuilder();
                    for (int i = 0; i < preferredCategoriesList.size(); i++) {
                        if (preferredCategoriesList.get(i).isSelected()) {
                            if (categoryIds.toString().equals(""))
                                categoryIds.append(preferredCategoriesList.get(i).getCatId());
                            else
                                categoryIds.append(",").append(preferredCategoriesList.get(i).getCatId());
                        }
                    }
                    if (categoryIds.toString().equals("")) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one));
                    } else {
                        this.categoryIds = categoryIds.toString();
                        hitSaveCategoryApi(categoryIds.toString());
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    @OnClick({R.id.iv_back, R.id.tv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_clear:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    StringBuilder categoryIds = new StringBuilder();
                    for (int i = 0; i < preferredCategoriesList.size(); i++) {
                        if (preferredCategoriesList.get(i).isSelected()) {
                            if (categoryIds.toString().equals(""))
                                categoryIds.append(preferredCategoriesList.get(i).getCatId());
                            else
                                categoryIds.append(",").append(preferredCategoriesList.get(i).getCatId());
                        }
                    }
                    if (categoryIds.toString().equals("")) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one));
                    } else {
                        this.categoryIds = categoryIds.toString();
                        hitSaveCategoryApi(categoryIds.toString());
                    }
                }
                break;
        }
    }

    /**
     * method to set toolbar
     */
    private void setToolbar() {
        setSupportActionBar(layoutToolbar);
        tvTitle.setText(getString(R.string.preferred_categories));
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
        if (fromClass.equals(Constants.AppConstant.FILTER) || fromClass.equals(Constants.AppConstant.FILTER_EARNING) ) {
            tvTitle.setText(getString(R.string.preferred_category));
            tvClear.setVisibility(View.GONE);
        }
        if (!isCategory) {
            tvTitle.setText(getString(R.string.sub_category));
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        tvClear.setText(getString(R.string.save));
        preferredCategoriesList = new ArrayList<>();
        subCategoriesList = new ArrayList<>();
        categories = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        preferredCategoriesAdapter = new PreferredCategoriesAdapter(this, preferredCategoriesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (fromClass.equals(Constants.AppConstant.FILTER)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.CATEGORY, preferredCategoriesList.get(position));
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    finish();
                } else {
                    if (preferredCategoriesList.get(position).isSelected()) {
                        preferredCategoriesList.get(position).setSelected(false);
                        categories.remove(preferredCategoriesList.get(position).getCatId());
                    } else {
                        preferredCategoriesList.get(position).setSelected(true);
                        categories.add(preferredCategoriesList.get(position).getCatId());
                    }
                    preferredCategoriesAdapter.notifyItemChanged(position);
                }
            }
        });
        subCategoriesAdapter = new SubCategoriesAdapter(this, subCategoriesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (fromClass.equals(Constants.AppConstant.FILTER)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.CATEGORY, subCategoriesList.get(position));
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    finish();
                }
            }
        });
        if (getIntent() != null && getIntent().getExtras() != null) {
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            selectedCategories = (List<Result>) getIntent().getExtras().getSerializable(Constants.IntentConstant.CATEGORIES);
            isCategory = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_CATEGORY, true);
            catId = getIntent().getExtras().getString(Constants.IntentConstant.CATEGORY, "");
        }
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        rvCategories.setLayoutManager(linearLayoutManager);
        rvCategories.setAdapter(preferredCategoriesAdapter);
    }


    /**
     * method to hit save category api
     *
     * @param categoryIds
     */
    private void hitSaveCategoryApi(String categoryIds) {
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
//        params.put(Constants.NetworkConstant.PARAM_USER_ID, "5");
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_CATEGORY_ID, categoryIds);
        Call<ResponseBody> call = apiInterface.hitSavePreferredCategoryApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SAVE_CATEGORY);
    }


    /**
     * method to hit get categories api
     */
    private void hitGetCategoryListApi() {
//        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call;
        if (isCategory) {
            call = apiInterface.hitGetPreferredCategoryListApi(AppUtils.getInstance().encryptData(params));
        } else {
            params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, catId);
            call = apiInterface.hitGetSubCategoryListApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CATEGORIES:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        if (isCategory) {
                            PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                preferredCategoriesList.clear();
                            }
                            for (Result category : categoriesResponse.getResult()){
                                if (!fromClass.equals(FILTER_EARNING)) {
                                    if (category.getCategoryType().equals(String.valueOf("1"))) {
                                        if (categories.contains(category.getCatId()))
                                            category.setSelected(true);
                                        preferredCategoriesList.add(category);
                                    }
                                }else {
                                    if (categories.contains(category.getCatId()))
                                        category.setSelected(true);
                                    preferredCategoriesList.add(category);
                                }
                            }
//                            preferredCategoriesList.addAll(categoriesResponse.getResult());
                            if (selectedCategories != null && selectedCategories.size() > 0) {
                                for (Result category : selectedCategories) {
                                    for (int i = 0; i < preferredCategoriesList.size(); i++) {
                                        if (category.getCatId().equals(preferredCategoriesList.get(i).getCatId())) {
                                            preferredCategoriesList.get(i).setSelected(true);
                                            break;
                                        }
                                    }
                                }
                            }
                            rvCategories.setAdapter(preferredCategoriesAdapter);
                            preferredCategoriesAdapter.notifyDataSetChanged();
                            isMoreData = categoriesResponse.getNext() != -1;
                            if (isMoreData) count = categoriesResponse.getNext();
                            if (preferredCategoriesList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                        } else {
                            SubCategoryResponse subCategoriesResponse = new Gson().fromJson(response, SubCategoryResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                subCategoriesList.clear();
                            }
                            subCategoriesList.addAll(subCategoriesResponse.getResult());
                            rvCategories.setAdapter(subCategoriesAdapter);
                            subCategoriesAdapter.notifyDataSetChanged();
                            isMoreData = subCategoriesResponse.getNext() != -1;
                            if (isMoreData) count = subCategoriesResponse.getNext();
                            if (subCategoriesList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_SAVE_CATEGORY:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        if (fromClass.equals(Constants.AppConstant.PROFILE)) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.IntentConstant.CATEGORIES, categoryIds);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_CATEGORY_SELECTED, true);
                            AppUtils.getInstance().openNewActivity(this, new Intent(this, HomeActivity.class));
//                            finish();
                            break;
                        }
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

    }
}
