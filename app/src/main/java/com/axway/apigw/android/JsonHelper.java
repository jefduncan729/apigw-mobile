package com.axway.apigw.android;

import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.model.MqConsumer;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.MqMessage;
import com.axway.apigw.android.model.MqSubscriber;
import com.axway.apigw.android.util.NameValuePair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public Kps kpsFromJson(String jsonStr) {
        JsonElement j = parse(jsonStr);
        if (j == null)
            return null;
        return kpsFromJson(j.getAsJsonObject());
    }

    public Kps kpsFromJson(JsonObject jsonIn) {
        if (jsonIn == null)
            return null;
        Kps rv = new Kps();
        if (jsonIn.has(PROP_VERSION))
            rv.setVersion(jsonIn.get(PROP_VERSION).getAsString());
        if (jsonIn.has(PROP_DESCRIPTION))
            rv.setDescription(jsonIn.get(PROP_DESCRIPTION).getAsString());
        JsonArray array = null;
        if (jsonIn.has(PROP_STORES)) {
            array = jsonIn.getAsJsonArray(PROP_STORES);
            KpsStore store = null;
            for (JsonElement ele: array) {
                store = storeFromJson(ele.getAsJsonObject());
                if (store != null)
                    rv.addStore(store.getIdentity(), store);
            }
            array = null;
        }
        if (jsonIn.has(PROP_TYPES)) {
            array = jsonIn.getAsJsonArray(PROP_TYPES);
            KpsType typ = null;
            for (JsonElement ele: array) {
                typ = typeFromJson(ele.getAsJsonObject());
                if (typ != null)
                    rv.addType(typ.getIdentity(), typ);
            }
            array = null;
        }
        if (jsonIn.has(PROP_ALIASES)) {
            array = jsonIn.getAsJsonArray(PROP_ALIASES);
            for (JsonElement ele: array) {
                JsonObject obj = ele.getAsJsonObject();
                if (obj != null && obj.has(PROP_ALIAS) && obj.has(PROP_IDENTITY)) {
                    rv.addAlias(obj.get(PROP_ALIAS).getAsString(), obj.get(PROP_IDENTITY).getAsString());
                }
            }
            array = null;
        }
        return rv;
    }

    public JsonObject toJson(Kps kps) {
        if (kps == null)
            return null;
        JsonObject rv = new JsonObject();
        JsonArray stores = new JsonArray();
        JsonArray types = new JsonArray();
        JsonArray aliases = new JsonArray();
        Set<String> keys = kps.getStores().keySet();
        for (String k: keys) {
            KpsStore store = kps.getStores().get(k);
            JsonObject j = toJson(store);
            if (j != null)
                stores.add(j);
        }
        keys = kps.getTypes().keySet();
        for (String k: keys) {
            KpsType typ = kps.getTypes().get(k);
            JsonObject j = toJson(typ);
            if (j != null)
                types.add(j);
        }
        keys = kps.getAliases().keySet();
        for (String k: keys) {
            String id = kps.getAliases().get(k);
            JsonObject j = new JsonObject();
            j.addProperty(PROP_ALIAS, k);
            j.addProperty(PROP_IDENTITY, id);
            aliases.add(j);
        }
        rv.addProperty(PROP_VERSION, kps.getVersion());
        rv.addProperty(PROP_DESCRIPTION, kps.getDescription());
        rv.add(PROP_TYPES, types);
        rv.add(PROP_STORES, stores);
        rv.add(PROP_ALIASES, aliases);
        return rv;
    }

    public KpsType typeFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return typeFromJson(e.getAsJsonObject());
    }

    public JsonObject toJson(KpsType kpsType) {
        if (kpsType == null)
            return null;
        JsonObject rv = new JsonObject();
        rv.addProperty(PROP_IDENTITY, kpsType.getIdentity());
        rv.addProperty(PROP_DESCRIPTION, kpsType.getDescription());
        JsonObject jProps = new JsonObject();
        Map<String, String> props = kpsType.getProperties();
        Set<String> keys = props.keySet();
        for (String k: keys) {
            jProps.addProperty(k, props.get(k));
        }
        rv.add(PROP_PROPERTIES, jProps);
        return rv;
    }

    public KpsType typeFromJson(JsonObject json) {
        if (json == null)
            return null;
        KpsType rv = new KpsType();
        if (json.has(PROP_IDENTITY))
            rv.setIdentity(json.get(PROP_IDENTITY).getAsString());
        if (json.has(PROP_DESCRIPTION)) {
            JsonElement je = json.get(PROP_DESCRIPTION);
            if (!je.isJsonNull())
                rv.setDescription(json.get(PROP_DESCRIPTION).getAsString());
        }
        if (json.has(PROP_PROPERTIES)) {
            JsonObject jo = json.get(PROP_PROPERTIES).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> props = jo.entrySet();
            for (Map.Entry<String, JsonElement> e: props) {
                rv.addProperty(e.getKey(), e.getValue().getAsString());
            }
        }
        return rv;
    }

    public KpsStore storeFromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JsonElement e = parse(jsonStr);
        if (e == null)
            return null;
        return storeFromJson(e.getAsJsonObject());
    }

    public KpsStore storeFromJson(JsonObject json) {
        if (json == null)
            return null;
//        Log.d(TAG, "storeFromJson: " + json.toString());
        KpsStore rv = new KpsStore();
        if (json.has(PROP_IDENTITY))
            rv.setIdentity(json.get(PROP_IDENTITY).getAsString());
        if (json.has(PROP_DESCRIPTION) && !json.isJsonNull()) {
            rv.setDescription(json.get(PROP_DESCRIPTION).getAsString());
        }
        if (json.has(PROP_TYPE_ID))
            rv.setTypeId(json.get(PROP_TYPE_ID).getAsString());
        if (json.has(PROP_IMPL_ID))
            rv.setImplId(json.get(PROP_IMPL_ID).getAsString());
        if (json.has(PROP_CONFIG)) {
            KpsStore.KpsStoreConfig cfg = rv.getConfig();
            JsonObject jo = json.get(PROP_CONFIG).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> props = jo.entrySet();
            JsonArray array = null;
            for (Map.Entry<String, JsonElement> e: props) {
                String k = e.getKey();
                JsonElement v = e.getValue();
                if (TextUtils.isEmpty(k) || v == null)
                    continue;
                if (k.equals(PROP_WRITE_CONSISTENCY_LEVEL))
                    cfg.setWriteConsistencyLevel(v.getAsString());
                else if (k.equals(PROP_PACKAGE))
                    cfg.setPackageName(v.getAsString());
                else if (k.equals(PROP_INDEXES)) {
                    array = v.getAsJsonArray();
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            cfg.addIndex(array.get(i).getAsString());
                        }
                    }
                }
                else if (k.equals(PROP_READ_KEY)) {
                    array = v.getAsJsonArray();
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            cfg.addReadKey(array.get(i).getAsString());
                        }
                    }
                }
                else if (k.equals(PROP_INTERNAL))
                    cfg.setInternal(v.getAsBoolean());
                else if (k.equals(PROP_KEY))
                    cfg.setKey(v.getAsString());
                else if (k.equals(PROP_READ_CONSISTENCY_LEVEL))
                    cfg.setReadConsistencyLevel(v.getAsString());
                else if (k.startsWith(PROP_PROCESSOR)) {
                    JsonObject jp = v.getAsJsonObject();
                    KpsStore.KpsStoreProcessor proc = rv.createProcessor();
                    if (jp.has(PROP_KEY_FIELD))
                        proc.setKeyField(jp.get(PROP_KEY_FIELD).getAsString());
                    if (jp.has(PROP_CLASS_NAME))
                        proc.setClassName(jp.get(PROP_CLASS_NAME).getAsString());
                    if (jp.has(PROP_SECURE_PROPERTIES)) {
                        array = jp.get(PROP_SECURE_PROPERTIES).getAsJsonArray();
                        if (array != null) {
                            for (int i = 0; i < array.size(); i++) {
                                proc.addSecureProperty(array.get(i).getAsString());
                            }
                        }
                    }
                    cfg.addProcessor(proc);
                }
            }
        }
        return rv;
    }

    public JsonObject toJson(KpsStore store) {
        if (store == null)
            return null;
        JsonObject rv = new JsonObject();
        rv.addProperty(PROP_IDENTITY, store.getIdentity());
        rv.addProperty(PROP_DESCRIPTION, store.getDescription());
        rv.addProperty(PROP_TYPE_ID, store.getTypeId());
        rv.addProperty(PROP_IMPL_ID, store.getImplId());
        JsonObject jCfg = new JsonObject();
        KpsStore.KpsStoreConfig cfg = store.getConfig();
        jCfg.addProperty(PROP_WRITE_CONSISTENCY_LEVEL, cfg.getWriteConsistencyLevel());
        jCfg.addProperty(PROP_PACKAGE, cfg.getPackageName());
        jCfg.addProperty(PROP_INTERNAL, cfg.isInternal());
        jCfg.addProperty(PROP_KEY, cfg.getKey());
        jCfg.addProperty(PROP_WRITE_CONSISTENCY_LEVEL, cfg.getReadConsistencyLevel());
        JsonArray indexes = new JsonArray();
        List<String> list = cfg.getIndexes();
        for (String s: list) {
            final JsonPrimitive prim = new JsonPrimitive(s);
            indexes.add(prim);
        }
        jCfg.add(PROP_INDEXES, indexes);
        list = cfg.getReadKeys();
        JsonArray readKeys = new JsonArray();
        for (String s: list) {
            final JsonPrimitive prim = new JsonPrimitive(s);
            readKeys.add(prim);
        }
        jCfg.add(PROP_READ_KEY, readKeys);
        int i = 0;
        JsonObject jo = null;
        for (KpsStore.KpsStoreProcessor p: cfg.getProcessors()) {
            jo = new JsonObject();
            jo.addProperty(PROP_CLASS_NAME, p.getClassName());
            if (!TextUtils.isEmpty(p.getKeyField()))
                jo.addProperty(PROP_KEY_FIELD, p.getKeyField());
            list = p.getSecureProps();
            JsonArray sp = new JsonArray();
            for (String s: list) {
                final JsonPrimitive prim = new JsonPrimitive(s);
                sp.add(prim);
            }
            jCfg.add(PROP_SECURE_PROPERTIES, sp);
            String nm = PROP_PROCESSOR + Integer.toString(i);
            jCfg.add(nm, jo);
            ++i;
        }
        rv.add(PROP_CONFIG, jCfg);
//        Log.d(TAG, "kpsStoreToJson: " + rv.toString());
        return rv;
    }

    public String prettyPrint(KpsType kpsType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n    Identity: ").append(kpsType.getIdentity());
        sb.append("\n    Description: ").append(kpsType.getDescription());
        sb.append("\n    Properties: ");
        Set<String> keys = kpsType.getProperties().keySet();
        for (String k: keys) {
            sb.append("\n        k: ").append(kpsType.getProperties().get(k));
        }
        return sb.toString();
    }


    public String prettyPrint(KpsStore kpsStore) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n    Identity: ").append(kpsStore.getIdentity());
        sb.append("\n    Description: ").append(kpsStore.getDescription());
        sb.append("\n    Config: ");
        KpsStore.KpsStoreConfig cfg = kpsStore.getConfig();
        sb.append("\n        writeConsistencyLevel: ").append(cfg.getWriteConsistencyLevel());
        sb.append("\n        package: ").append(cfg.getPackageName());
        sb.append("\n        indexes: ");
        List<String> list = cfg.getIndexes();
        int i = 0;
        for (String s: list) {
            if (i++ > 0)
                sb.append(", ");
            sb.append(s);
        }
        i = 0;
        List<KpsStore.KpsStoreProcessor> procs = cfg.getProcessors();
        for (KpsStore.KpsStoreProcessor p: procs) {
            sb.append("\n        processor").append(i);
            if (!TextUtils.isEmpty(p.getClassName()))
                sb.append("\n            className: ").append(p.getClassName());
            if (!TextUtils.isEmpty(p.getKeyField()))
                sb.append("\n            keyField: ").append(p.getKeyField());
            list = p.getSecureProps();
            if (list.size() > 0) {
                sb.append("\n            secureProps: ");
                i = 0;
                for (String s: list) {
                    if (i++ > 0)
                        sb.append(", ");
                    sb.append(s);
                }

            }
        }
        sb.append("\n        internal: ").append(Boolean.toString(cfg.isInternal()));
        sb.append("\n        key: ").append(cfg.getKey());
        sb.append("\n        readConsistencyLevel: ").append(cfg.getReadConsistencyLevel());
        return sb.toString();
    }

    public String prettyPrint(Kps kps) {
        StringBuilder sb = new StringBuilder();
        if (kps == null) {
            sb.append("(null)");
            return sb.toString();
        }
        Set<String> keys = kps.getStores().keySet();
        sb.append("Stores:");
        for (String k: keys)
            sb.append("\n").append(prettyPrint(kps.getStores().get(k)));
        keys = kps.getTypes().keySet();
        sb.append("\nTypes:");
        for (String k: keys)
            sb.append("\n").append(prettyPrint(kps.getTypes().get(k)));
        sb.append("\nAliases:");
        keys = kps.getAliases().keySet();
        for (String k: keys)
            sb.append("\n    ").append(k).append("=").append(kps.getAliases().get(k));
        return sb.toString();
    }

    public JsonObject toJson(DeploymentDetails dd) {
        if (dd == null)
            return null;
        JsonObject rv = new JsonObject();
        if (dd.getUser() != null)
            rv.addProperty(PROP_USER, dd.getUser());
        rv.addProperty(PROP_DEPLOYMENT_TIMESTAMP, dd.getTimestamp());
        if (dd.getStatus() != null)
            rv.addProperty(PROP_STATUS, dd.getStatus());
        rv.add(PROP_ROOT_PROPERTIES, dd.getRoot()); //writeDdProps(dd.getRootProperties()));
        rv.add(PROP_POLICY_PROPERTIES, dd.getPolicy()); //writeDdProps(dd.getPolicyProperties()));
        rv.add(PROP_ENVIRONMENT_PROPERTIES, dd.getEnv());   //writeDdProps(dd.getEnvironmentProperties()));
        return rv;
    }

    public DeploymentDetails deploymentDetailsFromJson(String jsonStr) {
        JsonObject json = parseAsObject(jsonStr);
        if (json == null)
            return null;
        return deploymentDetailsFromJson(json);
    }

    public DeploymentDetails deploymentDetailsFromJson(JsonObject json) {
        if (json == null)
            return null;
        DeploymentDetails rv = new DeploymentDetails();
        if (json.has(PROP_USER))
            rv.setUser(json.get(PROP_USER).getAsString());
        if (json.has(PROP_DEPLOYMENT_TIMESTAMP))
            rv.setTimestamp(json.get(PROP_DEPLOYMENT_TIMESTAMP).getAsLong());
        if (json.has(PROP_STATUS))
            rv.setStatus(json.get(PROP_STATUS).getAsString());
        DeploymentDetails.Props props = null;
        if (json.has(PROP_ROOT_PROPERTIES)) {
            rv.setRoot(json.get(PROP_ROOT_PROPERTIES).getAsJsonObject());
//            readDdProps(rv.getRootProperties(), json.get(PROP_ROOT_PROPERTIES).getAsJsonObject());
        }
        if (json.has(PROP_POLICY_PROPERTIES)) {
            rv.setPolicy(json.get(PROP_POLICY_PROPERTIES).getAsJsonObject());
//            props = rv.createProps();
//            readDdProps(rv.getPolicyProperties(), json.get(PROP_POLICY_PROPERTIES).getAsJsonObject());
        }
        if (json.has(PROP_ENVIRONMENT_PROPERTIES)) {
            rv.setEnv(json.get(PROP_ENVIRONMENT_PROPERTIES).getAsJsonObject());
//            props = rv.createProps();
//            readDdProps(rv.getEnvironmentProperties(), json.get(PROP_ENVIRONMENT_PROPERTIES).getAsJsonObject());
        }
        return rv;
    }
