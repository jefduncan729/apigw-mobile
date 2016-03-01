package com.axway.apigw.android.api;

import android.content.Context;
import android.util.Log;

import com.axway.apigw.android.JsonHelper;
import com.google.gson.JsonObject;
import com.vordel.api.topology.model.Topology;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 1/26/2016.
 */
public class TopologyLoader extends ApiLoader<String, Topology> {
    public static final String TAG = TopologyLoader.class.getSimpleName();

    public TopologyLoader(Context context, ApiClient client) {
        super(context, client);
        Log.d(TAG, "constructed");
    }

    @Override
    protected Request createRequest(ApiClient client) {
        return client.createRequest("api/topology");
    }

    @Override
    protected String consumeBody(Response resp) throws IOException {
        String rv = resp.body().string();
        resp.body().close();
        return rv;  //resp.body().string();
    }

    @Override
    protected Topology transformBody(String body) throws IOException {
        JsonObject obj = JsonHelper.getInstance().parseAsObject(body);
        if (obj != null && obj.has("result")) {
            obj = obj.getAsJsonObject("result");
        }
        return JsonHelper.getInstance().topologyFromJson(obj);
    }
}
