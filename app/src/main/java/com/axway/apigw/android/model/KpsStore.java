package com.axway.apigw.android.model;

import android.text.TextUtils;

import com.axway.apigw.android.api.KpsModel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by su on 10/2/2014.
 */
public class KpsStore extends KpsBase {

    private String alias;
    private String typeId;
    private String implId;
    private KpsStoreConfig config;

    public KpsStore() {
        super();
        alias = null;
        typeId = null;
        implId = null;
        config = null;
    }

    public KpsStore(String id) {
        super(id);
    }

    public KpsStore(String id, String desc) {
        super(id, desc);
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getImplId() {
        return implId;
    }

    public void setImplId(String implId) {
        this.implId = implId;
    }

    public KpsStoreConfig getConfig() {
        if (config == null)
            config = new KpsStoreConfig();
        return config;
    }

    public KpsStoreProcessor createProcessor() {
        return new KpsStoreProcessor();
    }

    public class KpsStoreProcessor {

        List<String> secureProps;
        String keyField;
        String className;

        public KpsStoreProcessor() {
            super();
            secureProps = null;
            keyField = null;
            className = null;
        }

        public KpsStoreProcessor addSecureProperty(String name) {
            if (!getSecureProps().contains(name))
                getSecureProps().add(name);
            return this;
        }

        public List<String> getSecureProps() {
            if (secureProps == null) {
                secureProps = new ArrayList<String>();
            }
            return secureProps;
        }

        public String getKeyField() {
            return keyField;
        }

        public void setKeyField(String keyField) {
            this.keyField = keyField;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public class KpsStoreConfig {
        String writeConsistencyLevel;
        String packageName;
        ArrayList<String> indexes;
        ArrayList<String> readKey;
        ArrayList<KpsStoreProcessor> processors;
        boolean internal;
        String key;
        String readConsistencyLevel;
        private ArrayList<String> secureFlds;
        private ArrayList<String> genFlds;

        KpsStoreConfig() {
            writeConsistencyLevel = "ANY";
            packageName = null;
            indexes = null;
            readKey = null;
            processors = null;
            internal = false;
            key = null;
            readConsistencyLevel = "ONE";
            secureFlds = null;
            genFlds = null;
        }

        public String getWriteConsistencyLevel() {
            return writeConsistencyLevel;
        }

        public void setWriteConsistencyLevel(String writeConsistencyLevel) {
            this.writeConsistencyLevel = writeConsistencyLevel;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public ArrayList<KpsStoreProcessor> getProcessors() {
            if (processors == null) {
                processors = new ArrayList<KpsStoreProcessor>();
            }
            return processors;
        }

        public void addProcessor(KpsStoreProcessor processor) {
            if (processor != null)
                getProcessors().add(processor);
        }

        public boolean isInternal() {
            return internal;
        }

        public void setInternal(boolean internal) {
            this.internal = internal;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getReadConsistencyLevel() {
            return readConsistencyLevel;
        }

        public void setReadConsistencyLevel(String readConsistencyLevel) {
            this.readConsistencyLevel = readConsistencyLevel;
        }

        public ArrayList<String> getIndexes() {
            if (indexes == null)
                indexes = new ArrayList<String>();
            return indexes;
        }

        public void addIndex(String index) {
            if (!TextUtils.isEmpty(index)) {
                if (!getIndexes().contains(index))
                    getIndexes().add(index);
            }
//            return this;
        }

        public ArrayList<String> getReadKeys() {
            if (readKey == null)
                readKey = new ArrayList<String>();
            return readKey;
        }

        public void addReadKey(String key) {
            if (!TextUtils.isEmpty(key)) {
                if (!getReadKeys().contains(key))
                    getReadKeys().add(key);
            }
//            return this;
        }

        public ArrayList<String> getSecureFieldNames() {
            if (secureFlds == null) {
                if (processors != null) {
                    secureFlds = new ArrayList<String>();
                    for (KpsStoreProcessor p: processors) {
                        List<String> sf = p.getSecureProps();
                        if (sf != null)
                            secureFlds.addAll(sf);
                    }
                }
            }
            return secureFlds;
        }

        public ArrayList<String> getGeneratedFieldNames() {
            if (genFlds == null) {
                if (processors != null) {
                    genFlds = new ArrayList<String>();
                    for (KpsStoreProcessor p: processors) {
                        String gf = p.getKeyField();
                        if (!TextUtils.isEmpty(gf))
                            genFlds.add(gf);
                    }
                }
            }
            return genFlds;
        }

    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean hasGeneratedId() {
        boolean rv = false;
        List<String> genFlds = getConfig().getGeneratedFieldNames();
        String idFld = getConfig().getKey();
        if (genFlds != null && genFlds.contains(idFld))
            rv = true;
        return rv;
    }

    public boolean isGeneratedField(String fldName) {
        boolean rv = false;
        List<String> genFlds = getConfig().getGeneratedFieldNames();
        if (genFlds != null && genFlds.contains(fldName))
            rv = true;
        return rv;
    }

    public boolean isEncryptedField(String fldName) {
        boolean rv = false;
        List<String> secFlds = getConfig().getSecureFieldNames();
        if (secFlds != null && secFlds.contains(fldName))
            rv = true;
        return rv;
    }

    public boolean isIndex(String fldName) {
        boolean rv = false;
        List<String> indexes = getConfig().getIndexes();
        if (indexes != null && indexes.contains(fldName))
            rv = true;
        return rv;
    }

    public boolean isPrimaryKey(String fldName) {
        boolean rv = false;
        String pk = getConfig().getKey();
        if (fldName != null && fldName.equals(pk))
            rv = true;
        return rv;
    }

    public JsonObject newObject() {
        JsonObject rv = new JsonObject();
        KpsType type = KpsModel.getInstance().getTypeById(getTypeId());
        for (Map.Entry<String, String> p : type.getProperties().entrySet()) {
            String nm = p.getKey();
            String cls = p.getValue();
            if (isGeneratedField(nm))
                continue;
            if ("java.lang.String".equals(cls)) {
                rv.addProperty(nm, "");
            }
            else if ("java.lang.Long".equals(cls)) {
                rv.addProperty(nm, (long) 0);
            }
            else if ("java.lang.Boolean".equals(cls)) {
                rv.addProperty(nm, false);
            }
            else if ("java.lang.Integer".equals(cls)) {
                rv.addProperty(nm, (int)0);
            }
            else if ("java.lang.Double".equals(cls)) {
                rv.addProperty(nm, (double)0);
            }
            else if ("java.lang.Byte".equals(cls)) {
                rv.addProperty(nm, (byte)0x00);
            }
        }
        return rv;
    }
}
