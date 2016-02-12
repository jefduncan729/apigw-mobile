package com.axway.apigw.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;

public class BaseActivity extends AppCompatActivity implements OnClickListener {
	private static final String TAG = BaseActivity.class.getSimpleName();
	
	protected static final String TAG_PROG_DLG = "progDlg";
	protected static final String TAG_INFO_DLG = "infoDlg";
	protected static final String TAG_ALERT_DLG = "alertDlg";
	protected static final String TAG_CONFIRM_DLG = "confirmDlg";

    protected static final int FRAG_TRANSITION = FragmentTransaction.TRANSIT_FRAGMENT_FADE;

    public interface CustomDialogCallback {
        public void populate(AlertDialog dlg);
        public void save(AlertDialog dlg);
        public boolean validate(AlertDialog dlg);
    }

	private SharedPreferences prefs;
    private Handler.Callback handlerCallback;
    private Handler handler;
//    private Bundle safeExtras;
    private ProgressBar progressBar;

    private boolean fromSavedState;

	public BaseActivity() {
		super();
		prefs = null;
        handlerCallback = null;
        handler = null;
//        safeExtras = null;
	}

//    protected int getLayoutId() {
//        return R.layout.single_pane;
//    }
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSavedState = (savedInstanceState != null);
        if (!fromSavedState) {
//            if (getIntent() != null) {
//                getSafeExtras().putString(Constants.EXTRA_INTENT_ACTION, getIntent().getAction());
//                if (getIntent().getExtras() != null) {
//                    getSafeExtras().putAll(getIntent().getExtras());
//                }
//            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (safeExtras != null)
//            outState.putBundle(Constants.EXTRA_EXTRAS, safeExtras);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null)
//            safeExtras = savedInstanceState.getBundle(Constants.EXTRA_EXTRAS);
    }
/*
    public AgreeApp getAgreeApp() {
        if (app == null)
            app = (AgreeApp)getApplication();
        return app;
    }
*/
    protected SharedPreferences getPrefs() {
		if (prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs;
	}

	protected void cancelTask(AsyncTask<?, ?, ?> task) {
		if (task == null)
			return;
		task.cancel(true);
	}
	
//	public void showSettings() {
//		Intent i = new Intent(this, SettingsActivity.class);
//		i.setAction(Intent.ACTION_VIEW);
//		startActivityForResult(i, R.id.action_settings);
//	}

    protected boolean isFromSavedState() {
        return fromSavedState;
    }
/*
	protected void showProgressDialog() {
		showProgressDialog(null, getString(R.string.confirm_msg));
	}

	protected void showProgressDialog(String message) {
		showProgressDialog(null, message);
	}

	protected void showProgressDialog(String title, String message) {
        final ProgressDialog dlg = new ProgressDialog(this);
        if (title != null)
            dlg.setTitle(title);
        if (TextUtils.isEmpty(message))
            message = "Loading...";
        dlg.setMessage(message);
        dlg.setIndeterminate(true);
        dlg.setCancelable(false);
        dlg.show();
	}

	protected void dismissProgressDialog() {
		DialogFragment frag = (DialogFragment)getFragmentManager().findFragmentByTag(TAG_PROG_DLG);
		if (frag != null)
			frag.dismiss();
	}
	
	protected boolean progressDialogShowing() {
		DialogFragment frag = (DialogFragment)getFragmentManager().findFragmentByTag(TAG_PROG_DLG);
		return (frag != null);
	}

	protected void confirmDialog(DialogInterface.OnClickListener onYes) {
		confirmDialog(getString(R.string.confirm_msg), onYes);
	}
*/
	protected void confirmDialog(String msg, DialogInterface.OnClickListener onYes) {
		confirmDialog(getString(R.string.confirm), msg, onYes);
	}	
	
	protected void confirmDialog(String title, String msg, DialogInterface.OnClickListener onYes) {
		confirmDialog(title, msg, onYes, null);
	}	
	
	protected void confirmDialog(String title, String msg, DialogInterface.OnClickListener onYes, DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(msg)
                .setTitle(title)
                .setIcon(R.mipmap.ic_action_help_holo_light)
                .setCancelable(false);
        if (onYes == null)
            onYes = Constants.NOOP_LISTENER;
        bldr.setPositiveButton(android.R.string.yes, onYes);
        if (onNo == null)
            onNo = Constants.NOOP_LISTENER;
        bldr.setNegativeButton(android.R.string.no, onNo);
        AlertDialog dlg = bldr.create();
        dlg.show();
	}
	
//	protected void alertDialog(String msg) {
//		alertDialog("Alert", msg);
//	}
//
	protected void alertDialog(String msg, DialogInterface.OnClickListener onYes) {
		alertDialog(getString(R.string.alert), msg, onYes);
	}

    protected void alertDialog(String title, String msg) {
        alertDialog(title, msg, null);
    }

    protected void alertDialog(String title, String msg, DialogInterface.OnClickListener onYes) {
		alertDialog(title, msg, onYes, null);
	}	
	
	protected void alertDialog(String title, String msg, DialogInterface.OnClickListener onYes, DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(msg)
            .setTitle(title)
            .setIcon(R.mipmap.ic_action_warning_holo_light)
            .setCancelable(false);
        if (onYes == null)
            onYes = Constants.NOOP_LISTENER;
        bldr.setPositiveButton(android.R.string.yes, onYes);
        if (onNo != null)
            bldr.setNegativeButton(android.R.string.no, onNo);
        AlertDialog dlg = bldr.create();
        dlg.show();
	}
	
	protected void alertDialog(String title, String msg, DialogInterface.OnClickListener onYes, DialogInterface.OnClickListener onNo, DialogInterface.OnClickListener onNeutral) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(msg)
                .setTitle(title)
                .setIcon(R.mipmap.ic_action_warning_holo_light)
                .setCancelable(false);
        if (onYes == null)
            onYes = Constants.NOOP_LISTENER;
        bldr.setPositiveButton(android.R.string.yes, onYes);
        if (onNo != null)
            bldr.setNegativeButton(android.R.string.no, onNo);
        if (onNeutral != null)
            bldr.setNeutralButton(android.R.string.cut, onNeutral);
        AlertDialog dlg = bldr.create();
        dlg.show();
	}
	
