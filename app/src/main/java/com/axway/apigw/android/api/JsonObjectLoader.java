package com.axway.apigw.android.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

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
public class JsonObjectLoader extends AsyncTaskLoader<JsonObject> {

    private static final String TAG = JsonArrayLoader.class.getSimpleName();

    ApiClient client;
    String innerName;
    String endpoint;

    protected JsonObjectLoader(Context context, ApiClient client, String endpoint) {
        super(context);
        this.client = client;
        this.endpoint = endpoint;
        innerName = null;
    }

    protected JsonObjectLoader(Context context, ApiClient client, String endpoint, String innerName) {
        this(context, client, endpoint);
        this.innerName = innerName;
    }

    @Override
    public JsonObject loadInBackground() {
        Request req = client.createRequest(endpoint);
        Response resp = null;
        try {
            resp = client.executeRequest(req);
            if (resp.isSuccessful()) {
                JsonElement json = JsonHelper.getInstance().parse(resp.body().string());
                resp.body().close();
                if (json != null && json.isJsonObject()) {
                    JsonObject jo = json.getAsJsonObject();
                    if (jo.has(innerName) && jo.get(innerName).isJsonObject())
                        return jo.getAsJsonObject(innerName);
                    return jo;
                }
            }
        }
        catch (IOException e) {
            Log.e(TAG, "IOException in loader", e);
        }
        return new JsonObject();
    }
}
