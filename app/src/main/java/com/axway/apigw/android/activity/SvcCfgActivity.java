package com.axway.apigw.android.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.NotifyChangeEvent;
import com.axway.apigw.android.fragment.SvcCfgFragment;
import com.axway.apigw.android.model.ServiceConfig;
import com.google.gson.JsonArray;
import com.squareup.otto.Subscribe;
import com.vordel.api.topology.model.Service;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by jef on 3/10/16.
 */
public class SvcCfgActivity extends BaseActivity implements Observer {
    private static final String TAG = SvcCfgActivity.class.getSimpleName();

    private String instId;
    private Service svc;
    private TopologyModel topoModel;
    private boolean dirty;
    private ServiceConfig scSave;
    private SvcCfgFragment scFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            instId = getIntent().getStringExtra(Constants.EXTRA_INSTANCE_ID);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
        }
        topoModel = TopologyModel.getInstance();
        svc = topoModel.getGatewayById(instId);
        setContentView(R.layout.toolbar_pane);
        showProgressBar(true);
//        if (topoModel.getSvcConfig(instId) == null) {
        topoModel.getSvcConfig(instId, new SvcConfigCallback(instId));
//            return;
//        }
//        onLoadComplete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onSave();
                return true;
            case R.id.action_cancel:
                onCancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        tb.setTitle(R.string.action_settings);
        tb.setSubtitle(svc.getName());
    }

    protected void onSave() {
        if (isDirty()) {
            confirmDialog(String.format("Touch OK to apply changes to %s", svc.getName()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performSave();
                }
            });
            return;
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    protected void onCancel() {
        confirmCancel();
    }

    private void confirmCancel() {
        if (isDirty()) {
            confirmDialog("Touch OK to discard changes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performCancel();
                }
            });
            return;
        }
        performCancel();
    }

    private void performCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void performSave() {
        setResult(RESULT_OK);
        finish();
    }

    private void onLoadComplete() {
        scSave = topoModel.getSvcConfig(instId);
        showProgressBar(false);
        scFrag = SvcCfgFragment.newInstance(instId, this);
        replaceFragment(R.id.container01, scFrag, Constants.TAG_SINGLE_PANE);
    }

    private boolean isDirty() {
        return dirty;
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, String.format("update: %s, %s", observable, data));
        dirty = true;
    }

    private class SvcConfigCallback extends BaseCallback {

        String sid;

        public SvcConfigCallback(String sid) {
            super();
            this.sid = sid;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonArray a = jsonHelper.parseAsArray(body);
            if (a == null || a.size() == 0) {
                return;
            }
            topoModel.addSvcConfig(sid, new ServiceConfig(sid, a.get(0).getAsJsonObject()));
            onLoadComplete();
        }
    }

    @Subscribe
    public void onChange(NotifyChangeEvent evt) {
        dirty = true;
    }
}