	protected void infoDialog(String msg) {
		infoDialog(getString(R.string.info), msg);
	}

	protected void infoDialog(String title, String msg) {
		infoDialog(title, msg, null);
	}
	
	protected void infoDialog(String title, String msg, DialogInterface.OnClickListener onYes) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage(msg)
                .setTitle(title)
//                .setIcon(R.mipmap.ic_action_about)
                .setCancelable(false);
        if (onYes == null)
            onYes = Constants.NOOP_LISTENER;
        bldr.setPositiveButton(android.R.string.yes, onYes);
        AlertDialog dlg = bldr.create();
        dlg.show();
	}

    protected void customDialog(String title, int layoutId, final CustomDialogCallback callback) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setView(layoutId)
                .setTitle(title)
                .setIcon(R.mipmap.ic_action_about_holo_light)
                .setCancelable(true);
        bldr.setPositiveButton(android.R.string.yes, Constants.NOOP_LISTENER);
        bldr.setNegativeButton(android.R.string.no, Constants.NOOP_LISTENER);
        final AlertDialog dlg = bldr.create();

        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            if (callback.validate(dlg)) {
                                callback.save(dlg);
                            }
                        }
                        dlg.dismiss();
                    }
                });
                if (callback != null)
                    callback.populate(dlg);
            }
        });
        dlg.show();
    }

	protected void handleException(Exception e) {
        hideProgressFrag();
		String msg = null;
		if (e != null)
			msg = e.getLocalizedMessage();
		if (msg == null)
			msg = "unknown exception";
		Log.e(TAG, msg, e);
		System.gc();
	}

	@Override
	public void onClick(View arg0) {
        Log.d(TAG, String.format("onClick: %d", arg0.getId()));
	}

    public boolean isMultiPane() {
        return false;
    }

    protected boolean onHandleMessage(Message msg) {
        return false;
    }

    private Handler.Callback getHandlerCallback() {
        if (handlerCallback == null) {
            handlerCallback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    return onHandleMessage(message);
                }
            };
        }
        return handlerCallback;
    }

