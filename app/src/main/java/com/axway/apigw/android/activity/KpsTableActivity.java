package com.axway.apigw.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.JsonArrayLoader;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.fragment.KpsTableFragment;
import com.axway.apigw.android.model.DisplayPrefs;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.view.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/19/2016.
 */
public class KpsTableActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<JsonArray>,FloatingActionButton.ClickedListener {
    public static final String TAG = KpsTableActivity.class.getSimpleName();

    public static final int MSG_LOADED = Constants.MSG_BASE + 6001;
    public static final int REQ_UPLOAD = Constants.MSG_BASE + 6002;

    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.container01) ViewGroup ctr01;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab01) FloatingActionButton fab;

    private KpsModel model;
    private String instId;
    private String storeId;
    private KpsStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tb_sr_frame);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            instId = getIntent().getExtras().getString(Constants.EXTRA_INSTANCE_ID, null);
            storeId = getIntent().getExtras().getString(Constants.EXTRA_ITEM_ID, null);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            storeId = savedInstanceState.getString(Constants.EXTRA_ITEM_ID);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!swipeRefresh.isRefreshing())
                    swipeRefresh.setRefreshing(true);
                postEmptyMessage(Constants.MSG_REFRESH);
            }
        });
        fab.setVisibility(View.GONE);
        fab.setClickedListener(this);
        model = KpsModel.getInstance();
        if (model.getKps() == null) {
            finishWithAlert("KPS has not been loaded");
            return;
        }
        store = model.getStoreById(storeId);
        if (store == null) {
            finishWithAlert(String.format("Store not found: %s", storeId));
            return;
        }
//        showProgress(true);
        toolbar.setTitle(String.format("Browsing %s", store.getAlias()));
        toolbar.setSubtitle(instId);
        setActionBar(toolbar);
//        refresh();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        outState.putString(Constants.EXTRA_ITEM_ID, storeId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_upload);
//        if (item != null && item.getIntent() == null) {
//            Intent i = new Intent(Intent.ACTION_SEND);
//            i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
//            i.putExtra(Constants.EXTRA_ITEM_ID, storeId);
//            item.setIntent(i);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            confirmUpload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_UPLOAD) {
            if (resultCode == RESULT_OK) {
                showToast("upload successful");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void confirmUpload() {
        Log.d(TAG, String.format("confirmUpload: %s - %s", instId, storeId));
        confirmDialog(getString(R.string.touch_to, "backup", storeId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performUpload();
            }
        });

    }

    private void performUpload() {
        Intent i = new Intent(this, DriveUploadActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_ID, Constants.KEY_KPS_FOLDER);
        i.putExtra(Constants.EXTRA_KPS_STORE_ID, storeId);
        i.putExtra(Constants.EXTRA_ACTION, R.id.action_upload);
        i.putExtra(Constants.EXTRA_FILENAME, String.format("%s_%s.json", instId, storeId));
        i.putExtra(Intent.EXTRA_TITLE, String.format("%s_%s.json", instId, storeId));
        i.putExtra(Intent.EXTRA_TEXT, String.format("KPS Backup: %s, %s", instId, storeId));
        startActivityForResult(i, REQ_UPLOAD);
    }

    @Override
    protected boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_REFRESH:
                return true;
            case MSG_LOADED:
                showFragment((JsonArray)msg.obj);
                return true;
        }
        return super.onHandleMessage(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
    }

    @Override
    public android.support.v4.content.Loader<JsonArray> onCreateLoader(int id, Bundle args) {
        String endpoint = KpsModel.KPS_STORE_ENDPOINT.replace("{svcId}", instId).replace("{alias}", store.getAlias());
        return new JsonArrayLoader(this, app.getApiClient(), endpoint);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<JsonArray> loader, JsonArray data) {
        Log.d(TAG, "loadFinished");
        if (data == null)
            data = new JsonArray();
        getMsgHandler().sendMessage(getMsgHandler().obtainMessage(MSG_LOADED, data));
    }

    private void showFragment(final JsonArray data) {
        String s = getPrefs().getString(String.format("layout_%s", storeId), null);
        DisplayPrefs layout = DisplayPrefs.inflate(s);
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, KpsTableFragment.newInstance(instId, store, layout, data), Constants.TAG_SINGLE_PANE).commit();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<JsonArray> loader) {

    }

    @Override
    public void onClicked(FloatingActionButton fab) {
        Intent i = new Intent(this, KpsItemActivity.class);
        i.setAction(Intent.ACTION_INSERT);
        i.putExtra(Constants.EXTRA_JSON_ITEM, new JsonObject().toString());
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_KPS_STORE_ID, storeId);
        startActivityForResult(i, Constants.MSG_SELECT);
    }

    @Subscribe
    public void onAction(final ActionEvent evt) {
        switch (evt.id) {
            case R.id.action_select:
                select(evt.data);
                break;
        }
    }

    private void select(Intent data) {
        Intent i = new Intent(this, KpsItemActivity.class);
        i.setAction(Intent.ACTION_EDIT);
        i.putExtras(data.getExtras());
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_KPS_STORE_ID, storeId);
        startActivityForResult(i, Constants.MSG_SELECT);
    }
}
