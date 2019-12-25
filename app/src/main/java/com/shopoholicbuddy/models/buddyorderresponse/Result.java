
package com.shopoholicbuddy.models.buddyorderresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("hunt_id")
    @Expose
    private String huntId;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("merchant_address")
    @Expose
    private String merchantAddress;
    @SerializedName("merchant_latitude")
    @Expose
    private String merchantLatitude;
    @SerializedName("merchant_longitude")
    @Expose
    private String merchantLongitude;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("dilevered_by")
    @Expose
    private String dileveredBy;
    @SerializedName("shipping_id")
    @Expose
    private String shippingId;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("assign_date")
    @Expose
    private String assignDate;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("order_confirm_date")
    @Expose
    private String orderConfirmDate;
    @SerializedName("shipped_date")
    @Expose
    private String shippedDate;
    @SerializedName("out_for_delivery_date")
    @Expose
    private String outForDeliveryDate;
    @SerializedName("dilevery_date")
    @Expose
    private String dileveryDate;
    @SerializedName("dilevered_date")
    @Expose
    private String dileveredDate;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("is_refunded")
    @Expose
    private String isRefunded;
    @SerializedName("dilevery_address")
    @Expose
    private String dileveryAddress;
    @SerializedName("shopper_name")
    @Expose
    private String shopperName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
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
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("deal_image")
    @Expose
    private String dealImage;

    @SerializedName("slot_arr")
    @Expose
    private List<ServiceSlot> slotArr = null;

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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getDileveredBy() {
        return dileveredBy;
    }

    public void setDileveredBy(String dileveredBy) {
        this.dileveredBy = dileveredBy;
    }

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderConfirmDate() {
        return orderConfirmDate;
    }

    public void setOrderConfirmDate(String orderConfirmDate) {
        this.orderConfirmDate = orderConfirmDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getOutForDeliveryDate() {
        return outForDeliveryDate;
    }

    public void setOutForDeliveryDate(String outForDeliveryDate) {
        this.outForDeliveryDate = outForDeliveryDate;
    }

    public String getDileveryDate() {
        return dileveryDate;
    }

    public void setDileveryDate(String dileveryDate) {
        this.dileveryDate = dileveryDate;
    }

    public String getDileveredDate() {
        return dileveredDate;
    }

    public void setDileveredDate(String dileveredDate) {
        this.dileveredDate = dileveredDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getIsRefunded() {
        return isRefunded;
    }

    public void setIsRefunded(String isRefunded) {
        this.isRefunded = isRefunded;
    }

    public String getDileveryAddress() {
        return dileveryAddress;
    }

    public void setDileveryAddress(String dileveryAddress) {
        this.dileveryAddress = dileveryAddress;
    }

    public String getShopperName() {
        return shopperName;
    }

    public void setShopperName(String shopperName) {
        this.shopperName = shopperName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public List<ServiceSlot> getSlotArr() {
        return slotArr;
    }

    public void setSlotArr(List<ServiceSlot> slotArr) {
        this.slotArr = slotArr;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }
}
