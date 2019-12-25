package com.shopoholicbuddy.models;

import com.dnitinverma.amazons3library.model.ImageBean;
import com.shopoholicbuddy.models.preferredcategorymodel.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddProductServiceModel implements Serializable{

    private boolean isProduct;
    private Result category;
    private ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> timeSlotsBean;
    private ArrayList<String> imagesList;
    private ArrayList<String> slotsDayList;
    private List<TaxArr> taxArrs;
    private com.shopoholicbuddy.models.subcategoryresponse.Result subCategory;


    private String id;
    private String currency;
    private String currencySymbol;
    private String currencyCode;
    private String dealName;
    private String productServiceName;
    private String dealDetailStartTiming;
    private String dealDetailEndTiming;
    private String description;
    private String originalPrice;
    private String validityStartDate;
    private String validityEndDate;
    private String dealPostingDate;
    private String discountPercentage;
    private String sellingPrice;
    private String address;
    private String latitude;
    private String longitude;
    private String paymentMode;
    private String mode;
    private boolean isRecursive;

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    private String quantity;
    private String deliveryCharges;

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getProductServiceName() {
        return productServiceName;
    }

    public void setProductServiceName(String productServiceName) {
        this.productServiceName = productServiceName;
    }

    public String getDealDetailStartTiming() {
        return dealDetailStartTiming;
    }

    public void setDealDetailStartTiming(String dealDetailStartTiming) {
        this.dealDetailStartTiming = dealDetailStartTiming;
    }

    public String getDealDetailEndTiming() {
        return dealDetailEndTiming;
    }

    public void setDealDetailEndTiming(String dealDetailEndTiming) {
        this.dealDetailEndTiming = dealDetailEndTiming;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(String validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public String getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(String validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public String getDealPostingDate() {
        return dealPostingDate;
    }

    public void setDealPostingDate(String dealPostingDate) {
        this.dealPostingDate = dealPostingDate;
    }

    public Result getCategory() {
        return category == null ? new Result() : category;
    }

    public void setCategory(Result category) {
        this.category = category;
    }

    public com.shopoholicbuddy.models.subcategoryresponse.Result getSubCategory() {
        return subCategory == null ? new com.shopoholicbuddy.models.subcategoryresponse.Result() : subCategory;
    }

    public void setSubCategory(com.shopoholicbuddy.models.subcategoryresponse.Result subCategory) {
        this.subCategory = subCategory;
    }

    public ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> getTimeSlotsBean() {
        return timeSlotsBean;
    }

    public void setTimeSlotsBean(ArrayList<com.shopoholicbuddy.models.buddyscheduleresponse.Result> timeSlotsBean) {
        this.timeSlotsBean = timeSlotsBean;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public ArrayList<String> getImagesList() {
        return imagesList == null ? new ArrayList<String>() : imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude == null ? "" : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude == null ? "" : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getMode() {
        return mode == null ? "" : mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public void setRecursive(boolean recursive) {
        isRecursive = recursive;
    }

    public ArrayList<String> getSlotsDayList() {
        return slotsDayList;
    }

    public void setSlotsDayList(ArrayList<String> slotsDayList) {
        this.slotsDayList = slotsDayList;
    }

    public String getCurrencySymbol() {
        return currencySymbol == null ? "" : currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<TaxArr> getTaxArrs() {
        return taxArrs;
    }

    public void setTaxArrs(List<TaxArr> taxArrs) {
        this.taxArrs = taxArrs;
    }
}
