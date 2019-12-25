package com.shopoholicbuddy.firebasechat.models;

import com.shopoholicbuddy.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholicbuddy.models.productservicedetailsresponse.SlotSelectedDate;

import java.io.Serializable;
import java.util.List;

public class HuntDeal implements Serializable {
    private String id = "";
    private String categoryName = "";
    private String subCategoryName = "";
    private String userId = "";
    private String productType = "";
//    private String firstName = "";
//    private String lastName = "";
    private String huntImage = "";
    private String userType = "";
    private String isRecursive = "";
    private String currencyCode = "";
    private String currencySymbol = "";
    private String dealStartTime = "";
    private String dealEndTime = "";
    private String price = "";
    private List<ServiceSlot> serviceSlot = null;
    private List<SlotSelectedDate> slotSelectedDate = null;

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

//    public String getFirstName() {
//        return firstName;
//    }

//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }

//    public String getLastName() {
//        return lastName;
//    }

//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }

    public String getHuntImage() {
        return huntImage;
    }

    public void setHuntImage(String huntImage) {
        this.huntImage = huntImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
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

    public String getDealStartTime() {
        return dealStartTime;
    }

    public void setDealStartTime(String dealStartTime) {
        this.dealStartTime = dealStartTime;
    }

    public String getDealEndTime() {
        return dealEndTime;
    }

    public void setDealEndTime(String dealEndTime) {
        this.dealEndTime = dealEndTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<ServiceSlot> getServiceSlot() {
        return serviceSlot;
    }

    public void setServiceSlot(List<ServiceSlot> serviceSlot) {
        this.serviceSlot = serviceSlot;
    }

    public List<SlotSelectedDate> getSlotSelectedDate() {
        return slotSelectedDate;
    }

    public void setSlotSelectedDate(List<SlotSelectedDate> slotSelectedDate) {
        this.slotSelectedDate = slotSelectedDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
