package com.axway.apigw.android;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by su on 1/25/2016.
 */
public class JsonHelper {
    private static final String TAG = JsonHelper.class.getSimpleName();
    private static final String DATE_FMT = "MMM d, yyyy";
    private static final String TIME_SHORT_FMT = "h:mm a";
    private static final String TIME_LONG_FMT = "hh:mm:ss a";
    private static final String DATE_TIME_FMT = DATE_FMT + " " + TIME_SHORT_FMT;

    public static final String PROP_CLIENT_ID = "clientId";
    public static final String PROP_SUBSCRIBER_NAME = "subscriberName";
    public static final String PROP_DESTINATION = "destination";
    public static final String PROP_ACTIVE = "active";
    public static final String PROP_MESSAGE_ID = "messageId";
    public static final String PROP_MESSAGE_TYPE = "messageType";
    public static final String PROP_MESSAGE_SIZE = "messageSize";
    public static final String PROP_MESSAGE_TIMESTAMP = "messageTimestamp";
    public static final String PROP_QUEUE_NAME = "queueName";
    public static final String PROP_QUEUE_TYPE = "queueType";
    public static final String PROP_MESSAGE_COUNT = "messageCount";
    public static final String PROP_ENQUEUES_COUNT = "enqueuesCount";
    public static final String PROP_DEQUEUES_COUNT = "dequeuesCount";
    public static final String PROP_CONSUMERS_COUNT = "consumersCount";
    public static final String PROP_PRODUCERS_COUNT = "producersCount";
    public static final String PROP_MESSAGES_CACHED_COUNT = "messagesCachedCount";
    public static final String PROP_DISPATCHED_COUNT = "dispatchedCount";
    public static final String PROP_INFLIGHT_COUNT = "inflightCount";
    public static final String PROP_EXPIRED_COUNT = "expiredCount";
    public static final String PROP_ID = "id";
    public static final String PROP_PRODUCT_VERSION = "productVersion";
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_VERSION = "version";
    public static final String PROP_GROUPS = "groups";
    public static final String PROP_HOSTS = "hosts";
    public static final String PROP_UNIQUE_ID_COUNTERS = "uniqueIdCounters";
    public static final String PROP_NAME = "name";
    public static final String PROP_SERVICES = "services";
    public static final String PROP_TAGS = "tags";
    public static final String PROP_ENABLED = "enabled";
    public static final String PROP_HOST_ID = "hostID";
    public static final String PROP_MANAGEMENT_PORT = "managementPort";
    public static final String PROP_SCHEME = "scheme";
    public static final String PROP_TYPE = "type";
    public static final String PROP_RESULT = "result";
    public static final String PROP_ERRORS = "errors";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_STORES = "stores";
    public static final String PROP_TYPES = "types";
    public static final String PROP_ALIASES = "aliases";
    public static final String PROP_ALIAS = "alias";
    public static final String PROP_IDENTITY = "identity";
    public static final String PROP_PROPERTIES = "properties";
    public static final String PROP_TYPE_ID = "typeId";
    public static final String PROP_IMPL_ID = "implId";
    public static final String PROP_CONFIG = "config";
    public static final String PROP_WRITE_CONSISTENCY_LEVEL = "writeConsistencyLevel";
    public static final String PROP_PACKAGE = "package";
    public static final String PROP_INDEXES = "indexes";
    public static final String PROP_READ_KEY = "readKey";
    public static final String PROP_INTERNAL = "internal";
    public static final String PROP_KEY = "key";
    public static final String PROP_READ_CONSISTENCY_LEVEL = "readConsistencyLevel";
    public static final String PROP_PROCESSOR = "processor";
    public static final String PROP_KEY_FIELD = "keyField";
    public static final String PROP_CLASS_NAME = "className";
    public static final String PROP_SECURE_PROPERTIES = "secureProperties";
    public static final String PROP_USER = "user";
    public static final String PROP_STATUS = "status";
    public static final String PROP_ROOT_PROPERTIES = "rootProperties";
    public static final String PROP_POLICY_PROPERTIES = "policyProperties";
    public static final String PROP_ENVIRONMENT_PROPERTIES = "environmentProperties";
    public static final String PROP_DEPLOYMENT_TIMESTAMP = "deploymentTimestamp";
    public static final String PROP_MANIFEST_VERSION = "Manifest-Version";
    public static final String PROP_DESTINATION_TYPE = "destinationType";
    public static final String PROP_DESTINATION_NAME = "destinationName";
    public static final String PROP_CONNECTION_ID = "connectionId";
    public static final String PROP_ENQUEUES = "enqueues";
    public static final String PROP_DEQUEUES = "dequeues";
    public static final String PROP_DISPATCHED = "dispatched";
    public static final String PROP_DISPATCHED_QUEUE_SIZE = "dispatchedQueueSize";
    public static final String PROP_PREFETCH = "prefetch";
    public static final String PROP_MAX_PENDING = "maxPending";
    public static final String PROP_EXCLUSIVE = "exclusive";
    public static final String PROP_RETROACTIVE = "retroactive";
    public static final String PROP_SELECTOR = "selector";
    public static final String PROP_SESSION_ID = "sessionId";

