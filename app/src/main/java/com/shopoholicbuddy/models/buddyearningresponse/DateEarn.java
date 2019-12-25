
package com.shopoholicbuddy.models.buddyearningresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateEarn {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("date_earning")
    @Expose
    private float dateEarning;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getDateEarning() {
        return dateEarning;
    }

    public void setDateEarning(float dateEarning) {
        this.dateEarning = dateEarning;
    }

}
