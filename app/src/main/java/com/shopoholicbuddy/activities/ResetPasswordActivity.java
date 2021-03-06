package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 21/3/18.
 */

public class ResetPasswordActivity extends BaseActivity implements View.OnTouchListener, NetworkListener {


    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.layout_toolbar_reset_password)
    LinearLayout layoutToolbarResetPassword;
    @BindView(R.id.et_old_password)
    CustomEditText etOldPassword;
    @BindView(R.id.view_old_password)
    View viewOldPassword;
    @BindView(R.id.et_new_password)
    CustomEditText etNewPassword;
    @BindView(R.id.et_confirm_password)
    CustomEditText etConfirmPassword;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    private boolean isNewPasswordShowing;
    private boolean isConfirmPasswordShowing;
    private Animation animation;
    private String userId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_or_change_password);
        ButterKnife.bind(this);
        setListeners();
    }

    private void setListeners() {
        btnAction.setText(getString(R.string.submit));
        etNewPassword.performClick();
        etConfirmPassword.performClick();
        etNewPassword.setOnTouchListener(this);
        etConfirmPassword.setOnTouchListener(this);

        layoutToolbarResetPassword.setVisibility(View.VISIBLE);
        layoutToolbar.setVisibility(View.GONE);
        etOldPassword.setVisibility(View.GONE);
        viewOldPassword.setVisibility(View.GONE);

        if (getIntent() != null && getIntent().getExtras() != null){
            userId = getIntent().getExtras().getString(Constants.NetworkConstant.USER_ID, "");
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        CustomEditText viewEditText;
        if (v.getId() == R.id.et_new_password) {
            viewEditText = etNewPassword;
        } else {
            viewEditText = etConfirmPassword;
        }
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = viewEditText.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (v.getRight() - drawable.getBounds().width())) {
                // your action here
                if (v.getId() == R.id.et_new_password ? !isNewPasswordShowing : !isConfirmPasswordShowing) {
                    if (v.getId() == R.id.et_new_password) isNewPasswordShowing = true;
                    else isConfirmPasswordShowing = true;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_visible, 0);
                    viewEditText.setTransformationMethod(null);
                } else {
                    if (v.getId() == R.id.et_new_password) isNewPasswordShowing = false;
                    else isConfirmPasswordShowing = false;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_not_visible, 0);
                    viewEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
                viewEditText.setSelection(viewEditText.getText().length());
                viewEditText.requestFocus();
                return true;
            }
        }
        return false;
    }

    @OnClick({R.id.iv_reset_back, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_reset_back:
                finish();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate()) {
                        hitResetPasswordApi();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                }
                break;
        }
    }


    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etNewPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (etNewPassword.getText().toString().length() < 6) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_must_be_6_15_chars));
            return false;
        } else if (etConfirmPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_confir_password));
            return false;
        }else if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_not_match));
            return false;
        }

        return true;
    }


    /**
     * Method to hit the reset password api
     */
    private void hitResetPasswordApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(ResetPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, userId);
        params.put(Constants.NetworkConstant.PARAM_PASSWORD, etNewPassword.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, Constants.NetworkConstant.BUDDY);
        Call<ResponseBody> call = apiInterface.hitResetPasswordApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_RESET_PASSWORD);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(ResetPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_RESET_PASSWORD:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            AppUtils.getInstance().openNewActivity(this, new Intent(this, LoginActivity.class));
//                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(ResetPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        AppUtils.getInstance().setButtonLoaderAnimation(ResetPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }
}
