package com.axway.apigw.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.adapter.BaseListAdapter;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by su on 2/26/2016.
 */
public class DeployDetailsFragment extends ListFragment implements AdapterView.OnItemClickListener {

//    @Bind(R.id.container01) ViewGroup parentView;
    private DeploymentDetails dd;

    public static DeployDetailsFragment newInstance(DeploymentDetails dd) {
        DeployDetailsFragment rv = new DeployDetailsFragment();
        rv.dd = dd; //DeploymentModel.getInstance().getDeploymentDetails(instId);
        return rv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
//        ButterKnife.bind(this, rv);
//        buildViews(inflater);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(this);
        getListView().setOnCreateContextMenuListener(this);
        setListAdapter(DeployDtlsAdapter.from(getActivity(), dd));   // list));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BasicViewHolder vh = (BasicViewHolder)view.getTag();
        DeployDtlsAdapter.Entry e = (DeployDtlsAdapter.Entry)vh.getData();
        if (e == null)
            return;
        BaseApp.post(new ItemSelectedEvent<DeployDtlsAdapter.Entry>(e));
    }
/*

    private void buildViews(LayoutInflater inflater) {
        if (dd == null || parentView == null)
            return;
        JsonHelper h = JsonHelper.getInstance();
        parentView.addView(titledText(inflater, "Root Properties", h.prettyPrint(dd.getRootProperties())));
        parentView.addView(titledText(inflater, "Policy Properties", h.prettyPrint(dd.getPolicyProperties())));
        parentView.addView(titledText(inflater, "Environment Properties", h.prettyPrint(dd.getEnvironmentProperties())));
    }

    private View titledText(LayoutInflater inflater, String title, String text) {
        View rv = inflater.inflate(R.layout.titled_text, null);
        BasicViewHolder vh = new BasicViewHolder(rv, android.R.id.title, android.R.id.text1);
        rv.setTag(vh);
        vh.setText1(title);
        vh.setText2(text);
        return rv;
    }
*/

    public static class DeployDtlsAdapter extends BaseListAdapter<DeployDtlsAdapter.Entry> {

        public static DeployDtlsAdapter from(Context ctx, DeploymentDetails dd) {
            List<Entry> list = new ArrayList<>();
            if (dd != null) {
                list.add(new Entry("Root Properties", dd.getRootProperties()));
                list.add(new Entry("Policy Properties", dd.getPolicyProperties()));
                list.add(new Entry("Environment Properties", dd.getEnvironmentProperties()));
            }
            return new DeployDtlsAdapter(ctx, list);
        }

        public DeployDtlsAdapter(Context ctx, List<Entry> list) {
            super(ctx, list);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.titled_text;
        }

        @Override
        protected void viewCreated(View view) {
            super.viewCreated(view);
            view.setTag(new BasicViewHolder(view, android.R.id.title, android.R.id.text1));
        }

        @Override
        protected void populateView(Entry item, View view) {
            BasicViewHolder vh = (BasicViewHolder)view.getTag();
            vh.setData(item);
            vh.setText1(item.title);
            vh.setText2(JsonHelper.getInstance().prettyPrint(item.props));
        }

        public static class Entry {
            public String title;
            //public DeploymentDetails.Props props;
            public JsonObject props;

            Entry(String title, JsonObject props) {
                super();
                this.title = title;
                this.props = props;
            }
        }
    }
}
