package com.shopoholicbuddy.dialogs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.DealImagesAdapter;
import com.shopoholicbuddy.adapters.TaxAdapter;
import com.shopoholicbuddy.customviews.CustomButton;
import com.shopoholicbuddy.customviews.CustomEditText;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.TaxArr;
import com.shopoholicbuddy.models.huntdetailresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


/**
 * Dialog for image selection
 */

public class CustomDialogForAcceptHunt extends DialogFragment implements AmazonCallback, NetworkListener {


    Unbinder unbinder;
    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.tv_upload_image)
    CustomTextView tvUploadImage;
    @BindView(R.id.image_loader)
    ProgressBar imageLoader;
    @BindView(R.id.tv_upload)
    CustomTextView tvUpload;
    @BindView(R.id.rv_upload_image)
    RecyclerView rvUploadImage;
    @BindView(R.id.tv_price)
    CustomEditText tvPrice;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.layout_button_loader)
    FrameLayout layoutButtonLoader;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;
    @BindView(R.id.tv_delivery_charges)
    CustomTextView tvDeliveryDate;
    @BindView(R.id.ll_delivery_date)
    LinearLayout llDeliveryDate;
    @BindView(R.id.label_bidding_price)
    CustomTextView labelBiddingPrice;
    @BindView(R.id.rv_taxes)
    RecyclerView rvTaxes;

    private AppCompatActivity mActivity;
    private AmazonS3 mAmazonS3;
    private String huntId = "";
    private ArrayList<ImageBean> imagesBeanList;
    private ArrayList<TaxArr> taxesArrayList;
    private DealImagesAdapter imagesAdapter;
    private TaxAdapter taxAdapter;
    private int id = 0;
    private boolean isLoading = false;
    private Result huntDetails;
    private boolean isClick = false;

    public CustomDialogForAcceptHunt() {
    }

    public static CustomDialogForAcceptHunt newInstance(String huntId, Result huntData) {
        CustomDialogForAcceptHunt fragment = new CustomDialogForAcceptHunt();
        Bundle args = new Bundle();
        args.putString(Constants.IntentConstant.HUNT_ID, huntId);
        args.putSerializable(Constants.IntentConstant.HUNT_DETAILS, huntData);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_accept_hunt, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
        initVariables();
        initializeAmazonS3();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
    }



    /**
     * function to initialize the variables
     */
    private void initVariables() {
        if (getArguments() != null) {
            huntId = getArguments().getString(Constants.IntentConstant.HUNT_ID, "");
            huntDetails = (Result) getArguments().getSerializable(Constants.IntentConstant.HUNT_DETAILS);
            if (huntDetails != null) labelBiddingPrice.setText(TextUtils.concat(getString(R.string.bidding_price) + "(" +huntDetails.getCurrencySymbol() + ")*"));
        }
        btnAction.setText(getString(R.string.submit));
        imagesBeanList = new ArrayList<>();
        taxesArrayList = new ArrayList<>();
        imagesAdapter = new DealImagesAdapter(mActivity, imagesBeanList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_remove_image:
                    imagesBeanList.remove(position);
                    imagesAdapter.notifyDataSetChanged();
            }
        });
        taxesArrayList.add(new TaxArr());
        taxAdapter = new TaxAdapter(mActivity, taxesArrayList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_remove:
                    taxesArrayList.remove(position);
                    taxAdapter.notifyDataSetChanged();
            }
        });
        rvUploadImage.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvUploadImage.setAdapter(imagesAdapter);

        rvTaxes.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvTaxes.setAdapter(taxAdapter);

        if (huntDetails != null && huntDetails.getProductType().equals("2")) {
            llDeliveryDate.setVisibility(View.GONE);
        }
    }

    
    /**
     * initialize amazon service
     */
    private void initializeAmazonS3() {
        mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(mActivity);
        mAmazonS3.setCallback(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_upload, R.id.btn_action, R.id.tv_delivery_charges, R.id.tv_cancel, R.id.tv_tax})
    public void onViewClicked(View view) {
        AppUtils.getInstance().hideKeyboardOfEditText(mActivity, tvPrice);
        switch (view.getId()) {
            case R.id.tv_upload:
                if (!isClick) {
                    isClick = true;
                    changeClick();
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        if (imagesBeanList.size() < 5) {
                            checkStoragePermission();
                        } else {
                            AppUtils.getInstance().showToast(mActivity, getString(R.string.max_5_images_allowed));
                        }
                    }
                }
                break;
            case R.id.tv_tax:
                if (taxesArrayList.size() >= 10) {
                    AppUtils.getInstance().showToast(mActivity, getString(R.string.maximum_10_tax_allow));
                }else if (taxesArrayList.size() > 0) {
                    String taxName = taxesArrayList.get(taxesArrayList.size() - 1).getTaxName();
                    String taxPer = taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage();
                    if (taxName == null || taxName.isEmpty()) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_tax_name));
                    }else if (taxPer == null || taxPer.isEmpty()) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_tax_percentage));
                    }else if (Double.parseDouble(taxPer) >= 100) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.tax_per_must_less_than_100));
                    }else
                        taxesArrayList.add(new TaxArr());
                }else {
                    taxesArrayList.add(new TaxArr());
                }
                taxAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    if (isValidate() && !isLoading) {
                        hitAcceptHuntApi();
                    }
                }
                break;
            case R.id.tv_delivery_charges:
                AppUtils.getInstance().openDatePicker(mActivity, tvDeliveryDate, Calendar.getInstance(), null, tvDeliveryDate.getText().toString().trim(), 1);
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    /**
     * function to change click status
     */
    private void changeClick() {
        new Handler().postDelayed(() -> {
            isClick = false;
        }, 2000);
    }

    /**
     * function for validation
     *
     * @return
     */
    private boolean isValidate() {
        if (imagesBeanList.size() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_upload_image));
            return false;
        } else if (tvPrice.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_bidding_price));
            return false;
        } else if (huntDetails != null && huntDetails.getProductType().equals("1") && tvDeliveryDate.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_delivery_date));
            return false;
        }else if (taxesArrayList.size() == 0 || taxesArrayList.get(taxesArrayList.size() - 1).getTaxName().equals("") || taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_taxes));
            return false;
        } else if (taxesArrayList.size() != 0 && !taxesArrayList.get(taxesArrayList.size() - 1).getTaxPercentage().equals("")) {
            try {
                for (TaxArr taxArr: taxesArrayList) {
                    if (Double.parseDouble(taxArr.getTaxPercentage()) >= 100) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.tax_per_must_less_than_100));
                        return false;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                AppUtils.getInstance().showToast(mActivity, getString(R.string.tax_per_must_less_than_100));
                return false;
            }
        } else if (imageLoader.getVisibility() == View.VISIBLE) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.images_loading_please_wait));
            return false;
        }
        return true;
    }



    /**
     * Method to hit the signup api
     */
    private void hitAcceptHuntApi() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
        params.put(Constants.NetworkConstant.PARAM_BIDDING_PRICE, tvPrice.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_IMAGE_ARRAY, new Gson().toJson(getImagesArray()));
        params.put(Constants.NetworkConstant.PARAM_TAX_ARR, new Gson().toJson(getTaxArray()));
        if (huntDetails != null && !huntDetails.getProductType().equals("2")) {
            params.put(Constants.NetworkConstant.PARAM_DELIVERY_DATE, AppUtils.getInstance().formatDate(tvDeliveryDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT));
        }else {
            params.put(Constants.NetworkConstant.PARAM_DELIVERY_DATE, "");
        }
        params.put(Constants.NetworkConstant.PARAM_STATUS, "2");
        if (huntDetails != null) {
            params.put(Constants.NetworkConstant.PARAM_SLOT_ID_ARR, new Gson().toJson(getSlotIdArray()));
        }
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        Call<ResponseBody> call = apiInterface.hitUpdateRequestApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST);
    }



    /**
     * method to get the slot id aray
     */
    private ArrayList<HashMap<String, String>> getSlotIdArray() {
        ArrayList<HashMap<String, String>> slotList = new ArrayList<>();
        if (huntDetails.getSelectedSlots() != null && !huntDetails.getSelectedSlots().equals("")) {
            String[] slotIdArray = huntDetails.getSelectedSlots().split(",");
            for (String id : slotIdArray) {
                for (String date : huntDetails.getSelectedDates()) {
                    HashMap<String, String> slotMap = new HashMap<>();
                    slotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, id);
                    slotMap.put(Constants.NetworkConstant.PARAM_DATE, date);
                    slotList.add(slotMap);
                }
            }
        }
        return slotList;
    }

    /**
     * method to get images array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getImagesArray() {
        ArrayList<HashMap<String, String>> imagesList = new ArrayList<>();
        for (int i = 0; i < imagesBeanList.size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_IMAGE_PATH, imagesBeanList.get(i).getServerUrl());
            imageMap.put(Constants.NetworkConstant.PARAM_MEDIA_TYPE, "1");
            imageMap.put(Constants.NetworkConstant.PARAM_DEFAULT_IMAGE, i == 0 ? "1" : "0");
            imagesList.add(imageMap);
        }
        return imagesList;
    }

    /**
     * method to get images array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getTaxArray() {
        ArrayList<HashMap<String, String>> taxList = new ArrayList<>();
        for (int i = 0; i < taxesArrayList.size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_TAX_NAME, taxesArrayList.get(i).getTaxName());
            imageMap.put(Constants.NetworkConstant.PARAM_TAX_VALUE, taxesArrayList.get(i).getTaxPercentage());
            taxList.add(imageMap);
        }
        return taxList;
    }


    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if (ContextCompat.checkSelfPermission(mActivity, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]
                        {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                // permission already granted
                CustomDialogForAcceptHunt.this.startActivityForResult(new Intent(mActivity, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 5 - imagesBeanList.size())
                        , Constants.IntentConstant.REQUEST_GALLERY);
            }
        } else {
            //before marshmallow
            CustomDialogForAcceptHunt.this.startActivityForResult(new Intent(mActivity, CameraGalleryActivity.class)
                            .putExtra("maxSelection", 5 - imagesBeanList.size())
                    , Constants.IntentConstant.REQUEST_GALLERY);
        }
    }


    @Override
    public void uploadSuccess(ImageBean imageBean) {
        if (isAdded()) {
            imageBean.setLoading(false);
            updateImageBeanList(imageBean, 1);
        }
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        if (isAdded()) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.image_upload_fail));
            updateImageBeanList(bean, 2);
        }
    }

    @Override
    public void uploadProgress(ImageBean bean) {
        if (isAdded()) {
            AppUtils.getInstance().printLogMessage(mActivity.getCallingPackage(), "Uploaded " + bean.getProgress() + " %");
        }
    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
    }


    /**
     * method to update image bean list
     *
     * @param imageBean
     * @param status
     */
    private void updateImageBeanList(ImageBean imageBean, int status) {
        for (int i = 0; i < imagesBeanList.size(); i++) {
            if (imageBean.getId().equals(imagesBeanList.get(i).getId())) {
                switch (status) {
                    case 1:
                        imagesBeanList.set(i, imageBean);
                        break;
                    case 2:
                        imagesBeanList.remove(imageBean);
                        break;
                }
                break;
            }
        }
        showImageProgress();
        imagesAdapter.notifyDataSetChanged();
    }


    /**
     * method to change loader state
     */
    private void showImageProgress() {
        if (isAdded()) {
            boolean check = false;
            for (ImageBean bean : imagesBeanList) {
                if (bean.isLoading())
                    check = true;
            }
            imageLoader.setVisibility(check ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isAdded()) {
            if (requestCode == Constants.IntentConstant.REQUEST_GALLERY && resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    ArrayList<String> imagesList = data.getExtras().getStringArrayList("result");
                    if (imagesList != null) {
                        for (String filePath : imagesList) {
                            startUpload(filePath);
                        }
                    }
                }
            }
        }
    }


    /**
     * upload file in S3
     *
     * @param path
     */
    private void startUpload(String path) {
        if (isAdded()) {
            ImageBean bean = addDataInBean(path, ++id);
            imagesBeanList.add(bean);
            imagesAdapter.notifyDataSetChanged();
            mAmazonS3.uploadImage(bean);
            showImageProgress();
        }
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path, int id) {
        ImageBean bean = new ImageBean();
        bean.setId(String.valueOf(id));
        bean.setName("pic");
        bean.setImagePath(path);
        bean.setLoading(true);
        return bean;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        if (isAdded()) {
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_UPDATE_BUDDDY_REQUEST:
                    switch (responseCode) {
                        case Constants.NetworkConstant.VERIFY_EMAIL:
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "0");
                            String email = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL);
                            new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                                    .setCancelable(false)
                                    .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                                    .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                        if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                            hitResendLinkApi();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        // do nothing
                                    })
                                    .show();
                            break;
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            Intent intent = new Intent();
                            intent.putExtra(Constants.IntentConstant.HUNT_ID, huntId);
//                            mActivity.setResult(RESULT_OK, intent);
                            if (!mActivity.isFinishing() || !mActivity.isDestroyed())
                                mActivity.finish();
                            break;
                        default:
                            AppUtils.getInstance().showToast(mActivity, response);
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                    break;
            }
        }
    }


    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
        }
    }

    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, null, 1001);
    }

}
