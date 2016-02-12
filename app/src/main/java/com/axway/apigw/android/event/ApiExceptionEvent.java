package com.axway.apigw.android.event;

/**
 * Created by su on 1/22/2016.
 */
public class ApiExceptionEvent {

    private Exception excp;

    public ApiExceptionEvent(Exception excp) {
        super();
        this.excp = excp;
    }
}
