package com.shopoholicbuddy.utils;

import android.support.v7.app.AppCompatActivity;

import com.shopoholicbuddy.BuildConfig;
import com.shopoholicbuddy.R;

/**
 * Created  on 25/10/16.
 */
public class Constants {

    public static final String initVector = "";
    public static final String SecretKey = "";
    public static final String ABOUT_US = "about_us";
    public static final String TERM_AND_CONDITION = "terms_and_conditions";
    public static final String PRIVACY_POLICY = "privacy_policy";
    public static final String COPY_RIGHT = "copy_right";
    public static final String LEGAL_INFORMATION = "legal_information";
    public static final String FAQ = "faq";
    public static final String USER_GUIDE = "user_guide";

    public static final String ABOUT_US_URL = BuildConfig.BASE_URL + "Buddyabout";
    public static final String TERM_CONDITION_URL = BuildConfig.BASE_URL + "Buddytermcondition";
    public static final String PRIVACY_POLICY_URL = BuildConfig.BASE_URL + "Buddyprivacy";
    public static final String COPY_RIGHT_URL = BuildConfig.BASE_URL + "Buddycopywrite";
    public static final String FAQ_URL = BuildConfig.BASE_URL + "Buddyfaq";

    public final  static  String VIDEO_URL="video_url";
    public final  static  String VIDEO_URL_THUMB="video_url_thumb";

    public interface AppConstant{
        int TIME_OUT = 2000;
        int PROFILE_REQUEST_CODE = 101;
        int VIEW_PAGER_SIZE = 3;
        int DATA = 1010;
        int PROGRESS = 1011;

        String APP_IMAGE_FOLDER = android.os.Environment.getExternalStorageDirectory() + "/" + new AppCompatActivity().getString(R.string.app_name) + "/";

        String NOTIFICATION_CHANNEL_GROUP = "notification_channel_group";

        String SIGNUP = "signup";
        String LOGIN = "login";
        String FORGOT_PASSWORD = "forgot_password";
        String CHANGE_PHONE_NUMBER = "change_phone_no";
        String QR_CODE = "qr_code";
        String OFFLINE_DEALS = "offline_deals";
        String PROFILE = "profile";
        String DEAL_PREVIEW = "deal_preview";
        String PROFILE_SKILLS = "profile_skills";
        String POSTED_BY_ME = "posted_by_me";
        String SHARED_BY_ME = "shared_by_me";
        String BOOKMARKS = "bookmarks";
        String FILTER = "filter";
        String FILTER_EARNING = "earning_filter";
        String EARNING = "earning";
        String SETTING = "settings";
        String NOTIFICATION = "notification";
        String REQUESTS = "requests";
        String MY_ORDERS = "my_orders";

        String DATE_FORMAT = "dd/MM/yyyy";
        String TIME_FORMAT = "hh:mm a";
        String ORDER_DATE_FORMAT = "dd MMM, yyyy";
        String WEEK_DATE_FORMAT = "EEEE, dd MMMM";
        String SERVICE_DATE_FORMAT = "yyyy-MM-dd";
        String NOTIFICATION_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        String FIREBASE_PASSWORD = BuildConfig.FIREBASE_PASSWORD;

        //language constants
        String ENGLISH = "english";
        String CHINES_TRAD = "chines_traditional";
        String CHINES_SIMPLE = "chines_simple";
        String MALAYALAM = "malayalam";
        String HINDI = "hindi";
        String ARABIC = "arabic";

        //language code constants
        String CODE_ENGLISH = "en";
        String CODE_CHINES_TRADITIONAL = "zh_TW";
        String CODE_CHINES_SIMPLIFIED = "zh_CN";
        String CODE_MALAYALAM = "ml";
        String CODE_HINDI = "hi";
        String CODE_ARABIC = "ar";


        String WEEKLY = "weekly";
        String MONTHLY = "monthly";
        String YEARLY = "yearly";
    }

    public interface NetworkConstant {

//        String BASE_URL = "https://shopoholic.appinventive.com/api/";   //Development base url
//        String BASE_URL = "https://shopoholicstage.appinventive.com/api/";     //Staging base url
        String BASE_URL = BuildConfig.BASE_URL + BuildConfig.VERSION;

//        NOTE :- PLEASE CHANGE GOOGLE JSON TOO ... AND SOCKET URL
//        SHA1: 67:F4:11:9B:3B:BC:F9:B7:BB:A4:F3:1E:B5:92:1D:AD:46:99:8B:19

