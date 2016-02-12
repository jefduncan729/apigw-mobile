package com.axway.apigw.android.model;

/**
 * Created by su on 10/5/2015.
 */
public class KpsDataHolder {

    private static KpsDataHolder instance = null;
    private Kps kps;

    public static KpsDataHolder getInstance() {
        if (instance == null) {
            instance = new KpsDataHolder();
        }
        return instance;
    }

    public Kps getKps() {
        return kps;
    }

    public void setKps(Kps kps) {
        reset();
        this.kps = kps;
    }

    private void reset() {
        kps = null;
    }
}
