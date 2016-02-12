package com.axway.apigw.android.model;

/**
 * Created by su on 4/3/2015.
 */
public class MqMessage {

    private String msgId;
    private String msgType;
    private long msgSize;
    private long msgTime;


    public MqMessage() {
        super();
        msgId = null;
        msgType = null;
        msgSize = 0;
        msgTime =0;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public long getMsgSize() {
        return msgSize;
    }

    public void setMsgSize(long msgSize) {
        this.msgSize = msgSize;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
