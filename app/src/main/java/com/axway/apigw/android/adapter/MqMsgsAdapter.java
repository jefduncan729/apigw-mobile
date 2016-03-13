package com.axway.apigw.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.model.MqConsumer;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.MqMessage;
import com.axway.apigw.android.model.MqSubscriber;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by su on 2/18/2016.
 */
public class MqMsgsAdapter extends JsonArrayAdapter {

    public MqMsgsAdapter(Context ctx, JsonArray a) {
        super(ctx, a);
    }

    @Override
    protected View createView(int i) {
        return inflater.inflate(R.layout.listitem_2, null);
    }

    @Override
    protected void populateView(View rv, JsonObject j) {
        BasicViewHolder vh = (BasicViewHolder)rv.getTag();
        vh.getImageView().setVisibility(View.GONE);
        MqMessage m = jsonHelper.mqMessageFromJson(j);
        if (m != null) {
            vh.setText1(m.getMsgId());
            vh.setText2(buildDetail(m));
        }
    }

    private String buildDetail(MqMessage m) {
        StringBuilder sb = new StringBuilder();
        long kb = m.getMsgSize()/1024;
        if (kb <= 0)
            kb = 1;
        sb.append("size: ").append(kb).append("KB, time: ").append(BaseApp.getInstance().formatDatetime(m.getMsgTime()));
        return sb.toString();
    }
}
