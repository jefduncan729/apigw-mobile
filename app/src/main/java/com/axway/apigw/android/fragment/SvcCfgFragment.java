package com.axway.apigw.android.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.adapter.BaseListAdapter;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.NotifyChangeEvent;
import com.axway.apigw.android.model.ServiceConfig;
import com.axway.apigw.android.util.Utilities;
import com.axway.apigw.android.view.BasicViewHolder;
import com.axway.apigw.android.view.CheckboxHolder;
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
public class SvcCfgFragment extends ListFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener  {

    private String instId;
    private ServiceConfig svcCfg;
    private TopologyModel topoModel;
    private JsonHelper jsonHelper = JsonHelper.getInstance();

    public static SvcCfgFragment newInstance(String instId, Observer obs) {
        SvcCfgFragment rv = new SvcCfgFragment();
        rv.instId = instId;
        rv.topoModel = TopologyModel.getInstance();
        rv.svcCfg = rv.topoModel.getSvcConfig(instId);
        if (obs != null)
            rv.svcCfg.addObserver(obs);
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
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object t = view.getTag();
        if (svcCfg == null || t == null)
            return;
        if (t instanceof SpinnerHolder) {
            SpinnerHolder h = (SpinnerHolder)t;
            svcCfg.changeString((String)h.getData(), (String)parent.getItemAtPosition(h.spinnerPosition()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class SvcCfgAdapter extends BaseAdapter {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_SYS = 1;
        public static final int TYPE_SVC_PORT = 2;
        public static final int TYPE_URI = 3;

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
//            entries.add(new Entry(svc.getName(), 1, buildInst()));
            entries.add(new Entry("System Settings", TYPE_HEADER));
            entries.add(new Entry("sys_settings", TYPE_SYS, buildSysSettings()));
            JsonArray svcs = sc.httpServices();
            if (svcs == null)
                return;
            for (int i = 0; i < svcs.size(); i++) {
                JsonObject json = svcs.get(i).getAsJsonObject();
                String nm = json.get("name").getAsString();
                ServiceConfig.HttpService svc = sc.httpService(nm);
                entries.add(new Entry(nm, TYPE_HEADER));
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
            if (e.kind == TYPE_SYS)
                return R.layout.sys_cfg;
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
                convertView = View.inflate(getActivity(), getLayoutId(e), null);
                vh = new BasicViewHolder(convertView);
                vh.setViewType(e.kind);
                convertView.setTag(vh);
            }
            vh = (BasicViewHolder)convertView.getTag();

            switch (e.kind) {
                case TYPE_HEADER:
                    vh.setText1(e.name);
                    break;
                case TYPE_SYS:
                    buildSysCfg(convertView);
                    break;
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

        private void buildSysCfg(View v) {
            ViewGroup parent = (ViewGroup)v.findViewById(R.id.container01);
            if (parent == null)
                return;
            parent.addView(checkbox("recordInboundTransactions", sc.recordInbound()));
            parent.addView(checkbox("recordOutboundTransactions", sc.recordOutbound()));
            parent.addView(checkbox("recordCircuitPath", sc.recordCircuitPath()));
            parent.addView(checkbox("recordTrace", sc.recordTrace()));
            parent.addView(spinner("traceLevel", ArrayAdapter.createFromResource(getActivity(), R.array.trace_levels, android.R.layout.simple_spinner_item)));
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
}
