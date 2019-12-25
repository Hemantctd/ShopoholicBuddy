
package com.shopoholicbuddy.models.launcherhomeresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("banner_arr")
    @Expose
    private List<BannerArr> bannerArr = null;
    @SerializedName("popular_deals")
    @Expose
    private List<com.shopoholicbuddy.models.productdealsresponse.Result> popularDeals = null;

    public List<BannerArr> getBannerArr() {
        return bannerArr;
    }

    public void setBannerArr(List<BannerArr> bannerArr) {
        this.bannerArr = bannerArr;
    }

    public List<com.shopoholicbuddy.models.productdealsresponse.Result> getPopularDeals() {
        return popularDeals;
    }

    public void setPopularDeals(List<com.shopoholicbuddy.models.productdealsresponse.Result> popularDeals) {
        this.popularDeals = popularDeals;
    }

}
