package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.MessagingModel;
import com.axway.apigw.android.fragment.MessagesFragment;
import com.axway.apigw.android.model.ServerInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/18/2016.
 */
public class MessagesActivity extends BaseActivity {

    private String instId;
    private String queueName;
    private int kind;
//    private MessagingModel msgModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        msgModel = MessagingModel.getInstance();
        if (savedInstanceState == null) {
            instId = getIntent().getStringExtra(Constants.EXTRA_INSTANCE_ID);
            queueName = getIntent().getStringExtra(Constants.EXTRA_ITEM_NAME);
            kind = getIntent().getIntExtra(Constants.EXTRA_ITEM_TYPE, MessagingModel.TYPE_QUEUE);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            queueName = savedInstanceState.getString(Constants.EXTRA_ITEM_NAME);
            kind = savedInstanceState.getInt(Constants.EXTRA_ITEM_TYPE);
        }
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        showProgressBar(true);
        MessagingModel.getInstance().loadMessages(instId, queueName, kind, new LoadCallback());
//        replaceFragment(R.id.container01, MessagesFragment.newInstance(instId, queueName, kind), Constants.TAG_SINGLE_PANE);
    }

    private void onLoadComplete(JsonArray res) {
        showProgressBar(false);
        replaceFragment(R.id.container01, MessagesFragment.newInstance(instId, queueName, res), Constants.TAG_SINGLE_PANE);
    }

    private class LoadCallback extends BaseCallback {

        public LoadCallback() {
            super();
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonArray res = null;
            String innerName = "messages";
            JsonElement json = jsonHelper.parse(body);
            if (json != null) {
                if (json.isJsonArray()) {
                    res = json.getAsJsonArray();
                }
                else if (json.isJsonObject() && !TextUtils.isEmpty(innerName)) {
                    JsonObject jo = json.getAsJsonObject();
                    if (jo.has(innerName) && jo.get(innerName).isJsonArray())
                        res = jo.getAsJsonArray(innerName);
                }
            }
            if (res == null)
                res = new JsonArray();
            onLoadComplete(res);
        }
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        tb.setTitle("Messages");
        tb.setSubtitle(String.format("%s - %s", queueName, instId));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        outState.putString(Constants.EXTRA_ITEM_NAME, queueName);
        outState.putInt(Constants.EXTRA_ITEM_TYPE, kind);
    }
}
