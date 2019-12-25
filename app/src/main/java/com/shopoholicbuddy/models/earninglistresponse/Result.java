
package com.shopoholicbuddy.models.earninglistresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholicbuddy.utils.Constants;

public class Result {

    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("dilevery_charge")
    @Expose
    private String dileveryCharge;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("total_earning")
    @Expose
    private String totalEarning;

    private int viewType = Constants.AppConstant.DATA;

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

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDileveryCharge() {
        return dileveryCharge;
    }

    public void setDileveryCharge(String dileveryCharge) {
        this.dileveryCharge = dileveryCharge;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(String totalEarning) {
        this.totalEarning = totalEarning;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getCurrencyCode() {
        return currencyCode == null ? "" : currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol == null ? "" : currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}
