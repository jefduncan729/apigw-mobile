package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.KpsItemFragment;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.model.ObservableJsonObject;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Observable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/19/2016.
 */
public class KpsItemActivity extends EditItemActivity<JsonObject> {
    public static final String TAG = KpsItemActivity.class.getSimpleName();

    private String instId;
    private String storeId;
    private KpsStore kpsStore;
    private KpsType kpsType;
    private ObservableJsonObject observable;
    private KpsModel kpsModel;

////    @Override
//    protected JsonObject createItem(Intent intent) {
//        String storeId = intent.getStringExtra(Constants.EXTRA_KPS_STORE_ID);
//        if (kpsStore == null)
//            kpsStore = KpsModel.getInstance().getStoreById(storeId);
//        return kpsStore.newObject();
//    }
//
////    @Override
//    protected JsonObject loadItem(Intent intent) {
//        return jsonHelper.parseAsObject(intent.getStringExtra(Constants.EXTRA_JSON_ITEM));
//    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle(String.format("%s item", (isInsert ? "Add" : "Update")));
        toolbar.setSubtitle(String.format("%s on %s", kpsStore.getAlias(), instId));
    }

    private void onUpdateSuccess(Call call, Response resp) {
        showToast("Item saved");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void createFromArgs(Bundle args, boolean saved) {
        kpsModel = KpsModel.getInstance();
        instId = args.getString(Constants.EXTRA_INSTANCE_ID);
        storeId = args.getString(Constants.EXTRA_KPS_STORE_ID);
        if (kpsStore == null)
            kpsStore = kpsModel.getStoreById(storeId);
        kpsType = kpsModel.getTypeById(kpsStore.getTypeId());
        if (isInsert && !saved) {
            item = kpsStore.newObject();
            return;
        }
        item = jsonHelper.parseAsObject(args.getString(Constants.EXTRA_JSON_ITEM));
    }

    @Override
    protected Observable getObservable() {
        if (observable == null) {
            observable = new ObservableJsonObject(item);
        }
        return observable;
    }

    @Override
    protected EditFrag<JsonObject> createFragment() {
        return KpsItemFragment.newInstance(kpsStore, kpsType, observable);
    }

    @Override
    protected void performSave() {
        String method = null;
        String endpoint = null;
        editFrag.collect(item, null);
        endpoint = String.format(KpsModel.KPS_STORE_ENDPOINT, instId, kpsStore.getAlias());
        if (isInsert && kpsStore.hasGeneratedId()) {
            method = "POST";
        }
        else {
            method = "PUT";
            String key = kpsStore.getConfig().getKey();
            String keyVal = null;
            if (item.has(key))
                keyVal = item.get(key).getAsString();
            endpoint = String.format("%s/%s", endpoint, keyVal);
        }
        ApiClient client = app.getApiClient();
        Request req = client.createRequest(endpoint, method, item);
        client.executeAsyncRequest(req, new UpdateCallback());
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
