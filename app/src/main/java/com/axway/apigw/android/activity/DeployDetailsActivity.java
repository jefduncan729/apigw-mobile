package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.DeployDetailsFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

/**
 * Created by su on 2/26/2016.
 */
public class DeployDetailsActivity extends SinglePaneActivity {
    public static final String TAG = DeployDetailsActivity.class.getSimpleName();
    String instId;
    DeploymentModel model = DeploymentModel.getInstance();

    @Override
    protected Fragment createFragment(Bundle args) {
        instId = args.getString(Constants.EXTRA_INSTANCE_ID);
        return DeployDetailsFragment.newInstance(instId);
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle("Deployment Details");
        toolbar.setSubtitle(instId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
    }

    @Subscribe
    public void onPropsSelected(ItemSelectedEvent<DeployDetailsFragment.DeployDtlsAdapter.Entry> evt) {
        Log.d(TAG, String.format("onPropsSelected: %s", evt));
        DeployDetailsFragment.DeployDtlsAdapter.Entry e = (DeployDetailsFragment.DeployDtlsAdapter.Entry)evt.data;
//        DeploymentDetails.Props props = e.props;
        JsonObject props = e.props;
        String aid = props.has("Id") ? props.get("Id").getAsString() : null;
        if (!TextUtils.isEmpty(aid))
            model.getDeploymentArchiveForService(instId, aid, new BaseCallback() {
                @Override
                protected void onSuccessResponse(int code, String msg, String body) {
                    JsonObject j = JsonHelper.getInstance().parseAsObject(body);
                    if (j == null)
                        return;
                    if (j.has(JsonHelper.PROP_RESULT))
                        j = j.get(JsonHelper.PROP_RESULT).getAsJsonObject();
                    if (j.has("data")) {
                        String enc = j.get("data").getAsString();
                        String dec = new String(Base64.decode(enc.getBytes(), 0));
                        Log.d(TAG, String.format("decoded data: %s", dec));
                    }
                }
            });
    }
}
