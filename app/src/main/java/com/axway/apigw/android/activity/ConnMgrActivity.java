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
import com.axway.apigw.android.fragment.ConnMgrListFragment;
import com.axway.apigw.android.model.ServerInfo;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConnMgrActivity extends BaseActivity implements ConnMgrListFragment.Callbacks {
	
	private static final String TAG = ConnMgrActivity.class.getSimpleName();

    private static final int REQ_CHECK_CERT = 1001;
    private static final int MSG_ADD_NEW = Constants.MSG_BASE + 1001;
    private static final int REQ_ADD_SERVER = 1002;
    private static final int REQ_EDIT_SERVER = 1003;

//	private Uri certCheckUri;
//    private TopologyClient client;

    @Bind(R.id.toolbar) Toolbar toolbar;
    private boolean firstOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        firstOne = false;
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.conn_mgr);
        toolbar.setLogo(R.mipmap.ic_axwaylogo);
        setActionBar(toolbar);
        int cnt = getIntent().getIntExtra(Intent.EXTRA_LOCAL_ONLY, -1);
        if (cnt == 0) {
            firstOne = true;
            setResult(RESULT_CANCELED);
            postEmptyMessage(MSG_ADD_NEW);
            return;
        }
        if (savedInstanceState == null) {
//            certCheckUri = null;
            refreshFrag();
        }
	}

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
//        checkCert();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }
/*

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
                    setCertTrusted(evt.info, true);
                    showToast("Cert trusted " + alias);
                    if (firstOne) {
                        Intent data = new Intent();
                        data.putExtra(Constants.EXTRA_SERVER_INFO, evt.info.toBundle());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
//                resetClient();
//                refresh();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Cancelled");
            }
        });
    }
*/

	private void performDelete(Uri uri, String name) {
		int i = getContentResolver().delete(uri, null, null);
        if (i > 0)
		    showToast(name + " deleted");
		refreshFrag();
	}

	private void refreshFrag() {
        ConnMgrListFragment frag = new ConnMgrListFragment();
        if (getIntent().getExtras() != null)
            frag.setArguments(getIntent().getExtras());
        replaceFragment(R.id.container01, frag, Constants.TAG_SINGLE_PANE);
	}
	
	@Override
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
//        ConnMgrEditFrag frag = ConnMgrEditFrag.newInstance(si);
//        replaceFragment(R.id.container01, frag, Constants.TAG_SINGLE_PANE);

//		ConnMgrDialog dlg = new ConnMgrDialog();
//		Bundle args = si.toBundle();
//		if (si.getId() == 0)
//			args.putString(Intent.EXTRA_TITLE, "New Connection");
//		else {
//			args.putString(Intent.EXTRA_TITLE, "Edit Connection");
//		}
//		dlg.setArguments(args);
//		dlg.setCancelable(true);
//		dlg.setListener(new ConnMgrDialog.Listener() {
//			@Override
//			public void onServerSaved(ServerInfo info) {
//				saveServer(info);
//			}
//		});
//		dlg.show(getFragmentManager(), "selSrvrDlg");
	}
/*

	private void saveServer(final ServerInfo info) {
        ContentValues values = info.toValues(); //new ContentValues();
        boolean adding = false;
        Uri uri = null;
		if (info.getId() == 0) {
            uri = getContentResolver().insert(DbHelper.ConnMgrColumns.CONTENT_URI, values);
            adding = true;
        }
		else {
			uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, info.getId());
			getContentResolver().update(uri, values, null, null);
		}
		refreshFrag();
        if (adding && info.isSsl() && info.getStatus() == Constants.STATUS_ACTIVE) {
            onCheckCert(uri);
        }
	}
*/

	@Override
	public void onAdd() {
//        ConnMgrEditFrag frag = ConnMgrEditFrag.newInstance(new ServerInfo("10.71.100.196", 8090, true, "admin", "changeme"), firstOne);
////        frag.setCallbacks(this);
//        replaceFragment(R.id.container01, frag, Constants.TAG_SINGLE_PANE);
        Intent i = new Intent(this, ConnMgrEditActivity.class);
        i.setAction(Intent.ACTION_INSERT);
        startActivityForResult(i, REQ_ADD_SERVER);
	}

	@Override
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

	@Override
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
//        certCheckUri = null;
    }

    private void onCertCheckFailed(final ServerInfo info) {
        showToast(R.string.cert_not_trusted);
        setCertTrusted(info, false);
    }

    private void onCertCheckSuccess(final ServerInfo info) {
        showToast(R.string.cert_trusted);
        setCertTrusted(info, true);
    }

	@Override
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
//        checkCert(info);
//        certCheckUri = uri;
//        BaseApplication.getInstance().setServerInfo(info);   //SessionData.getInstance().setServerInfo(info);
//        BaseApplication.getInstance().getTopologyClient().getCurrentTopology(new CertTrustHandler());
//        TopologyClient.from(info).getCurrentTopology(new CertTrustHandler(uri));
//        BaseApplication.getInstance().ensureTrusted(info);

//        BaseApplication.getInstance().setServerInfo(info);
//        BaseApplication.getInstance().getTopologyClient().getTopology(new CertTrustHandler(info));
	}

/*
    private void checkCert(final ServerInfo info) {
        final ApiClient client = ApiClient.from(info);
        client.checkCert(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final CertPath cp = BaseApp.certPathFromThrowable(e);
                if (cp != null) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCertTrusted(info);
                    }
                });
            }
        });
    }
*/

    @Override
    public void onViewCert(Uri uri) {
        ServerInfo info = getFor(uri);
        if (info == null) {
            Log.e(TAG, "no server info found: " + uri.toString());
            return;
        }
        Certificate[] certs = null; //BaseApplication.getInstance().getCerts(info);
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
/*
    private void onCertTrusted(ServerInfo info) {
//        if (certCheckUri == null)
//            return;
        showToast(R.string.cert_trusted);
        setCertTrusted(info, true);
        refreshFrag();
        if (firstOne) {
            Intent i = new Intent();
            i.putExtra(Constants.EXTRA_SERVER_INFO, info.toBundle());
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private void onCertNotTrusted(final CertPath cp, final ServerInfo info) {
        setCertTrusted(info, false);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Certificate c : cp.getCertificates()) {
            if (c.getType().equals("X.509")) {
                X509Certificate c509 = (X509Certificate) c;
                sb.append("[").append(++i).append("]: ").append(c509.getSubjectDN().toString()).append("\n");
            }
        }
        sb.append("\n").append(getString(R.string.add_to_truststore));
        alertDialog(getString(R.string.cert_not_trusted), sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (BaseApp.keystoreManager().addTrustedCert(info, cp))
                    onCertTrusted(info);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refreshFrag();
            }
        });
    }
*/
	@Override
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