/*

    private void readDdProps(DeploymentDetails.Props props, JsonObject json) {
        if (json == null || props == null)
            return;
        if (json.has(PROP_MANIFEST_VERSION))
            props.setManifestVersion(json.get(PROP_MANIFEST_VERSION).getAsString());
        if (json.has("Id"))
            props.setId(json.get("Id").getAsString());
        if (json.has("Timestamp"))
            props.setTimestamp(json.get("Timestamp").getAsLong());
        if (json.has("Name"))
            props.setName(json.get("Name").getAsString());
        if (json.has("Description"))
            props.setDescription(json.get("Description").getAsString());
        if (json.has("Version"))
            props.setVersion(json.get("Version").getAsString());
        if (json.has("VersionComment"))
            props.setVersionComment(json.get("VersionComment").getAsString());
        return;
    }

    private JsonObject writeDdProps(DeploymentDetails.Props props) {
        if (props == null)
            return null;
        JsonObject json = new JsonObject();
        if (props.getManifestVersion() != null)
            json.addProperty(PROP_MANIFEST_VERSION, props.getManifestVersion());
        if (props.getId() != null)
            json.addProperty("Id", props.getId());
        if (props.getTimestamp() != 0)
            json.addProperty("Timestamp", props.getTimestamp());
        if (props.getName() != null)
            json.addProperty("Name", props.getName());
        if (props.getDescription() != null)
            json.addProperty("Id", props.getDescription());
        if (props.getVersion() != null)
            json.addProperty("Version", props.getVersion());
        if (props.getVersionComment() != null)
            json.addProperty("VersionComment", props.getVersionComment());
        return json;
    }
*/

    public String prettyPrint(DeploymentDetails dd) {
        StringBuilder sb = new StringBuilder();
        if (dd == null)
            return sb.toString();
        String indent = " ";
        sb.append(indent).append("user: ").append(dd.getUser()).append("\n");
        if (dd.getTimestamp() != 0)
            sb.append(indent).append("deploymentTimestamp: ").append(formatDatetime(dd.getTimestamp())).append("\n");
        sb.append(indent).append("status: ").append(dd.getStatus()).append("\n");

        sb.append("\n").append(indent).append("Root Properties:\n");
        sb.append(prettyPrint(dd.getRoot()));    //dd.getRootProperties()));

        sb.append("\n").append(indent).append("Policy Properties:\n");
        sb.append(prettyPrint(dd.getPolicy()));  //Properties()));

        sb.append("\n").append(indent).append("Environment Properties:\n");
        sb.append(prettyPrint(dd.getEnv())); //ironmentProperties()));
        return sb.toString();
    }

    public String prettyPrint(JsonObject j) {
        StringBuilder sb = new StringBuilder();
        if (j == null)
            return sb.toString();

        Set<Map.Entry<String, JsonElement>> set = j.entrySet();
        for (Map.Entry<String, JsonElement> e: set) {
            String k = e.getKey();
            if (k.startsWith("Challenge") || k.startsWith("ChangedBy"))
                continue;
            JsonElement je = e.getValue();
            if (je.isJsonPrimitive()) {
                sb.append("\n").append(k).append(": ").append(je.getAsJsonPrimitive().getAsString());
            }
            else if (je.isJsonObject()) {
                sb.append("\n").append(k).append(prettyPrint(je.getAsJsonObject()));
            }
            else if (je.isJsonArray()) {
                sb.append("\n").append(k).append(je.getAsJsonArray());
            }
        }
        return sb.toString();
    }

    public List<NameValuePair> toNameValuePairs(JsonObject j) {
        List<NameValuePair> rv = new ArrayList<>();
        if (j == null)
            return rv;
        boolean sysProps = j.has(JsonHelper.PROP_MANIFEST_VERSION);
        Set<Map.Entry<String, JsonElement>> set = j.entrySet();
        for (Map.Entry<String, JsonElement> e: set) {
            String k = e.getKey();
            if (sysProps && (k.startsWith("Challenge") || k.startsWith("ChangedBy")))
                continue;
            JsonElement je = e.getValue();
            if (je.isJsonPrimitive()) {
                rv.add(new NameValuePair(k, je.getAsJsonPrimitive().getAsString()));
            }
        }
        return rv;
    }

    public String prettyPrint(DeploymentDetails.Props props) {
        StringBuilder sb = new StringBuilder();
        if (props == null)
            return sb.toString();
        String indent = "  ";
        if (props.getManifestVersion() != null)
            sb.append(indent).append("Manifest-Version: ").append(props.getManifestVersion()).append("\n");
        if (props.getId() != null)
            sb.append(indent).append("Id: ").append(props.getId()).append("\n");
        if (props.getTimestamp() != 0)
            sb.append(indent).append("Timestamp: ").append(formatDatetime(props.getTimestamp())).append("\n");
        if (props.getName() != null)
            sb.append(indent).append("Name: ").append(props.getName()).append("\n");
        if (props.getDescription() != null)
            sb.append(indent).append("Description: ").append(props.getDescription()).append("\n");
        if (props.getVersion() != null)
            sb.append(indent).append("Version: ").append(props.getVersion()).append("\n");
        if (props.getVersionComment() != null)
            sb.append(indent).append("VersionComment: ").append(props.getVersionComment()).append("\n");
        return sb.toString();
    }

    public MqDestination destinationFromJson(String str) {
        return destinationFromJson(parseAsObject(str));
    }

    public MqDestination destinationFromJson(JsonElement json) {
        if (json == null)
            return null;
        if (!json.isJsonObject())
            return null;
        return destinationFromJson(json.getAsJsonObject());
    }

    public MqDestination destinationFromJson(JsonObject json) {
        if (json == null)
            return null;
        MqDestination rv = new MqDestination();
        rv.setName(json.get(PROP_QUEUE_NAME).getAsString());
        rv.setType(json.get(PROP_QUEUE_TYPE).getAsString());
        rv.setMsgCount(json.get(PROP_MESSAGE_COUNT).getAsLong());
        rv.setEnqCount(json.get(PROP_ENQUEUES_COUNT).getAsLong());
        rv.setDeqCount(json.get(PROP_DEQUEUES_COUNT).getAsLong());
        rv.setConCount(json.get(PROP_CONSUMERS_COUNT).getAsLong());
        rv.setProCount(json.get(PROP_PRODUCERS_COUNT).getAsLong());
        rv.setCacheCount(json.get(PROP_MESSAGES_CACHED_COUNT).getAsLong());
        rv.setDispCount(json.get(PROP_DISPATCHED_COUNT).getAsLong());
        rv.setInfCount(json.get(PROP_INFLIGHT_COUNT).getAsLong());
        rv.setExpCount(json.get(PROP_EXPIRED_COUNT).getAsLong());
        return rv;
    }

    public JsonObject toJson(final MqDestination dest) {
        if (dest == null)
            return null;
        JsonObject json = new JsonObject();
        json.addProperty(PROP_QUEUE_NAME, dest.getName());
        json.addProperty(PROP_QUEUE_TYPE, dest.getType());
        json.addProperty(PROP_MESSAGE_COUNT, dest.getMsgCount());
        json.addProperty(PROP_ENQUEUES_COUNT, dest.getEnqCount());
        json.addProperty(PROP_DEQUEUES_COUNT, dest.getDeqCount());
        json.addProperty(PROP_CONSUMERS_COUNT, dest.getConCount());
        json.addProperty(PROP_PRODUCERS_COUNT, dest.getProCount());
        json.addProperty(PROP_MESSAGES_CACHED_COUNT, dest.getCacheCount());
        json.addProperty(PROP_DISPATCHED_COUNT, dest.getDispCount());
        json.addProperty(PROP_INFLIGHT_COUNT, dest.getInfCount());
        json.addProperty(PROP_EXPIRED_COUNT, dest.getExpCount());
        return json;
    }
