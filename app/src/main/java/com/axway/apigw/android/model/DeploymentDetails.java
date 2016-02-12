package com.axway.apigw.android.model;

/**
 * Created by su on 10/27/2014.
 */
public class DeploymentDetails {

    private String user;
    private Long timestamp;
    private String status;
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

    public Props createProps() {
        return new Props();
    }
}