    public static DateFormat DATE_TIME_FORMAT = new java.text.SimpleDateFormat(DATE_TIME_FMT, Locale.US);
    public static DateFormat DATE_ONLY_FORMAT = new java.text.SimpleDateFormat(DATE_FMT, Locale.US);
    public static DateFormat TIME_ONLY_FORMAT = new java.text.SimpleDateFormat(TIME_SHORT_FMT, Locale.US);
    public static DateFormat TIME_LONG_FORMAT = new java.text.SimpleDateFormat(TIME_LONG_FMT, Locale.US);

    private static JsonHelper inst = null;
    private JsonParser parser;

    private JsonHelper() {
        super();
        parser = null;
    }

    public static JsonHelper getInstance() {
        if (inst == null) {
            inst = new JsonHelper();
        }
        return inst;
    }

    public JsonParser getJsonParser() {
        if (parser == null)
            parser = new JsonParser();
        return parser;
    }

    public String formatDatetime(long time) {
        Date d = new Date(time);
        String rv = DATE_TIME_FORMAT.format(d);
        return rv;
    }

    public String formatDate(long time) {
        Date d = new Date(time);
        String rv = DATE_ONLY_FORMAT.format(d);
        return rv;
    }

    public String formatTime(long time) {
        Date d = new Date(time);
        String rv = TIME_ONLY_FORMAT.format(d);
        return rv;
    }

