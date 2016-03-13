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
import android.widget.EditText;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.MessagingModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.MqDestFragment;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.view.FloatingActionButton;
import com.axway.apigw.android.view.SlidingTabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/17/2016.
 */
public class MessagingActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();
    private static final int[] TITLE_IDS = {R.string.queues, R.string.topics, R.string.consumers, R.string.subscribers };

    @Bind(R.id.pager_title_strip) SlidingTabLayout slidingTabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private MessagingModel model;
    private String instId;
    private int curPg;
//    private FragAdapter pageAdapter;
    private String[] titles;

//    private Map<String, Fragment> fragMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = new String[TITLE_IDS.length];
        for (int i = 0; i < TITLE_IDS.length; i++) {
            titles[i] = getString(TITLE_IDS[i]);
        }
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
        setContentView(R.layout.toolbar_pager);
        ButterKnife.bind(this);
        model = MessagingModel.getInstance();
//        pageAdapter = new FragAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(this);
        slidingTabs.setCustomTabView(R.layout.tab_view, 0);
        slidingTabs.setBackgroundColor(getResources().getColor(R.color.axway_blue));
        slidingTabs.setDividerColors(getResources().getColor(android.R.color.white));
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.primary_text_default_material_dark));
        if (model.hasLoaded()) {
            refreshAdapter();
            return;
        }
        loadData();
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        tb.setTitle(R.string.action_gateway_messaging);
        tb.setSubtitle(instId);
    }

    @Override
    protected void setupFab(FloatingActionButton f) {
        f.setVisibility(View.VISIBLE);
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
        int kind = data.getIntExtra(Constants.EXTRA_ITEM_TYPE, 0);
        confirmDestAction(id, kind, name);
    }

    @Subscribe
    public void onItemSelected(ItemSelectedEvent<JsonObject> evt) {
        if (evt.data == null || !evt.data.has("queueName"))
            return;
        String name = evt.data.get("queueName").getAsString();
        String kind = evt.data.get("queueType").getAsString();
        Intent i = new Intent(this, MessagesActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_NAME, name);
        i.putExtra(Constants.EXTRA_ITEM_TYPE, kind.contains("ueue") ? MessagingModel.TYPE_QUEUE : MessagingModel.TYPE_TOPIC);
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPg = position;
        setFabVisibility(curPg <= 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClicked(FloatingActionButton fab) {
        addDest();
    }

    private void addDest() {
//        String kind = (curPg > 1 ? null : titles[curPg]);
//        if (TextUtils.isEmpty(kind))
//            return;
//        final String k = kind.substring(0, 5);
        String title = String.format("New %s", curPg == 0 ? "Queue":"Topic");
        customDialog(title, R.layout.name_dlg, new CustomDialogCallback() {

            @Override
            public void populate(AlertDialog dlg) {

            }

            @Override
            public void save(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                performDestAction(R.id.action_add, curPg + 1, s);
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

    private void confirmDestAction(final int id, final int kind, final String name) {
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

    private void performDestAction(int action, int kind, String name) {
        showProgressBar(true);
        switch (action) {
            case R.id.action_add:
                model.addDestination(instId, kind, name, new PutCallback(kind));
            break;
            case R.id.action_delete:
                model.removeDestination(instId, kind, name, new DeleteCallback(kind, name));
            break;
            case R.id.action_purge:
                model.purgeDestination(instId, kind, name, new PurgeCallback(name));
            break;
        }
    }

    private void loadData() {
        showProgressBar(true);
        model.loadQueues(instId, new LoadCallback(MessagingModel.TYPE_QUEUE));
        model.loadTopics(instId, new LoadCallback(MessagingModel.TYPE_TOPIC));
        model.loadConsumers(instId, new LoadCallback(MessagingModel.TYPE_CONSUMER));
        model.loadSubscribers(instId, new LoadCallback(MessagingModel.TYPE_SUBSCRIBER));
    }

    private void onLoadComplete(int kind, JsonArray data) {
        switch (kind) {
            case MessagingModel.TYPE_QUEUE:
                model.setQueues(data);
                break;
            case MessagingModel.TYPE_TOPIC:
                model.setTopics(data);
                break;
            case MessagingModel.TYPE_CONSUMER:
                model.setConsumers(data);
                break;
            case MessagingModel.TYPE_SUBSCRIBER:
                model.setSubscribers(data);
                break;
        }
        if (model.hasLoaded()) {
            Log.d(TAG, "onLoadComplete, all data loaded");
            showProgressBar(false);
            refreshAdapter();
        }
    }

    private class LoadCallback extends BaseCallback {

        int kind;   //innerName;
        public LoadCallback(int kind) {
            super();
            this.kind = kind;
        }

        public JsonArray filterData(JsonArray in) {
            if (kind == MessagingModel.TYPE_TOPIC && !BaseApp.getInstance().isShowMqAdvisories()) {
                JsonArray rv = new JsonArray();
                for (int i = 0; i < in.size(); i++) {
                    JsonObject j = in.get(i).getAsJsonObject();
                    String nm = j.has("queueName") ? j.get("queueName").getAsString() : "";
                    if (nm.contains("Advisory"))
                        continue;
                    rv.add(j);
                }
                return rv;
            }
            return in;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonArray res = null;
            String innerName = model.endpointFor(kind);
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
            else
                res = filterData(res);
            onLoadComplete(kind, res);
        }
    }

    private void refreshAdapter() {
//        pageAdapter = new FragAdapter(getSupportFragmentManager());
        viewPager.setAdapter(new FragAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(curPg);
        slidingTabs.setViewPager(viewPager);
    }

    private void onPutSuccessful(MqDestination d) {
        showToast(String.format("%s added", d.getName()));
        if ("Queue".equals(d.getType()))
            model.loadQueues(instId, new LoadCallback(MessagingModel.TYPE_QUEUE));
        else
            model.loadTopics(instId, new LoadCallback(MessagingModel.TYPE_TOPIC));
    }

    private void onDeleteSuccessful(int kind, String name) {
        showToast(String.format("%s deleted", name));
        if (kind == MessagingModel.TYPE_QUEUE)
            model.loadQueues(instId, new LoadCallback(kind));
        else
            model.loadTopics(instId, new LoadCallback(kind));
//        refreshAdapter();
    }

    private void onPurgeSuccessful(String name) {
        showToast(String.format("%s purged", name));
        model.loadQueues(instId, new LoadCallback(MessagingModel.TYPE_QUEUE));
    }

    private class PutCallback extends BaseCallback {

        protected int kind;

        protected PutCallback(int kind) {
            super();
            this.kind = kind;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            MqDestination d = jsonHelper.destinationFromJson(body);
            if (d == null) {
                showToast("request failed");
                return;
            }
            onPutSuccessful(d);
        }
    }

    private class DeleteCallback extends BaseCallback {
        int kind;
        String name;

        public DeleteCallback(int kind, String name) {
            super();
            this.kind = kind;
            this.name = name;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            onDeleteSuccessful(kind, name);
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

/*
    private Fragment getFragment(String which) {
        if (fragMap == null)
            return null;
        return fragMap.get(which);
    }

    private void putFragment(String which, Fragment frag) {
        if (fragMap == null) {
            fragMap = new HashMap<>(4);
        }
        if (fragMap.containsKey(which))
            fragMap.remove(which);
        fragMap.put(which, frag);
    }
*/

    private class FragAdapter extends FragmentStatePagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            JsonArray a = null;
            switch (position) {
                case 0:
                    a = model.getQueues();
                    break;
                case 1:
                    a = model.getTopics();
                    break;
                case 2:
                    a = model.getConsumers();
                    break;
                case 3:
                    a = model.getSubscribers();
                    break;
            }
            if (a == null)
                return null;
            return MqDestFragment.newInstance(instId, position+1, a);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int cnt = -1;
            switch (position) {
                case 0:
//                    cnt = model.getQueueCount();
                    break;
                case 1:
//                    cnt = topics == null ? -1 : topics.size();
                    break;
                case 2:
//                    cnt = consumers == null ? -1 : consumers.size();
                    break;
                case 3:
//                    cnt = subscribers == null ? -1 : subscribers.size();
                    break;
            }
//            if (cnt == -1)
            return titles[position];
//            return String.format("%s (%d)", titles[position], cnt);
        }

    }
}
