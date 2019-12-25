package com.shopoholicbuddy.network;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;


/**
 * ApiInterface.java
 * This class act as an interface between Retrofit and Classes used using Retrofit in Application
 *
 * @author Appinvetiv
 * @version 1.0
 * @since 1.0
 */

public interface ApiInterface {

    //  POST queries

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> hitLoginApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sociallogin")
    Call<ResponseBody> hitSocialLoginApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> hitSignUpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("forgot")
    Call<ResponseBody> hitForgotPasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("otpgenerate")
    Call<ResponseBody> hitResendOtpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("resetpass")
    Call<ResponseBody> hitResetPasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("changepassword")
    Call<ResponseBody> hitChangePasswordApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("otpverify")
    Call<ResponseBody> hitVerifyOtpApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("saveotherdetails")
    Call<ResponseBody> hitSaveInformationApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("deals")
    Call<ResponseBody> hitLikeDealsApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("bookmarkdeal")
    Call<ResponseBody> hitBookmarkProductsApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("edit")
    Call<ResponseBody> hitEditProfileDataApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savelocation")
    Call<ResponseBody> hitSaveAddressApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("createorder")
    Call<ResponseBody> hitCreateOrderApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("follow")
    Call<ResponseBody> hitBuddyFollowApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savecategorybyuser")
    Call<ResponseBody> hitSavePreferredCategoryApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sharedealrequest")
    Call<ResponseBody> hitShareDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("blockdeal")
    Call<ResponseBody> hitBlockDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("reportdeal")
    Call<ResponseBody> hitReportDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savedeal")
    Call<ResponseBody> hitAddDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("editdeal")
    Call<ResponseBody> hitEditDealApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("updatebuddyrequest")
    Call<ResponseBody> hitUpdateRequestApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("orderupdatebuddy")
    Call<ResponseBody> hitUpdateOrderStatusApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("savefeedback")
    Call<ResponseBody> hitFeedbackApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("deleteskills")
    Call<ResponseBody> hitDeleteSkillsApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("sendemaillink")
    Call<ResponseBody> hitResendLinkApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("acceptstatus")
    Call<ResponseBody> hitAcceptMerchantApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("updatereadcount")
    Call<ResponseBody> hitUpdateCountApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("generatechecksum")
    Call<ResponseBody> hitGenerateChecksumApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("paybuddydue")
    Call<ResponseBody> hitPayBuddyDueApi(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("checkversion")
    Call<ResponseBody> hitCheckVersionApi(@FieldMap HashMap<String, String> map);



    //  Put queries

    @FormUrlEncoded
    @PUT("logout")
    Call<ResponseBody> hitLogoutApi(@FieldMap HashMap<String, String> map);



    //  GET queries

    @GET("banner")
    Call<ResponseBody> hitLauncherHomeApi(@QueryMap HashMap<String, String> map);

    @GET("categories")
    Call<ResponseBody> hitCategoriesListApi(@QueryMap HashMap<String, String> map);

    @GET("deals")
    Call<ResponseBody> hitProductsDealsApi(@QueryMap HashMap<String, String> map);

    @GET("deals")
    Call<ResponseBody> hitProductServiceDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("Myprofile")
    Call<ResponseBody> hitProfileDataApi(@QueryMap HashMap<String, String> map);

    @GET("getlocation")
    Call<ResponseBody> hitGetAddressesApi(@QueryMap HashMap<String, String> map);

    @GET("getshopperbuddy")
    Call<ResponseBody> hitBuddyListApi(@QueryMap HashMap<String, String> map);

    @GET("getschedulebuddy")
    Call<ResponseBody> hitBuddyScheduleApi(@QueryMap HashMap<String, String> map);

    @GET("getbuddyorder")
    Call<ResponseBody> hitOrdersListApi(@QueryMap HashMap<String, String> map);

    @GET("getstore")
    Call<ResponseBody> hitStoreListApi(@QueryMap HashMap<String, String> map);

    @GET("getbuddydetail")
    Call<ResponseBody> hitBuddyDetailsListApi(@QueryMap HashMap<String, String> map);

    @GET("getdealbuddy")
    Call<ResponseBody> hitBuddyDealsApi(@QueryMap HashMap<String, String> map);

    @GET("searches")
    Call<ResponseBody> hitSearchListApi(@QueryMap HashMap<String, String> map);

    @GET("categories")
    Call<ResponseBody> hitGetPreferredCategoryListApi(@QueryMap HashMap<String, String> map);

    @GET("subcategory")
    Call<ResponseBody> hitGetSubCategoryListApi(@QueryMap HashMap<String, String> map);

    @GET("getratereview")
    Call<ResponseBody> hitGetRatingAndReviewApi(@QueryMap HashMap<String, String> map);

    @GET("getbuddyearning")
    Call<ResponseBody> hitBuddyEarningApi(@QueryMap HashMap<String, String> map);

    @GET("getbuddylastearning")
    Call<ResponseBody> hitBuddyLastEarningApi(@QueryMap HashMap<String, String> map);

    @GET("getearninglist")
    Call<ResponseBody> hitBuddyEarningListApi(@QueryMap HashMap<String, String> map);

    @GET("getdealbuddy")
    Call<ResponseBody> hitPostByMeApi(@QueryMap HashMap<String,String> map);

    @GET("getbuddyrequest")
    Call<ResponseBody> hitgetRequestApi(@QueryMap HashMap<String,String> map);

    @GET("getuserfans")
    Call<ResponseBody> hitGetMyFansApi(@QueryMap HashMap<String,String> map);

    @GET("getusernotification")
    Call<ResponseBody> hitGetNotificationListApi(@QueryMap HashMap<String, String> map);

    @GET("clearsearch")
    Call<ResponseBody> hitClearListApi(@QueryMap HashMap<String, String> map);

    @GET("getdistance")
    Call<ResponseBody> hitGetRangeApi(@QueryMap HashMap<String, String> map);

    @GET("chekuseremail")
    Call<ResponseBody> hitEmailValidateApi(@QueryMap HashMap<String, String> map);

    @GET("getblockdeals")
    Call<ResponseBody> hitBlockDealsApi(@QueryMap HashMap<String, String> map);

    @GET("getorderdetail")
    Call<ResponseBody> hitOrderDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("getrequestdetail")
    Call<ResponseBody> hitRequestDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("gethuntdetail")
    Call<ResponseBody> hitHuntDetailsApi(@QueryMap HashMap<String, String> map);

    @GET("permanentbuddyrequest")
    Call<ResponseBody> hitMerchantListApi(@QueryMap HashMap<String, String> map);

    @GET("getnotificationcount")
    Call<ResponseBody> hitGetUnreadCountApi(@QueryMap HashMap<String, String> map);


}
