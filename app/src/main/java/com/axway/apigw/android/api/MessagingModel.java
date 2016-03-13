package com.axway.apigw.android.api;

import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.MqProducer;
import com.axway.apigw.android.model.MqSubscriber;
import com.google.gson.JsonArray;

import java.util.List;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by su on 2/17/2016.
 */
public class MessagingModel extends ApiModel {
    private static final String TAG = MessagingModel.class.getSimpleName();

    public static final String ENDPOINT_MQ = "api/router/service/%s/api/ama";
    public static final String ENDPOINT_MQ_DESTS = ENDPOINT_MQ + "/%s";
    //    public static final String ENDPOINT_MQ_DESTS = ENDPOINT_MQ + "queues";
    public static final String ENDPOINT_MQ_MESSAGES = ENDPOINT_MQ_DESTS + "/%s/messages";
    public static final String ENDPOINT_MQ_CONSUMERS = ENDPOINT_MQ + "/consumers";
    public static final String ENDPOINT_MQ_SUBSCRIBERS = ENDPOINT_MQ + "/subscribers";

    public static final int TYPE_QUEUE = 1;
    public static final int TYPE_TOPIC = 2;
    public static final int TYPE_CONSUMER = 3;
    public static final int TYPE_SUBSCRIBER = 4;

    private static MessagingModel instance;
//    private List<MqDestination> queues;
//    private List<MqDestination> topics;
//    private List<MqSubscriber> subscribers;
//    private List<MqProducer> producers;
    private JsonArray queues;
    private JsonArray topics;
    private JsonArray consumers;
    private JsonArray subscribers;

    public static MessagingModel getInstance() {
        if (instance == null)
            instance = new MessagingModel();
        return instance;
    }

    protected MessagingModel() {
        super();
        reset();
    }

    public void reset() {
        Log.d(TAG, "reset");
        queues = null;
        topics = null;
        subscribers = null;
        consumers = null;
    }

    public Request loadQueues(String instId, Callback cb) {
        queues = null;
        return mqLoad(instId, TYPE_QUEUE, cb);
    }

    public Request loadTopics(String instId, Callback cb) {
        topics = null;
        return mqLoad(instId, TYPE_TOPIC, cb);
    }

    public Request loadMessages(String instId, String queueName, int kind, Callback cb) {
        assert client != null;
        assert cb != null;
        Request req = client.createRequest(String.format(MessagingModel.ENDPOINT_MQ_MESSAGES, instId, endpointFor(kind), queueName));
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public String endpointFor(int kind) {
        switch (kind) {
            case TYPE_CONSUMER:
                return "consumers";
            case TYPE_SUBSCRIBER:
                return "subscribers";
            case TYPE_QUEUE:
                return "queues";
            case TYPE_TOPIC:
                return "topics";
        }
        return null;
    }

    private Request mqLoad(String instId, int kind, Callback cb) {
        assert client != null;
        assert cb != null;
        String url = String.format(ENDPOINT_MQ_DESTS, instId, endpointFor(kind));
        Request req = client.createRequest(url);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request loadConsumers(String instId, Callback cb) {
        consumers = null;
        return mqLoad(instId, TYPE_CONSUMER, cb);
    }

    public Request loadSubscribers(String instId, Callback cb) {
        subscribers = null;
        return mqLoad(instId, TYPE_SUBSCRIBER, cb);
    }

/*
    public List<MqDestination> getQueues() {
        return queues;
    }

    public List<MqDestination> getTopics() {
        return topics;
    }

    public List<MqSubscriber> getSubscribers() {
        return subscribers;
    }

    public List<MqProducer> getProducers() {
        return producers;
    }
*/

    public Request addDestination(String instId, int kind, String name, Callback cb) {
//        if (!kind.endsWith("s"))
//            kind = kind + "s";
        String url = String.format("%s/%s", String.format(MessagingModel.ENDPOINT_MQ_DESTS, instId, endpointFor(kind)), name);
        Request req = client.createRequest(url, "PUT", null);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request removeDestination(String instId, int kind, String name, Callback cb) {
//        if (!kind.endsWith("s"))
//            kind = kind + "s";
        String url = String.format("%s/%s", String.format(MessagingModel.ENDPOINT_MQ_DESTS, instId, endpointFor(kind)), name);
        Request req = client.createRequest(url, "DELETE", null);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request purgeDestination(String instId, int kind, String name, Callback cb) {
//        if (!kind.endsWith("s"))
//            kind = kind + "s";
        String url = String.format("%s/%s/purge", String.format(MessagingModel.ENDPOINT_MQ_DESTS, instId, endpointFor(kind)), name);
        Request req = client.createRequest(url, "POST", null);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public JsonArray getQueues() {
        return queues;
    }

    public JsonArray getTopics() {
        return topics;
    }

    public JsonArray getConsumers() {
        return consumers;
    }

    public JsonArray getSubscribers() {
        return subscribers;
    }

    public void setQueues(JsonArray queues) {
        this.queues = queues;
    }

    public void setTopics(JsonArray topics) {
        this.topics = topics;
    }

    public void setConsumers(JsonArray consumers) {
        this.consumers = consumers;
    }

    public void setSubscribers(JsonArray subscribers) {
        this.subscribers = subscribers;
    }

    public boolean hasLoaded() {
        return (queues != null && topics != null && consumers != null && subscribers != null);
    }
}
