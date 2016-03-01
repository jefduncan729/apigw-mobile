package com.axway.apigw.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.model.MqConsumer;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.MqSubscriber;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by su on 2/18/2016.
 */
public class MqAdapter extends JsonArrayAdapter {

    private String kind;

    public MqAdapter(Context ctx, JsonArray a, String kind) {
        super(ctx, a);
        this.kind = kind;
    }

    @Override
    protected View createView(int i) {
        return inflater.inflate(R.layout.listitem_2, null);
    }

    @Override
    protected void populateView(View rv, JsonObject j) {
        BasicViewHolder vh = (BasicViewHolder)rv.getTag();
        vh.getImageView().setVisibility(View.GONE);
        if ("queues".equals(kind) || "topics".equals(kind)) {
            MqDestination d = JsonHelper.getInstance().destinationFromJson(j);
            if (d != null) {
//                vh.setData(d);
                vh.setText1(String.format("%s (%d)", d.getName(), d.getMsgCount()));
                vh.setText2(buildDetail(d));
            }
        }
        else if ("subscribers".equals(kind)) {
            MqSubscriber s = JsonHelper.getInstance().mqSubscriberFromJson(j);
            if (s != null) {
//                vh.setData(s);
                vh.setText1(s.getName());
                vh.setText2(buildDetail(s));
            }
        }
        else if ("consumers".equals(kind)) {
            MqConsumer c = JsonHelper.getInstance().mqConsumerFromJson(j);
            if (c != null) {
//                vh.setData(c);
                vh.setText1(c.getDestName());
                vh.setText2(buildDetail(c));
            }
        }
    }

    private String buildDetail(MqDestination d) {
        StringBuilder sb = new StringBuilder();
        sb.append("Consumers: ").append(d.getConCount()).append(", Producers: ").append(d.getProCount()).append("\n");
        sb.append("Enqueues: ").append(d.getEnqCount()).append(", Dequeues: ").append(d.getDeqCount()).append("\n");
        sb.append("Dispatched: ").append(d.getDispCount()).append(", Inflight: ").append(d.getInfCount()).append(", Expired: ").append(d.getExpCount());
        return sb.toString();
    }

    private String buildDetail(MqConsumer c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(c.getDestType()).append(", Client Id: ").append(c.getClientId()).append("\n");
        sb.append("Enqueues: ").append(c.getEnqueues()).append(", Dequeues: ").append(c.getDequeues()).append(", Dispatched: ").append(c.getDispatched()).append("\n");
        sb.append("Connection Id: ").append(c.getConnId());
        if (!TextUtils.isEmpty(c.getSelector())) {
            sb.append("\n").append(c.getSelector());
        }
        return sb.toString();
    }

    private String buildDetail(MqSubscriber s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Client Id: ").append(s.getClientId()).append(", Destination: ").append(s.getClientId()).append(", ");
        if (!s.isActive())
            sb.append("in");
        sb.append("active");
        return sb.toString();
    }
}
