package com.axway.apigw.android.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.db.DbHelper;
import com.axway.apigw.android.event.CertValidationEvent;
import com.axway.apigw.android.fragment.ConnMgrEditFrag;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.model.ServerInfo;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by su on 2/10/2016.
 */
public class ConnMgrEditActivity extends EditActivity<ServerInfo> {

    @Override
    protected EditFrag<ServerInfo> createFragment(Bundle args, ServerInfo item) {
        if (!BaseApp.getInstance().haveNetwork()) {
            showToast("Network connection not available. Certificates cannot be checked.");
        }
        return ConnMgrEditFrag.newInstance(item);
    }

    @Override
    protected ServerInfo createItem(Intent intent) {
        return new ServerInfo("10.71.100.196", 8090, true, "admin", "changeme");
    }

    @Override
    protected ServerInfo loadItem(Intent intent) {
        return ServerInfo.fromBundle(intent.getBundleExtra(Constants.EXTRA_SERVER_INFO));
    }

    @Override
    public void save() {
        try {
            editFrag.validate();
            Bundle extras = new Bundle();
            editFrag.collect(item, extras);
            ContentValues values = item.toValues(); //new ContentValues();
            Uri uri = null;
            if (item.getId() == 0) {
                uri = getContentResolver().insert(DbHelper.ConnMgrColumns.CONTENT_URI, values);
                String s = (uri == null ? "-1" : uri.getLastPathSegment());
                item.setId(Integer.parseInt(s));
            }
            else {
                uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, item.getId());
                getContentResolver().update(uri, values, null, null);
            }
            if ((item.isSsl() && !item.isCertTrusted()) && item.getStatus() == Constants.STATUS_ACTIVE) {
                checkCert(item);
                return;
            }
            setResult(RESULT_OK);
            finish();
        }
        catch (ValidationException e) {
            showToast(e.getMessage());
        }
    }

    @Override
    protected boolean saveItem(ServerInfo item, Bundle extras) {
/*
        ContentValues values = item.toValues(); //new ContentValues();
        Uri uri = null;
        if (item.getId() == 0) {
            uri = getContentResolver().insert(DbHelper.ConnMgrColumns.CONTENT_URI, values);
            String s = (uri == null ? "-1" : uri.getLastPathSegment());
            item.setId(Integer.parseInt(s));
        }
        else {
            uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, item.getId());
            getContentResolver().update(uri, values, null, null);
        }
        if ((item.isSsl() && !item.isCertTrusted()) && item.getStatus() == Constants.STATUS_ACTIVE) {
            checkCert(item);
            return false;
        }
*/
        return true;
    }

    @Subscribe
    public void onCertValidationEvent(final CertValidationEvent evt) {  //final CertPath cp, final String alias) {
        final CertPath cp = evt.cp;
        final String alias = evt.alias;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Certificate c : cp.getCertificates()) {
            if (c.getType().equals("X.509")) {
                X509Certificate c509 = (X509Certificate) c;
                sb.append("[").append(++i).append("]: ").append(c509.getSubjectDN().toString()).append("\n");
            }
        }
        sb.append("\n").append(getString(R.string.add_to_truststore));

        confirmDialog(getString(R.string.cert_not_trusted), sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (BaseApp.addTrustedCert(evt.info, cp)) {
                    onCertAdded(evt.info);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Cancelled");
            }
        });
    }

    private void onCertAdded(ServerInfo info) {
        setCertTrusted(info, true);
        showToast(R.string.cert_trusted);
        setResult(RESULT_OK);
        finish();
    }

    private void certAlreadyTrusted(ServerInfo info) {
        showToast(R.string.cert_trusted);
        setCertTrusted(info, true);
        finish();
    }

    private void setCertTrusted(ServerInfo info, boolean trusted) {
        if (info == null)
            return;
        Uri uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, info.getId());
        ContentValues values = new ContentValues();
        values.put(DbHelper.ConnMgrColumns.FLAG, (trusted ? Constants.FLAG_CERT_TRUSTED : Constants.FLAG_CERT_NOT_TRUSTED));
        getContentResolver().update(uri, values, null, null);
    }

    private void requestFailed(Call call, IOException e) {
        showToast(e.getMessage());
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        super.setupToolbar(toolbar);
        toolbar.setTitle(String.format("%s Connection", (isInsert ? "Add" : "Edit")));
        if (!isInsert)
            toolbar.setSubtitle(item.displayString());
    }

    private void checkCert(final ServerInfo info) {
        final ApiClient client = ApiClient.from(info);
        client.checkCert(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                final CertPath cp = BaseApp.certPathFromThrowable(e);
                if (cp == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestFailed(call, e);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseApp.post(new CertValidationEvent(info, cp));
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            certAlreadyTrusted(info);
                        }
                    });
                }
            }
        });
    }
}
