package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.fragment.KpsStoresFragment;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/18/2016.
 */
 public class KpsActivity extends BaseActivity {

    public static final String TAG = KpsActivity.class.getSimpleName();
//    public static final int MSG_KPS_LOADED = Constants.MSG_BASE + 6001;

    @Bind(R.id.container01) ViewGroup ctr01;

    private String instId;
    private KpsModel kpsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kpsModel = KpsModel.getInstance();
        if (savedInstanceState == null) {
            instId = getIntent().getExtras().getString(Constants.EXTRA_INSTANCE_ID, null);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
        }
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        refresh();
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        tb.setTitle(R.string.action_gateway_kps);
        tb.setSubtitle(instId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
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
    protected boolean onHandleMessage(Message msg) {
        switch (msg.what){
            case Constants.MSG_REFRESH:
                refresh();
                return true;
        }
        return super.onHandleMessage(msg);
    }

    @Subscribe
    public void onAction(ActionEvent evt) {
        final int a = evt.id;
        final Intent data = evt.data;
        final String id = data.getStringExtra(Constants.EXTRA_ITEM_ID);
        final int kind = data.getIntExtra(Constants.EXTRA_ITEM_TYPE, 0);

        switch (a) {
            case R.id.action_select:
                Log.d(TAG, String.format("selected: %s %d", id, kind));
                storeSelected(id);
                break;
            case R.id.action_kps_structure:
                showStructure(id);
                break;
            case R.id.action_kps_customize_list:
                customizeList(id);
                break;
        }
    }

    private void storeSelected(String id) {
        Intent i = new Intent(this, KpsTableActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_ID, id);
        startActivity(i);
    }

    private void showStructure(String id) {
        Intent i = new Intent(this, KpsStructureActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_ID, id);
        startActivity(i);
    }

    private void customizeList(String id) {
        Intent i = new Intent(this, KpsCustomizeListActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_ID, id);
        startActivity(i);
    }

    private void kpsLoaded() {
        showProgressBar(false);
        Log.d(TAG, String.format("kpsLoaded: %s", kpsModel.getKps()));
        replaceFragment(R.id.container01, KpsStoresFragment.newInstance(), Constants.TAG_SINGLE_PANE);
    }

    private void refresh() {
        getMsgHandler().removeMessages(Constants.MSG_REFRESH);
//        replaceFragment(R.id.container01, ProgressFragment.newInstance("Loading KPS"), Constants.TAG_SINGLE_PANE);
        showProgressBar(true);
        kpsModel.loadKps(instId, new KpsCallback());
    }

    private class KpsCallback extends BaseCallback {

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonObject obj = jsonHelper.parseAsObject(body);
            if (obj != null && obj.has("result")) {
                obj = obj.getAsJsonObject("result");
            }
            kpsModel.setKps(jsonHelper.kpsFromJson(obj));
            kpsLoaded();
        }
    }
}
