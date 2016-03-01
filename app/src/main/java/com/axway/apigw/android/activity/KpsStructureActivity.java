package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.KpsStructureFragment;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/19/2016.
 */
public class KpsStructureActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    private String instId;
    private String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            instId = getIntent().getExtras().getString(Constants.EXTRA_INSTANCE_ID, null);
            storeId = getIntent().getExtras().getString(Constants.EXTRA_ITEM_ID, null);
        }
        else {
            instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
            storeId = savedInstanceState.getString(Constants.EXTRA_ITEM_ID, null);
        }
        KpsStore kpsStore;
        kpsStore = KpsModel.getInstance().getStoreById(storeId);
        if (kpsStore == null) {
            finishWithAlert(String.format("KPS store not found: %s", storeId));
            return;
        }
        toolbar.setTitle(R.string.action_kps_structure);
        toolbar.setSubtitle(String.format("%s on %s", kpsStore.getAlias(), instId));
        KpsType kpsType = KpsModel.getInstance().getTypeById(kpsStore.getTypeId());
        if (kpsType == null) {
            finishWithAlert(String.format("KPS type '%s' not found for store: %s", kpsStore.getTypeId(), storeId));
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, KpsStructureFragment.newInstance(kpsStore, kpsType)).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_ITEM_ID, storeId);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
    }
}
