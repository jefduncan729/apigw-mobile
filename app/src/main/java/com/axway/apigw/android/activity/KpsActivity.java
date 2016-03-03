package com.axway.apigw.android.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.KpsStoresLoader;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.fragment.KpsStoresFragment;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.api.KpsModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/18/2016.
 */
 public class KpsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Kps> {
    public static final String TAG = KpsActivity.class.getSimpleName();
    public static final int MSG_KPS_LOADED = Constants.MSG_BASE + 6001;

//    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.container01) ViewGroup ctr01;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private String instId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            instId = getIntent().getExtras().getString(Constants.EXTRA_INSTANCE_ID, null);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
        }
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (!swipeRefresh.isRefreshing())
//                    swipeRefresh.setRefreshing(true);
//                postEmptyMessage(Constants.MSG_REFRESH);
//            }
//        });
//        showProgress(true);
        toolbar.setTitle(R.string.action_gateway_kps);
        toolbar.setSubtitle(instId);
        refresh();
//        getLoaderManager().initLoader(0, null, this);
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
            case MSG_KPS_LOADED:
                kpsLoaded();
                return true;
            case Constants.MSG_REFRESH:
                refresh();
                return true;
        }
        return super.onHandleMessage(msg);
    }

    @Override
    public Loader<Kps> onCreateLoader(int i, Bundle bundle) {
        return new KpsStoresLoader(this, app.getApiClient(), instId);
    }

    @Override
    public void onLoadFinished(Loader<Kps> loader, Kps kps) {
        KpsModel.getInstance().setKps(kps);
        getMsgHandler().sendEmptyMessage(MSG_KPS_LOADED);
    }

    @Override
    public void onLoaderReset(Loader<Kps> loader) {

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

    protected void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                swipeRefresh.setRefreshing(show);
            }
        });
    }

    private void kpsLoaded() {
        showProgress(false);
        Log.d(TAG, String.format("kpsLoaded: %s", KpsModel.getInstance().getKps()));
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, KpsStoresFragment.newInstance(), Constants.TAG_SINGLE_PANE).commit();
    }

    private void refresh() {
        getMsgHandler().removeMessages(Constants.MSG_REFRESH);
        showProgress(true);
        getLoaderManager().initLoader(0, null, this);
    }
}
