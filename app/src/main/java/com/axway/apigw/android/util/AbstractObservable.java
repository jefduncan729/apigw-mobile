package com.axway.apigw.android.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jef on 3/11/16.
 */
public class AbstractObservable<T> extends Observable {

    private T data;

    public AbstractObservable(T data) {
        super();
        this.data = data;
    }
}
