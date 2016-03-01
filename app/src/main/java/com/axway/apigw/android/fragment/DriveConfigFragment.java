package com.axway.apigw.android.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.activity.DriveConfigActivity;
import com.axway.apigw.android.adapter.BaseListAdapter;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by su on 11/10/2015.
 */
public class DriveConfigFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public interface Callbacks {
        public void onItemSelected(JsonObject item);
    }

    private Callbacks callbacks;
    private Button btn01;

    private DriveConfigActivity.DriveConfigModel model;
    //private DCListAdapter adapter;

    public static DriveConfigFragment newInstance(DriveConfigActivity.DriveConfigModel model) {
        DriveConfigFragment rv = new DriveConfigFragment();
        rv.model = model;
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
        btn01 = (Button)rv.findViewById(android.R.id.button1);
        if (btn01 != null)
            btn01.setVisibility(View.GONE);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnCreateContextMenuListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)){
            throw new IllegalStateException("activity must implement fragments callbacks");
        }
        callbacks = (Callbacks)activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayFolders();
    }

    private void displayFolders() {
        getListView().setOnItemClickListener(this);
        setListAdapter(new DCListAdapter(getActivity(), model.getFoldersAsList()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (callbacks == null)
            return;
        JsonObject obj = model.getFoldersAsList().get(i);
        callbacks.onItemSelected(obj);
    }

    private class DCListAdapter extends BaseListAdapter<JsonObject> {

        public DCListAdapter(Context ctx, List<JsonObject> list) {
            super(ctx, list);
        }

        @Override
        protected int getLayoutId() {
            return android.R.layout.simple_list_item_2;
        }

        @Override
        protected void viewCreated(View view) {
            BasicViewHolder holder = new BasicViewHolder(view);
            view.setTag(holder);
        }

        @Override
        protected void populateView(JsonObject item, View view) {
            BasicViewHolder holder = (BasicViewHolder)view.getTag();
            String key = item.get("key").getAsString();
            String typ = "";
            if (Constants.KEY_KPS_FOLDER.equals(key)) {
                typ = "KPS Backups";
            }
            else if (Constants.KEY_DEPLOY_FOLDER.equals(key)) {
                typ = "Deployment Backups";
            }
            holder.setText1(typ);
            holder.setText2(item.get("name").getAsString());
        }
    }
}
