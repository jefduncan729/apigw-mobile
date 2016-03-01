package com.axway.apigw.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.adapter.KpsStoresAdapter;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.api.KpsModel;

/**
 * Created by su on 2/19/2016.
 */
public class KpsStoresFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = KpsStoresFragment.class.getSimpleName();

    private int curPos;

    public static KpsStoresFragment newInstance() {
        KpsStoresFragment rv = new KpsStoresFragment();
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
        //setEmptyText("No Key Property Stores available");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: " + Integer.toString(i));
        KpsStoresAdapter.Entry e = (KpsStoresAdapter.Entry)getListView().getItemAtPosition(i);
        if (e == null) {
            Log.d(TAG, "entry is null");
            return;
        }
        if (e.type == KpsStoresAdapter.TYPE_PACKAGE) {
            return;
        }
        Intent data = new Intent()
            .putExtra(Constants.EXTRA_ITEM_ID, e.id)
            .putExtra(Constants.EXTRA_ITEM_TYPE, e.type);
        BaseApp.post(new ActionEvent(R.id.action_select, data));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)menuInfo;
        KpsStoresAdapter.Entry e = (KpsStoresAdapter.Entry)getListView().getItemAtPosition(cmi.position);
        if (e == null) {
            Log.d(TAG, "problem finding entry at position " + Integer.toString(cmi.position));
        }
        else if (e.type == KpsStoresAdapter.TYPE_STORE) {
            Intent iData = new Intent();
            iData.putExtra(Constants.EXTRA_ITEM_ID, e.id);
            iData.putExtra(Constants.EXTRA_KPS_STORE_ALIAS, e.name);
            int p = 0;
//            menu.add(0, R.id.action_export, p++, R.string.action_export).setIntent(iData);
            menu.add(0, R.id.action_kps_structure, p++, R.string.action_kps_structure).setIntent(iData);
            menu.add(0, R.id.action_kps_customize_list, p, R.string.action_kps_customize_list).setIntent(iData);
            menu.setHeaderTitle(e.name);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BaseApp.post(new ActionEvent(item.getItemId(), item.getIntent()));
        return true;
    }

    protected void refreshAdapter() {
        Kps kps = KpsModel.getInstance().getKps();
        if (kps == null) {
            setListAdapter(null);
        }
        else {
            KpsStoresAdapter adapter = new KpsStoresAdapter(getActivity(), kps, BaseApp.getInstance().isShowInternalKps());
            setListAdapter(adapter);
        }
   }
}
