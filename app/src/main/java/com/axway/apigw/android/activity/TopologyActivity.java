package com.axway.apigw.android.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.ClickEvent;
import com.axway.apigw.android.event.RequestTopologyEvent;
import com.axway.apigw.android.fragment.ProgressFragment;
import com.axway.apigw.android.fragment.TopologyFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.model.ServiceConfig;
import com.axway.apigw.android.view.ServiceViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Callback;

/**
 * Created by su on 1/22/2016.
 */
public class TopologyActivity extends BaseActivity {

    private static final String TAG = TopologyActivity.class.getSimpleName();

    public static final int MSG_CERT_ERR = Constants.MSG_BASE + 1001;
    public static final int MSG_LOADER_FINISHED = Constants.MSG_BASE + 1002;

    public static final int REQ_EDIT_SVC = 1;
    public static final int REQ_ADD_SVC = 2;
    public static final int REQ_CONSOLE = 3;
    public static final int REQ_CONSOLE_PERM = 4;

    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.container01) ViewGroup ctr01;

    private TopologyModel topoModel;
    private DeploymentModel deployModel;

    private String consoleHandle;
    private String sshTo;

    @Override
    protected boolean onHandleMessage(Message msg) {
        int w = msg.what;
        switch (w) {
            case Constants.MSG_REFRESH:
                refresh();
                return true;
        }
        return super.onHandleMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topoModel = TopologyModel.getInstance();    //new TopologyModel(app.getApiClient());
        deployModel = DeploymentModel.getInstance();
        setContentView(R.layout.toolbar_swipe);
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!swipeRefresh.isRefreshing())
                    swipeRefresh.setRefreshing(true);
                postEmptyMessage(Constants.MSG_REFRESH);
            }
        });
        if (savedInstanceState == null) {
            consoleHandle = null;
        }
        else {
            consoleHandle = savedInstanceState.getString(Constants.EXTRA_CONSOLE_HANDLE);
        }
        sshTo = null;
        if (topoModel.getTopology() == null) {
            refresh();
            return;
        }
        onTopologyLoaded();
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        tb.setTitle("Topology");
        tb.setSubtitle(app.getCurrentServer().displayString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(consoleHandle))
            outState.putString(Constants.EXTRA_CONSOLE_HANDLE, consoleHandle);
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
    public void onClick(View view) {
        int id = view.getId();
        Log.d(TAG, String.format("onClick: %d", id));
        if (id == android.R.id.button1) {
            refresh();
            return;
        }
        Object t = view.getTag();
        if (id == android.R.id.icon1) {
            if (t == null || !(t instanceof String))
                return;
            String instId = (String)t;
            int stat = topoModel.getCachedStatus(instId);
            if (stat == TopologyModel.GATEWAY_STATUS_RUNNING)
                confirmStartGateway(instId, false);
            else if (stat == TopologyModel.GATEWAY_STATUS_NOT_RUNNING)
                confirmStartGateway(instId, true);
            return;
        }
        if (t != null) {
            if (t instanceof ServiceViewHolder) {
                ServiceViewHolder svh = (ServiceViewHolder)t;
                Service svc = (Service)svh.getData();
                Bundle opts = ActivityOptions.makeSceneTransitionAnimation(this, svh.getStatusImage(), "img1").toBundle();
//                Bundle opts = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(v, "text1"));
                editService(svc, opts);
                return;
            }
            if (t instanceof Group) {
                Log.d(TAG, String.format("%s touched", ((Group)t).getId()));
                return;
            }
            if (t instanceof String) {
                String[] tkns = ((String)t).split(":");
                if (tkns.length == 2) {
                    if ("add_inst".equals(tkns[0])) {
                        addService(tkns[1]);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ADD_SVC || requestCode == REQ_EDIT_SVC) {
            if (resultCode == RESULT_OK) {
                getMsgHandler().sendEmptyMessage(Constants.MSG_REFRESH);
            }
            return;
        }
        if (requestCode == REQ_CONSOLE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    consoleHandle = data.getStringExtra(Constants.JACKPAL_EXTRA_WINDOW_HANDLE);
                    Log.d(TAG, String.format("consoleHandle: %s", consoleHandle));
                    if (!TextUtils.isEmpty(sshTo))
                        sshToHost(sshTo);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQ_CONSOLE_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission granted");
                openConsole(sshTo);
            }
            else {
                Log.d(TAG, "permission denied");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void resetClient() {
        BaseApp.resetSocketFactory();
    }

    private void editService(Service svc, Bundle opts) {
        Intent i = new Intent(this, EditServiceActivity.class);
        i.setAction(Intent.ACTION_EDIT);
        Group g = topoModel.getInstanceGroup(svc.getId());
        i.putExtra(Constants.EXTRA_JSON_ITEM, jsonHelper.toJson(svc).toString());
        i.putExtra(Constants.EXTRA_GROUP_ID, g.getId());
        if (opts == null)
            startActivityForResult(i, REQ_EDIT_SVC);
        else
            startActivityForResult(i, REQ_EDIT_SVC, opts);
    }

    private void addService(String grpId) {
        Intent i = new Intent(this, EditServiceActivity.class);
        Service svc = new Service();
        i.setAction(Intent.ACTION_INSERT);
        i.putExtra(Constants.EXTRA_JSON_ITEM, jsonHelper.toJson(svc).toString());
        i.putExtra(Constants.EXTRA_GROUP_ID, grpId);
        startActivityForResult(i, REQ_ADD_SVC);
    }

    private void refresh() {
        Log.d(TAG, "refresh");
        replaceFragment(ctr01.getId(), ProgressFragment.newInstance("Loading topology"), "topo");
        topoModel.loadTopology(new TopoCallback());
    }

    @Subscribe
    public void onClickEvent(ClickEvent evt) {
        onClick(evt.view);
    }

    @Subscribe
    public void onActionEvent(ActionEvent evt) {
//        String msg = null;
        String instId = (evt.data == null ? "" : evt.data.getStringExtra(Constants.EXTRA_INSTANCE_ID));
        switch (evt.id) {
            case R.id.action_delete:
                confirmRemoveGateway(instId);
                break;
            case R.id.action_start_gateway:
                confirmStartGateway(instId, true);
                break;
            case R.id.action_stop_gateway:
                confirmStartGateway(instId, false);
                break;
            case R.id.action_gateway_messaging:
                messagingActivity(instId);
                break;
            case R.id.action_gateway_kps:
                kpsActivity(instId);
                break;
            case R.id.action_deployment_details:
                requestDeployDetails(instId);
                break;
            case R.id.action_ssh_to_host:
                sshToHost(instId);
                break;
            case R.id.action_svc_cfg:
                svcCfgActivity(instId);
                break;
            case R.id.action_edit:
                requestEdit(evt.data);
                break;
        }
    }

    private void requestEdit(Intent intent) {
        if (intent == null)
            return;
        String gid = intent.getStringExtra(Constants.EXTRA_GROUP_ID);
        if (!TextUtils.isEmpty(gid)) {
            editGroup(gid);
        }
    }

    private void editGroup(String gid) {
        final Group g = topoModel.getGroupById(gid);
        if (g == null)
            return;
        customDialog("Edit Group", R.layout.name_dlg, new CustomDialogCallback() {
            @Override
            public void populate(AlertDialog dlg) {
                TextView lbl = (TextView)dlg.findViewById(R.id.label_name);
                EditText ed = (EditText)dlg.findViewById(R.id.edit_name);
                lbl.setText(R.string.name);
                ed.setText(g.getName());
            }

            @Override
            public void save(AlertDialog dlg) {
                EditText ed = (EditText)dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                g.setName(s);
                saveGroup(g);
            }

            @Override
            public boolean validate(AlertDialog dlg) {
                EditText ed = (EditText)dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                return !TextUtils.isEmpty(s);
            }
        });
    }

    private void saveGroup(final Group g) {
        topoModel.updateGroup(g, new BaseCallback() {
            @Override
            protected void onSuccessResponse(int code, String msg, String body) {
                showToast(String.format("%s updated", g.getName()));
                refresh();
            }
        });
    }

    private void messagingActivity(String instId) {
        Intent i = new Intent(this, MessagingActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        startActivity(i);
    }

    private void kpsActivity(String instId) {
        Intent i = new Intent(this, KpsActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        startActivity(i);
    }

    private void svcCfgActivity(String instId) {
        Intent i = new Intent(this, SvcCfgActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        startActivity(i);
    }

    private void requestDeployDetails(String instId) {
        DeploymentDetails dd = deployModel.getDeploymentDetails(instId);
        if (dd == null) {
            deployModel.loadDeploymentDetails(new DdCallback(instId));
        }
        else {
            showDeployDetails(instId);
        }
    }

    private void showDeployDetails(String instId) {
        Intent i = new Intent(this, DeployDetailsActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        startActivity(i);
    }

    private void sshToHost(String instId) {
        Log.d(TAG, String.format("sshToHost: %s", instId));
        if (TextUtils.isEmpty(consoleHandle)) {
//            sshTo = instId;
//            showToast("Opening a console");
            openConsole(instId);
//            showToast(R.string.open_console1);
            return;
        }
        sshTo = null;
        Service svc = topoModel.getGatewayById(instId);
        if (svc == null)
            return;
        Host h = topoModel.getHostById(svc.getHostID());
        if (h == null)
            return;
        String cmd = getString(R.string.ssh_cmd, "jef", h.getName());
        runScriptInConsole(consoleHandle, cmd);
    }

    private void openConsole() {
        openConsole(null);
    }

    private void openConsole(String instId) {
        Log.d(TAG, String.format("openConsole: %s", instId));
        String perm = "jackpal.androidterm.permission.RUN_SCRIPT";
        int can = ContextCompat.checkSelfPermission(this, perm);
        if (can != PackageManager.PERMISSION_GRANTED) {
            sshTo = instId;
            Log.d(TAG, "requesting permission");
            ActivityCompat.requestPermissions(this, new String[] {perm}, REQ_CONSOLE_PERM);
            return;
        }
        sshTo = null;
        Intent i = null;
        try {
            String action = (instId == null ? Constants.JACKPAL_ACTION_NEW_WINDOW : Constants.JACKPAL_ACTION_RUN_SCRIPT);
            String cmd = (instId == null ? null : getString(R.string.ssh_cmd, "jef", "10.71.100.197"));
            i = new Intent(action).addCategory(Intent.CATEGORY_DEFAULT);
            if (consoleHandle != null) {
                i.putExtra(Constants.JACKPAL_EXTRA_WINDOW_HANDLE, consoleHandle);
            }
            if (cmd != null)
                i.putExtra(Constants.JACKPAL_EXTRA_INITIAL_CMD, cmd);
            startActivityForResult(i, REQ_CONSOLE);
        }
        catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    private void runScriptInConsole(String consoleHandle, String script) {
        Intent i = null;
        try {
            i = getPackageManager().getLaunchIntentForPackage(Constants.JACKPAL_TERMINAL_PACKAGE);
            if (i != null) {
                i = new Intent(Constants.JACKPAL_ACTION_RUN_SCRIPT).addCategory(Intent.CATEGORY_DEFAULT);
                i.putExtra(Constants.JACKPAL_EXTRA_INITIAL_CMD, script);
                if (consoleHandle != null)
                    i.putExtra(Constants.JACKPAL_EXTRA_WINDOW_HANDLE, consoleHandle);
                startActivityForResult(i, REQ_CONSOLE);
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    private void confirmStartGateway(final String instId, final boolean start) {
        String a = (start ? "start" : "stop");
        confirmDialog(getString(R.string.confirm), getString(R.string.touch_to, a, instId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performStartGateway(instId, start);
            }
        });
    }

    private void performStartGateway(final String instId, boolean start) {
        String a = (start ? "start" : "stop");
        Callback cb = new GatewayActionCallback(instId, a);
        if (start)
            topoModel.startGateway(instId, cb);
        else
            topoModel.stopGateway(instId, cb);

    }

    private void confirmRemoveGateway(final String instId) {
        confirmDialog(getString(R.string.touch_to, "remove", instId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performRemoveGateway(instId);
            }
        });
    }

    private void performRemoveGateway(final String instId) {
        showProgressBar(true);
        Log.d(TAG, String.format("performRemoveGateway: %s", instId));
//        Group grp = topoModel.getInstanceGroup(instId);
//        if (grp != null && grp.getServices().size() == 1) {
//            orphanedGroup = grp;
//        }
        topoModel.removeGateway(instId, true, new DeleteSvcCallback(instId));
    }

    private void onTopologyLoaded() {
        Log.d(TAG, "onTopologyLoaded");
        if (topoModel.getTopology() == null) {
            ServerInfo svr = app.getCurrentServer();
            finishWithAlert(String.format("Could not load topology. Is %s available?", svr == null ? "Node Manager" : svr.displayString()));
            return;
        }
        if (deployModel.hasLoaded()) {
            onDetailsLoaded();
            return;
        }
        deployModel.loadDeploymentDetails(new DdCallback());
    }

    private void onDetailsLoaded() {
        Log.d(TAG, "onDetailsLoaded");
        replaceFragment(ctr01.getId(), TopologyFragment.newInstance(), "topo");
        Topology t = topoModel.getTopology();
        Collection<Service> svcs = t.getServices(Topology.ServiceType.gateway);
        for (Service s: svcs) {
            topoModel.getGatewayStatus(s.getId(), new GatewayActionCallback(s.getId(), "status"));
        }
        swipeRefresh.setRefreshing(false);
        showProgressBar(false);
    }

    private void updateStatus(String sid, int stat) {
        topoModel.setGatewayStatus(sid, stat);
        if (stat != TopologyModel.GATEWAY_STATUS_RUNNING) {
            topoModel.clearSvcConfig(sid);
        }
        if (isVisible()) {
            Log.d(TAG, String.format("updateStatus %s: %d", sid, stat));
        }
    }

    private class GatewayActionCallback extends BaseCallback {

        String sid;
        String action;

        public GatewayActionCallback(String sid, String action) {
            super();
            this.sid = sid;
            this.action = action;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            int stat = TopologyModel.GATEWAY_STATUS_UNKNOWN;
            JsonObject j = jsonHelper.parseAsObject(body);
            if (j != null && j.has("result")) {
                final String s = j.get("result").getAsString();
                stat = TopologyModel.parseStatus(s);
            }
            final int s = ("stop".equals(action) ? TopologyModel.GATEWAY_STATUS_NOT_RUNNING : stat);
            updateStatus(sid, s);
        }
    }

    private void onSvcDeleted(String id) {
        showToast(String.format("%s deleted", id));
        final Group og = topoModel.getOrphanedGroup();
        if (og != null) {
            confirmDialog(String.format("Delete empty group %s", og.getName()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    topoModel.removeGroup(og, new BaseCallback() {
                        @Override
                        protected void onSuccessResponse(int code, String msg, String body) {
                            showToast(String.format("%s deleted", og.getName()));
                            refresh();
                        }
                    });
                }
            });
            return;
        }
        refresh();
    }

    private class DeleteSvcCallback extends BaseCallback {

        String sid;

        public DeleteSvcCallback(String sid) {
            super();
            this.sid = sid;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            onSvcDeleted(sid);
        }
    }

    private class TestCallback extends BaseCallback {

        public TestCallback() {
            super();
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {

        }
    }

    private class DdCallback extends BaseCallback {

        String instId;

        public DdCallback() {
            super();
            instId = null;
        }

        public DdCallback(String instId) {
            this();
            this.instId = instId;
        }

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonObject j = jsonHelper.parseAsObject(body);
            if (j == null)
                return;
            if (j.has(JsonHelper.PROP_RESULT))
                j = j.get(JsonHelper.PROP_RESULT).getAsJsonObject();
            deployModel.setDeploymentDetails(j);
            if (instId == null)
                onDetailsLoaded();
            else
                showDeployDetails(instId);
        }
    }

    private class TopoCallback extends BaseCallback {

        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            JsonObject obj = jsonHelper.parseAsObject(body);
            if (obj != null && obj.has("result")) {
                obj = obj.getAsJsonObject("result");
            }
            topoModel.setTopology(jsonHelper.topologyFromJson(obj));
            onTopologyLoaded();
        }
    }

/*
    @Override
    protected void showSwipeRefresh(final boolean show) {
        Log.d(TAG, String.format("showSwipeRefresh: %s", show));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean do_it = false;
                if (show) {
                    do_it = (!swipeRefresh.isRefreshing());
                }
                else {
                    do_it = swipeRefresh.isRefreshing();
                }
                if (do_it) {
                    Log.d(TAG, "swipeRefresh toggled");
                    swipeRefresh.setRefreshing(show);
                }
            }
        });
    }

    @Subscribe
    public void onCertValidationEvent(CertValidationEvent evt) {  //final CertPath cp, final String alias) {
        final CertPath cp = evt.cp;
        final String alias = evt.alias;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Certificate c : cp.getCertificates()) {
            if (c.getType().equals("X.509")) {
                X509Certificate c509 = (X509Certificate) c;
                sb.append("[").append(++i).append("]: ").append(c509.getSubjectDN().toString()).append("\n");
            }
        }
        sb.append("\n").append(getString(R.string.add_to_truststore));

        confirmDialog(getString(R.string.cert_not_trusted), sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BaseApp.keystoreManager().addTrustedCert(alias, cp);
                showToast("Cert trusted " + alias);
                resetClient();
                refresh();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Cancelled");
            }
        });
    }
*/
}
