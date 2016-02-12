package com.axway.apigw.android.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.StatusObserver;
import com.google.gson.JsonObject;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by su on 11/9/2015.
 */
public class TopologyModel {
    private static final String TAG = TopologyModel.class.getSimpleName();

    public static final int GATEWAY_STATUS_UNKNOWN = 0;
    public static final int GATEWAY_STATUS_RUNNING = 1;
    public static final int GATEWAY_STATUS_NOT_RUNNING = 2;
    public static final int GATEWAY_STATUS_CHECKING = 3;

    public static final String TOPOLOGY_ENDPOINT = "api/topology";
    public static final String GATEWAY_MGMT_ENDPOINT = "api/management/{action}/{grpId}/{svcId}";

    public static final String HOSTS_ENDPOINT = TOPOLOGY_ENDPOINT + "/hosts";
    public static final String GROUPS_ENDPOINT = TOPOLOGY_ENDPOINT + "/groups";
    public static final String INSTANCES_ENDPOINT = TOPOLOGY_ENDPOINT + "/services";

    private Topology topology;
    private ApiClient client;
    private Map<String, Integer> statusCache;
    private Map<String, StatusObserver> statusObservers;
    private Map<String, DeploymentDetails> deployDetails;
    private static TopologyModel instance = null;
    private static ArrayList<String> hostNames;
    private static ArrayList<String> grpNames;
    protected static Service adminNodeMgr;
    private WeakReference<Context> ctxRef;

    public TopologyModel(ApiClient client) {
        super();
        this.client = client;
        ctxRef = null;
        reset();
    }

//    public static TopologyModel getInstance() {
//        if (instance == null) {
//            instance = new TopologyModel();
//        }
//        return instance;
//    }
//
    protected Context getContext() {
        if (ctxRef == null)
            return null;
        return ctxRef.get();
    }

    public void setContext(Context ctx) {
        if (ctxRef != null) {
            ctxRef.clear();
            ctxRef = null;
        }
//        if (topoClient != null)
//            topoClient.setContext(ctx);
        if (ctx == null)
            return;
        ctxRef = new WeakReference<Context>(ctx);
    }

//    public void loadTopology(JsonObjectHandler handler) {
//        topoClient.getTopology(handler);
//    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        if (this.topology != null) {
            if (topology == null) {
                Log.e(TAG, "cannot set topology to null, use reset() instead");
                return;
            }
            if (this.topology.getTopologyVersion() == topology.getTopologyVersion()) {
                return;
            }
        }
        reset();
        this.topology = topology;
    }

