package com.axway.apigw.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.adapter.KpsTableAdapter;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.model.DisplayPrefs;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by su on 2/19/2016.
 */
public class KpsTableFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = KpsTableFragment.class.getSimpleName();

    private int curPos;
    private String instId;
    private KpsStore kpsStore;
    private KpsType kpsType;
    private JsonArray data;
    private DisplayPrefs layout;

    public static KpsTableFragment newInstance(String instId, KpsStore store, DisplayPrefs layout, JsonArray data) {
        KpsTableFragment rv = new KpsTableFragment();
        rv.instId = instId;
        rv.kpsStore = store;
        rv.layout = layout;
        rv.data = data;
        rv.kpsType = KpsModel.getInstance().getTypeById(rv.kpsStore.getTypeId());
        return rv;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            curPos = 0;
        }
        else {
            curPos = savedInstanceState.getInt(Constants.EXTRA_CUR_PAGE, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.EXTRA_CUR_PAGE, curPos);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnCreateContextMenuListener(this);
        getListView().setOnItemClickListener(this);
//        setEmptyText(String.format("No rows in %s", kpsStore.getAlias()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: " + Integer.toString(i));
        JsonObject j = (JsonObject)getListView().getItemAtPosition(i);
        if (j == null) {
            return;
        }
        Intent data = new Intent()
            .putExtra(Constants.EXTRA_JSON_ITEM, j.toString());
        BaseApp.post(new ActionEvent(R.id.action_select, data));

//        KpsStoresAdapter.Entry e = (KpsStoresAdapter.Entry)getListView().getItemAtPosition(i);
//        if (e == null) {
//            Log.d(TAG, "entry is null");
//            return;
//        }
//        if (e.type == KpsStoresAdapter.TYPE_PACKAGE) {
//            return;
//        }
//        Intent data = new Intent()
//            .putExtra(Constants.EXTRA_ITEM_ID, e.id)
//            .putExtra(Constants.EXTRA_ITEM_TYPE, e.type);
//        BaseApp.postEvent(new ActionEvent(R.id.action_select, data));
    }

    protected void refreshAdapter() {
//        Kps kps = KpsModel.getInstance().getKps();
        if (kpsStore == null || kpsType == null) {
            setListAdapter(null);
        }
        else {
            KpsTableAdapter adapter = new KpsTableAdapter(getActivity(), kpsStore, kpsType, layout, data);
            setListAdapter(adapter);
        }
   }
}
