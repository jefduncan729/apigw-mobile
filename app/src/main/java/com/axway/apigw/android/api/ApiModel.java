package com.axway.apigw.android.api;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;

import okhttp3.Request;

/**
 * Created by su on 2/26/2016.
 */
public class ApiModel {

    protected ApiClient client;
    protected JsonHelper jsonHelper;

    protected ApiModel() {
        super();
        client = BaseApp.getInstance().getApiClient();
        jsonHelper = JsonHelper.getInstance();
    }
}
