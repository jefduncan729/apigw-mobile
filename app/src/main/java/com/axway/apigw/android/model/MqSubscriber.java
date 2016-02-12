package com.axway.apigw.android.model;

/**
 * Created by su on 4/4/2015.
 */
public class MqSubscriber {

    private String name;
    private String clientId;
    private String destination;
    private boolean active;

    public MqSubscriber() {
        super();
        name = null;
        clientId = null;
        destination = null;
        active = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
