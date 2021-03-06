package com.axway.apigw.android.api;

import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.R;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by su on 10/5/2015.
 */
public class KpsModel extends ApiModel {
    private static final String TAG = KpsModel.class.getSimpleName();

    public static final String KPS_ENDPOINT = "api/router/service/%s/api/kps";
    public static final String KPS_STORE_ENDPOINT = KPS_ENDPOINT + "/%s";
    public static final String KPS_START_ENDPOINT = KPS_ENDPOINT + "/iterator/start/%s";
    public static final String KPS_NEXT_ENDPOINT = KPS_ENDPOINT + "/iterator/next/%s";

    private static KpsModel instance = null;
    private Kps kps;

    protected KpsModel() {
        super();
        reset();
    }

    public static KpsModel getInstance() {
        if (instance == null) {
            instance = new KpsModel();
        }
        return instance;
    }

    public Kps getKps() {
        return kps;
    }

    public void setKps(Kps kps) {
        reset();
        this.kps = kps;
    }

    public void reset() {
        Log.d(TAG, "reset");
        if (kps != null) {
            kps.reset();
        }
        kps = null;
    }

/*
    public String[][] inflateDisplayPrefs(String s) {
        String[][] rv = new String[MAX_ROWS][SPINNER_IDS.length];
        for (int r = 0; r < MAX_ROWS; r++)
            for (int c = 0; c < SPINNER_IDS.length; c++)
                rv[r][c] = "";
        if (TextUtils.isEmpty(s))
            return rv;
        String[] rows = s.split(ROW_SEPARATOR);
        if (rows.length == 0)
            return null;
        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            if (TextUtils.isEmpty(row))
                continue;
            String[] flds = row.split(COL_SEPARATOR);
            if (flds.length == 0)
                continue;
            for (int c = 0; c < flds.length; c++)
                rv[r][c] = flds[c];
        }
        return rv;
    }

    public String deflateDisplayPrefs(String[][] prefs) {
        if (prefs == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_ROWS; i++) {
            if (i > 0)
                sb.append(ROW_SEPARATOR);
            String[] list = prefs[i];
            if (list != null && list.length > 0) {
                for (int j = 0; j < list.length; j++) {
                    if (j > 0)
                        sb.append(COL_SEPARATOR);
                    sb.append(list[j]);
                }
            }
        }
        Log.d(TAG, "deflatePrefs: " + sb.toString());
        return sb.toString();
    }
*/

    public KpsStore getStoreById(String storeId) {
        if (kps == null)
            return null;
        return kps.getStoreById(storeId);
    }

    public KpsType getTypeById(String typeId) {
        if (kps == null)
            return null;
        return kps.getType(typeId);
    }

    public Request loadKps(String instId, Callback cb) {
        assert client != null;
        assert cb != null;
        Request req = client.createRequest(String.format(KPS_ENDPOINT, instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }
}
