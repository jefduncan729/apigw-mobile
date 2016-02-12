package com.axway.apigw.android.model;

/**
 * Created by su on 10/2/2014.
 */
abstract public class KpsBase {

    private String identity;
    private String description;

    protected KpsBase() {
        super();
        this.identity = null;
        this.description = null;
    }

    protected KpsBase(String id) {
        this();
        this.identity = id;
    }

    protected KpsBase(String id, String desc) {
        this(id);
        this.description = desc;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
