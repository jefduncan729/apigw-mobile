package com.axway.apigw.android.model;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by su on 10/2/2014.
 */
public class KpsType extends KpsBase {

    private Map<String, String> props;

    public KpsType() {
        super();
        props = null;
    }

    public KpsType(String id) {
        super(id);
        props = null;
    }

    public KpsType(String id, String desc) {
        super(id, desc);
        props = null;
    }

    public KpsType addProperty(String name, String cls) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(cls)) {
            getProperties().put(name, cls);
        }
        return this;
    }

    public String getPropertyType(String name) {
        if (TextUtils.isEmpty(name))
            return null;
        return getProperties().get(name);
    }

    public Map<String, String> getProperties() {
        if (props == null) {
            props = new HashMap<String, String>();
        }
        return props;
    }
}
