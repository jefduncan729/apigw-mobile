package com.axway.apigw.android.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.api.KpsModel;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.DeployDetailsFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.service.DriveService;
import com.axway.apigw.android.util.IOUtils;
import com.axway.apigw.android.util.SafeAsyncTask;
import com.axway.apigw.android.view.FloatingActionButton;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 2/26/2016.
 */
public class DeployDetailsActivity extends BaseDriveActivity {
    public static final String TAG = DeployDetailsActivity.class.getSimpleName();

    String instId;
    String archiveId;
    String folderId;
    DeploymentDetails deployDtls;

    DeploymentModel deployModel = DeploymentModel.getInstance();

    private UploadTask uploadTask;
    private FileDetails curDtls;
    private PopupMenu archiveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Bundle args = savedInstanceState;
        if (args == null)
            args = getIntent().getExtras();
        instId = args.getString(Constants.EXTRA_INSTANCE_ID);
        deployDtls = deployModel.getDeploymentDetails(instId);
        archiveId = (deployDtls == null ? null : deployDtls.getId());
        final JsonObject j = jsonHelper.parseAsObject(getPrefs().getString(Constants.KEY_DEPLOY_FOLDER, null));
        if (j == null) {
            Log.d(TAG, String.format("error parsing folder info: %s", Constants.KEY_DEPLOY_FOLDER));
            folderId = null;
        }
        else {
            folderId = j.get("id").getAsString();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, DeployDetailsFragment.newInstance(deployDtls), Constants.TAG_SINGLE_PANE).commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_pane;
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle(R.string.action_deployment_details);
        toolbar.setSubtitle(instId);
    }

