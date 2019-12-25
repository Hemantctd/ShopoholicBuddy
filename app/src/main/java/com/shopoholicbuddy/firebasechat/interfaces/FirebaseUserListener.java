package com.shopoholicbuddy.firebasechat.interfaces;

import com.shopoholicbuddy.firebasechat.models.UserBean;

/**
 * interface to get user from firebase
 */

public interface FirebaseUserListener {
    void getUser(UserBean user);
    void updateUser(UserBean user);
}
