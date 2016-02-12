package com.axway.apigw.android.activity;

import android.app.LoaderManager;
import android.content.DialogInterface;
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
import com.axway.apigw.android.api.TopologyLoader;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.CertValidationEvent;
import com.axway.apigw.android.event.ClickEvent;
import com.axway.apigw.android.fragment.EmptyFragment;
import com.axway.apigw.android.fragment.TopologyFragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by su on 1/22/2016.
 */
public class TopologyActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Topology> {

    private static final String TAG = TopologyActivity.class.getSimpleName();

    public static final int MSG_CERT_ERR = Constants.MSG_BASE + 1001;
    public static final int MSG_LOADER_FINISHED = Constants.MSG_BASE + 1002;

    public static final int TOPO_LOADER = 1;
    public static final int STATUS_LOADER = 2;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.container01) ViewGroup ctr01;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private ApiClient client;
    private TopologyModel model;

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
        Bundle b = getIntent().getBundleExtra(Constants.EXTRA_SERVER_INFO);
        ServerInfo server = ServerInfo.fromBundle(b);
        if (server == null) {
            finishWithAlert("Please pass server info");
            return;
        }
        client = ApiClient.from(server);    //new ServerInfo("10.71.100.196", 8090, true, "admin", "changeme"));
        model = new TopologyModel(client);
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
                String instId = (String)svh.getText1Tag();
                showToast(String.format("select: %s", instId));
                return;
            }
            if (t instanceof String) {
                String[] tkns = ((String)t).split(":");
                if (tkns.length == 2) {
                    if ("add_inst".equals(tkns[0])) {
                        showToast(String.format("add inst: %s", tkns[1]));
                    }
                }
            }
        }
    }

    private void resetClient() {
        BaseApp.resetSocketFactory();
        client = null;
    }

/*
    private void checkCert() {
        client.checkCert(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final CertPath cp = BaseApp.certPathFromThrowable(e);
                if (cp != null)
                    post(new CertValidationEvent("10.71.100.196_8090", cp));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
*/

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
        replaceFragment(ctr01.getId(), EmptyFragment.newInstance(), "topo");
        Log.d(TAG, "initLoader");
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
//        Dialog dlg = new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.cert_not_trusted))
//                .setMessage(sb.toString())
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(TopologyActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        BaseApp.keystoreManager().addTrustedCert(alias, cp);
//                        Toast.makeText(TopologyActivity.this, "Cert trusted " + alias, Toast.LENGTH_SHORT).show();
//                        resetClient();
//                        refresh();
//                    }
//                })
//                .create();
//        dlg.show();
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
                msg = "messaging";
                break;
            case R.id.action_gateway_kps:
                msg = "kps";
                break;
            case R.id.action_deployment_details:
                msg = "deploy details";
                break;
            case R.id.action_save_deploy_archive:
                testAddGateway();
//                msg = "save archive";
                break;
        }
        if (!TextUtils.isEmpty(msg)) {
            showToast(String.format("%s: %s", msg, instId));
        }
    }

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

    private void confirmStartGateway(final String instId, final boolean start) {
        String a = (start ? "start" : "stop");
        confirmDialog(getString(R.string.confirm), getString(R.string.touch_to, a, instId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performStartGateway(instId, start);
            }
        });
//        AlertDialog dlg = new AlertDialog.Builder(this)
//                .setTitle(R.string.confirm)
//                .setMessage(getString(R.string.touch_to, a, instId))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        performStartGateway(instId, start);
//                    }
//                })
//                .setNegativeButton(android.R.string.no, Constants.NOOP_LISTENER)
//                .create();
//        dlg.show();
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
        model.removeGateway(instId, true, new DeleteCallback(instId));
    }

    @Override
    public Loader<Topology> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, String.format("onCreateLoader: %d", i));
        if (i == TOPO_LOADER) {
            return new TopologyLoader(this, client);
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
            Log.d(TAG, "no topology");
            return;
        }
        replaceFragment(ctr01.getId(), TopologyFragment.newInstance(model), "topo");
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

    private abstract class BaseCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, String.format("call failed: %s", call), e);
        }

    }

    private class PostCallback extends BaseCallback {
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                Log.d(TAG, "POST succeeded");
                refresh();
            }
            else {
                Log.d(TAG, "POST failed");
            }
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
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, String.format("onResponse: %s", response));
            int stat = TopologyModel.GATEWAY_STATUS_UNKNOWN;
            if (response.isSuccessful()) {
                JsonObject j = JsonHelper.getInstance().parseAsObject(response.body().string());
                if (j != null && j.has("result")) {
                    final String s = j.get("result").getAsString();
                    stat = TopologyModel.parseStatus(s);
                }
            }
            final int s = ("stop".equals(action) ? TopologyModel.GATEWAY_STATUS_NOT_RUNNING : stat);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateStatus(sid, s);
                }
            });
        }
    }

    private void onDeleted(String id, boolean success) {
        showToast(success ? String.format("%s deleted", id) : String.format("delete failed: %s", id));
    }

    private class DeleteCallback extends BaseCallback {

        String sid;

        public DeleteCallback(String sid) {
            super();
            this.sid = sid;
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, String.format("onResponse: %s", response));
            final boolean success = response.isSuccessful();
//            if (response.isSuccessful()) {
//                JsonObject j = JsonHelper.getInstance().parseAsObject(response.body().string());
//                if (j != null && j.has("result")) {
//                    final String s = j.get("result").getAsString();
//                }
//            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onDeleted(sid, success);
                }
            });
        }
    }

    private void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(show);
            }
        });
    }
}
