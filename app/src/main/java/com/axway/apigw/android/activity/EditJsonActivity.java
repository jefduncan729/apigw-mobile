package com.axway.apigw.android.activity;

import android.os.Bundle;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.ObservableJsonObject;
import com.google.gson.JsonObject;

import java.util.Observable;

/**
 * Created by jef on 3/11/16.
 */
public class EditJsonActivity extends EditItemActivity<JsonObject> {

    private ObservableJsonObject observable;
    private String instId;
    private int what;

    @Override
    protected void createFromArgs(Bundle savedInstanceState, boolean saved) {
        instId = savedInstanceState.getString(Constants.EXTRA_INSTANCE_ID);
        what = savedInstanceState.getInt(Constants.EXTRA_ITEM_TYPE);
        if (saved) {
            item = jsonHelper.parseAsObject(savedInstanceState.getString(Constants.EXTRA_JSON_ITEM));
        }
        else {
            DeploymentDetails dd = DeploymentModel.getInstance().getDeploymentDetails(instId);
            item = dd.getPolicyProperties();
            if (what == R.id.action_env_props)
                item = dd.getEnvironmentProperties();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (instId != null)
            outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        outState.putInt(Constants.EXTRA_ITEM_TYPE, what);
    }

    @Override
    protected Observable getObservable() {
        if (observable == null)
            observable = new ObservableJsonObject(item);
        return observable;
    }

    @Override
    protected EditFrag<JsonObject> createFragment() {
        return null;
    }

    @Override
    protected void performSave() {

    }
}
