
package com.shopoholicbuddy.models.launcherhomeresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LauncherHomeResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private Boolean msg;
    @SerializedName("result")
    @Expose
    private Result result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getMsg() {
        return msg;
    }

    public void setMsg(Boolean msg) {
        this.msg = msg;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
