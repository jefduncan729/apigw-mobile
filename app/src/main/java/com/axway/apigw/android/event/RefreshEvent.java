package com.axway.apigw.android.event;

/**
 * Created by su on 2/4/2016.
 */
public class RefreshEvent {

    public Object requestor;

    public RefreshEvent(Object requestor) {
        super();
        this.requestor = requestor;
    }
}
