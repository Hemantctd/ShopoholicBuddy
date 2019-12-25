package com.shopoholicbuddy.livetracking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appinventiv.codescanner.CodeScannerActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.ShopoholicBuddyApplication;
import com.shopoholicbuddy.activities.BaseActivity;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.CALL_PHONE;


public class BuddyMapsActivity extends BaseActivity implements OnMapReadyCallback, LocationUpdatesTask.LocationChanged, DirectionsTask.DirectionListener {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0x002;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_distance)
    CustomTextView tvDistance;
    @BindView(R.id.tv_shopper_name)
    CustomTextView tvShopperName;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.btn_action)
    CustomTextView btnAction;
    @BindView(R.id.iv_my_location)
    ImageView ivMyLocation;
    private final int MERCHANT = 1, SHOPPER = 2;
    private GoogleMap mMap;
    private LocationUpdatesTask mLocationUpdates;
    private Polyline merchantPolyline, shopperPolyline;
    private Location mCurrentLocation;
    private Location mDestinationLocation;
    private Location mOriginLocation;
    private boolean mReqGoogleApiConnection;
    private Marker mBuddyMarker;
    private String orderId = "";
    private String buddyId = "";
    private String merchantId = "";
    private String userType = "";
    private Socket mSocket;
    private String shopperNumber = "";
    private Emitter.Listener trackBuddy = args -> runOnUiThread(() -> {
        Toast.makeText(BuddyMapsActivity.this, args[0].toString(), Toast.LENGTH_LONG).show();
        Log.d("Track Buddy", args[0].toString());
    });
    private boolean onCenterClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        //Get origin and destination latlng from the intent and start processing it.
        getLatLngFromIntent();

        initVariables();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (mLocationUpdates != null) {
            mLocationUpdates.connectGoogleApiClient();
        }

        tvTitle.setText(R.string.track_locatiopn);
        ivBack.setVisibility(View.VISIBLE);

        setUpdateStatusApi();
    }



    @OnClick({R.id.iv_back, R.id.btn_action, R.id.iv_my_location, R.id.btn_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_call:
                checkCallPermission();
                break;
            case R.id.iv_my_location:
                onCenterClick = true;
                adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
                break;
            case R.id.btn_action:
                if (hasPermission(this, Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA)) {
                    startScan();
                }
//                AppUtils.getInstance().showToast(this, getString(R.string.under_development));
                break;
        }
    }
    private void initVariables() {
        mLocationUpdates = new LocationUpdatesTask(this,
                this,
                1000,
                1000);

        if (mSocket == null)
            mSocket = ((ShopoholicBuddyApplication) getApplication()).getSocket();


    }

    private void getLatLngFromIntent() {

        LatLng latLngMerchant = null;
        LatLng latLng = null;
        if (getIntent() != null && getIntent().getExtras() != null) {
            latLng = getIntent().getExtras().getParcelable(Constants.IntentConstant.LOCATION);
            latLngMerchant = getIntent().getExtras().getParcelable(Constants.IntentConstant.LOCATION_MERCHANT);
            orderId = getIntent().getExtras().getString(Constants.IntentConstant.ORDER_ID);
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID);
            merchantId = getIntent().getExtras().getString(Constants.IntentConstant.MERCHANT_ID);
            userType = getIntent().getExtras().getString(Constants.IntentConstant.USER_TYPE);

            tvDistance.setText("");
            tvShopperName.setText(getIntent().getExtras().getString(Constants.IntentConstant.SHOPPER_NAME, ""));
            tvAddress.setText(getIntent().getExtras().getString(Constants.IntentConstant.SHOPPER_ADDRESS, ""));
            shopperNumber = getIntent().getExtras().getString(Constants.IntentConstant.SHOPPER_NUNBER, "");
        }

        if (latLngMerchant != null) {
            mOriginLocation = new Location("origin");
            mOriginLocation.setLatitude(latLngMerchant.latitude);
            mOriginLocation.setLongitude(latLngMerchant.longitude);
//        mOriginLocation.setLatitude(28.570096);
//        mOriginLocation.setLongitude(77.322645);
        }

        mDestinationLocation = new Location("destination");
        mDestinationLocation.setLatitude(latLng == null ? 0 : latLng.latitude);
        mDestinationLocation.setLongitude(latLng == null ? 0 : latLng.longitude);