//    @Override
    protected void setupFab(FloatingActionButton fab) {
        fab.setImageResource(R.drawable.upload_fab);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_INSTANCE_ID, instId);
    }

    @Override
    protected boolean connectInOnResume() {
        return false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_upload:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onPropsSelected(ItemSelectedEvent<DeployDetailsFragment.DeployDtlsAdapter.Entry> evt) {
        Log.d(TAG, String.format("onPropsSelected: %s", evt));
/*
        DeployDetailsFragment.DeployDtlsAdapter.Entry e = (DeployDetailsFragment.DeployDtlsAdapter.Entry)evt.data;
        JsonObject props = e.props;
        int k = 0;
        if ("Environment Properties".equals(e.title))
            k = R.id.action_env_props;
        else if ("Policy Properties".equals(e.title))
            k = R.id.action_policy_props;
        if (k == 0)
            return;
        Intent i = new Intent(this, EditItemActivity.class);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_TYPE, k);
        startActivity(i);
*/
/*
//        DeploymentDetails.Props props = e.props;
        JsonObject props = e.props;
        String aid = props.has("Id") ? props.get("Id").getAsString() : null;
        Intent i = new Intent(this, DriveUploadActivity.class);
        i.putExtra(Constants.EXTRA_ACTION, R.id.action_save_deploy_archive);
        i.putExtra(Constants.EXTRA_INSTANCE_ID, instId);
        i.putExtra(Constants.EXTRA_ITEM_ID, Constants.KEY_DEPLOY_FOLDER);
        i.putExtra(Constants.EXTRA_FILENAME, String.format("%s_%s.json", instId, aid));
        i.putExtra(Intent.EXTRA_TITLE, String.format("%s_%s.json", instId, aid));
        i.putExtra(Intent.EXTRA_TEXT, String.format("Deployment Archive Backup: %s, %s", instId, aid));
        startActivityForResult(i, 0);
*/
    }

    @Override
    public void onClicked(FloatingActionButton fab) {
        if (TextUtils.isEmpty(folderId)) {
            showToast("Configure Cloud Storage to upload archives");
            return;
        }
        if (archiveMenu == null) {
            archiveMenu = new PopupMenu(this, fab.getImageView());
            archiveMenu.getMenuInflater().inflate(R.menu.archive, archiveMenu.getMenu());
            archiveMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_save_deploy_archive:
                        case R.id.action_save_policy_archive:
                        case R.id.action_save_env_archive:
                            confirmUpload(menuItem.getItemId());
                            return true;
                    }
                    return false;
                }
            });
        }
        archiveMenu.show();
    }

    private void confirmUpload(int which) {
        final FileDetails dtls = new FileDetails(which);
        String ttl = String.format("Upload archive - %s", instId);
        String msg = String.format("Touch OK to upload %s archive. Drive account: %s", dtls.type, acctName);
        confirmDialog(ttl, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performUpload(dtls);
            }
        });
    }

    private void performUpload(FileDetails dtls) {
        if (uploadTask != null) {
            showToast("Wait for the current upload to finish");
            return;
        }
        curDtls = dtls;
        Log.d(TAG, String.format("Connect Drive API: %s", curDtls));
        connectToDrive();
    }

    @Override
    protected void handleOnConnected(Bundle bundle) {
        //connected to Drive API
        Log.d(TAG, "handleOnConnected");
        if (uploadTask != null)
            return;
        Log.d(TAG, String.format("fetchDriveId: %s", folderId));
        uploadTask = new UploadTask(this, curDtls);
        uploadTask.execute(folderId);
    }

    private void onUploadComplete(FileDetails dtls, DriveFolder.DriveFileResult res) {
        Log.d(TAG, String.format("onUploadComplete: %s", res));
        uploadTask = null;
        if (res != null && res.getStatus().isSuccess()) {
//            setResult(RESULT_OK);
            showToast(String.format("%s archive uploaded to drive", dtls.type));
            return;
        }
        showToast("Error occurred");
        getGoogleApiClient().disconnect();
    }


    private class UploadTask extends SafeAsyncTask<String, Void, DriveFolder.DriveFileResult> {

        FileDetails dtls;

        protected UploadTask(Context ctx, FileDetails dtls) {
            super(ctx);
            this.dtls = dtls;
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
            Log.d(TAG, "uploading ");
            InputStream fis = null;
            Response apiResp = null;
//            boolean closeBody = false;
            try {
                switch (dtls.kind) {
                    case R.id.action_save_deploy_archive:
                        apiResp = deployModel.getDeploymentArchiveForService(instId);
                        break;
                    case R.id.action_save_policy_archive:
                        apiResp = deployModel.getPolicyArchiveForService(instId);
                        break;
                    case R.id.action_save_env_archive:
                        apiResp = deployModel.getEnvironmentArchiveForService(instId);
                        break;
                }
                if (apiResp != null) {
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
                    .setTitle(dtls.filename)
                    .setDescription(dtls.description)
                    .build();
            Log.d(TAG, String.format("createFile: %s", mcs));
            return folder.createFile(getGoogleApiClient(), mcs, dc).await();
        }

        @Override
        protected void doPostExecute(DriveFolder.DriveFileResult driveFileResult) {
            showProgressBar(false);
            if (!isCancelled())
                onUploadComplete(dtls, driveFileResult);
        }

        @Override
        protected void doPreExecute() {
            showProgressBar(true);
        }
    }

    private class FileDetails {
        public int kind;
        public String filename;
        public String description;
        public String type;

        public FileDetails(int kind) {
            this.kind = kind;
            build();
        }

        private void build() {
            switch (kind) {
                case R.id.action_save_deploy_archive:
                    type = "deployment";
                    break;
                case R.id.action_save_policy_archive:
                    type = "policy";
                    break;
                case R.id.action_save_env_archive:
                    type = "environment";
                    break;
            }
            long ts = System.currentTimeMillis();
            filename = String.format("%s_%s_%s_%d.json", instId, archiveId, type, ts);
            description = String.format("%s Archive Backup\nInstance: %s\nArchive Id: %s\nTaken %s by %s", type, instId, archiveId, app.formatDatetime(ts), app.getCurrentServer().getUser());
        }
    }
}
