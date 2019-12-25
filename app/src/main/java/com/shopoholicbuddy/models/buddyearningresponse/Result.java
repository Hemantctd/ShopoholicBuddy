
package com.shopoholicbuddy.models.buddyearningresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("total_earn")
    @Expose
    private double totalEarn;
    @SerializedName("lastweek_earn")
    @Expose
    private double lastweekEarn;
    @SerializedName("lastmonth_earn")
    @Expose
    private double lastmonthEarn;
    @SerializedName("lastyear_earn")
    @Expose
    private double lastyearEarn;
    @SerializedName("pending_amount")
    @Expose
    private double pendingAmount;
    @SerializedName("category_earn")
    @Expose
    private List<CategoryEarn> categoryEarn = null;
    @SerializedName("date_earn")
    @Expose
    private List<DateEarn> dateEarn = null;

    public double getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(double totalEarn) {
        this.totalEarn = totalEarn;
    }

    public double getLastweekEarn() {
        return lastweekEarn;
    }

    public void setLastweekEarn(double lastweekEarn) {
        this.lastweekEarn = lastweekEarn;
    }

    public double getLastmonthEarn() {
        return lastmonthEarn;
    }

    public void setLastmonthEarn(double lastmonthEarn) {
        this.lastmonthEarn = lastmonthEarn;
    }

    public double getLastyearEarn() {
        return lastyearEarn;
    }

    public void setLastyearEarn(double lastyearEarn) {
        this.lastyearEarn = lastyearEarn;
    }

    public List<CategoryEarn> getCategoryEarn() {
        return categoryEarn;
    }

    public void setCategoryEarn(List<CategoryEarn> categoryEarn) {
        this.categoryEarn = categoryEarn;
    }

    public List<DateEarn> getDateEarn() {
        return dateEarn;
    }

    public void setDateEarn(List<DateEarn> dateEarn) {
        this.dateEarn = dateEarn;
    }

    public double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }
}