//        mDestinationLocation.setLatitude(28.634293);
//        mDestinationLocation.setLongitude(77.2998647);

        mCurrentLocation = new Location("current");
        mCurrentLocation.setLatitude(0);
        mCurrentLocation.setLongitude(0);


    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.e("onLocationResult: ", String.valueOf(location.getLatitude()));
        Log.e("onLocationResult: ", String.valueOf(location.getLongitude()));
        if (mCurrentLocation == null || Math.abs(mCurrentLocation.getLatitude() - location.getLatitude()) >= 0.0002 ||
            Math.abs(mCurrentLocation.getLongitude() - location.getLongitude()) >= 0.0002) {
            mCurrentLocation = location;
            requestDirections(MERCHANT, mCurrentLocation, mDestinationLocation);
            if (mOriginLocation != null && userType.equals("1")) {
                requestDirections(SHOPPER, mCurrentLocation, mOriginLocation);
            }
            setBuddyCurrentLocationMarker(location);
            emitLocation(location);
            float distanceInMeters = mCurrentLocation.distanceTo(mDestinationLocation);
            tvDistance.setText(distanceInMeters > 1000 ? String.format(Locale.ENGLISH, "%.1f", ((double) distanceInMeters / 1000)) + " " + getString(R.string.km) :
                    (int) distanceInMeters + " " + getString(R.string.m));
            if (onCenterClick) {
                adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
            }
        }
    }



    /**
     * method to make phone call to buddy
     */
    private void callShopper() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + shopperNumber));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }

    private void emitLocation(Location location) {
        if (mSocket != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", String.valueOf(location.getLatitude()));
                jsonObject.put("user_id", AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
                jsonObject.put("order_id", orderId);
                jsonObject.put("long", String.valueOf(location.getLongitude()));
                mSocket.emit("locationupdate", jsonObject);
                AppUtils.getInstance().printLogMessage("Location", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestDirections(int type, Location originLocation, Location destinationLocation) {
        new DirectionsTask(this, type).execute(
                DirectionsTask.
                        getUrl(this, new LatLng(originLocation.getLatitude(), originLocation.getLongitude()),
                                new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude())));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            Log.d("TAG", "onMapClick");
            onCenterClick = false;
        });

        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d("TAG", "onCameraMoveStarted");
                onCenterClick = false;
            }
        });

        //here map instance is with us so we can add markers of origin and destination.
        setOriginAndDestinationMarker();

    }


    private void setBuddyCurrentLocationMarker(Location buddyCurrentLocation) {

        if (mBuddyMarker == null && buddyCurrentLocation != null && mMap != null) {
            mBuddyMarker = mMap.addMarker
                    (new MarkerOptions()
                            .position(
                                    new LatLng(buddyCurrentLocation.getLatitude(),
                                            buddyCurrentLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_request_dot_icon))
                            .title(getString(R.string.buddy)));

        } else if (buddyCurrentLocation != null && mMap != null)
            mBuddyMarker
                    .setPosition(
                            new LatLng(buddyCurrentLocation.getLatitude(),
                                    buddyCurrentLocation.getLongitude()));

    }

    /**
     * functipon to set origin and destination marker
     */
    private void setOriginAndDestinationMarker() {

        if (mOriginLocation != null && userType.equals("1")) {
            mMap.addMarker
                    (new MarkerOptions()
                            .position(
                                    new LatLng(mOriginLocation.getLatitude(), mOriginLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_2))
                            .title(getString(R.string.merchant)));
        }

        mMap.addMarker
                (new MarkerOptions()
                        .position(
                                new LatLng(mDestinationLocation.getLatitude(), mDestinationLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_2))
                        .title(getString(R.string.shopper)));
        if (onCenterClick) {
            adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
        }
        if (mOriginLocation != null && userType.equals("1")) {
            requestDirections(SHOPPER, mDestinationLocation, mOriginLocation);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationUpdates != null)
            mLocationUpdates.disconnectGoogleApiClient();
    }


    @Override
    public void onDirectionRequestCompleted(PolylineOptions polylineOptions, int type) {
        if (mMap != null && polylineOptions != null) {
            if (type == MERCHANT) {
                if (merchantPolyline != null)
                    merchantPolyline.remove();
                merchantPolyline = mMap.addPolyline(polylineOptions);
                merchantPolyline.setColor(ContextCompat.getColor(this, R.color.colorToolbarStart));
            } else {
                if (shopperPolyline != null)
                    shopperPolyline.remove();
                shopperPolyline = mMap.addPolyline(polylineOptions);
                shopperPolyline.setColor(ContextCompat.getColor(this, R.color.colorToolbarStart));
            }
        }
    }



    /**
     * Adjust camera according to Lat Lng values for adjusting zoom area.
     *
     * @param originLocation
     * @param currentLocation     is the parameter for current location of
     *                            user which he is fetching from location listener
     * @param destinationLocation is the parameter for static location
     *                            to which user is drawing path from current
     */
    public void adjustCamera(Location currentLocation, Location destinationLocation, Location originLocation) {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (currentLocation != null && currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0)
                builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            if (destinationLocation != null && destinationLocation.getLatitude() != 0 && destinationLocation.getLongitude() != 0)
                builder.include(new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude()));

            if (originLocation != null && userType.equals("1") && originLocation.getLatitude() != 0 && originLocation.getLongitude() != 0)
                builder.include(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()));


            int padding = 200;
            LatLngBounds bounds = builder.build();
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * methdo to open scan activity
     */
    private void startScan()
    {
        Intent intent = new Intent(this, CodeScannerActivity.class);
        startActivityForResult(intent, Constants.IntentConstant.REQUEST_SCAN);
    }

    /**
     * check the camera permission
     * @param activity
     * @param permission
     * @param reqId
     * @return
     */
    public boolean hasPermission(Activity activity, String permission, int reqId) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, reqId);
            return false;
        }
    }

    /**
     * method to check the call permission
     */
    public void checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, Constants.IntentConstant.REQUEST_CALL);
            } else {
                callShopper();
            }
        } else {
            callShopper();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.IntentConstant.REQUEST_CALL:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callShopper();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
            case LocationUpdatesTask.FINE_LOCATION_PERMISSIONS:
                mLocationUpdates.setPermissionResult(requestCode, permissions, grantResults);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mLocationUpdates.REQUEST_CHECK_SETTINGS) {
            mLocationUpdates.onActivityResult(requestCode, resultCode);
        }
        if(requestCode== Constants.IntentConstant.REQUEST_SCAN && resultCode==RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }


    /**
     * method to update status
     */

    private void setUpdateStatusApi() {
        CodeScannerActivity.mApiListener = (orderId, apiResponseListener) -> {
            if (BuddyMapsActivity.this.orderId.equals(orderId)) {
                ApiInterface apiInterface = RestApi.createServiceAccessToken(BuddyMapsActivity.this, ApiInterface.class);//empty field is for the access token
                final HashMap<String, String> params = new HashMap<>();
                params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(BuddyMapsActivity.this, AppSharedPreference.PREF_KEY.USER_ID));
                params.put(Constants.NetworkConstant.PARAM_ORDER_ID, orderId);
                params.put(Constants.NetworkConstant.PARAM_STATUS, "5");
                params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "2");
                params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

                Call<ResponseBody> call = apiInterface.hitUpdateOrderStatusApi(AppUtils.getInstance().encryptData(params));
                ApiCall.getInstance().hitService(BuddyMapsActivity.this, call, new NetworkListener() {
                    @Override
                    public void onSuccess(int responseCode, String response, int requestCode) {
                        switch (responseCode) {
                            case Constants.NetworkConstant.SUCCESS_CODE:
                                AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                                apiResponseListener.getResponse(1);
                                break;
                            default:
                                try {
                                    AppUtils.getInstance().showToast(BuddyMapsActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }

                    @Override
                    public void onError(String response, int requestCode) {
                        AppUtils.getInstance().showToast(BuddyMapsActivity.this, response);
                    }

                    @Override
                    public void onFailure() {
                        apiResponseListener.getError();
                    }
                }, Constants.NetworkConstant.REQUEST_UPDATE_ORDER_REQUEST);

            }else {
                AppUtils.getInstance().showToast(BuddyMapsActivity.this, getString(R.string.order_not_match));
            }
        };
    }


}
