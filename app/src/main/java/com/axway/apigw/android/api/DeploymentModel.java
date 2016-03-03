package com.axway.apigw.android.api;

import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.model.DeploymentDetails;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/26/2016.
 */
public class DeploymentModel extends ApiModel {
    private static final String TAG = DeploymentModel.class.getSimpleName();

    private static final String ENDPOINT_BASE = "api/deployment/";
    public static final String ENDPOINT_DEPLOYMENT_DETAILS = ENDPOINT_BASE + "domain/deployments";
    public static final String ENDPOINT_ARCHIVE = ENDPOINT_BASE + "archive/";
    public static final String ENDPOINT_DEPLOYMENT_INST_ARCHIVE = ENDPOINT_ARCHIVE + "service/{svcId}";
    public static final String ENDPOINT_DEPLOYMENT_INST_POLICY_ARCHIVE = ENDPOINT_ARCHIVE + "policy/service/{svcId}";
    public static final String ENDPOINT_DEPLOYMENT_INST_ENV_ARCHIVE = ENDPOINT_ARCHIVE + "environment/service/{svcId}";
    public static final String ENDPOINT_DEPLOYMENT_ARCHIVE = ENDPOINT_ARCHIVE + "group/{grpId}/{archiveId}";
    public static final String ENDPOINT_DEPLOYMENT_POLICY_ARCHIVE = ENDPOINT_ARCHIVE + "policy/group/{grpId}/{archiveId}";
    public static final String ENDPOINT_DEPLOYMENT_ENV_ARCHIVE = ENDPOINT_ARCHIVE + "environment/group/{grpId}/{archiveId}";

    public static final String ENDPOINT_ENV_SETTINGS = ENDPOINT_BASE + "envsettings/";
    public static final String ENDPOINT_ENV_SETTINGS_GRP = ENDPOINT_ENV_SETTINGS + "{grpId}/{archiveId}";
    public static final String ENDPOINT_ENV_SETTINGS_INST = ENDPOINT_ENV_SETTINGS + "{svcId}";

    public static final String ENDPOINT_UPLOAD_CFG = ENDPOINT_BASE + "group/configuration/";
    public static final String ENDPOINT_UPLOAD_CFG_FILE = ENDPOINT_BASE + "group/configuration/file/";
    public static final String ENDPOINT_UPLOAD_POLICY = ENDPOINT_BASE + "group/configuration/file/policy/";
    public static final String ENDPOINT_UPLOAD_ENV = ENDPOINT_BASE + "group/configuration/file/environment/";

    private Map<String, DeploymentDetails> deployDetails;
    private List<String> inconsistentGroups;

    private static DeploymentModel instance;

    protected DeploymentModel() {
        super();
        deployDetails = null;
    }

    public static DeploymentModel getInstance() {
        if (instance == null)
            instance = new DeploymentModel();
        return instance;
    }

    public DeploymentDetails getDeploymentDetails(String instanceId) {
        if (TextUtils.isEmpty(instanceId) || deployDetails == null)
            return null;
        return deployDetails.get(instanceId);
    }

    public Request getDeploymentDetails(Callback cb) {
        assert client != null;
        assert cb != null;
        deployDetails = null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_DETAILS);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public void setDeploymentDetails(JsonObject json) {
        if (json == null) {
            Log.d(TAG, "setDeploymentDetails: null");
            deployDetails = null;
            return;
        }
        Log.d(TAG, "setDeploymentDetails: " + json.toString());
        inconsistentGroups = null;
        Set<Map.Entry<String, JsonElement>> groups = json.entrySet();
        if (groups == null || groups.size() == 0)
            return;
        deployDetails = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> svcs = null;
        String grpId;
        String svcId;
        JsonObject jsonGrp;
        JsonObject jsonSvc;
        for (Map.Entry<String, JsonElement> ge: groups) {
            grpId = ge.getKey();
            String prevId = null;
            jsonGrp = ge.getValue().getAsJsonObject();
            if (jsonGrp != null) {
                svcs = jsonGrp.entrySet();
                for (Map.Entry<String, JsonElement> se: svcs) {
                    svcId = se.getKey();
                    jsonSvc = se.getValue().getAsJsonObject();
                    DeploymentDetails dd = JsonHelper.getInstance().deploymentDetailsFromJson(jsonSvc);
                    if (dd != null) {
                        if (prevId != null && !prevId.equals(dd.getId())) {
                            addInconsistentGroup(grpId);
                        }
                        prevId = dd.getId();
                        deployDetails.put(svcId, dd);
                    }
                }
            }
        }
    }

