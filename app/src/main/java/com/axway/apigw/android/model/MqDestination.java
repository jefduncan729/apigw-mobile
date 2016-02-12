package com.axway.apigw.android.model;

/**
 * Created by su on 4/3/2015.
 */
public class MqDestination {

    private String name;
    private String qType;
    private long msgCount;
    private long enqCount;
    private long deqCount;
    private long conCount;
    private long proCount;
    private long cacheCount;
    private long dispCount;
    private long infCount;
    private long expCount;

    public MqDestination() {
        super();
        name = null;
        qType = null;
        msgCount = 0;
        enqCount = 0;
        deqCount = 0;
        conCount = 0;
        proCount = 0;
        cacheCount = 0;
        dispCount = 0;
        infCount = 0;
        expCount = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return qType;
    }

    public void setType(String qType) {
        this.qType = qType;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public long getEnqCount() {
        return enqCount;
    }

    public void setEnqCount(long enqCount) {
        this.enqCount = enqCount;
    }

    public long getDeqCount() {
        return deqCount;
    }

    public void setDeqCount(long deqCount) {
        this.deqCount = deqCount;
    }

    public long getConCount() {
        return conCount;
    }

    public void setConCount(long conCount) {
        this.conCount = conCount;
    }

    public long getProCount() {
        return proCount;
    }

    public void setProCount(long proCount) {
        this.proCount = proCount;
    }

    public long getCacheCount() {
        return cacheCount;
    }

    public void setCacheCount(long cacheCount) {
        this.cacheCount = cacheCount;
    }

    public long getDispCount() {
        return dispCount;
    }

    public void setDispCount(long dispCount) {
        this.dispCount = dispCount;
    }

    public long getInfCount() {
        return infCount;
    }

    public void setInfCount(long infCount) {
        this.infCount = infCount;
    }

    public long getExpCount() {
        return expCount;
    }

    public void setExpCount(long expCount) {
        this.expCount = expCount;
    }
}
