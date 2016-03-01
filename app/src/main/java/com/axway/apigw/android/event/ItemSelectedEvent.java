package com.axway.apigw.android.event;

import android.content.Intent;

/**
 * Created by su on 2/4/2016.
 */
public class ItemSelectedEvent<T> {

    public T data;

    public ItemSelectedEvent(T data) {
        super();
        this.data = data;
    }
}
