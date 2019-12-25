package com.appinventiv.codescanner;

/**
 * Class created by Sachin on 31-May-18.
 */
public interface ApiResponseListener {
    void getResponse(int status);
    void getError();
}
