package com.axway.apigw.android.model;

import com.axway.apigw.android.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vordel.kps.json.Json;

import java.util.Observable;

/**
 * Created by jef on 3/10/16.
 */
public class ServiceConfig extends Observable {

    private String instId;
    private JsonHelper.Wrapper delegate;
    private JsonArray httpSvcs;
    private String traceLevel;

    public ServiceConfig(String instId, JsonObject from) {
        super();
        this.instId = instId;
        delegate = JsonHelper.getInstance().wrap(from);
        httpSvcs = null;
        traceLevel = null;
    }

    @Override
    public String toString() {
        if (delegate == null)
            return "{}";
        return delegate.toString();
    }

    public String name() {
        if (delegate == null)
            return null;
        return delegate.getString("name");
    }

    public boolean recordInbound() {
        if (delegate == null)
            return false;
        return delegate.getBoolean("recordInboundTransactions");
    }

    public boolean recordOutbound() {
        if (delegate == null)
            return false;
        return delegate.getBoolean("recordOutboundTransactions");
    }

    public boolean recordCircuitPath() {
        if (delegate == null)
            return false;
        return delegate.getBoolean("recordCircuitPath");
    }

    public boolean recordTrace() {
        if (delegate == null)
            return false;
        return delegate.getBoolean("recordTrace");
    }

    public String traceLevel() {
        return traceLevel;
    }

    public void changeString(String key, String val) {
        String v = delegate.getString(key);
        if (v != null && !v.equals(val)) {
            delegate.setString("name", val);
            setChanged();
            notifyObservers(key);
        }
    }

    public void changeBoolean(String key, boolean val) {
        boolean v = delegate.getBoolean(key);
        if (v != val) {
            delegate.setBoolean("name", val);
            setChanged();
            notifyObservers(key);
        }
    }

    public void name(String newVal) {
        if (delegate == null)
            return;
        changeString("name", newVal);
    }

    public void recordInbound(boolean newVal) {
        if (delegate == null)
            return;
        changeBoolean("recordInboundTransactions", newVal);
    }

    public void recordOutbound(boolean newVal) {
        if (delegate == null)
            return;
        changeBoolean("recordOutboundTransactions", newVal);
    }

    public void recordCircuitPath(boolean newVal) {
        if (delegate == null)
            return;
        changeBoolean("recordCircuitPath", newVal);
    }

    public void recordTrace(boolean newVal) {
        if (delegate == null)
            return;
        changeBoolean("recordTrace", newVal);
    }

    public void traceLevel(String newVal) {
        if (traceLevel == null || !traceLevel.equals(newVal)) {
            traceLevel = newVal;
            setChanged();
            notifyObservers();
        }
    }

    public JsonArray httpServices() {
        if (httpSvcs == null) {
            if (delegate == null)
                return null;
            JsonArray a = delegate.getArray("processes");
            if (a == null)
                return null;
            for (int i = 0; i < a.size(); i++) {
                JsonObject j = a.get(i).getAsJsonObject();
                if (j.has("name") && instId.equals(j.get("name").getAsString())) {
                    if (j.has("traceLevel"))
                        traceLevel = j.get("traceLevel").getAsString();
                    httpSvcs = j.getAsJsonArray("httpServices");
                    break;
                }
            }
        }
        return httpSvcs;
    }

    public HttpService httpService(int ndx) {
        JsonArray a = httpServices();
        if (a == null)
            return null;
        for (int i = 0; i < a.size(); i++) {
            JsonObject j = a.get(i).getAsJsonObject();
            if (i == ndx) {
                return new HttpService(j);
            }
        }
        return null;
    }

    public HttpService httpService(String name) {
        JsonArray a = httpServices();
        if (a == null)
            return null;
        for (int i = 0; i < a.size(); i++) {
            JsonObject j = a.get(i).getAsJsonObject();
            if (j.has("name") && name.equals(j.get("name").getAsString())) {
                return new HttpService(j);
            }
        }
        return null;
    }

    public class HttpService {
        private JsonHelper.Wrapper delegate;

        public HttpService(JsonObject from) {
            super();
            delegate = JsonHelper.getInstance().wrap(from);
        }

        public JsonArray ports() {
            return delegate.getArray("ports");
        }

        public JsonArray uris() {
            return delegate.getArray("uris");
        }

        public String name() {
            return delegate.getString("name");
        }

        public boolean enabled() {
            return delegate.getBoolean("enable");
        }
    }
}
