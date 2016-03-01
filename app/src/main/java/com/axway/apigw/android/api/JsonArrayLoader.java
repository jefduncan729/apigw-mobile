package com.axway.apigw.android.api;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/18/2016.
 */
public class JsonArrayLoader extends AsyncTaskLoader<JsonArray> {

    private static final String TAG = JsonArrayLoader.class.getSimpleName();

    ApiClient client;
    String innerName;
    String endpoint;

    public JsonArrayLoader(Context context, ApiClient client, String endpoint) {
        super(context);
        this.client = client;
        this.endpoint = endpoint;
        innerName = null;
    }

    public JsonArrayLoader(Context context, ApiClient client, String endpoint, String innerName) {
        this(context, client, endpoint);
        this.innerName = innerName;
    }

    @Override
    public JsonArray loadInBackground() {
        Request req = client.createRequest(endpoint);
        Response resp = null;
        try {
            resp = client.executeRequest(req);
            if (resp.isSuccessful()) {
                JsonElement json = JsonHelper.getInstance().parse(resp.body().string());
                resp.body().close();
                if (json == null)
                    return new JsonArray();
                if (json.isJsonArray()) {
                    return filterData(json.getAsJsonArray());
                }
                if (json.isJsonObject() && !TextUtils.isEmpty(innerName)) {
                    JsonObject jo = json.getAsJsonObject();
                    if (jo.has(innerName) && jo.get(innerName).isJsonArray())
                        return filterData(jo.getAsJsonArray(innerName));
                }
            }
        }
        catch (IOException e) {
            Log.e(TAG, "IOException in loader", e);
        }
        return new JsonArray();
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading");
        cancelLoad();
    }

    protected JsonArray filterData(JsonArray in) {
        if ("topics".equals(innerName) && !BaseApp.getInstance().isShowMqAdvisories()) {
            JsonArray filtered = new JsonArray();
            for (int i = 0; i < in.size(); i++) {
                JsonObject j = in.get(i).getAsJsonObject();
                String n = "";
                if (j.has("queueName")) {
                    n = j.get("queueName").getAsString();
                }
                if (n.contains("Advisory"))
                    continue;
                filtered.add(j);
            }
            return filtered;
        }
        return in;
    }
}