        //        String SOCKET_BASE_URL = "http://appinventive.com:7109";   //Development base url
//        String SOCKET_BASE_URL = "http://appinventive.com:7175";     //Staging base url
        String SOCKET_BASE_URL = BuildConfig.SOCKET_BASE_URL + BuildConfig.PORT;

        String USER_NAME = BuildConfig.USER_NAME;
        String PASSWORD = BuildConfig.PASSWORD;


        int SUCCESS_CODE = 200;
        int NO_DATA = 202;
        int USER_NOT_VERIFIED = 103;
        int NUMBER_NOT_REGISTERED = 418;
        int USER_NOT_REGISTER = 307;
        int EMAIL_NOT_VERIFIED = 313;
        int VERIFY_EMAIL = 314;
        int PAYMENT_DUE = 315;
        int PAYMENT_REQUIRE = 316;

        int REQUEST_SIGN_UP = 11;
        int REQUEST_LOGIN = 12;
        int REQUEST_RESET_PASSWORD = 13;
        int REQUEST_CHANGE_PASSWORD = 14;
        int REQUEST_FORGOT_PASSWORD = 15;
        int REQUEST_RESEND_OTP = 16;
        int REQUEST_VERIFY_OTP = 17;
        int REQUEST_OTHER_INFORMATION = 18;
        int REQUEST_SOCIAL_LOGIN = 19;
        int REQUEST_LAUNCHER_HOME = 20;
        int REQUEST_PRODUCTS = 21;
        int REQUEST_CATEGORIES = 22;
        int REQUEST_DEAL_DETAILS = 23;
        int REQUEST_GET_ADDRESS = 24;
        int REQUEST_SAVE_ADDRESS = 25;
        int REQUEST_BUDDY=26;
        int REQUEST_STORES = 27;
        int REQUEST_SEARCH = 28;
        int REQUEST_BUDDY_DETAILS=29;
        int BUDDY_REQUEST =30;
        int BUDDY_DEAL = 31;
        int REQUEST_SAVE_CATEGORY = 32;
        int REQUEST_PROFILE = 33;
        int REQUEST_SUB_CATEGORY = 34;
        int REQUEST_RATING_REVIEW= 35;
        int REQUEST_SHARE_DEAL= 36;
        int REQUEST_BLOCK_DEAL= 37;
        int REQUEST_REPORT_DEAL= 38;
        int REQUEST_ADD_DEAL= 39;
        int REQUEST_EARNING = 40;
        int REQUEST_BUDDY_SCHEDULE= 41;
        int REQUEST_UPDATE_BUDDDY_REQUEST = 42;
        int REQUEST_UPDATE_ORDER_REQUEST = 43;
        int REQUEST_DELETE_DEAL = 44;
        int REQUEST_MY_FANS = 45;
        int NOTIFICATION_LIST = 46;
        int REQUEST_FEEDBACK = 47;
        int REQUEST_POSTED = 48;
        int REQUEST_CLEAR = 49;
        int REQUEST_ORDER = 50;
        int REQUEST_BUDDY_REQUESTS = 51;
        int REQUEST_MERCHANT = 52;
        int REQUEST_ACCEPT_MERCHANT = 53;
        int REQUEST_CHECKSUM = 54;
        int REQUEST_PAYMENT = 55;
        int REQUEST_LAST_EARNING = 56;

        String MESSAGE = "message";
        String ALERT = "alert";
        String RESULT = "result";
        String PHONE_NUMBER = "mobile_no";
        String COUNTRY_CODE = "country_code";
        String USER_ID = "user_id";
        String FOLLOWER_ID = "follower_id";

        String RESPONSE_MESSAGE = "msg";
        String CODE = "code";
        String GET_ALL_DEALS = "get_all_deals";
        String GET_DEAL_DETAILS = "get_deal_detail";

