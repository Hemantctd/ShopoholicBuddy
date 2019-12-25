
package com.shopoholicbuddy.models.unreadcountresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("unread_request_count")
    @Expose
    private Integer unreadRequestCount;

    public Integer getUnreadRequestCount() {
        return unreadRequestCount;
    }

    public void setUnreadRequestCount(Integer unreadRequestCount) {
        this.unreadRequestCount = unreadRequestCount;
    }

}
