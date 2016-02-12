package com.axway.apigw.android.event;

import com.axway.apigw.android.model.ServerInfo;

import java.security.cert.CertPath;

/**
 * Created by su on 1/22/2016.
 */
public class CertValidationEvent {

    public CertPath cp;
    public String alias;
    public ServerInfo info;

    public CertValidationEvent(ServerInfo info, CertPath cp) {
        super();
        this.alias = String.format("%s_%d", info.getHost(), info.getPort());
        this.info = info;
        this.cp = cp;
    }
}
