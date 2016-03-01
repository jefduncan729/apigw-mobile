package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axway.apigw.android.adapter.KpsStructureAdapter;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;

/**
 * Created by su on 2/19/2016.
 */
public class KpsStructureFragment extends ListFragment {
    private static final String TAG = KpsStructureFragment.class.getSimpleName();

    private KpsStore kpsStore;
    private KpsType kpsType;

    public static KpsStructureFragment newInstance(KpsStore store, KpsType type) {
        KpsStructureFragment rv = new KpsStructureFragment();
        rv.kpsStore = store;
        rv.kpsType = type;
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(android.R.layout.list_content, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    protected void refreshAdapter() {
        Kps kps = KpsModel.getInstance().getKps();
        if (kps == null) {
            setListAdapter(null);
        }
        else {
            KpsStructureAdapter adapter = new KpsStructureAdapter(getActivity(), kpsStore, kpsType);
            setListAdapter(adapter);
        }
   }
}
