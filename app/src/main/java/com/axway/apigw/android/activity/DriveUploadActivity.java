package com.axway.apigw.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.util.IOUtils;
import com.axway.apigw.android.util.SafeAsyncTask;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 11/10/2015.
 */
public class DriveUploadActivity extends BaseDriveActivity {
    private static final String TAG = DriveUploadActivity.class.getSimpleName();

    private String folderKey;
    private String filename;
    private String fileTitle;
    private String fileDescription;
    private UploadTask uploadTask;
    private int action;
    private String instId;
    private String storeId;
    private DeploymentModel deployModel;
    private KpsModel kpsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args;
        if (savedInstanceState == null) {
            args = getIntent().getExtras();
        }
        else {
            args = savedInstanceState;
        }
        deployModel = DeploymentModel.getInstance();
        kpsModel = KpsModel.getInstance();
        folderKey = args.getString(Constants.EXTRA_ITEM_ID, "");
        filename = args.getString(Constants.EXTRA_FILENAME, "");
        fileDescription = args.getString(Intent.EXTRA_TEXT, "");
        fileTitle = args.getString(Intent.EXTRA_TITLE, "");
        action = args.getInt(Constants.EXTRA_ACTION, 0);
        instId = args.getString(Constants.EXTRA_INSTANCE_ID, null);
        storeId = args.getString(Constants.EXTRA_KPS_STORE_ID, null);
        if (TextUtils.isEmpty(folderKey))
            throw new IllegalStateException("DriveUploadActivity expecting a folder key");
        showProgressFrag("Uploading to Drive");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_ITEM_ID, folderKey);
        outState.putString(Constants.EXTRA_FILENAME, filename);
        outState.putString(Intent.EXTRA_TEXT, fileDescription);
        outState.putString(Intent.EXTRA_TITLE, fileTitle);
        outState.putInt(Constants.EXTRA_ACTION, action);
        if (instId != null)
            outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
        if (storeId != null)
            outState.putString(Constants.EXTRA_KPS_STORE_ID, storeId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.single_pane;
    }

    @Override
    protected void handleOnConnected(Bundle bundle) {
        if (uploadTask != null) {
            uploadTask.cancel(true);
            uploadTask = null;
        }
        final JsonObject j = jsonHelper.parseAsObject(getPrefs().getString(folderKey, null));
        if (j == null) {
            Log.d(TAG, "error parsing folder info: " + folderKey);
            return;
        }
        final String did = j.get("id").getAsString();
        Log.d(TAG, "fetchDriveId: " + did);
        uploadTask = new UploadTask(this);
        uploadTask.execute(did);
    }

    private void onUploadComplete(DriveFolder.DriveFileResult res) {
        uploadTask = null;
        if (res == null) {
            setResult(RESULT_CANCELED);
            finishWithToast("Error occurred");
            return;
        }
        if (res.getStatus().isSuccess()) {
            setResult(RESULT_OK);
            finishWithToast("file uploaded to drive");
            return;
        }
        setResult(RESULT_CANCELED);
        finishWithAlert("Error occurred: " + res.getStatus().getStatusMessage());
    }

    private class UploadTask extends SafeAsyncTask<String, Void, DriveFolder.DriveFileResult> {

        protected UploadTask(Context ctx) {
            super(ctx);
        }

        @Override
        protected DriveFolder.DriveFileResult run(String... params) {
            DriveApi.DriveIdResult dir = Drive.DriveApi.fetchDriveId(getGoogleApiClient(), params[0]).await();
            if (!dir.getStatus().isSuccess()) {
                Log.d(TAG, "fetchDriveId failed: " + dir.getStatus().getStatusMessage());
                return null;
            }
            DriveId driveId = dir.getDriveId();
            DriveFolder folder = driveId.asDriveFolder();
            Log.d(TAG, "Create new contents in folder " + driveId.encodeToString());
            DriveApi.DriveContentsResult dcr = Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();
            if (!dcr.getStatus().isSuccess()) {
                Log.d(TAG, "newDriveContents failed: " + dcr.getStatus().getStatusMessage());
                return null;
            }
            DriveContents dc = dcr.getDriveContents();
            Log.d(TAG, "uploading file " + filename);
            InputStream fis = null;
            Response apiResp = null;
            ApiClient client = app.getApiClient();
//            boolean closeBody = false;
            try {
                switch (action) {
                    case R.id.action_save_deploy_archive:
                        apiResp = deployModel.getDeploymentArchiveForService(instId);
                    break;
                    case R.id.action_save_policy_archive:
                        apiResp = deployModel.getPolicyArchiveForService(instId);
                    break;
                    case R.id.action_save_env_archive:
                        apiResp = deployModel.getEnvironmentArchiveForService(instId);
                    break;
                    case R.id.action_upload:
                        KpsStore store = kpsModel.getStoreById(storeId);
                        String endpt = String.format(KpsModel.KPS_STORE_ENDPOINT, instId, store.getAlias());
                        Request req = client.createRequest(endpt);
                        apiResp = client.executeRequest(req);
                    break;
                }
                if (apiResp == null) {
                    fis = new FileInputStream(filename);
                }
                else {
                    if (apiResp.isSuccessful()) {
                        fis = apiResp.body().byteStream();
//                        closeBody = true;
                    }
                }
                if (fis != null)
                    IOUtils.copy(fis, dc.getOutputStream());
            } catch (FileNotFoundException e) {
                Log.d(TAG, "file not found", e);
            } catch (IOException e) {
                Log.d(TAG, "io exception", e);
            } finally {
                IOUtils.closeQuietly(fis);
//                if (closeBody)
//                    apiResp.body().close();;
            }
            MetadataChangeSet mcs = new MetadataChangeSet.Builder()
                    .setMimeType("application/json")
                    .setTitle(fileTitle)
                    .setDescription(fileDescription)
                    .build();
            return folder.createFile(getGoogleApiClient(), mcs, dc).await();
        }

        @Override
        protected void doPostExecute(DriveFolder.DriveFileResult driveFileResult) {
            if (!isCancelled())
                onUploadComplete(driveFileResult);
        }
    }
