package com.axway.apigw.android.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.adapter.BaseListAdapter;
import com.axway.apigw.android.adapter.JsonArrayAdapter;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.NotifyChangeEvent;
import com.axway.apigw.android.model.ServiceConfig;
import com.axway.apigw.android.util.Utilities;
import com.axway.apigw.android.view.BasicViewHolder;
import com.axway.apigw.android.view.CheckboxHolder;
import com.axway.apigw.android.view.RecCfgHolder;
import com.axway.apigw.android.view.SpinnerHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vordel.api.topology.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by jef on 3/10/16.
 */
public class SvcCfgFragment extends ListFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = SvcCfgFragment.class.getSimpleName();
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SYS = 1;
    public static final int TYPE_SVC_PORT = 2;
    public static final int TYPE_URI = 3;
    public static final int TYPE_SERVICE = 4;


    private String instId;
    private ServiceConfig svcCfg;
    private TopologyModel topoModel;
    private JsonHelper jsonHelper = JsonHelper.getInstance();
    private Service svc;

    public static SvcCfgFragment newInstance(String instId, ServiceConfig sc) {  //, Observer obs) {
        SvcCfgFragment rv = new SvcCfgFragment();
        rv.instId = instId;
        rv.topoModel = TopologyModel.getInstance();
        rv.svc = rv.topoModel.getGatewayById(rv.instId);
        rv.svcCfg = sc; //new ServiceConfig(instId, json); //rv.topoModel.getSvcConfig(instId);
//        if (obs != null)
//            rv.svcCfg.addObserver(obs);
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    private void refreshAdapter() {
        if (svcCfg == null) {
            setListAdapter(null);
            return;
        }
        setListAdapter(new SvcCfgAdapter(svcCfg));
    }

    private void updateBoolean(String nm, boolean newVal) {
    }

    @Override
    public void onClick(View v) {
        Object t = v.getTag();
        if (svcCfg == null || t == null)
            return;
        if (t instanceof CheckboxHolder) {
            CheckboxHolder h = (CheckboxHolder)t;
            svcCfg.changeBoolean((String) h.getData(), h.isChecked());
            return;
        }
        if (t instanceof BasicViewHolder) {
            BasicViewHolder h = (BasicViewHolder)t;
            JsonObject j = (JsonObject)h.getData();
            if (j == null)
                return;
            Log.d(TAG, String.format("onClick: %s", j.toString()));
            if (j.has("port")) {
                editPort(h, j);
                return;
            }
            if (j.has("uri")) {
                editUri(h, j);
            }
        }
    }

    private void validatePort(DialogInterface dlg) throws ValidationException {

    }

    private void savePort(DialogInterface dlg, BasicViewHolder h, JsonObject j) {

    }

    private void populatePort(RecCfgHolder rch, JsonObject j) {
        JsonHelper.Wrapper w = JsonHelper.getInstance().wrap(j);
        rch.recordInbound(w.getBoolean("recordInboundTransactions"))
            .recordOutbound(w.getBoolean("recordOutboundTransactions"))
            .recordPath(w.getBoolean("recordCircuitPath"))
            .recordTrace(w.getBoolean("recordTrace"));
    }

    private void editPort(final BasicViewHolder h, final JsonObject j) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(getActivity());
        View v = View.inflate(getActivity(), R.layout.port_cfg, null);
        final RecCfgHolder rch = new RecCfgHolder(v);
        v.setTag(rch);
        bldr.setView(v)
                .setTitle(String.format("Port %s", j.get("port").getAsString()))
                .setCancelable(true);
        bldr.setPositiveButton(android.R.string.yes, Constants.NOOP_LISTENER);
        bldr.setNegativeButton(android.R.string.no, Constants.NOOP_LISTENER);
        final AlertDialog dlg = bldr.create();

        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            validatePort(dlg);
                            savePort(dlg, h, j);
                            dlg.dismiss();
                        }
                        catch (ValidationException e) {
                            return;
                        }
                    }
                });
                populatePort(rch, j);
            }
        });
        dlg.show();

    }

    private void editUri(BasicViewHolder h, JsonObject j) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object t = view.getTag();
        if (svcCfg == null || t == null)
            return;