        String ANDROID_PLATFORM = "1";
        String REQUEST_EMAIL = "1";
        String REQUEST_PHONE = "2";
        String FACEBOOK_LOGIN = "1";
        String GOOGLE_LOGIN = "2";
        String BUDDY = "2";
        String SHOPPER = "3";
        String CATEGORY = "1";
        String PERCENTAGE = "2";
        String PRODUCT = "1";
        String SERVICE = "2";
        String RANGE = "20";
        String PARAM_CATEGORY_ID = "cat_id";
        String BUDDY_BOOKMARKED = "1";
        String BUDDY_SHARED = "1";



        String PARAM_AUTHORIZATION = "Authorization";

        String PARAM_USER_ID = "user_id";
        String PARAM_FIRST_NAME = "first_name";
        String PARAM_LAST_NAME = "last_name";
        String PARAM_EMAIL = "email";
        String PARAM_COUNTRY_ID = "country_id";
        String PARAM_MOBILE_NO = "mobile_no";
        String PARAM_PASSWORD = "password";
        String PARAM_OLD_PASSWORD = "oldpassword";
        String PARAM_DEVICE_ID = "device_id";
        String PARAM_CONFIRM_PASSWORD = "confirm_password";
        String PARAM_DEVICE_TOKEN = "device_token";
        String PARAM_PLATFORM = "platform";
        String PARAM_IMAGE = "image";
        String PARAM_OTP = "otpNo";
        String PARAM_FORGOT_TYPE = "forgot_type";
        String PARAM_ACCESS_TOKEN = "accesstoken";
        String PARAM_GENDER = "gender";
        String PARAM_DOB = "date_of_birth";
        String PARAM_ANNIVERSARY = "anniversary";
        String PARAM_SOCIAL_ID = "social_id";
        String PARAM_ACCOUNT_TYPE = "account_type";
        String PARAM_USER_TYPE = "user_type";
        String PARAM_METHOD = "method";
        String PARAM_LATITUDE = "latitude";
        String PARAM_LONGITUDE = "longitude";
        String PARAM_RANGE = "range";
        String PARAM_ISO_CODE = "iso_code";
        String PARAM_DISCOUNT = "discount";
        String PARAM_DEAL_CATEGORY_ID = "category_id";
        String PARAM_DEAL_ID = "deal_id";
        String PARAM_DELIVERY_CHARGE = "delivery_charge";
        String PARAM_TYPE = "type";
        String SAVE_FAVOURITES_DEALS = "save_favourite_deal";
        String PARAM_IS_FAVOURITE = "is_favourite";
        String PARAM_ADDRESS = "address";
        String PARAM_ADDRESS2 = "address2";
        String PARAM_NAME = "name";
        String PARAM_QUANTITY = "quantity";
        String PARAM_ACTUAL_AMOUNT = "actual_amount";
        String PARAM_AMOUNT = "amount";
        String PARAM_DELIVERED_BY = "dilevered_by";
        String PARAM_DELIVERY_ADDRESS = "dilevery_address";
        String PARAM_SEARCH = "searchval";
        String PARAM_STORE = "store";
        String PARAM_POSTED_BY = "posted_by";
        String PARAM_EXPIRE_DATE = "expiry_date";
        String PARAM_PRODUCT_TYPE = "product_type";
        String PARAM_IS_BOOKMARKED = "is_bookmarked";
        String PARAM_BUDDY_ID = "buddy_id";
        String PARAM_FOLLOW_ID = "following_id";
        String PARAM_STATUS = "status";
        String PARAM_IS_SHARED = "is_shared";
        String PARAM_COUNT = "count";
        String PARAM_SUB_CATEGORY_ID = "subcategory_id";
        String PARAM_DEAL_NAME = "deal_name";
        String PARAM_DEAL_START_DATE = "deal_start_date";
        String PARAM_DEAL_END_DATE = "deal_end_date";
        String PARAM_ORIGINAL_PRICE = "orignal_price";
        String PARAM_PUBLISH_DATE = "publish_date";
        String PARAM_CURRENCY = "currency";
        String PARAM_CURRENCY_CODE = "currency_code";
        String PARAM_CURRENCY_SYMBOL = "currency_symbol";
        String PARAM_SELLING_PRICE = "selling_price";
        String PARAM_DISCOUNT_VALIDATION_START = "discount_validate_start";
        String PARAM_DISCOUNT_VALIDATION_END = "discount_validate_end";
        String PARAM_IS_SAVE_DEAL = "is_save_deal";
        String PARAM_DESCRIPTION = "description";
        String PARAM_DELIVERY_CHARGES = "dilevery_charge";
        String PARAM_IMAGE_ARRAY = "image_arr";
        String PARAM_TIME_SLOT_ARRAY = "time_slot_arr";
        String PARAM_TAX_ARR = "tax_arr";
        String PARAM_IMAGE_PATH = "image_path";
        String PARAM_MEDIA_TYPE = "media_type";
        String PARAM_DEFAULT_IMAGE = "default_image";
        String PARAM_SLOT_START_TIME = "slot_start_time";
        String PARAM_SLOT_END_TIME = "slot_end_time";
        String PARAM_SLOT_START_DATE = "slot_start_date";
        String PARAM_SLOT_END_DATE = "slot_end_date";
        String PARAM_SERVICE_NAME = "service_name";
        String PARAM_ORDER_DATE = "order_date";
        String PARAM_START_DATE = "start_date";
        String PARAM_START_WEEK = "start_week";
        String PARAM_START_MONTH = "start_month";
        String PARAM_START_YEAR = "start_year";
        String PARAM_END_DATE = "end_date";
        String PARAM_MONTH = "month";
        String PARAM_YEAR = "year";
        String PARAM_ORDER_MONTH = "order_month";
        String PARAM_ORDER_YEAR = "order_year";
        String PARAM_TIME_PEROID = "time_period";
        String PARAM_SCHEDULE_DATE = "schedule_date";
        String PARAM_ORDER_ID = "order_id";
        String PARAM_DELIVERY_DATE = "dilevery_date";
        String PARAM_BUDDY_ADDRESS = "buddy_address";
        String PARAM_BUDDY_LATITUDE = "buddy_latitude";
        String PARAM_BUDDY_LONGITUDE = "buddy_longitude";
        String PARAM_REPORT = "report";
        String PARAM_REVIEW = "review";
        String PARAM_FEEDBACK = "feedback";
        String PARAM_SERVICE_ID = "service_id";
        String PARAM_USER_LANGUAGE = "user_language";
        String PARAM_NOTIFICATION_STATUS = "notification_status";
        String PARAM_LOCATION_STATUS = "location_status";
        String PARAM_TEMP_ID = "temp_id";
        String PARAM_IS_AVAILABLE = "is_available";
        String PARAM_PAYMENT_METHOD = "payment_method";
        String PARAM_TIME_ZONE = "time_zone";
        String PARAM_HUNT_ID = "hunt_id";
        String PARAM_REQ_ID = "req_id";
        String PARAM_REQUEST_ID = "request_id";
        String PARAM_SERVICE_TYPE = "service_type";
        String PARAM_ALL_DAYS = "all_days";
        String PARAM_HOURS = "hour";
        String PARAM_MIN = "min";
        String PARAM_PRICE = "price";
        String PARAM_SLOT_ID = "slot_id";
        String PARAM_IS_RECURSIVE = "is_recursive";
        String PARAM_SLOT_SELECTED_DATE = "slot_selected_date";
        String PARAM_BIDDING_PRICE = "bid_price";
        String PARAM_SLOT_ID_ARR = "slot_id_arr";
        String PARAM_DATE = "date";
        String PARAM_HOME_DELIVERY = "home_delivery";
        String PARAM_USER_CURRENCY_CODE = "user_currency_code";
        String PARAM_BUDDY_DELIVERY_CHARGE = "buddy_delivery_charge";
        String PARAM_PAYMENT_ID = "payment_id";
        String PARAM_PAYMENT_TYPE = "payment_type";
        String PARAM_TRANSACTION_ID = "transaction_id";
        String PARAM_TAX_NAME = "tax_name";
        String PARAM_TAX_PERCENTAGE = "tax_percentage";
        String PARAM_TAX_VALUE = "tax_value";

