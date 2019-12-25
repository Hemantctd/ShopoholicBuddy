package com.shopoholicbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.models.versionupdateresponse.VersionUpdateResponse;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Splash activity.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.getInstance().generateFBKeyHash(this);
        AppUtils.getInstance().getDeviceToken(this);


        checkForceUpdateAPI();

//        showSplashScreen();
    }


    /**
     * method for checking force update------------
     */
    private void checkForceUpdateAPI() {
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            final HashMap<String, String> params = new HashMap<>();
            params.put(Constants.NetworkConstant.PARAM_VERSION_NAME, getAppVersion(this));
            params.put(Constants.NetworkConstant.PARAM_PLATFORM, "1");
            params.put(Constants.NetworkConstant.PARAM_APP_TYPE, "2");
            ApiInterface apiInterface = RestApi.createService(this, ApiInterface.class);
            Call<ResponseBody> call = apiInterface.hitCheckVersionApi(params);
            ApiCall.getInstance().hitService(this, call, new NetworkListener() {
                @Override
                public void onSuccess(int responseCode, String response, int requestCode) {
                    try {
                        VersionUpdateResponse bean = new Gson().fromJson(response, VersionUpdateResponse.class);
                        if (bean.getCode() == 310) {
                            if (bean.getResult().getVersionName() != null && Double.parseDouble(bean.getResult().getVersionName())<=(Double.parseDouble(getAppVersion(SplashActivity.this)))) {
                                showSplashScreen();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this, R.style.DatePickerTheme);
                                builder.setTitle(getString(R.string.new_version_available));
                                builder.setMessage(getString(R.string.upgrade_to_new_version));
                                builder.setPositiveButton(getString(R.string.update), (dialogInterface, i) -> redirectStore("https://play.google.com/store/apps/details?id=com.shopoholic"));
                                if (bean.getResult().getUpdateType() != null && bean.getResult().getUpdateType().equals("1")) {
                                    builder.setNegativeButton(getString(R.string.no_thanks), (dialogInterface, i) -> showSplashScreen());
                                }
                                builder.setCancelable(false);
                                builder.show();
                            }
                        } else {
                            showSplashScreen();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSplashScreen();
                    }

                }

                @Override
                public void onError(String response, int requestCode) {
                    showSplashScreen();

                }

                @Override
                public void onFailure() {
                    showSplashScreen();
                }
            }, 1);
        } else {
            showSplashScreen();
        }
    }



    /*
       * method to start new activity after 2 seconds
       * */
    public void showSplashScreen() {
        new Handler().postDelayed(() -> {
            Intent intent;
//                if (true){
//                    intent = new Intent(SplashActivity.this, OtherInformationActivity.class);
//                }
//                else

            if (AppSharedPreference.getInstance().getBoolean(SplashActivity.this, AppSharedPreference.PREF_KEY.IS_CATEGORY_SELECTED)){
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else if (AppSharedPreference.getInstance().getBoolean(SplashActivity.this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)){
                intent = new Intent(SplashActivity.this, PreferredCategoriesActivity.class);
            } else if (AppSharedPreference.getInstance().getBoolean(SplashActivity.this, AppSharedPreference.PREF_KEY.IS_TUTORIAL_SEEN)){
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }else {
                intent = new Intent(SplashActivity.this, WalkThroughActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            finish();
        }, Constants.AppConstant.TIME_OUT);

    }


    /**
     * redirect to play store
     * @param updateUrl
     */
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * function to get the app versions
     * @param context
     * @return
     */
    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            // Log.e(TAG, e.getMessage());
        }
        return result;
    }


}
