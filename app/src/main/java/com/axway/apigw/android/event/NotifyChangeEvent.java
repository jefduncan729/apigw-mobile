package com.axway.apigw.android.event;

/**
 * Created by su on 2/22/2016.
 */
public class NotifyChangeEvent {

    public Object requestor;

    public NotifyChangeEvent() {
        super();
        this.requestor = null;
    }

    public NotifyChangeEvent(Object requestor) {
        this();
        this.requestor = requestor;
    }
}