//    public Map<String, DeploymentDetails> getDeployDetails() {
//        return deployDetails;
//    }
//
    public void reset() {
        Log.d(TAG, "reset");
        topology = null;
        if (statusCache != null)
            statusCache.clear();
        if (deployDetails != null)
            deployDetails.clear();
        statusCache = null;
        deployDetails = null;
    }

    public void addCachedStatus(final String instId, final int status) {
        if (statusCache == null)
            statusCache = new HashMap<String, Integer>();
        statusCache.put(instId, status);
    }

    public int getCachedStatus(final String instId) {
        if (TextUtils.isEmpty(instId) || statusCache == null)
            return GATEWAY_STATUS_UNKNOWN;
        if (statusCache.containsKey(instId))
            return statusCache.get(instId);
        return GATEWAY_STATUS_UNKNOWN;
    }

    public Host getHostById(String id) {
        if (topology == null)
            return null;
        return topology.getHost(id);
    }

    public Group getGroupById(String id) {
        if (topology == null)
            return null;
        return topology.getGroup(id);
    }

    public Service getGatewayById(String id) {
        if (topology == null)
            return null;
        return topology.getService(id);
    }

    public ArrayList<String> getHostNames() {
        if (hostNames == null) {
            assert topology != null;
            hostNames = new ArrayList<String>();
            for (Host h : topology.getHosts()) {
                hostNames.add(h.getName());
            }
        }
        return hostNames;
    }

    public ArrayList<String> getGroupNames() {
        if (grpNames == null) {
            assert topology != null;
            grpNames = new ArrayList<String>();
            Service anm = getAdminNodeMgr();
            Group anmGrp = topology.getGroupForService(anm.getId());
            for (Group g : topology.getGroups()) {
                if (g.getId().equals(anmGrp.getId()))
                    continue;
                grpNames.add(g.getName());
            }
        }
        return grpNames;
    }

    public void resetHostNames() {
        hostNames = null;
    }

    public void resetGroupNames() {
        grpNames = null;
    }

    public Service getAdminNodeMgr() {
        if (adminNodeMgr == null) {
            adminNodeMgr = findAdminNodeMgr();
        }
        return adminNodeMgr;
    }

    public Service findAdminNodeMgr() {
        Service rv = null;
        if (topology == null)
            return null;
        Collection<Service> svcs = topology.getServices(Topology.ServiceType.nodemanager);
        if (svcs == null || svcs.size() == 0)
            return null;
        for (Service s: svcs) {
            if (Topology.isTaggedAsAdminNodeManager(s)) {
                rv = s;
                break;
            }
        }
        return rv;
    }

    public int groupCount(boolean inclAnm) {
        if (topology == null)
            return 0;
        Collection<Group> grps = topology.getGroups(Topology.ServiceType.gateway);
        return (grps == null ? 0 : grps.size());
    }

    public Group getInstanceGroup(String instId) {
        if (topology == null || TextUtils.isEmpty(instId))
            return null;
        return topology.getGroupForService(instId);
    }

    public void setGatewayStatus(String instId, int newVal) {
        addCachedStatus(instId, newVal);
        notifyStatusObserver(instId, newVal);
    }

    private void notifyStatusObserver(final String instId, final int newStat) {
        if (statusObservers == null)
            return;
        StatusObserver o = statusObservers.get(instId);
        if (o ==  null)
            return;
        o.onStatusChange(instId, newStat);
    }