    private void addInconsistentGroup(String grpId) {
        if (inconsistentGroups == null)
            inconsistentGroups = new ArrayList<>();
        if (!inconsistentGroups.contains(grpId))
            inconsistentGroups.add(grpId);
    }

    public boolean isInconsistentGroup(String grpId) {
        if (inconsistentGroups == null)
            return false;
        return inconsistentGroups.contains(grpId);
    }

    public Request getDeploymentArchive(String grpId, String archiveId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getDeploymentArchive(String grpId, String archiveId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        return client.executeRequest(req);
    }

    public Request getEnvironmentArchive(String grpId, String archiveId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_ENV_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getEnvironmentArchive(String grpId, String archiveId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_ENV_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        return client.executeRequest(req);
    }

    public Request getPolicyArchive(String grpId, String archiveId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_POLICY_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getPolicyArchive(String grpId, String archiveId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_POLICY_ARCHIVE.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        return client.executeRequest(req);
    }

    public Request getDeploymentArchiveForService(String instId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_ARCHIVE.replace("{svcId}", instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getDeploymentArchiveForService(String instId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_ARCHIVE.replace("{svcId}", instId));
        return client.executeRequest(req);
    }

    public Request getEnvironmentArchiveForService(String instId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_ENV_ARCHIVE.replace("{svcId}", instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getEnvironmentArchiveForService(String instId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_ENV_ARCHIVE.replace("{svcId}", instId));
        return client.executeRequest(req);
    }

    public Request getPolicyArchiveForService(String instId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_POLICY_ARCHIVE.replace("{svcId}", instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getPolicyArchiveForService(String instId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_DEPLOYMENT_INST_POLICY_ARCHIVE.replace("{svcId}", instId));
        return client.executeRequest(req);
    }

    public Request getEnvironmentSettings(String grpId, String archiveId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_ENV_SETTINGS_GRP.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getEnvironmentSettings(String grpId, String archiveId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_ENV_SETTINGS_GRP.replace("{grpId}", grpId).replace("{archiveId}", archiveId));
        return client.executeRequest(req);
    }

    public Request getEnvironmentSettingsForService(String instId, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_ENV_SETTINGS_INST.replace("{svcId}", instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Response getEnvironmentSettingsForService(String instId) throws IOException {
        assert client != null;
        Request req = client.createRequest(ENDPOINT_ENV_SETTINGS_INST.replace("{svcId}", instId));
        return client.executeRequest(req);
    }

    public Request uploadDeploymentArchive(String grpId, DeploymentDetails dd, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_UPLOAD_CFG + grpId);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request uploadDeploymentArchiveFile(String grpId, DeploymentDetails dd, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_UPLOAD_CFG_FILE + grpId);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request uploadPolicyArchive(String grpId, DeploymentDetails dd, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_UPLOAD_POLICY + grpId);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request uploadEnvironmentArchive(String grpId, DeploymentDetails dd, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(ENDPOINT_UPLOAD_ENV + grpId);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request updateDeploymentPassphrase(String grpId, String newVal, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(String.format("%spassphrase/group/%s", ENDPOINT_BASE, grpId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request updateNodeMgrPassphrase(String instId, String newVal, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(String.format("%spassphrase/nodemanager/%s", ENDPOINT_BASE, instId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request updatePolicyProperties(String grpId, String archiveId, JsonObject props, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(String.format("%s%s/%s/properties/policy", ENDPOINT_ARCHIVE, grpId, archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request updateEnvironmentProperties(String grpId, String archiveId, JsonObject props, Callback cb) {
        assert cb != null;
        assert client != null;
        Request req = client.createRequest(String.format("%s%s/%s/properties/environment", ENDPOINT_ARCHIVE, grpId, archiveId));
        client.executeAsyncRequest(req, cb);
        return req;
    }
}
