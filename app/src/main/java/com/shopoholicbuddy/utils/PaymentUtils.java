package com.shopoholicbuddy.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.interfaces.PaymentResponseCallback;
import com.shopoholicbuddy.models.checksumresponse.ChecksumResponse;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.stripe.android.SourceCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PaymentUtils implements NetworkListener {

    private static PaymentUtils mPaymentUtils;
    private AppCompatActivity mActivity;
    private String paytmMerchantId = "";
    private String customerId = "";
    private String transactionId = "";
    private String newChecksum = "";
    private String checksum = "";
    private double amount = 0.0;
    private String paymentId = "";
    private String currencyCode = "";
    private String sourceToken = "";
    private String paymentType = "1";
    private PaymentResponseCallback paymentResponseCallback;
    private String publishableKey = "pk_test_pNQwocVkhFKS4Nz1aG9qC8ic"; //alipay testing key
//    private String publishableKey = "pk_live_tymoCcAHJqvRuefVvdYmJ9Kw";  //alipay live key

    public static PaymentUtils getInstance() {
        if (mPaymentUtils == null) {
            mPaymentUtils = new PaymentUtils();
        }
        return mPaymentUtils;
    }

    /**
     * function to make online payment
     */
    public void payOnlineBill(AppCompatActivity mActivity, String currencyCode, double amount, PaymentResponseCallback paymentResponseCallback) {
        this.mActivity = mActivity;
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.paymentResponseCallback = paymentResponseCallback;
        if (!AppUtils.getInstance().isInternetAvailable(this.mActivity)) {
            return;
        }
        switch (currencyCode) {
            case "INR":
                //implementing paytm payment gateway
                if (transactionId.equals("") || checksum.equals("")) {
                    paymentType = "2";
                    hitGenerateChecksumApiForPaytm();
                } else {
                    hitPaymentApi();
                }
                break;
            case "HKD":
            case "SGD":
                //implementing alipay (strip) payment gateway
//                    hitGenerateChecksumApiForPaytm();
                paymentType = "1";
                hitAlipayPayment();
                break;
            case "MOP":
            case "AED":
            default:
                //implementing (strip) payment gateway
                // todo... till now its paytm
                paymentType = "2";
                if (transactionId.equals("") || checksum.equals("") || paymentId.equals("")) {
                    hitGenerateChecksumApiForPaytm();
                } else {
                    hitPaymentApi();
                }
                break;
        }
    }

    /**
     * Method to hit the Generate Checksum Api
     */
    private void hitGenerateChecksumApiForPaytm() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, String.format(Locale.ENGLISH, "%.2f", amount));
        Call<ResponseBody> call = apiInterface.hitGenerateChecksumApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_CHECKSUM);

    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
         switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CHECKSUM:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        ChecksumResponse checksumResponse = new Gson().fromJson(response, ChecksumResponse.class);
                        paymentId = checksumResponse.getResult().getOrderNumber();
                        paytmMerchantId = checksumResponse.getResult().getPaytmMerchantId();
                        customerId = checksumResponse.getResult().getCustomerId();
                        checksum = checksumResponse.getResult().getChecksum();
                        openPaytmGateway();
                        break;
                    default:
                        try {
//                            AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            paymentResponseCallback.onPaymentFailuer(responseCode, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_PAYMENT:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        try {
                            paymentResponseCallback.onPaymentSuccess(responseCode, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
//                            AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            paymentResponseCallback.onPaymentFailuer(responseCode, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            default:
                try {
//                    AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    paymentResponseCallback.onPaymentFailuer(responseCode, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        AppUtils.getInstance().showToast(mActivity, response);
        paymentResponseCallback.onPaymentFailuer(400, response);
    }

    @Override
    public void onFailure() {
    }




    /**
     * function to start paytm payment gateway
     */
    private void openPaytmGateway() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put( "MID" , paytmMerchantId);
        paramMap.put( "ORDER_ID" , paymentId);
        paramMap.put( "CUST_ID" , customerId);
        paramMap.put( "MOBILE_NO" , AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.PHONE_NO));
        paramMap.put( "EMAIL" , AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL));
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT", String.format(Locale.ENGLISH, "%.2f", amount));
        paramMap.put( "WEBSITE" , "WEBSTAGING");
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + paymentId);
        paramMap.put( "CHECKSUMHASH" , checksum);
        PaytmOrder Order = new PaytmOrder(paramMap);


        Service.initialize(Order, null);

        Service.startPaymentTransaction(mActivity, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {
                AppUtils.getInstance().showToast(mActivity, "UI Error ");
            }
            public void onTransactionResponse(Bundle inResponse) {
                AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.SHOPPER, "Payment Transaction response " + inResponse.toString());
                String status = inResponse.getString("STATUS", "");
                if (status.equals("TXN_SUCCESS")) {
                    transactionId = inResponse.getString("TXNID");
                    newChecksum = inResponse.getString("CHECKSUMHASH");
                    new Handler().postDelayed(() -> hitPaymentApi(), 1000);
                } else {
                    paymentResponseCallback.onPaymentFailuer(504, mActivity.getString(R.string.transaction_fail));
                }

            }
            public void networkNotAvailable() {
                paymentResponseCallback.onPaymentFailuer(500, mActivity.getString(R.string.network_connection_error));
            }
            public void clientAuthenticationFailed(String inErrorMessage) {
                paymentResponseCallback.onPaymentFailuer(501, mActivity.getString(R.string.auth_fail));
            }
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                paymentResponseCallback.onPaymentFailuer(502, mActivity.getString(R.string.unable_to_load_page));
            }
            public void onBackPressedCancelTransaction() {
                paymentResponseCallback.onPaymentFailuer(503, mActivity.getString(R.string.transaction_fail));
            }
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                paymentResponseCallback.onPaymentFailuer(504, mActivity.getString(R.string.transaction_canceled));
            }
        });
    }


    /**
     * payment using alipay
     */
    private void hitAlipayPayment() {
        SourceParams alipayParams = SourceParams.createAlipaySingleUseParams(
                (long) (Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", amount)) * 100), // Amount is a long int in the lowest denomination.
                currencyCode, //currency code
                AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.FIRST_NAME) + " "
                        + AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LAST_NAME), // customer name
                AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL), // customer email
                "shopoholic://alipaybuddy"); // a redirect address to get the user back into your app
        Stripe stripe = new Stripe(mActivity, publishableKey);
        stripe.createSource(alipayParams, new SourceCallback() {
            @Override
            public void onError(Exception error) {
//                AppUtils.getInstance().showToast(mActivity, error.toString());
                paymentResponseCallback.onPaymentFailuer(601, error.toString());
            }

            @Override
            public void onSuccess(Source source) {
                paymentResponseCallback.onPaymentSuccess(201, "");
                sourceToken = source.getId();
                if (source.getStatus().equalsIgnoreCase("pending")) {
                    String redirectUrl = source.getRedirect().getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(redirectUrl));
                    mActivity.startActivity(intent);
                }
            }
        });

    }


    /**
     * function to hit payment api
     */
    public void hitPaymentApi() {
        if (mActivity != null) {
            ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);
            final HashMap<String, String> params = new HashMap<>();
            params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
            params.put(Constants.NetworkConstant.PARAM_AMOUNT, String.format(Locale.ENGLISH, "%.2f", amount));
            params.put(Constants.NetworkConstant.PARAM_PAYMENT_ID, paymentId);
            params.put(Constants.NetworkConstant.PARAM_PAYMENT_TYPE, paymentType);
            params.put(Constants.NetworkConstant.PARAM_TRANSACTION_ID, paymentType.equals("1") ? sourceToken : transactionId);
            Call<ResponseBody> call = apiInterface.hitPayBuddyDueApi(AppUtils.getInstance().encryptData(params));
            ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PAYMENT);
        }
    }
}
