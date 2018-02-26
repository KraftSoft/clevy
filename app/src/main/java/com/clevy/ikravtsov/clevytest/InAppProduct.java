package com.clevy.ikravtsov.clevytest;

/**
 * Created by ikravtsov on 18/02/2018.
 */

class InAppProduct {

    static final int BILLING_RESPONSE_RESULT_OK = 0;
    static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    static final int PURCHASE_STATUS_PURCHASED = 0;
    static final int PURCHASE_STATUS_CANCELLED = 1;
    static final int PURCHASE_STATUS_REFUNDED = 2;

    static final int REQUEST_CODE_BUY = 1234;


    String storeName;
    String storeDescription;
    String price;
    boolean isSubscription;
    int priceAmountMicros;
    String currencyIsoCode;
    String productId;

    String getId() {
        return productId;
    }

    String getType() {
        return isSubscription ? "subs" : "inapp";
    }

}