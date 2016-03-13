package com.axway.apigw.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.adapter.MqAdapter;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.JsonArrayLoader;
import com.axway.apigw.android.api.MessagingModel;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.model.MqDestination;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/18/2016.
 */
public class MqDestFragment extends JsonArrayFragment {

    private int kind;
    private String instId;
    private JsonArray data;

    public static MqDestFragment newInstance(String instId, int kind, JsonArray data) {
        MqDestFragment rv = new MqDestFragment();
        rv.kind = kind;
        rv.instId = instId;
        rv.data = data;
        return rv;
    }

    @Override
    protected ListAdapter createAdapter(JsonArray a) {
        return new MqAdapter(getActivity(), a, kind);
    }

    @Override
    protected void loadData(Bundle savedState) {
        onLoadFinished(data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (kind == MessagingModel.TYPE_QUEUE || kind == MessagingModel.TYPE_TOPIC) {
            AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)menuInfo;
            JsonObject o = (JsonObject)getListView().getItemAtPosition(cmi.position);
//            BasicViewHolder vh = (BasicViewHolder)v.getTag();
            MqDestination d = JsonHelper.getInstance().destinationFromJson(o);
            if (d == null)
                return;
            menu.setHeaderTitle(d.getName());
            int p = 0;
            Intent iData = new Intent();
//            iData.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
            iData.putExtra(Constants.EXTRA_ITEM_TYPE, kind);
            iData.putExtra(Constants.EXTRA_ITEM_NAME, d.getName());
            menu.add(0, R.id.action_delete, p++, R.string.action_delete).setIntent(iData);
            if (kind == MessagingModel.TYPE_QUEUE)
                menu.add(0, R.id.action_purge, p++, R.string.action_purge).setIntent(iData);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BaseApp.post(new ActionEvent(item.getItemId(), item.getIntent()));
        return true;    //super.onContextItemSelected(item);
    }

    @Override
    protected boolean hasContextMenu() {
        return true;
    }
}
