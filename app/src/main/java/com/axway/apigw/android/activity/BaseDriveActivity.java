package com.axway.apigw.android.activity;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by su on 10/29/2014.
 */
abstract public class BaseDriveActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = BaseDriveActivity.class.getSimpleName();

    private static final int REQ_RESOLVE_ERR = 101;
    private static final int REQ_PICK_USER = 102;
    private static final int REQ_RECOVER_FROM_AUTH_ERROR = 103;
    private static final int REQ_RECOVER_FROM_PLAY_SVCS_ERROR = 104;

    private GoogleApiClient googleApiClient;
    private boolean resolvingError;
    protected String acctName;
    private boolean connectedOnce;

    private String title;
    private String description;

    protected int getLayoutId() {
        return android.R.layout.list_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        connectedOnce = false;
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: savedInstanceState is null; first time?");
            googleApiClient = null;
            resolvingError = false;
            acctName = null;
        }
        else {
            Log.d(TAG, "onCreate: savedInstanceState is non-null");
        }
        Bundle args = savedInstanceState;
        if (args == null) {
            args = getIntent().getExtras();
        }
        if (args != null) {
            title = args.getString(Intent.EXTRA_TITLE, null);
            description = args.getString(Intent.EXTRA_TEXT, null);
            resolvingError = args.getBoolean("resolvingErr", false);
        }
        acctName = getPrefs().getString(Constants.KEY_GOOGLE_ACCT, null);
    }

    protected boolean connectInOnResume() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acctName == null) {
            pickUserAccount();
            return;
        }
        if (googleApiClient == null) {
            Log.d(TAG, "creating googleApiClient");
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .setAccountName(acctName)
                    .build();
        }
        if (connectInOnResume()) {
            Log.d(TAG, "onResume: connecting googleApiClient");
            connectToDrive();
        }
    }

    @Override
    protected void onPause() {
        if (googleApiClient != null && (googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            Log.d(TAG, "onPause: disconnecting googleApiClient");
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resolvingErr", resolvingError);
        if (!TextUtils.isEmpty(title))
            outState.putString(Intent.EXTRA_TITLE, title);
        if (!TextUtils.isEmpty(description))
            outState.putString(Intent.EXTRA_TEXT, description);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RESOLVE_ERR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                if (googleApiClient != null && !googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    Log.d(TAG, "connecting googleApiClient from Resolve Error");
                    connectToDrive();
                }
            }
            return;
        }
        if (requestCode == REQ_PICK_USER) {
            if (resultCode == RESULT_OK) {
                acctName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getPrefs().edit().putString(Constants.KEY_GOOGLE_ACCT, acctName).commit();
                if (googleApiClient != null && !googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    Log.d(TAG, "connecting googleApiClient from PickUser Activity");
                    connectToDrive();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                finishWithToast("Select an account");
            }
            return;
        }
        if (requestCode == REQ_RECOVER_FROM_AUTH_ERROR || requestCode == REQ_RECOVER_FROM_PLAY_SVCS_ERROR) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    abstract protected void handleOnConnected(Bundle bundle);

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + (bundle == null ? "null" : bundle.toString()));
        connectedOnce = true;
        handleOnConnected(bundle);
    }

    protected void connectToDrive() {
        if (connectedOnce)
            getGoogleApiClient().reconnect();
        else
            getGoogleApiClient().connect();
    }
//
//    private void saveToDrive() {
//        File f = new File(filename);
//        if (!f.exists()) {
//        //if (TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
//            Log.w(TAG, "saveToDrive: file not found: " + filename);
//            return;
//        }
//        Log.i(TAG, "saveToDrive: creating new contents");
//        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(contentsCallback);
//    }
//
//    private final ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
//        @Override
//        public void onResult(final DriveApi.DriveContentsResult result) {
//            if (!result.getStatus().isSuccess()) {
//                Log.w(TAG, "contentsCallback: Could not create new contents");
//                return;
//            }
//            Log.i(TAG, "contentsCallback: new contents created");
//            new Thread() {
//                @Override
//                public void run() {
//                    File f = new File(filename);
////                    final String j = jsonStr;
//                    final String t = title;
//                    final String d = description;
//                    FileInputStream fis = null;
//                    OutputStream os = result.getDriveContents().getOutputStream();
//                    byte[] buf = new byte[10240];
//                    int nr;
//                    try {
//                        fis = new FileInputStream(f);
//                        nr = fis.read(buf);
//                        while (nr > 0) {
//                            os.write(buf);
//                            nr = fis.read(buf);
//                        }
//                    } catch (IOException e) {
//                        Log.e(TAG, "contentsCallback: unable to write", e);
//                    }
//                    finally {
//                        try { fis.close(); } catch (IOException e) {}
//                    }
//                    MetadataChangeSet mcs = new MetadataChangeSet.Builder()
//                            .setMimeType("application/json")
//                            .setTitle(t)
//                            .setDescription(d)
//                            .build();
//                    IntentSender intentSender = Drive.DriveApi
//                            .newCreateFileActivityBuilder()
//                            .setInitialMetadata(mcs)
//                            .setInitialDriveContents(result.getDriveContents())
//                            .build(googleApiClient);
//                    try {
//                        startIntentSenderForResult(intentSender, REQ_CREATE, null, 0, 0, 0);
//                    }
//                    catch (IntentSender.SendIntentException e) {
//                        Log.w(TAG, "unable to send intent", e);
//                    }
//                    f.delete();
//                }
//            }.start();
//        }
//    };

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (resolvingError) {
            return;
        }
        Log.d(TAG, String.format("onConnectionFailed: %s", connectionResult));
        if (connectionResult.hasResolution()) {
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(this, REQ_RESOLVE_ERR);
            }
            catch (IntentSender.SendIntentException e) {
                connectToDrive();
            }
        }
        else {
            finishWithAlert("Error code: " + Integer.toString(connectionResult.getErrorCode()));
        }
    }

    private void pickUserAccount() {
        String[] acctTypes = new String[]{ "com.google" };
        Intent i = AccountPicker.newChooseAccountIntent(null, null, acctTypes, false, null, null, null, null);
        startActivityForResult(i, REQ_PICK_USER);
    }


    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int sc = ((GooglePlayServicesAvailabilityException)e).getConnectionStatusCode();
                    GooglePlayServicesUtil.showErrorDialogFragment(sc, BaseDriveActivity.this, null, REQ_RECOVER_FROM_PLAY_SVCS_ERROR, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            showToast("Cancelled");
                        }
                    });
//                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(sc, BaseDriveActivity.this, REQ_RECOVER_FROM_PLAY_SVCS_ERROR);
//                    dialog.show();
                }
                else if (e instanceof UserRecoverableAuthException) {
                    Intent i = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(i, REQ_RECOVER_FROM_PLAY_SVCS_ERROR);
                }
            }
        });
    }

    protected GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
