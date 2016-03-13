package com.axway.apigw.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.MessagingModel;
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
    private static final String TAG = MqAdapter.class.getSimpleName();

    private int kind;
    private JsonHelper jsonHelper;

    public MqAdapter(Context ctx, JsonArray a, int kind) {
        super(ctx, a);
        this.kind = kind;
        jsonHelper = JsonHelper.getInstance();
        Log.d(TAG, String.format("new MqAdapter: %d", kind));
    }

    @Override
    protected View createView(int i) {
        return inflater.inflate(R.layout.listitem_2, null);
    }

    @Override
    protected void populateView(View rv, JsonObject j) {
        BasicViewHolder vh = (BasicViewHolder)rv.getTag();
        vh.getImageView().setVisibility(View.GONE);
        if (kind == MessagingModel.TYPE_QUEUE || kind == MessagingModel.TYPE_TOPIC) {
            MqDestination d = jsonHelper.destinationFromJson(j);
            if (d != null) {
//                vh.setData(d);
                vh.setText1(String.format("%s (%d)", d.getName(), d.getMsgCount()));
                vh.setText2(buildDetail(d));
            }
            return;
        }
        if (kind == MessagingModel.TYPE_SUBSCRIBER) {
            MqSubscriber s = jsonHelper.mqSubscriberFromJson(j);
            if (s != null) {
//                vh.setData(s);
                vh.setText1(s.getName());
                vh.setText2(buildDetail(s));
            }
            return;
        }
        if (kind == MessagingModel.TYPE_CONSUMER) {
            MqConsumer c = jsonHelper.mqConsumerFromJson(j);
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
