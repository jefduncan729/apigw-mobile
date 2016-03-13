package com.axway.apigw.android.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 * Created by jef on 3/11/16.
 */
public class ObservableJsonObject extends Observable {

    private JsonObject obj;
    private Set<String> changedKeys;

    public ObservableJsonObject(JsonObject j) {
        super();
        obj = j;
        changedKeys = null;
    }

    public JsonObject jsonObject() {
        return obj;
    }

    private void addChangedKey(String key) {
        if (changedKeys == null)
            changedKeys = new HashSet<>();
        if (!changedKeys.contains(key))
            changedKeys.add(key);
        setChanged();
        notifyObservers(key);
    }

    public ObservableJsonObject setProperty(String key, String newVal) {
        if (obj == null)
            return this;
        String v = getString(key);
        if (v == null || !v.equals(newVal)) {
            obj.addProperty(key, newVal);
            addChangedKey(key);
        }
        return this;
    }

    public ObservableJsonObject setProperty(String key, boolean newVal) {
        if (obj == null)
            return this;
        boolean v = getBoolean(key);
        if (v != newVal) {
            obj.addProperty(key, newVal);
            addChangedKey(key);
        }
        return this;
    }

    public ObservableJsonObject setProperty(String key, int newVal) {
        if (obj == null)
            return this;
        int v = getInt(key);
        if (v != newVal) {
            obj.addProperty(key, newVal);
            addChangedKey(key);
        }
        return this;
    }

    public ObservableJsonObject setProperty(String key, long newVal) {
        if (obj == null)
            return this;
        long v = getLong(key);
        if (v != newVal) {
            obj.addProperty(key, newVal);
            addChangedKey(key);
        }
        return this;
    }

    public String getString(String key) {
        if (obj == null || !obj.has(key))
            return null;
        return obj.get(key).getAsString();
    }

    public boolean getBoolean(String key) {
        if (obj == null || !obj.has(key))
            return false;
        return obj.get(key).getAsBoolean();
    }

    public int getInt(String key) {
        if (obj == null || !obj.has(key))
            return 0;
        return obj.get(key).getAsInt();
    }

    public long getLong(String key) {
        if (obj == null || !obj.has(key))
            return 0L;
        return obj.get(key).getAsLong();
    }

    public JsonObject getJsonObject(String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonObject())
            return null;
        return obj.get(key).getAsJsonObject();
    }

    public JsonArray getJsonArray(String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonArray())
            return null;
        return obj.get(key).getAsJsonArray();
    }

    public boolean hasChangedKeys() {
        return (changedKeys != null && changedKeys.size() > 0);
    }

    public boolean hasChangedKey(String key) {
        if (hasChangedKeys()) {
            return changedKeys.contains(key);
        }
        return false;
    }

    public Set<String> getChangedKeys() {
        return changedKeys;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObservableJsonObject{");
        sb.append("obj=").append(obj);
        sb.append('}');
        return sb.toString();
    }
}
