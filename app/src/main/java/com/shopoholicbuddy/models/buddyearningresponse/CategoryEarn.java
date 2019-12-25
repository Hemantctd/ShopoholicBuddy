
package com.shopoholicbuddy.models.buddyearningresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryEarn {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("category_earning")
    @Expose
    private float categoryEarning;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getCategoryEarning() {
        return categoryEarning;
    }

    public void setCategoryEarning(float categoryEarning) {
        this.categoryEarning = categoryEarning;
    }

}
