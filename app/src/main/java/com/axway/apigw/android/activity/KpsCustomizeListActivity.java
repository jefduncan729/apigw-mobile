package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.KpsCustomizeListFragment;
import com.axway.apigw.android.model.DisplayPrefs;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;

/**
 * Created by su on 2/19/2016.
 */
public class KpsCustomizeListActivity extends EditActivity<DisplayPrefs> {

    private static final String TAG = KpsCustomizeListActivity.class.getSimpleName();

    private String instId;
    private String storeId;

    @Override
    protected EditFrag<DisplayPrefs> createFragment(Bundle args, DisplayPrefs item) {
        storeId = args.getString(Constants.EXTRA_ITEM_ID, null);
        instId = args.getString(Constants.EXTRA_INSTANCE_ID, null);
        KpsStore kpsStore = KpsModel.getInstance().getStoreById(storeId);
        KpsType kpsType = KpsModel.getInstance().getTypeById(kpsStore.getTypeId());
        return KpsCustomizeListFragment.newInstance(kpsStore, kpsType, item);
    }

    @Override
    protected DisplayPrefs createItem(Intent intent) {
        return new DisplayPrefs();
    }

    @Override
    protected DisplayPrefs loadItem(Intent intent) {
        String storeId = intent.getStringExtra(Constants.EXTRA_ITEM_ID);
        String s = getPrefs().getString(String.format("layout_%s", storeId), null);
        DisplayPrefs rv = DisplayPrefs.inflate(s);
        Log.d(TAG, String.format("Display prefs loaded %s: %s", storeId, rv));
        return rv;
    }

    @Override
    protected boolean saveItem(DisplayPrefs item, Bundle extras) {
        Log.d(TAG, String.format("Saving Display prefs %s: %s", storeId, item));
        getPrefs().edit().putString(String.format("layout_%s", storeId), item.deflate()).apply();
        return true;
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        KpsStore kpsStore;
        kpsStore = KpsModel.getInstance().getStoreById(storeId);
        toolbar.setTitle(R.string.action_kps_customize_list);
        toolbar.setSubtitle(String.format("%s on %s", kpsStore.getAlias(), instId));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_ITEM_ID, storeId);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
    }
}