//        if (t instanceof SpinnerHolder) {
//            SpinnerHolder h = (SpinnerHolder)t;
//            svcCfg.changeString((String)h.getData(), (String)parent.getItemAtPosition(h.spinnerPosition()));
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object t = view.getTag();
        if (svcCfg == null || t == null)
            return;
        BasicViewHolder vh = (BasicViewHolder)t;
        SvcCfgAdapter.Entry e = (SvcCfgAdapter.Entry)vh.getData();
        if (e == null)
            return;
        Intent intent = null;
        switch (e.kind) {
            case TYPE_HEADER:
                break;
            case TYPE_SERVICE:
                break;
            case TYPE_SYS:
            case TYPE_SVC_PORT:
            case TYPE_URI:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra(Constants.EXTRA_ITEM_TYPE, e.kind);
                if (e.obj != null) {
                    intent.putExtra(Constants.EXTRA_JSON_ITEM, e.obj.toString());
                }
                break;
        }
        if (intent != null) {
            BaseApp.post(new ActionEvent(R.id.action_select, intent));
        }
    }

    private class SvcCfgAdapter extends BaseAdapter {

        ServiceConfig sc;
        List<Entry> entries;
        LayoutInflater inflater;

        public class Entry {
            String name;
            int kind;
            JsonObject obj;

            public Entry(String name, int kind) {
                this(name, kind, null);
            }

            public Entry(String name, int kind, JsonObject obj) {
                super();
                this.name = name;
                this.kind = kind;
                this.obj = obj;
            }
        }

        public SvcCfgAdapter(ServiceConfig sc) {
            super();
            this.sc = sc;
            this.inflater = LayoutInflater.from(getActivity());
            build();
        }

        private JsonObject buildSysSettings() {
            JsonObject json = new JsonObject();
            json.addProperty("recordInboundTransactions", sc.recordInbound());
            json.addProperty("recordOutboundTransactions", sc.recordOutbound());
            json.addProperty("recordCircuitPath", sc.recordCircuitPath());
            json.addProperty("recordTrace", sc.recordTrace());
            json.addProperty("traceLevel", sc.traceLevel());
            return json;
        }

        private void build() {
            entries = new ArrayList<>();
//            entries.add(new Entry("APIGW Configuration", 0, buildRoot()));
            entries.add(new Entry("System Settings", TYPE_HEADER));
            entries.add(new Entry(svc.getName(), TYPE_SYS));
//            entries.add(new Entry("sys_settings", TYPE_SYS, buildSysSettings()));
            JsonArray svcs = sc.httpServices();
            if (svcs == null)
                return;
            for (int i = 0; i < svcs.size(); i++) {
                JsonObject json = svcs.get(i).getAsJsonObject();
                String nm = json.get("name").getAsString();
                entries.add(new Entry(nm, TYPE_HEADER));
                ServiceConfig.HttpService svc = sc.httpService(nm);
                JsonArray ports = svc.ports();
                if (ports != null) {
                    for (int j = 0; j < ports.size(); j++) {
                        JsonObject p = ports.get(j).getAsJsonObject();
                        entries.add(new Entry(p.get("port").getAsString(), TYPE_SVC_PORT, p));
                    }
                }
                JsonArray uris = svc.uris();
                if (uris != null) {
                    for (int j = 0; j < uris.size(); j++) {
                        JsonObject u = uris.get(j).getAsJsonObject();
                        entries.add(new Entry(u.get("uri").getAsString(), TYPE_URI, u));
                    }
                }
            }
        }

        @Override
        public int getCount() {
            if (entries == null)
                return 0;
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            if (entries == null)
                return null;
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        protected int getLayoutId(Entry e) {
            if (e.kind == TYPE_HEADER)
                return R.layout.list_hdr;
//            if (e.kind == TYPE_SYS || e.kind == TYPE_SERVICE)
//                return R.layout.titled_card;
            return android.R.layout.simple_list_item_activated_2;
//            if (e.kind == TYPE_SVC_PORT)
//            if (e.kind == TYPE_URI)
//                return android.R.layout.simple_list_item_activated_2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Entry e = (Entry)getItem(position);
            if (e == null)
                return convertView;
            BasicViewHolder vh;
            if (convertView == null || ((BasicViewHolder)convertView.getTag()).getViewType() != e.kind) {
                convertView = inflater.inflate(getLayoutId(e), null);
                vh = new BasicViewHolder(convertView);
                vh.setViewType(e.kind);
                vh.setData(e);
                convertView.setTag(vh);
            }
            vh = (BasicViewHolder)convertView.getTag();

            switch (e.kind) {
                case TYPE_HEADER:
                case TYPE_SYS:
                case TYPE_SERVICE:
                    vh.setText1(e.name);
                    break;
//                case TYPE_SYS:
//                    buildSysCfg(convertView);
//                    break;
//                case TYPE_SERVICE:
//                    buildService(e.name, convertView);
//                    break;
                case TYPE_SVC_PORT:
                    vh.setText1(String.format("Port %s", e.name));
                    vh.setText2(buildSvcPort(e.obj));
                    break;
                case TYPE_URI:
                    vh.setText1(String.format("URI %s", e.name));
                    vh.setText2(buildUri(e.obj));
                    break;
            }
            return convertView;
        }

        private View checkbox(String nm, boolean val) {
            View v = inflater.inflate(R.layout.checkbox, null);
            CheckboxHolder h = new CheckboxHolder(v);
            v.setTag(h);
            h.setData(nm);
            h.setLabel(Utilities.splitCamelCase(nm)).setChecked(val).setListener(SvcCfgFragment.this);
            return v;
        }

        private View spinner(String nm, ArrayAdapter adapter) {
            View v = inflater.inflate(R.layout.spinner, null);
            SpinnerHolder h = new SpinnerHolder(v);
            v.setTag(h);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            h.setData(nm);
            h.setLabel(Utilities.splitCamelCase(nm)).setAdapter(adapter);
            return v;
        }

        private View divider() {
            return inflater.inflate(R.layout.section_divider, null);
        }

        private void buildSysCfg(View v) {
            ViewGroup parent = (ViewGroup)v.findViewById(R.id.container03);
            if (parent == null)
                return;
            parent.removeAllViews();
            TextView txt = (TextView)v.findViewById(android.R.id.title);
            txt.setText("System Settings");
            View recV = inflater.inflate(R.layout.rec_cfg, null);
            RecCfgHolder recH = new RecCfgHolder(recV, sc);
            parent.addView(recV);
            parent.addView(spinner("traceLevel", ArrayAdapter.createFromResource(getActivity(), R.array.trace_levels, android.R.layout.simple_spinner_item)));
        }

        private void buildService(String nm, View v) {
            ViewGroup parent = (ViewGroup)v.findViewById(R.id.container03);
            if (parent == null)
                return;
            parent.removeAllViews();
            TextView txt = (TextView)v.findViewById(android.R.id.title);
            txt.setText(nm);
            ServiceConfig.HttpService svc = sc.httpService(nm);
            JsonArray ports = svc.ports();

            if (ports != null) {
                for (int j = 0; j < ports.size(); j++) {
                    JsonObject p = ports.get(j).getAsJsonObject();
                    ViewGroup pv = (ViewGroup)inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
                    BasicViewHolder vh = new BasicViewHolder(pv);
                    vh.setText1(String.format("Port %s", p.get("port").getAsString()))
                        .setText2(buildSvcPort(p))
                        .setData(p);
                    pv.setTag(vh);
                    pv.setOnClickListener(SvcCfgFragment.this);
                    parent.addView(pv);
//                    entries.add(new Entry(p.get("port").getAsString(), TYPE_SVC_PORT, p));
                }
            }
            JsonArray uris = svc.uris();
            if (uris != null) {
                for (int j = 0; j < uris.size(); j++) {
                    JsonObject u = uris.get(j).getAsJsonObject();
                    ViewGroup pv = (ViewGroup)inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
                    BasicViewHolder vh = new BasicViewHolder(pv);
                    vh.setText1(String.format("URI %s", u.get("uri").getAsString()))
                        .setText2(buildUri(u))
                        .setData(u);
                    pv.setTag(vh);
                    pv.setOnClickListener(SvcCfgFragment.this);
                    parent.addView(pv);
//                    entries.add(new Entry(u.get("uri").getAsString(), TYPE_URI, u));
                }
            }
        }

        private String buildSvcPort(JsonObject json) {
            StringBuilder sb = new StringBuilder();
            if (json == null)
                return sb.toString();
            JsonHelper.Wrapper w = jsonHelper.wrap(json);
            sb.append("type: ").append(w.getString("type"));
            sb.append(", enabled: ").append(w.getBoolean("enable"));
            sb.append(", traceLevel: ").append(w.getString("traceLevel"));
            w = null;
            return sb.toString();
        }

        private String buildUri(JsonObject json) {
            StringBuilder sb = new StringBuilder();
            if (json == null)
                return sb.toString();
            JsonHelper.Wrapper w = jsonHelper.wrap(json);
            sb.append("enabled: ").append(w.getBoolean("enable"));
            return sb.toString();
        }

    }

/*
    private class PortAdapter extends JsonArrayAdapter {

        public PortAdapter(Context ctx, JsonArray array) {
            super(ctx, array);
        }

        @Override
        protected View createView(int i) {
            return inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
        }

        @Override
        protected void populateView(View rv, JsonObject j) {
            BasicViewHolder vh = (BasicViewHolder)rv.getTag();
            vh.setText1(String.format("Port %s", j.get("port").getAsString()));
            vh.setText2(buildSvcPort(j));
        }

        private String buildSvcPort(JsonObject json) {
            StringBuilder sb = new StringBuilder();
            if (json == null)
                return sb.toString();
            JsonHelper.Wrapper w = jsonHelper.wrap(json);
            sb.append("type: ").append(w.getString("type"));
            sb.append(", enabled: ").append(w.getBoolean("enable"));
            sb.append(", traceLevel: ").append(w.getString("traceLevel"));
            w = null;
            return sb.toString();
        }
    }

    private class UriAdapter extends JsonArrayAdapter {

        public UriAdapter(Context ctx, JsonArray array) {
            super(ctx, array);
        }

        @Override
        protected View createView(int i) {
            return inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
        }

        @Override
        protected void populateView(View rv, JsonObject j) {
            BasicViewHolder vh = (BasicViewHolder)rv.getTag();
            vh.setText1(String.format("URI %s", j.get("uri").getAsString()));
            vh.setText2(buildUri(j));
        }

        private String buildUri(JsonObject json) {
            StringBuilder sb = new StringBuilder();
            if (json == null)
                return sb.toString();
            JsonHelper.Wrapper w = jsonHelper.wrap(json);
            sb.append("enabled: ").append(w.getBoolean("enable"));
            return sb.toString();
        }
    }
*/
}
