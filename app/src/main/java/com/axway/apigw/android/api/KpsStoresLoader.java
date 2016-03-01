package com.axway.apigw.android.api;

import android.content.Context;

import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.model.Kps;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/18/2016.
 */
public class KpsStoresLoader extends ApiLoader<String, Kps> {

    private String instId;

    public KpsStoresLoader(Context context, ApiClient client, String instId) {
        super(context, client);
        this.instId = instId;
    }

    @Override
    protected Request createRequest(ApiClient client) {
        Request req = client.createRequest(KpsModel.KPS_ENDPOINT.replace("{svcId}", instId));
        return req;
    }

    @Override
    protected String consumeBody(Response resp) throws IOException {
        String rv = resp.body().string();
        resp.body().close();
        return rv;  //resp.body().string();
    }

    @Override
    protected Kps transformBody(String body) throws IOException {
        JsonObject obj = JsonHelper.getInstance().parseAsObject(body);
        if (obj != null && obj.has("result")) {
            obj = obj.getAsJsonObject("result");
        }
        return JsonHelper.getInstance().kpsFromJson(obj);
    }
}
