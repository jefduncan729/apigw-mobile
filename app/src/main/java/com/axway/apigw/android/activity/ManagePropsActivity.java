package com.axway.apigw.android.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.ManagePropsFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.util.NameValuePair;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import java.util.Map;

/**
 * Created by su on 2/29/2016.
 */
public class ManagePropsActivity extends EditActivity<JsonObject> {

    private static final String TAG = ManagePropsActivity.class.getSimpleName();
    private String instId;
    private int what;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItem() {
        Log.d(TAG, "addItem");
    }

    @Override
    protected EditFrag<JsonObject> createFragment(Bundle args, JsonObject item) {
        instId = args.getString(Constants.EXTRA_INSTANCE_ID);
        what = args.getInt(Constants.EXTRA_ITEM_TYPE);
        return ManagePropsFragment.newInstance(item);
    }

    @Override
    protected JsonObject createItem(Intent intent) {
        JsonObject rv = new JsonObject();
        rv.addProperty("Id", "");
        rv.addProperty("Description", "");
        rv.addProperty("Manifest-Version", "");
        rv.addProperty("Name", "");
        rv.addProperty("Version", "");
        rv.addProperty("VersionComment", "");
        rv.addProperty("Timestamp", 0L);
        return rv;
    }

    private void putNotNull(Map<String, String> map, String key, String val) {
        if (TextUtils.isEmpty(val))
            return;
        map.put(key, val);
    }

    @Override
    protected JsonObject loadItem(Intent intent) {
        String instId = intent.getStringExtra(Constants.EXTRA_INSTANCE_ID);
        DeploymentDetails dd = DeploymentModel.getInstance().getDeploymentDetails(instId);
        int k = intent.getIntExtra(Constants.EXTRA_ITEM_TYPE, 0);
        JsonObject rv = dd.getPolicyProperties();
        if (k == R.id.action_env_props)
            rv = dd.getEnvironmentProperties();
        return rv;
    }

    @Override
    protected boolean saveItem(JsonObject item, Bundle extras) {
        return false;
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle(String.format("%s Properties", what == R.id.action_policy_props ? "Policy" : "Environment"));
        toolbar.setSubtitle(instId);
    }

    @Subscribe
    public void onSelected(ItemSelectedEvent<NameValuePair> evt) {
        final NameValuePair item = evt.data;
        String title = String.format("Edit %s", item.name);
        customDialog(title, R.layout.name_dlg, new CustomDialogCallback() {
          @Override
            public void populate(AlertDialog dlg) {
              TextView txt = (TextView) dlg.findViewById(R.id.label_name);
              EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
//              txt.setText(item.name);
              txt.setVisibility(View.GONE);
              ed.setText(item.value);
            }
            @Override
            public void save(AlertDialog dlg) {
                TextView txt = (TextView) dlg.findViewById(R.id.label_name);
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
//                performDestAction(R.id.action_add, k, s);
            }
            @Override
            public boolean validate(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    showToast("Provide a value");
                    return false;
                }
                return true;
            }
        });
    }
}
