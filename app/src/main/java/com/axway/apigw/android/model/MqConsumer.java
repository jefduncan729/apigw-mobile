package com.axway.apigw.android.model;

/**
 * Created by su on 4/4/2015.
 */
public class MqConsumer {

    private String destType;
    private String destName;
    private String clientId;
    private String connId;
    private long sessionId;
    private long enqueues;
    private long dequeues;
    private long dispatched;
    private long dispatchedQueueSize;
    private long prefetch;
    private long maxPending;
    private boolean exclusive;
    private boolean retroactive;
    private String selector;

    public MqConsumer() {
        super();
        destType = null;
        destName = null;
        clientId = null;
        connId = null;
        sessionId = 0;
        enqueues = 0;
        dequeues = 0;
        dispatched = 0;
        dispatchedQueueSize = 0;
        prefetch = 0;
        maxPending = 0;
        exclusive = false;
        retroactive = false;
        selector = null;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getConnId() {
        return connId;
    }

    public void setConnId(String connId) {
        this.connId = connId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getEnqueues() {
        return enqueues;
    }

    public void setEnqueues(long enqueues) {
        this.enqueues = enqueues;
    }

    public long getDequeues() {
        return dequeues;
    }

    public void setDequeues(long dequeues) {
        this.dequeues = dequeues;
    }

    public long getDispatched() {
        return dispatched;
    }

    public void setDispatched(long dispatched) {
        this.dispatched = dispatched;
    }

    public long getDispatchedQueueSize() {
        return dispatchedQueueSize;
    }

    public void setDispatchedQueueSize(long dispatchedQueueSize) {
        this.dispatchedQueueSize = dispatchedQueueSize;
    }

    public long getPrefetch() {
        return prefetch;
    }

    public void setPrefetch(long prefetch) {
        this.prefetch = prefetch;
    }

    public long getMaxPending() {
        return maxPending;
    }

    public void setMaxPending(long maxPending) {
        this.maxPending = maxPending;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isRetroactive() {
        return retroactive;
    }

    public void setRetroactive(boolean retroactive) {
        this.retroactive = retroactive;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }
}
