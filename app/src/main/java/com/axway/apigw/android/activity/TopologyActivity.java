package com.axway.apigw.android.activity;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.api.TopologyLoader;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.CertValidationEvent;
import com.axway.apigw.android.event.ClickEvent;
import com.axway.apigw.android.fragment.EmptyFragment;
import com.axway.apigw.android.fragment.TopologyFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.view.ServiceViewHolder;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 1/22/2016.
 */
public class TopologyActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Topology> {

    private static final String TAG = TopologyActivity.class.getSimpleName();

    public static final int MSG_CERT_ERR = Constants.MSG_BASE + 1001;
    public static final int MSG_LOADER_FINISHED = Constants.MSG_BASE + 1002;

    public static final int REQ_EDIT_SVC = 1;
    public static final int REQ_ADD_SVC = 2;

    public static final int TOPO_LOADER = 1;
    public static final int STATUS_LOADER = 2;

    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.container01) ViewGroup ctr01;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private TopologyModel model;
    private Group orphanedGroup;

    @Override
    protected boolean onHandleMessage(Message msg) {
        int w = msg.what;
        switch (w) {
            case MSG_LOADER_FINISHED:
                if (msg.arg1 == TOPO_LOADER) {
                    onTopologyLoaded();
                    return true;
                }
            case Constants.MSG_REFRESH:
                refresh();
                return true;
        }
        return super.onHandleMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BaseApp.keystoreManager().removeKeystore();
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
        showProgress(true);
        ServerInfo server = BaseApp.getInstance().getCurrentServer();
        model = TopologyModel.getInstance();    //new TopologyModel(BaseApp.getInstance().getApiClient());
        toolbar.setTitle("Topology");
        toolbar.setSubtitle(server.displayString());
        getLoaderManager().initLoader(TOPO_LOADER, null, this);
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
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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
            int stat = model.getCachedStatus(instId);
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
                editService(svc);
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void resetClient() {
        BaseApp.resetSocketFactory();
    }

    private void editService(Service svc) {
        Intent i = new Intent(this, EditServiceActivity.class);
        i.setAction(Intent.ACTION_EDIT);
        Group g = model.getInstanceGroup(svc.getId());
        i.putExtra(Constants.EXTRA_JSON_ITEM, JsonHelper.getInstance().toJson(svc).toString());
        i.putExtra(Constants.EXTRA_GROUP_ID, g.getId());
        startActivityForResult(i, REQ_EDIT_SVC);
    }

    private void addService(String grpId) {
        Intent i = new Intent(this, EditServiceActivity.class);
        Service svc = new Service();
        i.setAction(Intent.ACTION_INSERT);
        i.putExtra(Constants.EXTRA_JSON_ITEM, JsonHelper.getInstance().toJson(svc).toString());
        i.putExtra(Constants.EXTRA_GROUP_ID, grpId);
        startActivityForResult(i, REQ_ADD_SVC);
    }

    protected void post(final Object evt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseApp.post(evt);
            }
        });
    }

    private void refresh() {
        showProgress(true);
//        replaceFragment(ctr01.getId(), EmptyFragment.newInstance(), "topo");
        getSupportFragmentManager().beginTransaction().replace(ctr01.getId(), EmptyFragment.newInstance(), "topo").commit();
        Log.d(TAG, "initLoader");
        getLoaderManager().destroyLoader(TOPO_LOADER);
        getLoaderManager().initLoader(TOPO_LOADER, null, this);
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

    @Subscribe
    public void onClickEvent(ClickEvent evt) {
        onClick(evt.view);
    }

    @Subscribe
    public void onActionEvent(ActionEvent evt) {
        String msg = null;
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
            case R.id.action_save_deploy_archive:
                msg = "save deployment archive";
                break;
            case R.id.action_policy_props:
            case R.id.action_env_props:
                manageProperties(instId, evt.id);
                break;
        }
        if (!TextUtils.isEmpty(msg)) {
            showToast(String.format("%s: %s", msg, instId));
        }
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

    private void requestDeployDetails(String instId) {
        DeploymentDetails dd = DeploymentModel.getInstance().getDeploymentDetails(instId);
        if (dd == null) {
            DeploymentModel.getInstance().getDeploymentDetails(new DdCallback(instId));
        }
        else {
            showDeployDetails(instId);
        }
    }

    private void showDeployDetails(String instId) {
//        Log.d(TAG, String.format("showDeployDetails: %s, %s", instId, dd));
        Intent i = new Intent(this, DeployDetailsActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        startActivity(i);
    }

    private void manageProperties(String instId, int what) {
//        DeploymentDetails dd = DeploymentModel.getInstance().getDeploymentDetails(instId);
//        if (dd == null)
//            return;
//        DeploymentDetails.Props props = (what == R.id.action_policy_props ? dd.getPolicyProperties() : dd.getEnvironmentProperties());
        Log.d(TAG, String.format("manageProperties: %s, %s", instId, what));
        Intent i = new Intent(this, ManagePropsActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_TYPE, what);
        startActivity(i);
    }

    private void testDeployClient() {
        Group g = model.getGroupById("group-6");
        ApiClient client = BaseApp.getInstance().getApiClient();
        Request req = client.createRequest(String.format("api/deployment/archive/%s", g.getId()));
        client.executeAsyncRequest(req, new TestCallback());
    }

/*
    private void testAddGateway() {
        Group g = model.getGroupById("group-6");
        Service svc = new Service();
        svc.setManagementPort(28085);
        svc.setHostID("host-1");
        svc.setEnabled(true);
        svc.setName("New Gateway");
        svc.setScheme("HTTPS");
        svc.setType(Topology.ServiceType.gateway);
        model.addGateway(g, svc, 28080, new PostCallback());
    }
*/

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
            model.startGateway(instId, cb);
        else
            model.stopGateway(instId, cb);

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
        showProgress(true);
        Group grp = model.getInstanceGroup(instId);
        if (grp != null && grp.getServices().size() == 1) {
            orphanedGroup = grp;
        }
        model.removeGateway(instId, true, new DeleteSvcCallback(instId));
    }

    @Override
    public Loader<Topology> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, String.format("onCreateLoader: %d", i));
        if (i == TOPO_LOADER) {
            model.reset();
            return new TopologyLoader(this, BaseApp.getInstance().getApiClient());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Topology> loader, Topology o) {
        model.setTopology(o);
        Log.d(TAG, String.format("onLoadFinished: %d", loader.getId()));
        postMessage(MSG_LOADER_FINISHED, loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Topology> loader) {
        Log.d(TAG, String.format("onLoaderReset: %d", loader.getId()));
    }

    private void onTopologyLoaded() {
        if (model.getTopology() == null) {
            ServerInfo svr = BaseApp.getInstance().getCurrentServer();
            finishWithAlert(String.format("Could not load topology. Is %s available?", svr == null ? "Node Manager" : svr.displayString()));
            return;
        }

        orphanedGroup = null;
        replaceFragment(ctr01.getId(), TopologyFragment.newInstance(model), "topo");
        DeploymentModel.getInstance().getDeploymentDetails(new DdCallback());
        Topology t = model.getTopology();
        Collection<Service> svcs = t.getServices(Topology.ServiceType.gateway);
        for (Service s: svcs) {
            model.getGatewayStatus(s.getId(), new GatewayActionCallback(s.getId(), "status"));
        }
        showProgress(false);
    }

    private void updateStatus(String sid, int stat) {
        Log.d(TAG, String.format("updateStatus %s: %d", sid, stat));
        if (model != null)
            model.setGatewayStatus(sid, stat);
    }
/*

    protected abstract class BaseCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            showProgress(false);
            Log.e(TAG, String.format("call failed: %s", call), e);
            showToast(String.format("%s", e.getMessage()));
        }

        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                showProgress(false);
                final String body = response.body().string();
                response.body().close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSuccessResponse(response.code(), response.message(), body);
                    }
                });
            }
            else {
                onFailure(call, new IOException(String.format("%d %s", response.code(), (response.message()))));
            }
        }

        abstract protected void onSuccessResponse(int code, String msg, String body);
    }
*/
/*

    private class PostCallback extends BaseCallback {
        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            refresh();
        }
    }
*/

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
            JsonObject j = JsonHelper.getInstance().parseAsObject(body);
            if (j != null && j.has("result")) {
                final String s = j.get("result").getAsString();
                stat = TopologyModel.parseStatus(s);
            }
            final int s = ("stop".equals(action) ? TopologyModel.GATEWAY_STATUS_NOT_RUNNING : stat);
            updateStatus(sid, s);
        }
    }

    private void onDeleted(String id) {
        showToast(String.format("%s deleted", id));
        if (orphanedGroup != null) {
            confirmDialog(String.format("Delete empty group %s", orphanedGroup.getName()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    model.removeGroup(orphanedGroup, new BaseCallback() {
                        @Override
                        protected void onSuccessResponse(int code, String msg, String body) {
                            showToast(String.format("%s deleted", orphanedGroup.getName()));
                            orphanedGroup = null;
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
            onDeleted(sid);
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
            JsonObject j = JsonHelper.getInstance().parseAsObject(body);
            if (j == null)
                return;
            if (j.has(JsonHelper.PROP_RESULT))
                j = j.get(JsonHelper.PROP_RESULT).getAsJsonObject();
//            DeploymentDetails dd = JsonHelper.getInstance().deploymentDetailsFromJson(j);
            DeploymentModel.getInstance().setDeploymentDetails(j);
            consistencyCheck();
            if (instId != null)
                showDeployDetails(instId);
        }
    }

    private void consistencyCheck() {
        for (Group g: model.getTopology().getGroups(Topology.ServiceType.gateway)) {
            Map<String, String> ids = new HashMap<>();
            Collection<Service> svcs = g.getServices();
            for (Service s: svcs) {
                DeploymentDetails dd = DeploymentModel.getInstance().getDeploymentDetails(s.getId());
                ids.put(s.getId(), dd.getRootProperties().getId());
            }
            String id = null;
            for (String k: ids.keySet()) {
                if (id == null) {
                    id = ids.get(k);
                    continue;
                }
                if (!id.equals(ids.get(k))) {
                    showToast(String.format("%s has inconsistent deployments!", g.getName()));
                    break;
                }
                id = ids.get(k);
            }
        }
    }

    @Override
    protected void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(show);
            }
        });
    }
}