        String PARAM_VERSION_NAME = "version_name";
        String PARAM_APP_TYPE= "app_type";
    }



    public interface IntentConstant {

        int MY_BUDDY_FRAGMENT = 101;

        int REQUEST_CAMERA = 1001;
        int REQUEST_GALLERY = 1122;
        int REQUEST_LOCATION = 1003;
        int REQUEST_CROPPER_CODE = 1004;
        int REQUEST_EDIT_PROFILE = 1006;
        int REQUEST_CHANGE_PHONE_NUMBER = 1007;
        int REQUEST_ADDRESS = 1008;
        int REQUEST_FILTER = 1009;
        int REQUEST_PLACE_PICKER = 1010;
        int REQUEST_SEARCH = 1011;
        int REQUEST_MULTIPLE_IMAGE_INTENT = 1012;
        int REQUEST_ORDER_CODE = 1013;
        int REQUEST_SCAN = 1014;
        int REQUEST_MAP = 1015;
        int REQUEST_EDIT_DEAL = 1016;
        int REQUEST_PRODUCT_DEAL = 1017;
        int REQUEST_STORES = 1018;
        int REQUEST_CATEGORIES = 1019;
        int REQUEST_SUB_CATEGORIES = 1020;
        int REQUEST_TIME_SLOTS = 1021;
        int REQUEST_SLOTS = 1022;
        int REQUEST_CALL = 1023;
        int REQUEST_PUSH_ORDER_CODE = 1024;

