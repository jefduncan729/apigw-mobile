package com.axway.apigw.android.service;

import android.content.Intent;
import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.TopologyModel;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 3/3/2016.
 */
public class TestIntentService extends BaseIntentService {
    public static final String TAG = TestIntentService.class.getSimpleName();

    public TestIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        ApiClient c = BaseApp.getInstance().getApiClient();
        Request req = c.createRequest(TopologyModel.TOPOLOGY_ENDPOINT);
        Response resp = null;
        try {
            resp = c.executeRequest(req);
            if (resp.isSuccessful())
                Log.d(TAG, String.format("success: %s", resp));
            else
                Log.d(TAG, String.format("failure: %s", resp));
        }
        catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
    }
}
