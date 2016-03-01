package com.axway.apigw.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.PassphraseEnteredEvent;
import com.axway.apigw.android.fragment.DomainPassphraseDialog;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.EditServiceFragment;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;
import com.vordel.api.topology.model.Topology;

/**
 * Created by su on 2/23/2016.
 */
public class EditServiceActivity extends EditActivity<Service> {

    private static final String TAG = EditServiceActivity.class.getSimpleName();

    private String grpId;
    private Group grp;
    boolean addGroup;
    private Bundle extras;
    private TopologyModel model = TopologyModel.getInstance();

    @Override
    protected EditFrag<Service> createFragment(Bundle args, Service item) {
        grpId = args.getString(Constants.EXTRA_GROUP_ID);
        grp = model.getGroupById(grpId);
//        grpToAdd = null;
        return EditServiceFragment.newInstance(grp, item);
    }

    @Override
    protected Service createItem(Intent intent) {
        return new Service();
    }

    @Override
    protected Service loadItem(Intent intent) {
        Service svc = JsonHelper.getInstance().serviceFromJson(intent.getStringExtra(Constants.EXTRA_JSON_ITEM));
        return svc;
    }

    @Override
    public void save() {
//        super.save();
        grp = model.getGroupById(grpId);
        addGroup = false;
        try {
            editFrag.validate();
            extras = new Bundle();
            editFrag.collect(item, extras);
            if (isInsert) {
                String gnm = extras.getString(Constants.EXTRA_GROUP_NAME, "");
                if (grp == null || model.getTopology().getGroupByName(gnm) == null) {
                    confirmAddGroup(gnm);
                    return;
                }
                if (grp.getName().equals(gnm)) {
                    addService();
                    return;
                }
                grp = model.getTopology().getGroupByName(gnm);
                addService();
            }
            else {
                updateService();
            }
        }
        catch (ValidationException e) {
            showToast(e.getMessage());
        }
    }

    @Override
    protected boolean saveItem(Service item, Bundle extras) {
        return true;
    }

    private void confirmAddGroup(final String nm) {
        confirmDialog(String.format("Touch OK to add new group: %s", nm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performAddGroup(nm);
            }
        });
    }

    private void addService() {
        if (grp == null || item == null) {
            return;
        }
        Host h = model.getTopology().getHostByName(extras.getString(Constants.EXTRA_HOST_NAME));
        item.setHostID(h.getId());
        item.setType(Topology.ServiceType.gateway);
        item.setScheme(Constants.HTTPS_SCHEME);
        item.setEnabled(true);
        Log.d(TAG, String.format("addService: %s - %s", grp.getName(), item.getName()));
        int caOpt = extras.getInt(Constants.EXTRA_CA_OPTION, R.id.edit_system_ca);
        switch (caOpt) {
            case R.id.edit_system_ca:
                break;
            case R.id.edit_user_ca:
                break;
            case R.id.edit_external_ca:
                break;
        }
        promptForPassphrase(caOpt);
    }

    private void updateService() {
        showProgressBar(true);
        Log.d(TAG, String.format("updateService: %s", item.getName()));
        model.updateGateway(item, new BaseCallback() {
            @Override
            protected void onSuccessResponse(int code, String msg, String body) {
                done("updated");
            }
        });
    }

    private void performAddGroup(String nm) {
        Log.d(TAG, String.format("performAddGroup: %s", nm));
        grp = new Group();
        grp.setName(nm);
        addGroup = true;
        addService();
    }

    private void promptForPassphrase(int sslOpt) {
        DomainPassphraseDialog dlg = DomainPassphraseDialog.newInstance("Enter Passphrase", "", sslOpt);
        dlg.show(getFragmentManager(), "passphrase");
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        if (isInsert) {
            toolbar.setTitle("Add API Gateway Instance");
        }
        else {
            toolbar.setTitle(String.format("Edit %s", item.getName()));
        }
        toolbar.setSubtitle(String.format("%s", grpId));
    }

    @Subscribe
    public void onPassphraseEntered(final PassphraseEnteredEvent evt) {
        Log.d(TAG, String.format("passphraseEntered: %s - %s", evt.getDomainPhrase(), evt.getTempPhrase()));
        final int sp = extras.getInt(Constants.EXTRA_SERVICES_PORT, 0);
        final String alg = extras.getString(Constants.EXTRA_SIGN_ALG);
        showProgressBar(true);
        if (addGroup) {
            model.addGroup(grp, new BaseCallback() {
/*
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    response.body().close();
                    JsonObject j = JsonHelper.getInstance().parseAsObject(s);
                    if (j != null) {
                        if (j.has("result"))
                            j = j.get("result").getAsJsonObject();
                        if (j.has("id"))
                            grp.setId(j.get("id").getAsString());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(String.format("%s added", grp.getName()));
                            model.addGateway(grp, item, sp, alg, evt.getTempPhrase(), evt.getDomainPhrase(), new AddSvcCallback());
                        }
                    });
                }
*/

                @Override
                protected void onSuccessResponse(int code, String msg, String body) {
                    JsonObject j = JsonHelper.getInstance().parseAsObject(body);
                    if (j != null) {
                        if (j.has("result"))
                            j = j.get("result").getAsJsonObject();
                        if (j.has("id"))
                            grp.setId(j.get("id").getAsString());
                    }
                    showToast(String.format("%s added", grp.getName()));
                    model.addGateway(grp, item, sp, alg, evt.getTempPhrase(), evt.getDomainPhrase(), new AddSvcCallback());
                }
            });
            return;
        }
        model.addGateway(grp, item, sp, alg, evt.getTempPhrase(), evt.getDomainPhrase(), new AddSvcCallback());
    }

    private void done(String what) {
        showProgressBar(false);
        showToast(String.format("%s %s", item.getName(), what));
        setResult(RESULT_OK);
        finish();
    }

    private class AddSvcCallback extends BaseCallback {
        @Override
        protected void onSuccessResponse(int code, String msg, String body) {
            done("added");
        }
    }
}