        String USER_ID = "user_id";
        String PHONE_NUMBER = "phone_number";
        String COUNTRY_CODE = "country_code";
        String FROM_CLASS = "from_class";
        String NOTIFICATION = "notification";
        String USER_SOCIAL_MODEL = "user_social_model";
        String DEAL_ID = "deal_id";
        String DEAL_IMAGE = "deal_image";
        String LOCATION = "location";
        String MAP_LOCATION = "map_location";
        String LOCATION_MERCHANT = "location_merchant";
        String FROM_TUTORIAL = "from_tutorial";
        String PRODUCT_DETAILS = "product_details";
        String DELIVERY_ADDRESS = "delivery_address";
        String FRAGMENT_TYPE = "fragment_type";
        String ORDER_DETAILS = "order_details";
        String FILTER_DATA = "filter_data";
        String BUDDY = "buddy";
        String BUDDY_ID = "buddy_id";
        String MERCHANT_ID = "merchant_id";
        String BUDDY_DEAL ="is_deal";
        String SEARCH = "search";
        String CATEGORIES = "categories";
        String IS_PRODUCT = "is_product";
        String ORDER_ID = "order_id";
        String EARNING_FILTER_MODEL = "earning_filter_model";
        String PRODUCT_ID = "product_id";
        String IS_SHOPPER = "is_shopper";
        String SHOPPER_NAME = "shopper_name";
        String SHOPPER_ADDRESS = "shopper_address";
        String SHOPPER_NUNBER = "shopper_number";
        String USER_TYPE = "user_type";
        String STORE = "store";
        String CATEGORY = "category";
        String IS_BOOKMARK = "is_bookmark";
        String IS_CATEGORY = "is_category";
        String TYPE = "type";
        String MAP_GRID = "map_grid";
        String SLOTS = "slots";
        String START_DATE = "start_date";
        String END_DATE = "end_date";
        String TIME_SLOTS = "time_slots";
        String DAYS_LIST = "days_list";
        String CURRENCY = "currency";
        String CURRENCY_CODE = "currency_code";
        String PERCENTAGE = "percentage";
        String HUNT_DETAILS = "hunt_details";
        String HUNT_ID = "hunt_id";
        String SELECTED_DAYS_LIST = "selected_days_list";
        String TERMS = "terms";
        String COUNT = "count";
        String BADGE = "badge";
    }

    public interface UrlConstant {
        String ABOUT_US_URL = "http://www.google.com/";
        String TERM_CONDITION_URL = "http://www.google.com/";
        String PRIVACY_POLICY_URL = "http://www.google.com/";

/*
        String AMAZON_POOLID = "us-east-1:b1f250f2-66a7-4d07-96e9-01817149a439";
        String BUCKET = "appinventiv-development";
        String AMAZON_SERVER_URL = "https://appinventiv-development.s3.amazonaws.com/";
        String END_POINT = "s3.amazonaws.com";*/

        String AMAZON_POOLID = BuildConfig.AMAZON_POOLID;
        String BUCKET = BuildConfig.BUCKET;
        String AMAZON_SERVER_URL = BuildConfig.AMAZON_SERVER_URL;
        String END_POINT = BuildConfig.END_POINT;
        String REGION = BuildConfig.REGION;

    }

    public interface ChartConstants {
        int WEEKLY = 101;
        int MONTHLY = 102;
        int YEARLY = 103;
    }

}
