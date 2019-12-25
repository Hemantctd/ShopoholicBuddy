
package com.shopoholicbuddy.models.requestresponse;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;

public class Result implements Serializable {

    @SerializedName("req_id")
    @Expose
    private String reqId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("deal_name")
    @Expose
    private String dealName;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("hunt_id")
    @Expose
    private String huntId;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("slot_id")
    @Expose
    private String slotId;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("request_status")
    @Expose
    private String requestStatus;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("dilevery_date")
    @Expose
    private String dileveryDate;
    @SerializedName("shopper_name")
    @Expose
    private String shopperName;
    @SerializedName("dilevery_address")
    @Expose
    private String dileveryAddress;
    @SerializedName("pickup_address")
    @Expose
    private String pickupAddress;
    @SerializedName("pickup_address2")
    @Expose
    private String pickupAddress2;
    @SerializedName("pickup_latitude")
    @Expose
    private String pickupLatitude;
    @SerializedName("pickup_longitude")
    @Expose
    private String pickupLongitude;
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
    @SerializedName("shopper_latitude")
    @Expose
    private String shopperLatitude;
    @SerializedName("shopper_longitude")
    @Expose
    private String shopperLongitude;
    @SerializedName("home_delivery")
    @Expose
    private String homeDelivery;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;
    @SerializedName("price_start")
    @Expose
    private String priceStart;
    @SerializedName("price_end")
    @Expose
    private String priceEnd;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("dilevery_charge")
    @Expose
    private String deliveryCharge;
    @SerializedName("is_read")
    @Expose
    private String isRead;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_hunt_close")
    @Expose
    private String isHuntClose;
    @SerializedName("is_shared")
    @Expose
    private String isShared;
    @SerializedName("shopper_delivery_charge")
    @Expose
    private String shopperDeliveryCharge;
    @SerializedName("shopper_delivery_date")
    @Expose
    private String shopperDeliveryDate;

    @SerializedName("slot_arr")
    @Expose
    private List<ServiceSlot> slotArr = null;
    @SerializedName("selected_date_arr")
    @Expose
    private List<String> selectedDateArr = null;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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

    public String getDileveryDate() {
        return dileveryDate;
    }

    public void setDileveryDate(String dileveryDate) {
        this.dileveryDate = dileveryDate;
    }

    public String getShopperName() {
        return shopperName;
    }

    public void setShopperName(String shopperName) {
        this.shopperName = shopperName;
    }

    public String getDileveryAddress() {
        return dileveryAddress;
    }

    public void setDileveryAddress(String dileveryAddress) {
        this.dileveryAddress = dileveryAddress;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getPickupAddress2() {
        return pickupAddress2;
    }

    public void setPickupAddress2(String pickupAddress2) {
        this.pickupAddress2 = pickupAddress2;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
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

    public String getShopperLatitude() {
        return shopperLatitude;
    }

    public void setShopperLatitude(String shopperLatitude) {
        this.shopperLatitude = shopperLatitude;
    }

    public String getShopperLongitude() {
        return shopperLongitude;
    }

    public void setShopperLongitude(String shopperLongitude) {
        this.shopperLongitude = shopperLongitude;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public List<ServiceSlot> getSlotArr() {
        return slotArr;
    }

    public void setSlotArr(List<ServiceSlot> slotArr) {
        this.slotArr = slotArr;
    }

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getSelectedDateArr() {
        return selectedDateArr;
    }

    public void setSelectedDateArr(List<String> selectedDateArr) {
        this.selectedDateArr = selectedDateArr;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealName() {
        return dealName == null ? "" : dealName;
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

    public String getCurrencyCode() {
        return currencyCode == null || currencyCode.equals("") ? "AED" : currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol == null ?"" : currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getActualAmount() {
        return actualAmount == null ? "0" : actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPriceStart() {
        return priceStart == null ? "0" : priceStart;
    }

    public void setPriceStart(String priceStart) {
        this.priceStart = priceStart;
    }

    public String getPriceEnd() {
        return priceEnd == null ? "0" : priceEnd;
    }

    public void setPriceEnd(String priceEnd) {
        this.priceEnd = priceEnd;
    }

    public String getDeliveryCharge() {
        return deliveryCharge == null ? "0" : deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getShopperDeliveryCharge() {
        return shopperDeliveryCharge;
    }

    public void setShopperDeliveryCharge(String shopperDeliveryCharge) {
        this.shopperDeliveryCharge = shopperDeliveryCharge;
    }

    public String getShopperDeliveryDate() {
        return shopperDeliveryDate;
    }

    public void setShopperDeliveryDate(String shopperDeliveryDate) {
        this.shopperDeliveryDate = shopperDeliveryDate;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsHuntClose() {
        return isHuntClose == null ? "" : isHuntClose;
    }

    public void setIsHuntClose(String isHuntClose) {
        this.isHuntClose = isHuntClose;
    }

    public String getIsShared() {
        return isShared == null ? "" : isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }
}
