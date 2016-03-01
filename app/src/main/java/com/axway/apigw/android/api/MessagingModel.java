package com.axway.apigw.android.api;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.MqProducer;
import com.axway.apigw.android.model.MqSubscriber;

import java.util.List;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by su on 2/17/2016.
 */
public class MessagingModel extends ApiModel {
    public static final String ENDPOINT_MQ = "api/router/service/{svcId}/api/ama";
    public static final String ENDPOINT_MQ_DESTS = ENDPOINT_MQ + "/{destType}";
    //    public static final String ENDPOINT_MQ_DESTS = ENDPOINT_MQ + "queues";
    public static final String ENDPOINT_MQ_MESSAGES = ENDPOINT_MQ_DESTS + "/{queueName}/messages";
    public static final String ENDPOINT_MQ_CONSUMERS = ENDPOINT_MQ + "/consumers";
    public static final String ENDPOINT_MQ_SUBSCRIBERS = ENDPOINT_MQ + "/subscribers";

    private static MessagingModel instance;
    private List<MqDestination> queues;
    private List<MqDestination> topics;
    private List<MqSubscriber> subscribers;
    private List<MqProducer> producers;

    public static MessagingModel getInstance() {
        if (instance == null)
            instance = new MessagingModel();
        return instance;
    }

    protected MessagingModel() {
        super();
        queues = null;
        topics = null;
        subscribers = null;
        producers = null;
    }

    public Request loadQueues(String instId, Callback cb) {
        assert client != null;
        assert cb != null;
        return mqLoad(instId, "queues", cb);
    }

    public Request loadTopics(String instId, Callback cb) {
        return mqLoad(instId, "topics", cb);
    }

    private Request mqLoad(String instId, String kind, Callback cb) {
        assert client != null;
        assert cb != null;
        String url = ENDPOINT_MQ_DESTS.replace("{svcId}", instId).replace("{destType}", kind);
        Request req = client.createRequest(url);
        client.executeAsyncRequest(req, cb);
        return req;
    }

    public Request loadConsumers(String instId, Callback cb) {
        return mqLoad(instId, "consumers", cb);
    }

    public Request loadSubscribers(String instId, Callback cb) {
        return mqLoad(instId, "subscribers", cb);
    }

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
}