/*
    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.d(TAG, "idCallback not successful: " + result.getStatus().getStatusMessage());
                finishWithAlert("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveId driveId = result.getDriveId();
            folder = driveId.asDriveFolder();
            Log.d(TAG, "Create new contents in folder " + driveId.encodeToString());
            Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(contentsCallback);
        }
    };

    final private ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            Log.d(TAG, "contentsCallback reached");
            if (result.getStatus().isSuccess()) {
                final DriveContents dc = result.getDriveContents();
                new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "uploading file " + filename);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(filename);
                            IOUtils.copy(fis, dc.getOutputStream());
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "file not found", e);
                        } catch (IOException e) {
                            Log.d(TAG, "io exception", e);
                        } finally {
                            IOUtils.closeQuietly(fis);
                        }
                        MetadataChangeSet mcs = new MetadataChangeSet.Builder()
                                .setMimeType("application/json")
                                .setTitle(fileTitle)
                                .setDescription(fileDescription)
                                .build();
                        folder.createFile(getGoogleApiClient(), mcs, dc).setResultCallback(createCallback);
                    }
                }.start();
            }
        }
    };

    final ResultCallback<DriveFolder.DriveFileResult> createCallback = new ResultCallback<DriveFolder.DriveFileResult>() {

        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            Log.d(TAG, "createCallback reached");
            if (result.getStatus().isSuccess()) {
                setResult(RESULT_OK);
                finishWithToast("file uploaded to drive");
            }
            else {
                setResult(RESULT_CANCELED);
                finishWithAlert("Error occurred: " + result.getStatus().getStatusMessage());
            }
        }
    };
*/
}
