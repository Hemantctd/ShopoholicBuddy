
package com.shopoholicbuddy.models.buddyscheduleresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("slot_start_date")
    @Expose
    private String slotStartDate;
    @SerializedName("slot_end_date")
    @Expose
    private String slotEndDate;
    @SerializedName("slot_start_time")
    @Expose
    private String slotStartTime;
    @SerializedName("slot_end_time")
    @Expose
    private String slotEndTime;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("all_day")
    @Expose
    private String isAllDay;
    private boolean allDay;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;
    @SerializedName("is_recursive")
    @Expose
    private String isRecursive;
    @SerializedName("order_count")
    @Expose
    private String orderCount;
    @SerializedName("currency")
    @Expose
    private String currency;


    public String getId() {
        return id == null ? "" : id;
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

    public String getSlotStartDate() {
        return slotStartDate;
    }

    public void setSlotStartDate(String slotStartDate) {
        this.slotStartDate = slotStartDate;
    }

    public String getSlotEndDate() {
        return slotEndDate;
    }

    public void setSlotEndDate(String slotEndDate) {
        this.slotEndDate = slotEndDate;
    }

    public String getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(String slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public String getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(String slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public String getPrice() {
        return price == null ? "" : price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(String isAllDay) {
        this.isAllDay = isAllDay;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
