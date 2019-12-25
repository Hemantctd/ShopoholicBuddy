package com.shopoholicbuddy.firebasechat.interfaces;

import com.shopoholicbuddy.firebasechat.models.HuntDeal;
import com.shopoholicbuddy.models.huntdetailresponse.Result;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseHuntResponseListener {
    void getHuntDetails(HuntDeal huntData);
}
