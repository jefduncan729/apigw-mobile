package com.axway.apigw.android.event;

import android.content.Intent;

/**
 * Created by su on 2/4/2016.
 */
public class ActionEvent {

    public int id;
    public Intent data;

    private ActionEvent() {
        super();
        id = 0;
        data = null;
    }

    public ActionEvent(int id) {
        this();
        this.id = id;
    }

    public ActionEvent(int id, Intent data) {
        this(id);
        this.data = data;
    }
}