/*
{"messages":[{"messageId":"PROP_ID:ITEM-A25394-53257-1428082483139-3:1:1:1:1","messageType":"text","messageSize":1052,"messageTimestamp":1428091955478}]
     */

    public MqMessage mqMessageFromJson(final JsonElement json) {
        if (json == null)
            return null;
        if (!json.isJsonObject())
            return null;
        return mqMessageFromJson(json.getAsJsonObject());
    }

    public MqMessage mqMessageFromJson(final JsonObject json) {
        if (json == null)
            return null;
        MqMessage rv = new MqMessage();
        rv.setMsgId(json.get(PROP_MESSAGE_ID).getAsString());
        rv.setMsgType(json.get(PROP_MESSAGE_TYPE).getAsString());
        rv.setMsgSize(json.get(PROP_MESSAGE_SIZE).getAsLong());
        rv.setMsgTime(json.get(PROP_MESSAGE_TIMESTAMP).getAsLong());
        return rv;
    }

    public JsonObject toJson(final MqMessage msg) {
        if (msg == null)
            return null;
        JsonObject json = new JsonObject();
        json.addProperty(PROP_MESSAGE_ID, msg.getMsgId());
        json.addProperty(PROP_MESSAGE_TYPE, msg.getMsgType());
        json.addProperty(PROP_MESSAGE_SIZE, msg.getMsgSize());
        json.addProperty(PROP_MESSAGE_TIMESTAMP, msg.getMsgTime());
        return json;
    }

    public MqSubscriber mqSubscriberFromJson(final JsonElement json) {
        if (json == null)
            return null;
        if (!json.isJsonObject())
            return null;
        return mqSubscriberFromJson(json.getAsJsonObject());
    }

    public MqSubscriber mqSubscriberFromJson(final JsonObject json) {
        if (json == null)
            return null;
        MqSubscriber rv = new MqSubscriber();
        if (json.has(PROP_CLIENT_ID))
            rv.setClientId(json.get(PROP_CLIENT_ID).getAsString());
        if (json.has(PROP_DESTINATION))
            rv.setDestination(json.get(PROP_DESTINATION).getAsString());
        if (json.has(PROP_SUBSCRIBER_NAME))
            rv.setName(json.get(PROP_SUBSCRIBER_NAME).getAsString());
        if (json.has(PROP_ACTIVE))
            rv.setActive(json.get(PROP_ACTIVE).getAsBoolean());
        return rv;
    }

    public JsonObject toJson(final MqSubscriber sub) {
        if (sub == null)
            return null;
        JsonObject json = new JsonObject();
        json.addProperty(PROP_CLIENT_ID, sub.getClientId());
        json.addProperty(PROP_SUBSCRIBER_NAME, sub.getName());
        json.addProperty(PROP_DESTINATION, sub.getDestination());
        json.addProperty(PROP_ACTIVE, sub.isActive());
        return json;
    }

    public MqConsumer mqConsumerFromJson(final JsonElement json) {
        if (json == null || !json.isJsonObject())
            return null;
        return mqConsumerFromJson(json.getAsJsonObject());
    }

    public MqConsumer mqConsumerFromJson(final JsonObject json) {
        if (json == null)
            return null;
        MqConsumer rv = new MqConsumer();
        rv.setDestType(json.get(PROP_DESTINATION_TYPE).getAsString());
        rv.setDestName(json.get(PROP_DESTINATION_NAME).getAsString());
        rv.setClientId(json.get(PROP_CLIENT_ID).getAsString());
        rv.setConnId(json.get(PROP_CONNECTION_ID).getAsString());
        rv.setSessionId(json.get(PROP_SESSION_ID).getAsLong());
        rv.setEnqueues(json.get(PROP_ENQUEUES).getAsLong());
        rv.setDequeues(json.get(PROP_DEQUEUES).getAsLong());
        rv.setDispatched(json.get(PROP_DISPATCHED).getAsLong());
        rv.setDispatchedQueueSize(json.get(PROP_DISPATCHED_QUEUE_SIZE).getAsLong());
        rv.setPrefetch(json.get(PROP_PREFETCH).getAsLong());
        rv.setMaxPending(json.get(PROP_MAX_PENDING).getAsLong());
        rv.setExclusive(json.get(PROP_EXCLUSIVE).getAsBoolean());
        rv.setRetroactive(json.get(PROP_RETROACTIVE).getAsBoolean());
        JsonElement je = json.get(PROP_SELECTOR);
        if (!je.isJsonNull())
            rv.setSelector(json.get(PROP_SELECTOR).getAsString());

        return rv;
    }

    public JsonObject toJson(final MqConsumer con) {
        if (con == null)
            return null;
        JsonObject json = new JsonObject();
        json.addProperty(PROP_DESTINATION_TYPE, con.getDestType());
        json.addProperty(PROP_DESTINATION_NAME, con.getDestName());
        json.addProperty(PROP_CLIENT_ID, con.getClientId());
        json.addProperty(PROP_CONNECTION_ID, con.getConnId());
        json.addProperty(PROP_SESSION_ID, con.getSessionId());
        json.addProperty(PROP_ENQUEUES, con.getEnqueues());
        json.addProperty(PROP_DEQUEUES, con.getDequeues());
        json.addProperty(PROP_DISPATCHED, con.getDispatched());
        json.addProperty(PROP_DISPATCHED_QUEUE_SIZE, con.getDispatchedQueueSize());
        json.addProperty(PROP_PREFETCH, con.getPrefetch());
        json.addProperty(PROP_EXCLUSIVE, con.isExclusive());
        json.addProperty(PROP_RETROACTIVE, con.isRetroactive());
        json.addProperty(PROP_SELECTOR, con.getSelector());
        return json;
    }
}
