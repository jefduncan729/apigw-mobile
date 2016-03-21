package com.axway.apigw.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.NotifyChangeEvent;
import com.axway.apigw.android.fragment.EmptyFragment;
import com.axway.apigw.android.fragment.ProgressFragment;
import com.axway.apigw.android.fragment.ProgressSupportFragment;
import com.axway.apigw.android.fragment.SvcCfgFragment;
import com.axway.apigw.android.model.ServiceConfig;
import com.axway.apigw.android.view.FloatingActionButton;
import com.axway.apigw.android.view.SlidingTabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;
import com.vordel.api.topology.model.Service;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by jef on 3/10/16.
 */
public class SvcCfgActivity extends BaseActivity implements Observer, ViewPager.OnPageChangeListener {
    private static final String TAG = SvcCfgActivity.class.getSimpleName();

    private String instId;
    private Service svc;
    private TopologyModel topoModel;
    private boolean dirty;
    private ServiceConfig scSave;
    private SvcCfgFragment scFrag;
    private JsonObject json;

//    private ViewPager viewPager;
//    private SlidingTabLayout slidingTabs;
    private int curPg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            instId = getIntent().getStringExtra(Constants.EXTRA_INSTANCE_ID);
            curPg = 0;
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            curPg = savedInstanceState.getInt(Constants.EXTRA_CUR_PAGE, 0);
        }
        topoModel = TopologyModel.getInstance();
        svc = topoModel.getGatewayById(instId);
        setContentView(R.layout.toolbar_2pane);
/*
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.pager_title_strip);
        viewPager.addOnPageChangeListener(this);
        slidingTabs.setCustomTabView(R.layout.tab_view, 0);
        slidingTabs.setBackgroundColor(getResources().getColor(R.color.axway_blue));
        slidingTabs.setDividerColors(getResources().getColor(android.R.color.white));
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.primary_text_default_material_dark));
*/
        showProgressBar(true);
        scSave = topoModel.getSvcConfig(instId);
        if (scSave != null) {
            refreshAdapter();
            return;
        }
        topoModel.getSvcConfig(instId, new SvcConfigCallback(instId));
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

    @Override
    protected void setupFab(FloatingActionButton f) {
        f.setVisibility(View.GONE);
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

    private void onLoadComplete(JsonObject j) {
        json = j;
//        scSave = topoModel.getSvcConfig(instId);
        scSave = new ServiceConfig(instId, j);
        scSave.addObserver(this);
        showProgressBar(false);
        refreshAdapter();
    }

    private void refreshAdapter() {
//        viewPager.setAdapter(new FragAdapter(getSupportFragmentManager()));
//        viewPager.setCurrentItem(curPg);
//        slidingTabs.setViewPager(viewPager);
        scFrag = SvcCfgFragment.newInstance(instId, scSave);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPg = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
//            json = ;
//            topoModel.addSvcConfig(sid, new ServiceConfig(sid, a.get(0).getAsJsonObject()));
            onLoadComplete(a.get(0).getAsJsonObject());
        }
    }

    private class FragAdapter extends FragmentStatePagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ProgressSupportFragment.newInstance("Position 1");
            }
            int i = position-1;
            ServiceConfig.HttpService svc = scSave.httpService(position-1);
            return EmptyFragment.newInstance();
        }

        @Override
        public int getCount() {
            if (scSave == null)
                return 0;
            return scSave.httpServices().size()+1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "System";
            ServiceConfig.HttpService svc = scSave.httpService(position-1);
            if (svc == null)
                return "";
            return svc.name();
        }
    }

    private void itemSelected(Intent i) {
        int kind = i.getIntExtra(Constants.EXTRA_ITEM_TYPE, -1);
        String s = i.getStringExtra(Constants.EXTRA_JSON_ITEM);
        JsonObject j = null;
        if (!TextUtils.isEmpty(s)) {
            j = jsonHelper.parseAsObject(s);
        }
        String t = null;
        switch (kind) {
            case SvcCfgFragment.TYPE_SYS:
                t = "System settings";
                break;
            case SvcCfgFragment.TYPE_SVC_PORT:
                t = s;
                break;
            case SvcCfgFragment.TYPE_URI:
                t = s;
                break;
        }
        if (TextUtils.isEmpty(t))
            return;
        replaceFragment(R.id.container02, ProgressFragment.newInstance(t), "dual_pane");
    }

    @Subscribe
    public void onAction(ActionEvent evt) {
        Log.d(TAG, String.format("onAction: %s", evt));
        switch (evt.id) {
            case R.id.action_select:
                itemSelected(evt.data);
                break;
        }
    }
}
