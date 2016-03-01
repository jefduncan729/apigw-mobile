package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.fragment.MessagesFragment;
import com.axway.apigw.android.model.ServerInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/18/2016.
 */
public class MessagesActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    private String instId;
    private String queueName;
    private String kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            instId = getIntent().getStringExtra(Constants.EXTRA_INSTANCE_ID);
            queueName = getIntent().getStringExtra(Constants.EXTRA_ITEM_NAME);
            kind = getIntent().getStringExtra(Constants.EXTRA_ITEM_TYPE);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            queueName = savedInstanceState.getString(Constants.EXTRA_ITEM_NAME);
            kind = savedInstanceState.getString(Constants.EXTRA_ITEM_TYPE);
        }
        toolbar.setTitle("Messages");
        toolbar.setSubtitle(String.format("%s - %s", queueName, instId));
        setActionBar(toolbar);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container01, MessagesFragment.newInstance(instId, queueName, kind), Constants.TAG_SINGLE_PANE)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        outState.putString(Constants.EXTRA_ITEM_NAME, queueName);
        outState.putString(Constants.EXTRA_ITEM_TYPE, kind);
    }
}
