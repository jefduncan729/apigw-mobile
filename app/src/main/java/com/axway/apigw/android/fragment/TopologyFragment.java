package com.axway.apigw.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.event.ClickEvent;
import com.axway.apigw.android.event.RefreshEvent;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.view.BasicViewHolder;
import com.axway.apigw.android.view.ServiceViewHolder;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/3/2016.
 */
public class TopologyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = TopologyFragment.class.getSimpleName();

    private TopologyModel topoModel;
    private DeploymentModel deployModel;
    private BaseApp app;

    @Bind(R.id.container01) ViewGroup parentView;

    public static TopologyFragment newInstance() {
        return newInstance(null);
    }

    public static TopologyFragment newInstance(Bundle args) {
        TopologyFragment rv = new TopologyFragment();
        if (args != null)
            rv.setArguments(args);
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.single_pane, null);
        ButterKnife.bind(this, rv);
        parentView.removeAllViews();
        app.resetColors();
        if (topoModel == null) {
            BaseApp.post(new RefreshEvent(this));
            return rv;
        }
        Topology t = topoModel.getTopology();
        if (t == null) {
            return rv;
        }
        ViewGroup info = (ViewGroup)inflater.inflate(R.layout.titled_card, null);
        buildInfoCard(t, info, inflater);
        parentView.addView(info);
        Collection<Service> svcs = t.getServices(Topology.ServiceType.gateway);
        List<Group> grps = new ArrayList<>();
        for (Service s: svcs) {
            Group g = topoModel.getInstanceGroup(s.getId());
            if (!grps.contains(g))
                grps.add(g);
        }
        for (Group grp: grps) {
            ViewGroup card = (ViewGroup)inflater.inflate(R.layout.titled_card, null);
            buildGroupCard(t, grp, card, inflater);
            parentView.addView(card);
        }
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private View newSimpleText(LayoutInflater inflater, String txt) {
        View rv = inflater.inflate(R.layout.simple_text, null);
        BasicViewHolder h1 = new BasicViewHolder(rv);
        rv.setTag(h1);
        h1.setText1(txt);
        return rv;
    }

    private View newLabelText(LayoutInflater inflater, String lbl, String txt) {
        View rv = inflater.inflate(R.layout.labeled_text, null);
        BasicViewHolder h1 = new BasicViewHolder(rv);
        if (!lbl.endsWith(": "))
            lbl = lbl + ": ";
        rv.setTag(h1);
        h1.setText1(lbl)
            .setText2(txt);
        return rv;
    }

    private View newDivider(LayoutInflater inflater) {
        return inflater.inflate(R.layout.section_divider, null);
    }

    private String plural(int n) {
        return (n == 1 ? "" : "s");
    }

    private void buildInfoCard(Topology t, ViewGroup v, LayoutInflater inflater) {
        TextView ttl = (TextView)v.findViewById(android.R.id.title);
        ImageView img = (ImageView)v.findViewById(android.R.id.icon);
//        img.setImageResource(R.mipmap.server);
        ViewGroup ctr = (ViewGroup)v.findViewById(R.id.container03);
        ttl.setText("Details");
        ctr.addView(newLabelText(inflater, "Id", t.getId()));
        ctr.addView(newLabelText(inflater, "Product Version", t.getProductVersion()));
        ctr.addView(newLabelText(inflater, "Topology Version", Integer.toString(t.getVersion())));
        ctr.addView(newLabelText(inflater, "Last Updated", app.relativeTime(t.getTimestamp())));    //DateUtil.relativeTime(t.getTimestamp()
        DateUtils.getRelativeTimeSpanString(t.getTimestamp());
        int nh = t.getHosts().size();
        int ng = topoModel.getGroupNames().size();
        int ni = t.getServiceIds(Topology.ServiceType.gateway).size();
        ctr.addView(newSimpleText(inflater, String.format("%d host%s, %d group%s, %d instance%s", nh, plural(nh), ng, plural(ng), ni, plural(ni))));
    }

    private void buildGroupCard(Topology t, Group grp, ViewGroup v, LayoutInflater inflater) {
        TextView ttl = (TextView)v.findViewById(android.R.id.title);
        ImageView img = (ImageView)v.findViewById(android.R.id.icon);
        ViewGroup ctr = (ViewGroup)v.findViewById(R.id.container03);
        img.setImageResource(R.mipmap.ic_action_good_holo_light);
        if (deployModel.isInconsistentGroup(grp.getId())) {
            img.setImageResource(R.mipmap.ic_action_warning_holo_light);
        }
        img.setVisibility(View.VISIBLE);
        ttl.setText(String.format("%s (%s)", grp.getName(), grp.getId()));
        ttl.setTag(grp);
        ttl.setOnClickListener(this);
        registerForContextMenu(ttl);
        Collection<Service> svcs = grp.getServicesByType(Topology.ServiceType.gateway);
        for (Service svc: svcs) {
            if (ctr.getChildCount() > 0)
                ctr.addView(newDivider(inflater));
            ctr.addView(newServiceView(inflater, grp, svc));
        }
        ctr.addView(newDivider(inflater));
        ViewGroup addItem = (ViewGroup)inflater.inflate(R.layout.listitem_1, null);
        TextView a = (TextView)addItem.findViewById(android.R.id.text1);
        a.setText(R.string.add_inst);
        addItem.setOnClickListener(this);
        addItem.setTag(String.format("add_inst:%s", grp.getId()));
        ctr.addView(addItem);
    }

    private View newServiceView(LayoutInflater inflater, Group grp, Service svc) {
        View rv = inflater.inflate(R.layout.svc_item, null);
        ServiceViewHolder h = new ServiceViewHolder(rv);
        Host host = topoModel.getHostById(svc.getHostID());
        rv.setTag(h);
        String txt2 = String.format("host: %s\nMgmt port: %d, %s", host.getName(), svc.getManagementPort(), svc.getEnabled() ? "enabled" : "disabled");
        DeploymentDetails dd = deployModel.getDeploymentDetails(svc.getId());
        if (dd != null) {
            txt2 = String.format("%s\nDeployed %s (%s)", txt2, app.relativeTime(dd.getTimestamp()), dd.getUser());
        }
        h.setData(svc)
            .setText1(String.format("%s (%s)", svc.getName(), svc.getId()))
            .setText2(txt2)
            .setImageBitmap(app.drawBitmapForState(Topology.EntityType.Gateway, svc.getName()));
        h.setStatus(TopologyModel.GATEWAY_STATUS_CHECKING)
            .setStatClickListener(svc.getId(), this);
        topoModel.registerStatusObserver(svc.getId(), h);
        registerForContextMenu(rv);
        rv.setOnClickListener(this);
        return rv;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = BaseApp.getInstance();
        topoModel = TopologyModel.getInstance();
        deployModel = DeploymentModel.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getTag() == null)
            return;
        int p = 0;
        Object t = v.getTag();
        if (t instanceof Group) {
            Group g = (Group)t;
            Intent iData = new Intent();
            iData.putExtra(Constants.EXTRA_GROUP_ID, g.getId());
            menu.add(0, R.id.action_edit, p++, R.string.action_edit).setIntent(iData);  //.setIcon(R.mipmap.ic_action_discard_holo_light);
//            menu.add(0, R.id.action_delete, p++, R.string.action_delete).setIntent(iData);  //.setIcon(R.mipmap.ic_action_discard_holo_light);
            menu.setHeaderTitle(g.getName());
            return;
        }
        if (t instanceof ServiceViewHolder) {
            ServiceViewHolder svh = (ServiceViewHolder)t;
            Service svc = (Service)svh.getData();
            String id = svc.getId();
            Intent iData = new Intent();
            iData.putExtra(Constants.EXTRA_INSTANCE_ID, id);
            int stat = svh.getStatus();
            if (stat == TopologyModel.GATEWAY_STATUS_RUNNING) {
                menu.add(0, R.id.action_stop_gateway, p++, R.string.action_stop_gateway).setIntent(iData);
                menu.add(0, R.id.action_gateway_kps, p++, R.string.action_gateway_kps).setIntent(iData);
                menu.add(0, R.id.action_gateway_messaging, p++, R.string.action_gateway_messaging).setIntent(iData);
                menu.add(0, R.id.action_svc_cfg, p++, R.string.action_settings).setIntent(iData);
            }
            else if (stat == TopologyModel.GATEWAY_STATUS_NOT_RUNNING) {
                menu.add(0, R.id.action_start_gateway, p++, R.string.action_start_gateway).setIntent(iData);
                menu.add(0, R.id.action_delete, p++, R.string.action_delete).setIntent(iData);  //.setIcon(R.mipmap.ic_action_discard_holo_light);
            }
            menu.add(0, R.id.action_deployment_details, p++, R.string.action_deployment_details).setIntent(iData);
            menu.add(0, R.id.action_ssh_to_host, p, R.string.action_ssh_to_host).setIntent(iData);
//            menu.add(0, R.id.action_policy_props, p++, R.string.action_policy_props).setIntent(iData);
//            menu.add(0, R.id.action_env_props, p++, R.string.action_env_props).setIntent(iData);
            menu.setHeaderTitle(svc.getName());
            menu.setHeaderIcon(svh.getImageView().getDrawable());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BaseApp.post(new ActionEvent(item.getItemId(), item.getIntent()));
        return true;    //super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        BaseApp.post(new ClickEvent(view));
    }
}
