package com.shopoholicbuddy.firebasechat.interfaces;

import com.shopoholicbuddy.firebasechat.models.ProductBean;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseProductResponseListener {
    void getProductDetails(ProductBean productBean);
}
