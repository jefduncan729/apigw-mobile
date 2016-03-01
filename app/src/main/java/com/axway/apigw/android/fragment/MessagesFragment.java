package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.adapter.MqMsgsAdapter;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.JsonArrayLoader;
import com.axway.apigw.android.api.MessagingModel;
import com.google.gson.JsonArray;

/**
 * Created by su on 2/18/2016.
 */
public class MessagesFragment extends JsonArrayFragment {

    private String instId;
    private String queueName;
    private String kind;

    public static MessagesFragment newInstance(String instId, String queueName, String kind) {
        MessagesFragment rv = new MessagesFragment();
        rv.instId = instId;
        rv.queueName = queueName;
        rv.kind = kind.toLowerCase()+"s";
        return rv;
    }

    @Override
    protected ListAdapter createAdapter(JsonArray a) {
        return new MqMsgsAdapter(getActivity(), a, String.format("%s_messages", kind));
    }

    @Override
    public Loader<JsonArray> onCreateLoader(int id, Bundle args) {
        String endpoint = MessagingModel.ENDPOINT_MQ_MESSAGES.replace("{destType}", kind).replace("{svcId}", instId).replace("{queueName}", queueName);
        return new JsonArrayLoader(getActivity(), BaseApp.getInstance().getApiClient(), endpoint, "messages");
    }
}
