
package com.shopoholicbuddy.models.blockeddeals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("deal_name")
    @Expose
    private String dealName;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("merchant_address")
    @Expose
    private String merchantAddress;
    @SerializedName("merchant_address2")
    @Expose
    private String merchantAddress2;
    @SerializedName("merchant_latitude")
    @Expose
    private String merchantLatitude;
    @SerializedName("merchant_longitude")
    @Expose
    private String merchantLongitude;
    @SerializedName("merchant_number")
    @Expose
    private String merchantNumber;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("deal_image")
    @Expose
    private String dealImage;
    @SerializedName("user_type")
    @Expose
    private String userType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getMerchantAddress2() {
        return merchantAddress2;
    }

    public void setMerchantAddress2(String merchantAddress2) {
        this.merchantAddress2 = merchantAddress2;
    }

    public String getMerchantLatitude() {
        return merchantLatitude;
    }

    public void setMerchantLatitude(String merchantLatitude) {
        this.merchantLatitude = merchantLatitude;
    }

    public String getMerchantLongitude() {
        return merchantLongitude;
    }

    public void setMerchantLongitude(String merchantLongitude) {
        this.merchantLongitude = merchantLongitude;
    }

    public String getMerchantNumber() {
        return merchantNumber;
    }

    public void setMerchantNumber(String merchantNumber) {
        this.merchantNumber = merchantNumber;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
