package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.KpsItemFragment;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/19/2016.
 */
public class KpsItemActivity extends EditActivity<JsonObject> {
    public static final String TAG = KpsItemActivity.class.getSimpleName();
    private String instId;
    private String storeId;
    private KpsStore store;

    @Override
    protected EditFrag<JsonObject> createFragment(Bundle args, JsonObject item) {
        instId = args.getString(Constants.EXTRA_INSTANCE_ID);
        storeId = args.getString(Constants.EXTRA_KPS_STORE_ID);
        if (store == null)
            store = KpsModel.getInstance().getStoreById(storeId);
        KpsType type = KpsModel.getInstance().getTypeById(store.getTypeId());

        return KpsItemFragment.newInstance(store, type, item);
    }

    @Override
    protected JsonObject createItem(Intent intent) {
        String storeId = intent.getStringExtra(Constants.EXTRA_KPS_STORE_ID);
        if (store == null)
            store = KpsModel.getInstance().getStoreById(storeId);
        return store.newObject();
    }

    @Override
    protected JsonObject loadItem(Intent intent) {
        return JsonHelper.getInstance().parseAsObject(intent.getStringExtra(Constants.EXTRA_JSON_ITEM));
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        super.setupToolbar(toolbar);
        toolbar.setTitle(String.format("%s item", (isInsert ? "Add" : "Update")));
        toolbar.setSubtitle(String.format("%s on %s", store.getAlias(), instId));
    }

    @Override
    protected boolean saveItem(JsonObject item, Bundle extras) {
        setResult(RESULT_CANCELED);
        String method = null;
        String endpoint = null;
//        KpsStore store = KpsModel.getInstance().getStoreById(storeId);
        endpoint = KpsModel.KPS_STORE_ENDPOINT.replace("{svcId}", instId).replace("{alias}", store.getAlias());
        if (isInsert && store.hasGeneratedId()) {
            method = "POST";
        }
        else {
            method = "PUT";
            String key = store.getConfig().getKey();
            String keyVal = null;
            if (item.has(key))
                keyVal = item.get(key).getAsString();
            endpoint = String.format("%s/%s", endpoint, keyVal);
        }
        ApiClient client = app.getApiClient();
        Request req = client.createRequest(endpoint, method, item);
        client.executeAsyncRequest(req, new UpdateCallback());
        return true;
    }

    private void onUpdateSuccess(Call call, Response resp) {
        showToast("Item saved");
        setResult(RESULT_OK);
        finish();
    }

    private class UpdateCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, String.format("updateFailed: %s", call), e);
        }

        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                Log.d(TAG, String.format("updateSuccess: %s - %s", call, response));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUpdateSuccess(call, response);
                    }
                });
            }
            else {
                onFailure(call, new IOException(String.format("%d %s", response.code(), response.message())));
            }
        }
    }
}
