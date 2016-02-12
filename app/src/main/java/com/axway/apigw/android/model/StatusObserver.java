package com.axway.apigw.android.model;

/**
 * Created by su on 5/3/2015.
 */
public interface StatusObserver {
    public void onStatusChange(final String instId, final int newVal);
}
