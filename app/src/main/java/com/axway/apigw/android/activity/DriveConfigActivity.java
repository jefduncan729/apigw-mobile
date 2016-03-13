package com.axway.apigw.android.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.DriveConfigFragment;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by su on 11/10/2015.
 */
public class DriveConfigActivity extends BaseDriveActivity implements DriveConfigFragment.Callbacks {
    private static final String TAG = DriveConfigActivity.class.getSimpleName();

    private static final int REQ_SELECT_FOLDER = 106;

    private DriveConfigModel model;
    private String currentKey;

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_pane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        showProgressBar(true);
        if (savedInstanceState == null) {
            Log.d(TAG, "creating model");
            model = new DriveConfigModel();
        }
    }

    @Override
    protected void setupToolbar(Toolbar tb) {
        String acct = getPrefs().getString(Constants.KEY_GOOGLE_ACCT, "");
        tb.setTitle("Configure Cloud Storage");
        tb.setSubtitle(String.format("Google Drive: %s", acct));
    }

    private void onModelLoaded() {
        DriveConfigFragment frag = DriveConfigFragment.newInstance(model);
        replaceFragment(R.id.container01, frag, Constants.TAG_SINGLE_PANE);
        showProgressBar(false);
    }

    @Override
    protected void handleOnConnected(Bundle bundle) {
        model.loadFromPrefs(getPrefs());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SELECT_FOLDER) {
            if (resultCode == RESULT_OK) {
                Bundle bnd = data.getExtras();
                if (bnd != null && !bnd.isEmpty()) {
                    DriveId did = (DriveId)bnd.getParcelable(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    if (did != null) {
                        DriveFolder f = did.asDriveFolder();
                        f.getMetadata(getGoogleApiClient()).setResultCallback(metadataCallback);
                        //model.setId(currentKey, did.encodeToString());
                    }
                }
            }
            else {
                setResult(RESULT_CANCELED);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    final private ResultCallback<DriveResource.MetadataResult> metadataCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Problem while trying to fetch metadata");
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    model.updateItem(currentKey, metadata.getDriveId().getResourceId(), metadata.getTitle());
                    Log.d(TAG, "Metadata successfully fetched. Title: " + metadata.getTitle());
                }
            };

    @Override
    public void onItemSelected(JsonObject item) {
        String key = null;
        String id = null;
        String name = null;
        if (item.has("key"))
            key = item.get("key").getAsString();
        if (item.has("id"))
            id = item.get("id").getAsString();
        if (item.has("name"))
            name = item.get("name").getAsString();
        String title = "Select folder";
        if (Constants.KEY_KPS_FOLDER.equals(key))
            title += " for KPS backups";
        else if (Constants.KEY_DEPLOY_FOLDER.equals(key))
            title += " for deployments";
        currentKey = key;
        selectFolder(title);
    }

    private void selectFolder(String title) {
        IntentSender is = Drive.DriveApi.newOpenFileActivityBuilder()
                .setActivityTitle(title)
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(is, REQ_SELECT_FOLDER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "selectFolder: intent exception", e);
        }
    }

    public class DriveConfigModel {
        public final String[] FOLDER_KEYS = new String[] { Constants.KEY_KPS_FOLDER, Constants.KEY_DEPLOY_FOLDER};
        private Map<String, JsonObject> folders;

        public void loadFromPrefs(SharedPreferences prefs) {
            folders = new HashMap<>();
            for (String k: FOLDER_KEYS) {
                folders.put(k, inflateFromPrefs(prefs, k));
            }
            onModelLoaded();
        }

        public Map<String, JsonObject> getFolders() {
            return folders;
        }

        public List<JsonObject> getFoldersAsList() {
            List<JsonObject> rv = new ArrayList<>();
            if (folders != null) {
                for (String k: folders.keySet()) {
                    rv.add(folders.get(k));
                }
            }
            return rv;
        }

        public void saveToPrefs(SharedPreferences prefs) {
            if (folders == null || prefs == null)
                return;
            SharedPreferences.Editor ed = prefs.edit();
            for (Map.Entry<String, JsonObject> e: folders.entrySet()) {
                ed.putString(e.getKey(), e.getValue().toString());
            }
            ed.apply();
        }

        public void updateItem(String key, String newId, String newName) {
            if (folders == null || TextUtils.isEmpty(key))
                return;
            JsonObject j = folders.get(key);
            if (j == null)
                return;
            j.remove("id");
            j.addProperty("id", newId);
            j.remove("name");
            j.addProperty("name", newName);
            getPrefs().edit().putString(key, j.toString()).apply();
            onModelLoaded();
        }

        private JsonObject inflateFromPrefs(SharedPreferences prefs, String key) {
            String s = prefs.getString(key, null);
            JsonObject rv = null;

            if (TextUtils.isEmpty(s)) {
                rv = new JsonObject();
                rv.addProperty("id", "");
                rv.addProperty("name", "");
                rv.addProperty("key", key);
            }
            else {
                rv = jsonHelper.parseAsObject(s);
            }
            return rv;
        }
    }
}
