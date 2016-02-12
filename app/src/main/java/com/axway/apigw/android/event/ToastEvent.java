package com.axway.apigw.android.event;

import android.widget.Toast;

/**
 * Created by su on 2/8/2016.
 */
public class ToastEvent {

    public String msg;
    public int len;

    public ToastEvent(String msg) {
        this(msg, Toast.LENGTH_SHORT);
    }

    public ToastEvent(String msg, int len) {
        super();
        this.msg = msg;
        this.len = len;
    }
}
