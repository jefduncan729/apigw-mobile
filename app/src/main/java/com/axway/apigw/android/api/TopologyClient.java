package com.axway.apigw.android.api;

import com.axway.apigw.android.model.ServerInfo;

/**
 * Created by su on 2/4/2016.
 */
public class TopologyClient extends ApiClient {

    private TopologyClient() {
        super();
    }

    protected TopologyClient(ServerInfo info) {
        super(info);
    }
    public static TopologyClient from(ServerInfo info) {
        return new TopologyClient(info);
    }
}
