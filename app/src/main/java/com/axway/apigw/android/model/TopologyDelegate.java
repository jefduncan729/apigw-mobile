package com.axway.apigw.android.model;

import com.vordel.api.topology.model.Topology;

/**
 * Created by su on 11/17/2015.
 */
public class TopologyDelegate {
    private Topology delegate;

    public TopologyDelegate() {
        super();
        delegate = new Topology();
    }


    public String getId() {
        if (delegate == null)
            return null;
        return delegate.getId();
    }

    public void setId(String id) {
        if (delegate == null)
            return;
        delegate.setId(id);
    }
}