//    protected BaseApplication getApp() {
//        return (BaseApplication)getApp();
//    }

    protected void replaceFragment(int ctrId, Fragment frag, String tag) {
        getFragmentManager().beginTransaction().replace(ctrId, frag, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    protected Fragment findFragment(String tag) {
        return getFragmentManager().findFragmentByTag(tag);
    }

    protected Fragment findFragment(int ctrId) {
        return getFragmentManager().findFragmentById(ctrId);
    }

    protected void addFragment(int ctrId, Fragment frag, String tag) {
        getFragmentManager().beginTransaction().add(ctrId, frag, tag).setTransition(FRAG_TRANSITION).commit();
    }

    protected void removeFragment(String tag) {
        Fragment frag = findFragment(tag);
        if (frag == null)
            return;
        getFragmentManager().beginTransaction().remove(frag).setTransition(FRAG_TRANSITION).commit();
    }

    protected void removeFragment(int id) {
        Fragment frag = findFragment(id);
        if (frag == null)
            return;
        getFragmentManager().beginTransaction().remove(frag).setTransition(FRAG_TRANSITION).commit();
    }

    protected void hideFragment(String tag) {
        Fragment frag = findFragment(tag);
        if (frag == null)
            return;
        getFragmentManager().beginTransaction().hide(frag).setTransition(FRAG_TRANSITION).commit();
    }

    protected void hideFragment(int id) {
        Fragment frag = findFragment(id);
        if (frag == null)
            return;
        getFragmentManager().beginTransaction().hide(frag).setTransition(FRAG_TRANSITION).commit();
    }
//
//    public String getSafeExtra(String key, String defVal) {
//        if (getSafeExtras().containsKey(key))
//            return getSafeExtras().getString(key);
//        return defVal;
//    }
//
//    public String getSafeExtra(String key) {
//        return getSafeExtra(key, "");
//    }
//
//    public int getSafeExtra(String key, int defVal) {
//        if (getSafeExtras().containsKey(key))
//            return getSafeExtras().getInt(key);
//        return defVal;
//    }
//
//    public long getSafeExtra(String key, long defVal) {
//        if (getSafeExtras().containsKey(key))
//            return getSafeExtras().getLong(key);
//        return defVal;
//    }
//
//    public Bundle getSafeExtras() {
//        if (safeExtras == null)
//            safeExtras = new Bundle();
//        return safeExtras;
//    }

    public void showProgressFrag(String msg) {
        showProgressFrag(msg, R.id.container01);
    }

    protected void showProgressFrag(String msg, int ctrId) {
        showProgressBar(true);
//        LoadingFragment progFrag = LoadingFragment.newInstance(msg);
//        if (!isMultiPane())
//            ctrId = R.id.container01;
//        replaceFragment(ctrId, progFrag, Constants.TAG_PROGRESS);
        invalidateOptionsMenu();
    }

    protected void hideProgressFrag() {
        showProgressBar(false);
        hideFragment(Constants.TAG_PROGRESS);
    }

    protected ProgressBar getProgressBar() {
        if (progressBar == null)
            progressBar = (ProgressBar)findViewById(android.R.id.progress);
        return progressBar;
    }

    public void showProgressBar(final boolean show) {
        if (getProgressBar() == null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    protected void finishWithAlert(String msg) {
        Log.v(TAG, "finishWithAlert: " + msg);
        alertDialog(msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
    }

    protected void finishWithToast(String msg) {
        Log.v(TAG, "finishWithToast: " + msg);
        showToast(msg);
        finish();
    }

//    protected void finishWithError(String msg) {
//        errorDialog(msg, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        });
//    }

    protected void postEmptyMessage(int what) {
        postEmptyMessage(what, 0);
    }

    protected void postEmptyMessage(int what, long delay) {
        getMsgHandler().removeMessages(what);
        if (delay > 0)
            getMsgHandler().sendEmptyMessageDelayed(what, delay);
        else
            getMsgHandler().sendEmptyMessage(what);
    }

    protected void postMessage(int what, int arg1) {
        postMessage(what, arg1, 0, null);
    }

    protected void postMessage(int what, int arg1, int arg2) {
        postMessage(what, arg1, arg2, null);
    }

    protected void postMessage(int what, int arg1, int arg2, Object obj) {
        getMsgHandler().removeMessages(what);
        Message msg = getMsgHandler().obtainMessage(what, arg1, arg2);
        if (obj != null)
            msg.obj = obj;
        getMsgHandler().sendMessage(msg);
    }

    public Handler getMsgHandler() {
        if (handler == null) {
            handler = new Handler(getHandlerCallback());
        }
        return handler;
    }

    public void showToast(final String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public void showToast(final int strId) {
        showToast(getString(strId));
    }

    public void showToast(final String msg, final int duration) {
        getMsgHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, msg, duration).show();
            }
        });
    }
}
