
package com.shopoholicbuddy.models.dedicatedresponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DedicatedResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("next")
    @Expose
    private Integer next;
    @SerializedName("termconditionlink")
    @Expose
    private String termconditionlink;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public String getTermconditionlink() {
        return termconditionlink;
    }

    public void setTermconditionlink(String termconditionlink) {
        this.termconditionlink = termconditionlink;
    }

}
