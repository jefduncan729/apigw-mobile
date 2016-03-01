package com.axway.apigw.android.api;

import com.axway.apigw.android.BaseApp;

/**
 * Created by su on 2/26/2016.
 */
public class ApiModel {

    protected ApiClient client;

    protected ApiModel() {
        super();
        client = BaseApp.getInstance().getApiClient();
    }
}
