package com.shopoholicbuddy.interfaces;

public interface PaymentResponseCallback {
    void onPaymentSuccess(int responseCode, String message);
    void onPaymentFailuer(int responseCode, String message);
}
