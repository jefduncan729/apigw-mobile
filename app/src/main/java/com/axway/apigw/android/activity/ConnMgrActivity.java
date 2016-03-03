package com.axway.apigw.android.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.api.ApiClient;
import com.axway.apigw.android.db.DbHelper;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.fragment.ConnMgrListFragment;
import com.axway.apigw.android.model.ServerInfo;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConnMgrActivity extends BaseActivity {
	
	private static final String TAG = ConnMgrActivity.class.getSimpleName();

    private static final int REQ_CHECK_CERT = 1001;
    private static final int MSG_ADD_NEW = Constants.MSG_BASE + 1001;
    private static final int REQ_ADD_SERVER = 1002;
    private static final int REQ_EDIT_SERVER = 1003;

    @Bind(R.id.toolbar) Toolbar toolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        setResult(RESULT_CANCELED);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.conn_mgr);
        setActionBar(toolbar);
        int cnt = getIntent().getIntExtra(Intent.EXTRA_LOCAL_ONLY, -1);
        if (cnt == 0) {
            postEmptyMessage(MSG_ADD_NEW);
            return;
        }
        if (savedInstanceState == null) {
            refreshFrag();
        }
	}

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

	private void performDelete(Uri uri, String name) {
		int i = getContentResolver().delete(uri, null, null);
        if (i > 0)
		    showToast(name + " deleted");
		refreshFrag();
	}

	private void refreshFrag() {
        replaceFragment(R.id.container01, ConnMgrListFragment.newInstance(), Constants.TAG_SINGLE_PANE);
	}

    @Subscribe
    public void onAction(ActionEvent evt) {
        switch (evt.id) {
            case R.id.action_add:
                onAdd();
                break;
            case R.id.action_edit:
                onEdit(evt.data.getData());
                break;
            case R.id.action_remove_trust:
                onRemoveTrustStore();
                break;
            case R.id.action_delete:
                onDelete(evt.data.getData(), evt.data.getStringExtra(Constants.EXTRA_ITEM_NAME));
                break;
            case R.id.action_enable:
            case R.id.action_disable:
                onStatusChange(evt.data.getData(), evt.data.getIntExtra(Constants.EXTRA_GATEWAY_STATUS, 0));
                break;
            case R.id.action_check_cert:
                onCheckCert(evt.data.getData());
                break;
            case R.id.action_view_cert:
                onViewCert(evt.data.getData());
                break;
            default:
        }
    }

	public void onDelete(final Uri uri, final String name) {
		if (uri == null)
			return;
		String msg = "Touch OK to delete " + (name == null ? "": name);
		confirmDialog(msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performDelete(uri, name);
            }
        });
	}

	protected void editDialog(final ServerInfo si) {
        Intent i = new Intent(this, ConnMgrEditActivity.class);
        i.setAction(Intent.ACTION_EDIT);
        i.putExtra(Constants.EXTRA_SERVER_INFO, si.toBundle());
        startActivityForResult(i, REQ_EDIT_SERVER);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ADD_SERVER || requestCode == REQ_EDIT_SERVER) {
            if (resultCode == RESULT_OK) {
                refreshFrag();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	public void onAdd() {
        Intent i = new Intent(this, ConnMgrEditActivity.class);
        i.setAction(Intent.ACTION_INSERT);
        startActivityForResult(i, REQ_ADD_SERVER);
	}

	public void onEdit(Uri uri) {
		Cursor c = getContentResolver().query(uri, null, null, null, null);
		if (c == null)
			return;
        ServerInfo si = null;
		if (c.moveToFirst()) {
            si = ServerInfo.from(c);
        }
	    c.close();
        if (si != null)
	        editDialog(si);
	}

	public void onStatusChange(Uri uri, int newStatus) {
		if (newStatus == Constants.STATUS_ACTIVE || newStatus == Constants.STATUS_INACTIVE) {
			ContentValues values = new ContentValues();
			values.put(DbHelper.ConnMgrColumns.STATUS, newStatus);
			getContentResolver().update(uri, values, null, null);
			refreshFrag();
		}
	}

    private void setCertTrusted(ServerInfo info, boolean trusted) {
        if (info == null)
            return;
        Uri uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, info.getId());
        ContentValues values = new ContentValues();
        values.put(DbHelper.ConnMgrColumns.FLAG, (trusted ? Constants.FLAG_CERT_TRUSTED : Constants.FLAG_CERT_NOT_TRUSTED));
        getContentResolver().update(uri, values, null, null);
        refreshFrag();
    }

    private void onCertCheckFailed(final ServerInfo info) {
        showToast(R.string.cert_not_trusted);
        setCertTrusted(info, false);
    }

    private void onCertCheckSuccess(final ServerInfo info) {
        showToast(R.string.cert_trusted);
        setCertTrusted(info, true);
    }

	public void onCheckCert(Uri uri) {
        final ServerInfo info = getFor(uri);
        if (info == null) {
            Log.e(TAG, "no server info found: " + uri.toString());
            return;
        }
        ApiClient client = ApiClient.from(info);
        client.checkCert(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCertCheckFailed(info);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCertCheckSuccess(info);
                        }
                    });
            }
        });
	}

    public void onViewCert(Uri uri) {
        ServerInfo info = getFor(uri);
        if (info == null) {
            Log.e(TAG, "no server info found: " + uri.toString());
            return;
        }
        Certificate[] certs = app.getCerts(info);
        if (certs == null)
            return;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Certificate c : certs) {
            if (c.getType().equals("X.509")) {
                X509Certificate c509 = (X509Certificate) c;
                sb.append("[").append(++i).append("]: ").append(c509.getSubjectDN().toString()).append("\n");
            }
        }
        sb.append("\n");
        infoDialog("View Certificate", sb.toString());
    }

	private ServerInfo getFor(Uri uri) {
		Cursor c = getContentResolver().query(uri, null, null, null, null);
		if (c == null)
			return null;
        ServerInfo rv = null;
        if (c.moveToFirst()) {
			rv = ServerInfo.from(c);
		}
		c.close();
		return rv;
	}

	public void onRemoveTrustStore() {
        confirmDialog(getString(R.string.confirm_remove_truststore), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeTrustStore();
            }
        });
	}

    private void removeTrustStore() {
        BaseApp.keystoreManager().removeKeystore();
        BaseApp.resetSocketFactory();
        showToast("Trust Store removed");
        final ContentValues values = new ContentValues();
        values.put(DbHelper.ConnMgrColumns.FLAG, Constants.FLAG_CERT_NOT_TRUSTED);
        getContentResolver().update(DbHelper.ConnMgrColumns.CONTENT_URI, values, null, null);
        refreshFrag();
    }

    @Override
    protected boolean onHandleMessage(Message msg) {
        if (msg.what == MSG_ADD_NEW) {
            onAdd();
            return true;
        }
        return super.onHandleMessage(msg);
    }
}
