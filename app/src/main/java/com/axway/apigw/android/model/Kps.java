package com.axway.apigw.android.model;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by su on 10/2/2014.
 */
public class Kps {

    private String version;
    private String description;
    private Map<String, KpsType> types;
    private Map<String, KpsStore> stores;
    private Map<String, String> aliases;
    private Map<String, List<String>> packages;

    public Kps() {
        super();
        version = null;
        description = null;
        types = null;
        stores = null;
        aliases = null;
        packages = null;
    }

    public void addType(String id, KpsType t) {
        if (TextUtils.isEmpty(id) || t == null)
            return;
        if (!getTypes().containsKey(id))
            getTypes().put(id, t);
    }

    public KpsType getType(String id) {
        return getTypes().get(id);
    }

    public Map<String, KpsType> getTypes() {
        if (types == null)
            types = new HashMap<String, KpsType>();
        return types;
    }

    private void addStoreToPackage(String pkg, KpsStore s) {
        if (packages == null)
            packages = new HashMap<String, List<String>>();
        List<String> storeIds = packages.get(pkg);
        if (storeIds == null)
            storeIds = new ArrayList<String>();
        String sid = s.getIdentity();
        if (!storeIds.contains(sid))
            storeIds.add(sid);
        packages.put(pkg, storeIds);
    }

    public List<String> getPackageNames() {
        List<String> rv = null;
        if (packages == null)
            return rv;
        Set<String> keys = packages.keySet();
        rv = new ArrayList();
        for (String k: keys)
            rv.add(k);
        return rv;
    }

    public List<KpsStore> getStoresInPackage(String pkg, boolean getInternal) {
        List<KpsStore> rv = null;
        if (packages == null)
            return rv;
        List<String> ids = packages.get(pkg);
        if (ids == null || ids.size() == 0)
            return rv;
        rv = new ArrayList<KpsStore>();
        for (String id: ids) {
            KpsStore s = getStoreById(id);
            if (s != null) {
                if (getInternal || !s.getConfig().isInternal())
                    rv.add(s);
            }
        }
        return rv;
    }

    public void addStore(String id, KpsStore s) {
        if (TextUtils.isEmpty(id) || s == null)
            return;
        if (getStores().containsKey(id)) {
            getStores().remove(id);
        }
        String pkg = s.getConfig().getPackageName();
        if (!TextUtils.isEmpty(pkg))
            addStoreToPackage(pkg, s);
        getStores().put(id, s);
    }

    public KpsStore getStoreById(String id) {
        return getStores().get(id);
    }

    public Map<String, KpsStore> getStores() {
        if (stores == null)
            stores = new HashMap<String, KpsStore>();
        return stores;
    }

    public void addAlias(String alias, String storeId) {
        if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(storeId))
            return;
        if (aliases == null)
            aliases = new HashMap<String, String>();
        if (aliases.containsKey(alias))
            aliases.remove(alias);
        KpsStore s = getStoreById(storeId);
        s.setAlias(alias);
        aliases.put(alias, storeId);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KpsStore getStoreByAlias(String alias) {
        if (TextUtils.isEmpty(alias) || aliases == null)
            return null;
        String id = aliases.get(alias);
        if (TextUtils.isEmpty(id))
            return null;
        return getStoreById(id);
    }

    public Map<String, String> getAliases() {
        if (aliases == null)
            aliases = new HashMap<String, String>();
        return aliases;
    }

    public JsonObject createJsonObject(String storeId) {
        KpsStore store = getStoreById(storeId);
        if (store == null)
            return null;
        KpsType typ = getType(store.getTypeId());
        if (typ == null)
            return null;
        JsonObject rv = new JsonObject();
        String idFld = store.getConfig().getKey();
        Map<String, String> flds = typ.getProperties();
        String nm, cls;
        for (Map.Entry<String, String> f: flds.entrySet()) {
            nm = f.getKey();
            if (store.isGeneratedField(nm))
                continue;
            cls = f.getValue();
            if ("java.lang.Integer".equals(cls))
                rv.addProperty(nm, (int)0);
            else if ("java.lang.Long".equals(cls))
                rv.addProperty(nm, (long)0);
            else if ("java.lang.Boolean".equals(cls))
                rv.addProperty(nm, false);
            else
                rv.addProperty(nm, "");
        }
        return rv;
    }
}
