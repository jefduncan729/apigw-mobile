package com.axway.apigw.android.model;

import com.axway.apigw.android.JsonHelper;
import com.google.gson.JsonObject;

/**
 * Created by su on 10/27/2014.
 */
public class DeploymentDetails {

    private String user;
    private Long timestamp;
    private String status;
    private JsonObject root;
    private JsonObject policy;
    private JsonObject env;

/*
    private Props rootProps;
    private Props policyProps;
    private Props envProps;

    public class Props {
        private String manifestVersion;
        private String id;
        private long timestamp;
        private String name;
        private String description;
        private String version;
        private String versionComment;

        public Props() {
            manifestVersion = null;
            id = null;
            timestamp = 0;
            name = null;
            description = null;
            version = null;
            versionComment = null;
        }

        public String getManifestVersion() {
            return manifestVersion;
        }

        public void setManifestVersion(String manifestVersion) {
            this.manifestVersion = manifestVersion;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersionComment() {
            return versionComment;
        }

        public void setVersionComment(String versionComment) {
            this.versionComment = versionComment;
        }
    }
*/

    public DeploymentDetails() {
        user = null;
        timestamp = 0L;
        status = null;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        JsonObject r = getRootProperties();
        if (r == null || !r.has("Id"))
            return null;
        return r.get("Id").getAsString();
    }
/*
    public Props getRootProperties() {
        if (rootProps == null)
            rootProps = new Props();
        return rootProps;
    }

    public Props getPolicyProperties() {
        if (policyProps == null)
            policyProps = new Props();
        return policyProps;
    }

    public Props getEnvironmentProperties() {
        if (envProps == null)
            envProps = new Props();
        return envProps;
    }
*/

    public JsonObject getRootProperties() {
        if (root == null) {
            root = new JsonObject();
            root.addProperty("Id", "");
            root.addProperty("Timestamp", 0L);
        }
        return root;
    }

    public JsonObject getPolicyProperties() {
        if (policy == null) {
            policy = new JsonObject();
            _initialize(policy);
        }
        return policy;
    }

    public JsonObject getEnvironmentProperties() {
        if (env == null) {
            env = new JsonObject();
            _initialize(env);
        }
        return env;
    }

    public void setRootProperties(JsonObject root) {
        this.root = root;
    }

    public void setPolicyProperties(JsonObject policy) {
        this.policy = policy;
    }

    public void setEnvironmentProperties(JsonObject env) {
        this.env = env;
    }

    private void _initialize(JsonObject j) {
        j.addProperty(JsonHelper.PROP_MANIFEST_VERSION, "");
        j.addProperty("Id", "");
        j.addProperty("Timestamp", 0L);
        j.addProperty("Name", "");
        j.addProperty("Description", "");
        j.addProperty("Version", "");
        j.addProperty("VersionComment", "");
    }
//
//    public Props createProps() {
//        return new Props();
//    }
}