//
//    public void removeService(Service svc, boolean deleteDisk, JsonObjectHandler handler) {
//        Group g = getInstanceGroup(svc.getId());
//        topoClient.removeService(g, svc, deleteDisk, handler);
//    }
//
//    public void removeGroup(Group g, JsonObjectHandler handler) {
//        topoClient.removeGroup(g, handler);
//    }
//
//    public void startOrStopGateway(Service svc, boolean start, JsonObjectHandler handler) {
//        Group g = getInstanceGroup(svc.getId());
//        topoClient.startOrStopGateway(g, svc, start, handler);
//    }

    public void registerStatusObserver(String instId, StatusObserver observer) {
        if (statusObservers == null)
            statusObservers = new HashMap<>();
        StatusObserver o = statusObservers.get(instId);
        if (o != null)
            statusObservers.remove(instId);
        statusObservers.put(instId, observer);
    }

    public void unregisterStatusObserver(String instId) {
        if (statusObservers == null)
            return;
        StatusObserver o = statusObservers.get(instId);
        if (o == null)
            return;
        statusObservers.remove(instId);
    }

    public static String statusString(int cd) {
        switch(cd) {
            case GATEWAY_STATUS_CHECKING:
                return "Checking";
            case GATEWAY_STATUS_RUNNING:
                return "Started";
            case GATEWAY_STATUS_NOT_RUNNING:
                return "Not started";
        }
        return "Unknown";
    }

    public static int parseStatus(String s) {
        if ("0".equals(s))
            return TopologyModel.GATEWAY_STATUS_RUNNING;
        if ("3".equals(s))
            return TopologyModel.GATEWAY_STATUS_NOT_RUNNING;
        return TopologyModel.GATEWAY_STATUS_UNKNOWN;
    }

    private Request startOrStop(String instId, boolean start, Callback cb) {
        assert client != null;
        String a = (start ? "start" : "stop");
        Group g = getInstanceGroup(instId);
        Request req = client.createRequest(GATEWAY_MGMT_ENDPOINT.replace("{svcId}", instId).replace("{action}", a).replace("{grpId}", g.getId()), "POST", null);
        setGatewayStatus(instId, GATEWAY_STATUS_CHECKING);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request startGateway(String instId, Callback cb) {
        return startOrStop(instId, true, cb);
    }

    public Request stopGateway(String instId, Callback cb) {
        return startOrStop(instId, false, cb);
    }

    public Request getGatewayStatus(String instId, Callback cb){
        assert client != null;
        setGatewayStatus(instId, GATEWAY_STATUS_CHECKING);
        Group g = getInstanceGroup(instId);
        Request req = client.createRequest(GATEWAY_MGMT_ENDPOINT.replace("{grpId}", g.getId()).replace("{svcId}", instId).replace("{action}", "status"));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request removeGateway(String instId, boolean deleteDisk, Callback cb) {
        assert client != null;
        Group g = getInstanceGroup(instId);
        Request req = client.createRequest(String.format("%s/%s/%s?deleteDiskInstance=%s", INSTANCES_ENDPOINT, g.getId(), instId, deleteDisk), "DELETE", null);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request addGateway(Group g, Service svc, int svcsPort, Callback cb) {
        return addGateway(g, svc, svcsPort, null, cb);
    }

    public Request addGateway(Group g, Service svc, int svcsPort, String signAlg, Callback cb) {
        return addGateway(g, svc, svcsPort, signAlg, null, cb);
    }

    public Request addGateway(Group g, Service svc, int svcsPort, String signAlg, String tempPhrase, Callback cb) {
        return addGateway(g, svc, svcsPort, signAlg, tempPhrase, null, cb);
    }

    public Request addGateway(Group g, Service svc, int svcsPort, String signAlg, String tempPhrase, String domainPhrase, Callback cb) {
        return addGateway(g, svc, svcsPort, signAlg, tempPhrase, domainPhrase, null, cb);
    }

    public Request addGateway(Group g, Service svc, int svcsPort, String signAlg, String tempPhrase, String domainPhrase, String p12Path, Callback cb) {
        assert client != null;
        assert g != null;
        assert svc != null;
        JsonObject json = JsonHelper.getInstance().toJson(svc);
        if (json.has("id"))
            json.remove("id");
        File p12File = null;
        if (!TextUtils.isEmpty(p12Path))
            p12File = new File(p12Path);
        String endpoint = String.format("%s/%s?servicesPort=%d", INSTANCES_ENDPOINT, g.getId(), svcsPort);
        if (!TextUtils.isEmpty(signAlg))
            endpoint = endpoint + "&signAlg=" + signAlg;
        JsonObject toPost = new JsonObject();
        toPost.add("service", json);
        if (!TextUtils.isEmpty(domainPhrase))
            toPost.addProperty("domainPassphrase", domainPhrase);
        if (!TextUtils.isEmpty(tempPhrase))
            toPost.addProperty("keyPassphrase", tempPhrase);

        Request req = client.createRequest(endpoint, "POST", toPost);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request updateGateway(Service svc, Callback cb) {
        assert client != null;
        assert svc != null;
        Group g = getInstanceGroup(svc.getId());
        JsonObject json = JsonHelper.getInstance().toJson(svc);
        Request req = client.createRequest(String.format("%s/%s", INSTANCES_ENDPOINT, g.getId()), "PUT", json);
        client.executeAsyncRequest(req, cb);
        return req;
    }
//
//    public void loadGatewayStatus(String instId, JsonObjectHandler handler) {
//        setGatewayStatus(instId, BaseApplication.GATEWAY_STATUS_UNKNOWN);
//        Group g = getInstanceGroup(instId);
//        topoClient.setResponseTimeout(1000 * 60 * 5);
//        topoClient.getGatewayStatus(g.getId(), instId, handler);
//    }
}
