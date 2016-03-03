package com.axway.apigw.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.MessagingModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.MqDestFragment;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.view.FloatingActionButton;
import com.axway.apigw.android.view.SlidingTabLayout;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/17/2016.
 */
public class MessagingActivity extends BaseActivity implements ViewPager.OnPageChangeListener, FloatingActionButton.ClickedListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();
    private static final int[] TITLE_IDS = {R.string.queues, R.string.topics, R.string.consumers, R.string.subscribers };

    @Bind(R.id.pager_title_strip) SlidingTabLayout slidingTabs;
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab01) FloatingActionButton fab;

//    private ApiClient client;
    private MessagingModel model;
    private String instId;
    private int curPg;
    private FragAdapter pageAdapter;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pager);
        titles = new String[TITLE_IDS.length];
        for (int i = 0; i < TITLE_IDS.length; i++) {
            titles[i] = getString(TITLE_IDS[i]);
        }
        ButterKnife.bind(this);
        pageAdapter = new FragAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        if (savedInstanceState == null) {
            instId = getIntent().getExtras().getString(Constants.EXTRA_INSTANCE_ID, null);
            curPg = 0;
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            curPg = savedInstanceState.getInt(Constants.EXTRA_CUR_PAGE, 0);
        }
        if (TextUtils.isEmpty(instId)) {
            throw new IllegalStateException("instId cannot be null");
        }
//        client = BaseApp.getInstance().getApiClient();
        model = MessagingModel.getInstance();
        slidingTabs.setCustomTabView(R.layout.tab_view, 0);
        slidingTabs.setBackgroundColor(getResources().getColor(R.color.axway_blue));
        slidingTabs.setDividerColors(getResources().getColor(android.R.color.white));
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.primary_text_default_material_dark));
        slidingTabs.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(curPg);
        toolbar.setTitle(R.string.action_gateway_messaging);
        toolbar.setSubtitle(instId);
        setActionBar(toolbar);
        fab.setClickedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        outState.putInt(Constants.EXTRA_CUR_PAGE, curPg);
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

    @Subscribe
    public void onAction(ActionEvent evt) {
        final int id = evt.id;
        final Intent data = evt.data;
        String name = data.getStringExtra(Constants.EXTRA_ITEM_NAME);
        String kind = data.getStringExtra(Constants.EXTRA_ITEM_TYPE);
        confirmDestAction(id, kind, name);
    }

    @Subscribe
    public void onItemSelected(ItemSelectedEvent<JsonObject> evt) {
        if (evt.data == null)
            return;
        String name = evt.data.get("queueName").getAsString();
        String kind = evt.data.get("queueType").getAsString();
        Intent i = new Intent(this, MessagesActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_NAME, name);
        i.putExtra(Constants.EXTRA_ITEM_TYPE, kind);
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPg = position;
        fab.setVisibility(curPg <= 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClicked(FloatingActionButton fab) {
        addDest();
    }

    private void addDest() {
        String kind = null;
        if (curPg == 0) {
            kind = "Queue";
        }
        else if (curPg == 1) {
            kind = "Topic";
        }
        if (TextUtils.isEmpty(kind))
            return;
        final String k = kind;
        String title = String.format("New %s", kind);
        customDialog(title, R.layout.name_dlg, new CustomDialogCallback() {

            @Override
            public void populate(AlertDialog dlg) {

            }

            @Override
            public void save(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                performDestAction(R.id.action_add, k, s);
            }

            @Override
            public boolean validate(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    showToast("Provide a value");
                    return false;
                }
                return true;
            }
        });

    }

    private void confirmDestAction(final int id, final String kind, final String name) {
        String a = null;
        if (id == R.id.action_delete)
            a = "delete";
        else if (id == R.id.action_purge)
            a = "purge";
        if (TextUtils.isEmpty(a))
            return;
        confirmDialog(getString(R.string.touch_to, a, name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performDestAction(id, kind, name);
            }
        });
    }

    private void performDestAction(int action, String kind, String name) {
        if (!kind.endsWith("s"))
            kind = kind + "s";
        ApiClient client = app.getApiClient();
        if (action == R.id.action_add) {
            String url = String.format("%s/%s", MessagingModel.ENDPOINT_MQ_DESTS.replace("{svcId}", instId).replace("{destType}", kind.toLowerCase()), name);
            Request req = client.createRequest(url, "PUT", null);
            client.executeAsyncRequest(req, new PutCallback(kind));
            return;
        }
        if (action == R.id.action_delete) {
            String url = String.format("%s/%s", MessagingModel.ENDPOINT_MQ_DESTS.replace("{svcId}", instId).replace("{destType}", kind.toLowerCase()), name);
            Request req = client.createRequest(url, "DELETE", null);
            client.executeAsyncRequest(req, new DeleteCallback(name));
            return;
        }
        if (action == R.id.action_purge) {
            String url = String.format("%s/%s/purge", MessagingModel.ENDPOINT_MQ_DESTS.replace("{svcId}", instId).replace("{destType}", kind.toLowerCase()), name);
            Request req = client.createRequest(url, "POST", null);
            client.executeAsyncRequest(req, new PurgeCallback(name));
            return;
        }
    }

    private void onRequestFailed(Call call, IOException e) {
        Log.d(TAG, String.format("RequestFailed: %s, %s", call, e.getMessage()));
    }

    private void refresh() {
        int cur = curPg;
        pageAdapter = new FragAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(cur);
    }

    private void onPutSuccessful(MqDestination d) {
        showToast(String.format("%s added", d.getName()));
        refresh();
    }

    private void onDeleteSuccessful(String name) {
        showToast(String.format("%s deleted", name));
        refresh();
    }

    private void onPurgeSuccessful(String name) {
        showToast(String.format("%s purged", name));
    }

    private class PutCallback extends BaseCallback {

        protected String kind;

        protected PutCallback(String kind) {
            super();
            this.kind = kind;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            MqDestination d = JsonHelper.getInstance().destinationFromJson(body);
            if (d == null) {
                showToast("request failed");
                return;
            }
            onPutSuccessful(d);
        }
    }

    private class DeleteCallback extends BaseCallback {
        String name;

        public DeleteCallback(String name) {
            super();
            this.name = name;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            onDeleteSuccessful(name);
        }

    }

    private class PurgeCallback extends BaseCallback {
        String name;

        public PurgeCallback(String name) {
            super();
            this.name = name;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            onPurgeSuccessful(name);
        }

    }

    private class FragAdapter extends FragmentStatePagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MqDestFragment.newInstance(instId, "queues");
                case 1:
                    return MqDestFragment.newInstance(instId, "topics");
                case 2:
                    return MqDestFragment.newInstance(instId, "consumers");
                case 3:
                    return MqDestFragment.newInstance(instId, "subscribers");
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return titles[position];
            }
            return "";
        }

    }
}
