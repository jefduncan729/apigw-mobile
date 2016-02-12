package com.axway.apigw.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.db.DbHelper;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.fragment.HomeFrag;
import com.axway.apigw.android.model.ServerInfo;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/8/2016.
 */
public class LauncherActivity extends BaseActivity {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private static final int REQ_ADD_SERVER = 1001;

    @Bind(R.id.container01) ViewGroup ctr01;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("For Android");
//        toolbar.setNavigationIcon(R.mipmap.ic_ab_drawer_holo_light);
//        toolbar.setNavigationOnClickListener(this);
        setActionBar(toolbar);
        int cnt = getServerCount();
        if (cnt == 0) {
//            showWelcomeFrag();
            Intent i = new Intent(this, ConnMgrActivity.class);
            i.putExtra(Intent.EXTRA_LOCAL_ONLY, cnt);
            startActivityForResult(i, REQ_ADD_SERVER);
        }
        else {
            showHomeFrag();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
//            BaseApp.post(new ActionEvent(R.id.action_settings));
            settings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
//        checkCert();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ADD_SERVER) {
            if (resultCode == RESULT_OK) {
                Bundle b = (data == null ? new Bundle() : data.getBundleExtra(Constants.EXTRA_SERVER_INFO));
                ServerInfo info = ServerInfo.fromBundle(b);
                if (info == null) {
                }
                else {
                    Intent i = new Intent(this, TopologyActivity.class);
                    i.setAction(Intent.ACTION_VIEW);
                    i.putExtra(Constants.EXTRA_SERVER_INFO, info.toBundle());
                    startActivity(i);
                }
                finish();
            }
            else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showHomeFrag() {
        setTitle(R.string.app_name);
        replaceFragment(ctr01.getId(), HomeFrag.newInstance(), Constants.TAG_SINGLE_PANE);
    }

    private void connMgr() {
        Intent i = new Intent(this, ConnMgrActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        startActivity(i);
    }

    private void connect() {
        ServerInfo info = getOnlyServerInfo();
        if (info == null) {
            pickConnection();
        }
        else {
            startConnection(info);
        }
    }

    private void settings() {
        Log.d(TAG, "show settings");
    }

    private void pickConnection() {
        showToast("not yet implemented");
    }

    private void startConnection(ServerInfo info) {
        Intent i = new Intent(this, TopologyActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra(Constants.EXTRA_SERVER_INFO, info.toBundle());
        startActivity(i);
    }

    @Subscribe
    public void onAction(ActionEvent evt) {
        int id = evt.id;
        switch (id) {
            case R.id.action_conn_mgr:
                connMgr();
                break;
            case R.id.action_connect:
                connect();
                break;
            case R.id.action_settings:
                settings();
                break;

        }
    }
    private int getServerCount() {
        Cursor c = getContentResolver().query(DbHelper.ConnMgrColumns.CONTENT_URI, null, null, null, null);
        if (c == null) {
            return 0;
        }
        int n = c.getCount();
        c.close();
        return n;
    }

    private ServerInfo getOnlyServerInfo() {
        ServerInfo rv = null;
        String where = DbHelper.ConnMgrColumns.STATUS + " = ?";  // AND ((" + ConnMgrColumns.USE_SSL + " = ?) OR (" + ConnMgrColumns.USE_SSL + " = ? AND " + ConnMgrColumns.FLAG + " = ?))";
        String[] whereArgs = new String[] { Integer.toString(Constants.STATUS_ACTIVE) };    //, "0", "1" , Integer.toString(Constants.FLAG_CERT_TRUSTED) };
        Cursor c = getContentResolver().query(DbHelper.ConnMgrColumns.CONTENT_URI, null, where, whereArgs, null);
        if (c == null) {
            return rv;
        }
        if (c.getCount() == 1) {
            if (c.moveToFirst()) {
                rv = ServerInfo.from(c);
            }
        }
        c.close();
        return rv;
    }

    @Override
    public void onClick(View arg0) {
        Log.d(TAG, String.format("onClick: %d", arg0.getId()));
        super.onClick(arg0);
    }
}
