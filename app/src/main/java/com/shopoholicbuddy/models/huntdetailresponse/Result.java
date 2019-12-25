
package com.shopoholicbuddy.models.huntdetailresponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholicbuddy.models.productservicedetailsresponse.SlotSelectedDate;

public class Result implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("subcat_id")
    @Expose
    private String subcatId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("hunt_title")
    @Expose
    private String huntTitle;
    @SerializedName("price_start")
    @Expose
    private String priceStart;
    @SerializedName("price_end")
    @Expose
    private String priceEnd;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("buddy_assign_date")
    @Expose
    private String buddyAssignDate;
    @SerializedName("expected_delivery_date")
    @Expose
    private String expectedDeliveryDate;
    @SerializedName("target_area")
    @Expose
    private String targetArea;
    @SerializedName("target_lat")
    @Expose
    private String targetLat;
    @SerializedName("target_long")
    @Expose
    private String targetLong;
    @SerializedName("is_recursive")
    @Expose
    private String isRecursive;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("choose_buddy")
    @Expose
    private String chooseBuddy;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("subcategory_name")
    @Expose
    private String subcategoryName;
    @SerializedName("is_hunt_close")
    @Expose
    private String isHuntClose;
    @SerializedName("image_arr")
    @Expose
    private ArrayList<ImageArr> imageArr = null;
    @SerializedName("selected_date_arr")
    @Expose
    private ArrayList<SlotSelectedDate> slotSelectedDate = null;
    @SerializedName("slot_arr")
    @Expose
    private ArrayList<ServiceSlot> serviceSlot = null;



    @SerializedName("selected_slots")
    @Expose
    private String selectedSlots;
    @SerializedName("selected_dates")
    @Expose
    private ArrayList<String> selectedDates;


    @SerializedName("request_status")
    @Expose
    private String biddingStatus;
    @SerializedName("buddy_delivery_date")
    @Expose
    private String biddingDate;
    @SerializedName("bid_price")
    @Expose
    private String biddingPrice;
    @SerializedName("buddy_image_arr")
    @Expose
    private ArrayList<ImageArr> biddingImageArr = null;

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

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(String subcatId) {
        this.subcatId = subcatId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceStart() {
        return priceStart;
    }

    public void setPriceStart(String priceStart) {
        this.priceStart = priceStart;
    }

    public String getPriceEnd() {
        return priceEnd;
    }

    public void setPriceEnd(String priceEnd) {
        this.priceEnd = priceEnd;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getBuddyAssignDate() {
        return buddyAssignDate;
    }

    public void setBuddyAssignDate(String buddyAssignDate) {
        this.buddyAssignDate = buddyAssignDate;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public String getTargetLat() {
        return targetLat;
    }

    public void setTargetLat(String targetLat) {
        this.targetLat = targetLat;
    }

    public String getTargetLong() {
        return targetLong;
    }

    public void setTargetLong(String targetLong) {
        this.targetLong = targetLong;
    }

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getChooseBuddy() {
        return chooseBuddy;
    }

    public void setChooseBuddy(String chooseBuddy) {
        this.chooseBuddy = chooseBuddy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public ArrayList<ImageArr> getImageArr() {
        return imageArr;
    }

    public void setImageArr(ArrayList<ImageArr> imageArr) {
        this.imageArr = imageArr;
    }

    public ArrayList<SlotSelectedDate> getSlotSelectedDate() {
        return slotSelectedDate;
    }

    public void setSlotSelectedDate(ArrayList<SlotSelectedDate> slotSelectedDate) {
        this.slotSelectedDate = slotSelectedDate;
    }

    public ArrayList<ServiceSlot> getServiceSlot() {
        return serviceSlot;
    }

    public void setServiceSlot(ArrayList<ServiceSlot> serviceSlot) {
        this.serviceSlot = serviceSlot;
    }

    public String getSelectedSlots() {
        return selectedSlots;
    }

    public void setSelectedSlots(String selectedSlots) {
        this.selectedSlots = selectedSlots;
    }

    public ArrayList<String> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(ArrayList<String> selectedDates) {
        this.selectedDates = selectedDates;
    }

    public String getHuntTitle() {
        return huntTitle;
    }

    public void setHuntTitle(String huntTitle) {
        this.huntTitle = huntTitle;
    }

    public String getBiddingStatus() {
        return biddingStatus == null ? "" : biddingStatus;
    }

    public void setBiddingStatus(String biddingStatus) {
        this.biddingStatus = biddingStatus;
    }

    public String getBiddingDate() {
        return biddingDate == null ? "" : biddingDate;
    }

    public void setBiddingDate(String biddingDate) {
        this.biddingDate = biddingDate;
    }

    public String getBiddingPrice() {
        return biddingPrice == null ? "" : biddingPrice;
    }

    public void setBiddingPrice(String biddingPrice) {
        this.biddingPrice = biddingPrice;
    }

    public ArrayList<ImageArr> getBiddingImageArr() {
        return biddingImageArr == null ? new ArrayList<>() : biddingImageArr;
    }

    public void setBiddingImageArr(ArrayList<ImageArr> biddingImageArr) {
        this.biddingImageArr = biddingImageArr;
    }

    public String getIsHuntClose() {
        return isHuntClose;
    }

    public void setIsHuntClose(String isHuntClose) {
        this.isHuntClose = isHuntClose;
    }
}
