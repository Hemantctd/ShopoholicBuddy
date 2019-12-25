
package com.shopoholicbuddy.models.launcherhomeresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerArr {

    @SerializedName("banner_id")
    @Expose
    private String bannerId;
    @SerializedName("banner_name")
    @Expose
    private String bannerName;
    @SerializedName("banner_image")
    @Expose
    private String bannerImage;
    @SerializedName("type")
    @Expose
    private String type;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