    public JsonElement parse(String json) {
        JsonElement rv = null;
        if (!TextUtils.isEmpty(json))
            try {
                rv = getJsonParser().parse(json);
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "malformed json: " + json, e);
            }
        return rv;
    }

    public JsonObject parseAsObject(String json) {
        JsonObject rv = null;
        JsonElement e = parse(json);
        if (e != null && e.isJsonObject())
            rv = e.getAsJsonObject();
        return rv;
    }

    public JsonArray parseAsArray(String json) {
        JsonArray rv = null;
        JsonElement e = parse(json);
        if (e != null && e.isJsonArray())
            rv = e.getAsJsonArray();
        return rv;
    }
    public Topology topologyFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return topologyFromJson(e.getAsJsonObject());
    }

    public Topology topologyFromJson(JsonElement json) {
        if (json == null)
            return null;
        return topologyFromJson(json.getAsJsonObject());
    }

    public Topology topologyFromJson(JsonObject json) {
        Topology rv = null;
        if (json == null)
            return rv;
        rv = new Topology();
        if (json.has(PROP_ID))
            rv.setId(json.get(PROP_ID).getAsString());
        if (json.has(PROP_PRODUCT_VERSION))
            rv.setProductVersion(json.get(PROP_PRODUCT_VERSION).getAsString());
        if (json.has(PROP_TIMESTAMP))
            rv.setTimestamp(json.get(PROP_TIMESTAMP).getAsLong());
        if (json.has(PROP_VERSION))
            rv.setVersion(json.get(PROP_VERSION).getAsInt());
        if (json.has(PROP_GROUPS)) {
            JsonArray grps = json.getAsJsonArray(PROP_GROUPS);
            if (grps != null && grps.size() > 0) {
                for (int i = 0; i < grps.size(); i++) {
                    Group g = groupFromJson(grps.get(i).getAsJsonObject());
                    if (g != null)
                        rv.addGroup(g);
                }
            }
        }
        if (json.has(PROP_HOSTS)) {
            JsonArray hosts = json.getAsJsonArray(PROP_HOSTS);
            if (hosts != null && hosts.size() > 0) {
                for (int i = 0; i < hosts.size(); i++) {
                    Host h = hostFromJson(hosts.get(i).getAsJsonObject());
                    if (h != null)
                        rv.addHost(h);
                }
            }
        }
        if (json.has(PROP_UNIQUE_ID_COUNTERS)) {
            Map<Topology.EntityType, Integer> ctrs = jsonToCounters(json.getAsJsonObject(PROP_UNIQUE_ID_COUNTERS));
            if (ctrs != null)
                rv.setUniqueIdCounters(ctrs);
        }
        return rv;
    }

    private JsonObject countersToJson(Map<Topology.EntityType, Integer> ctrs) {
        if (ctrs == null)
            return null;
        JsonObject rv = new JsonObject();
        Set<Topology.EntityType> keys = ctrs.keySet();
        for (Topology.EntityType k: keys)
            rv.addProperty(k.name(), ctrs.get(k));
        return rv;
    }

    private Map<Topology.EntityType, Integer> jsonToCounters(JsonObject json) {
        Map<Topology.EntityType, Integer> rv = new HashMap<Topology.EntityType, Integer>();
        if (json == null)
            return rv;
        Set<Map.Entry<String, JsonElement>> ctrs = json.entrySet();
        if (ctrs == null || ctrs.size() == 0)
            return rv;
        for (Map.Entry<String, JsonElement> e: ctrs) {
            rv.put(Topology.EntityType.valueOf(e.getKey()), e.getValue().getAsInt());
        }
        return rv;
    }

    public JsonObject toJson(Topology t) {
        JsonObject rv = null;
        if (t == null)
            return rv;
        rv = new JsonObject();
        rv.addProperty(PROP_ID, t.getId());
        rv.addProperty(PROP_PRODUCT_VERSION, t.getProductVersion());
        rv.addProperty(PROP_TIMESTAMP, t.getTimestamp());
        rv.addProperty(PROP_VERSION, t.getVersion());
        if (t.getGroups() != null) {
            JsonArray grps = new JsonArray();
            for (Group g: t.getGroups()) {
                grps.add(toJson(g));
            }
            rv.add(PROP_GROUPS, grps);
        }
        if (t.getGroups() != null) {
            JsonArray hosts = new JsonArray();
            for (Host h: t.getHosts()) {
                hosts.add(toJson(h));
            }
            rv.add(PROP_HOSTS, hosts);
        }
        if (t.getUniqueIdCounters() != null)
            rv.add(PROP_UNIQUE_ID_COUNTERS, countersToJson(t.getUniqueIdCounters()));
        return rv;
    }

    public Group groupFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return groupFromJson(e.getAsJsonObject());
    }

    public Group groupFromJson(JsonObject json) {
        Group rv = null;
        if (json == null)
            return rv;
        rv = new Group();
        if (json.has(PROP_ID)) {
            String s = json.get(PROP_ID).getAsString();
            if (!TextUtils.isEmpty(s))
                rv.setId(s);
        }
        if (json.has(PROP_NAME))
            rv.setName(json.get(PROP_NAME).getAsString());
        if (json.has(PROP_SERVICES)) {
            JsonArray svcs = json.getAsJsonArray(PROP_SERVICES);
            if (svcs != null && svcs.size() > 0) {
                for (int i = 0; i < svcs.size(); i++) {
                    Service s = serviceFromJson(svcs.get(i).getAsJsonObject());
                    if (s != null)
                        rv.addService(s);
                }
            }
        }
        if (json.has(PROP_TAGS))
            rv.setTags(jsonToTags(json.getAsJsonObject(PROP_TAGS)));
        return rv;
    }

    public JsonObject toJson(Group g) {
        if (g == null)
            return null;
        JsonObject rv = new JsonObject();
        rv.addProperty(PROP_ID, g.getId() == null ? "" : g.getId());
        rv.addProperty(PROP_NAME, g.getName() == null ? "" : g.getName());
        if (g.getServices() != null) {
            JsonArray svcs = new JsonArray();
            for (Service s: g.getServices()) {
                svcs.add(toJson(s));
            }
            rv.add(PROP_SERVICES, svcs);
        }
        rv.add(PROP_TAGS, tagsToJson(g.getTags()));
        return rv;
    }

    public Service serviceFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return serviceFromJson(e.getAsJsonObject());
    }

    public Service serviceFromJson(JsonObject json) {
        Service rv = null;
        if (json == null)
            return rv;
        rv = new Service();
        if (json.has(PROP_ID)) {
            String s = json.get(PROP_ID).getAsString();
            if (!TextUtils.isEmpty(s))
                rv.setId(s);
        }
        if (json.has(PROP_NAME))
            rv.setName(json.get(PROP_NAME).getAsString());
        if (json.has(PROP_ENABLED))
            rv.setEnabled(json.get(PROP_ENABLED).getAsBoolean());
        if (json.has(PROP_HOST_ID))
            rv.setHostID(json.get(PROP_HOST_ID).getAsString());
        if (json.has(PROP_MANAGEMENT_PORT))
            rv.setManagementPort(json.get(PROP_MANAGEMENT_PORT).getAsInt());
        if (json.has(PROP_SCHEME))
            rv.setScheme(json.get(PROP_SCHEME).getAsString());
        if (json.has(PROP_TYPE))
            rv.setType(Topology.ServiceType.valueOf(json.get(PROP_TYPE).getAsString()));
        if (json.has(PROP_TAGS))
            rv.setTags(jsonToTags(json.getAsJsonObject(PROP_TAGS)));
        return rv;
    }

    public JsonObject toJson(Service s) {
        JsonObject rv = null;
        if (s == null)
            return rv;
        rv = new JsonObject();
        rv.addProperty(PROP_ID, s.getId() == null ? "" : s.getId());
        rv.addProperty(PROP_NAME, s.getName() == null ? "" : s.getName());
        rv.addProperty(PROP_ENABLED, s.getEnabled());
        rv.addProperty(PROP_HOST_ID, s.getHostID() == null ? "" : s.getHostID());
        rv.addProperty(PROP_MANAGEMENT_PORT, s.getManagementPort());
        rv.addProperty(PROP_SCHEME, s.getScheme() == null ? "http" : s.getScheme());
        rv.addProperty(PROP_TYPE, s.getType().toString());
        rv.add(PROP_TAGS, tagsToJson(s.getTags()));
        return rv;
    }

    private JsonObject tagsToJson(Map<String, String> tags) {
        if (tags == null)
            return null;
        JsonObject rv = new JsonObject();
        Set<String> keys = tags.keySet();
        for (String k: keys)
            rv.addProperty(k, tags.get(k));
        return rv;
    }

    private Map<String, String> jsonToTags(JsonObject json) {
        Map<String, String> rv = new HashMap<String, String>();
        if (json == null)
            return rv;
        Set<Map.Entry<String, JsonElement>> tags = json.entrySet();
        if (tags == null || tags.size() == 0)
            return rv;
        for (Map.Entry<String, JsonElement> e: tags) {
            rv.put(e.getKey(), e.getValue().getAsString());
        }
        return rv;
    }

    public Host hostFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return hostFromJson(e.getAsJsonObject());
    }

    public Host hostFromJson(JsonObject json) {
        Host rv = null;
        if (json == null)
            return rv;
        rv = new Host();
        if (json.has(PROP_ID)) {
            String s = json.get(PROP_ID).getAsString();
            if (!TextUtils.isEmpty(s))
                rv.setId(s);
        }
        if (json.has(PROP_NAME))
            rv.setName(json.get(PROP_NAME).getAsString());
        return rv;
    }

    public JsonObject toJson(Host host) {
        if (host == null)
            return null;
        JsonObject rv = new JsonObject();
        rv.addProperty(PROP_ID, host.getId() == null ? "" : host.getId());
        rv.addProperty(PROP_NAME, host.getName() == null ? "" : host.getName());
        return rv;
    }

    public String endpointFor(Object o) {
        String rv = null;
        if (o instanceof Topology) {
            rv = "topology/";
        }
        else if (o instanceof Group) {
            rv = "topology/groups/";
            rv += ((Group)o).getId();
        }
        else if (o instanceof Host) {
            rv = "topology/hosts/";
            rv += ((Host)o).getId();
        }
        else if (o instanceof Service) {
            rv = "topology/services/";
        }
        return rv;
    }

    public String prettyPrint(Topology topology) {
        StringBuilder sb = new StringBuilder();
        if (topology == null)
            return sb.toString();
        sb.append("\nPROP_ID: ").append(topology.getId());
        sb.append("\nproductVersion: ").append(topology.getProductVersion());
        sb.append("\ntimestamp: ").append(topology.getTimestamp());
        sb.append("\nversion: ").append(topology.getVersion());
        Collection<Host> hosts = topology.getHosts();
        if (hosts != null && hosts.size() > 0) {
            sb.append("\n\nHosts: ");
            for (Host h: hosts) {
                sb.append("\n    PROP_ID: ").append(h.getId());
                sb.append("\n    name: ").append(h.getName());
            }
        }
        Collection<Group> grps = topology.getGroups();
        if (grps != null && grps.size() > 0) {
            sb.append("\n\nGroups: ");
            for (Group g: grps) {
                sb.append("\n    PROP_ID: ").append(g.getId());
                sb.append("\n    name: ").append(g.getName());
                Collection<Service> svcs = g.getServices();
                if (svcs != null && svcs.size() > 0) {
                    sb.append("\n    Services: ");
                    for (Service s: svcs) {
                        sb.append("\n        PROP_ID: ").append(s.getId());
                        sb.append("\n        name: ").append(s.getName());
                        sb.append("\n        hostID: ").append(s.getHostID());
                        sb.append("\n        mgmtPort: ").append(s.getManagementPort());
                        sb.append("\n        scheme: ").append(s.getScheme());
                        sb.append("\n        enabled: ").append(s.getEnabled());
                        sb.append("\n        type: ").append(s.getType());
                    }
                }
            }
        }
        return sb.toString();
    }
}
