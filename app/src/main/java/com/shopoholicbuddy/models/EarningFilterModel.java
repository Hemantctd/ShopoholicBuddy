package com.shopoholicbuddy.models;

import com.shopoholicbuddy.models.preferredcategorymodel.Result;

import java.io.Serializable;

/**
 * Class created by Sachin on 15-May-18.
 */
public class EarningFilterModel implements Serializable{
    private String timePeriod = "";
    private String startDate = "";
    private String endDate = "";
    private String month = "";
    private String year = "";
    private Result category;
    private int count = 0;

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Result getCategory() {
        return category;
    }

    public void setCategory(Result category) {
        this.category = category;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
