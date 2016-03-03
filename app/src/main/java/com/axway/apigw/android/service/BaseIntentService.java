package com.axway.apigw.android.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

abstract public class BaseIntentService extends IntentService {

    private static final String TAG = BaseIntentService.class.getSimpleName();

    public static final String ACTION_BASE = "axway.gcm.demo.";
	public static final String ACTION_KILL_RES_RCVR = ACTION_BASE + "killResRcvr";

	private SharedPreferences prefs;
	private NotificationManager notificationMgr;
    private Notification.Builder notificationBldr;
	private Handler handler;
    private Handler.Callback handlerCallback;

	private ResultReceiver resRcvr;
    private ConnectivityManager connMgr;
    private ToastRunnable toastRunnable;

	protected BaseIntentService(String name) {
		super(name);
		prefs = null;
		notificationMgr = null;
        notificationBldr = null;
		handler = null;
		resRcvr = null;
        toastRunnable = null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		handler = null;
	}

	@Override
	public void onDestroy() {
        drainHandler();
        handler = null;
		notificationMgr = null;
		prefs = null;
		super.onDestroy();
	}

    protected void drainHandler() {
        if (handler == null || toastRunnable == null)
            return;
        handler.removeCallbacks(toastRunnable);
    }

	protected void showToast(String msg) {
        toastRunnable = new ToastRunnable(msg);
		getHandler().post(toastRunnable);
	}

	protected void showToastLong(String msg) {
        toastRunnable = new ToastRunnable(msg, Toast.LENGTH_LONG);
		getHandler().post(toastRunnable);
	}

	protected SharedPreferences getPrefs() {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		return prefs;
	}
	
	protected NotificationManager getNotificationManager() {
		if (notificationMgr == null)
			notificationMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		return notificationMgr;
	}

    protected Notification.Builder getNotificationBuilder() {
        if (notificationBldr == null)
            notificationBldr = new Notification.Builder(this);
        return notificationBldr;
    }

    @Override
	protected void onHandleIntent(final Intent intent) {
		if (ACTION_KILL_RES_RCVR.equals(intent.getAction()))
			resRcvr = null;
		else
			resRcvr = intent.getParcelableExtra(Intent.EXTRA_RETURN_RESULT);
	}

    protected boolean onHandleMessage(final Message msg) {
        //default implementation does nothing
        return true;
    }

    protected Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getHandlerCallback());
        }
        return handler;
    }

    private Handler.Callback getHandlerCallback() {
        if (handlerCallback == null) {
            handlerCallback = new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    return onHandleMessage(msg);
                }
            };
        }
        return handlerCallback;
    }

    protected void sendResult(final int code) {
        sendResult(code, null);
    }

    protected void sendResult(final int code, final Bundle data) {
        if (getResultReceiver() == null) {
            Log.d(TAG, "sendResult: no resultReceiver");
        }
        else {
            getResultReceiver().send(code, data);
        }
    }

	protected ResultReceiver getResultReceiver() {
		return resRcvr;
	}

    protected ConnectivityManager getConnectivityMgr() {
        if (connMgr == null) {
            connMgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        }
        return connMgr;
    }

    private NetworkInfo getNetworkInfo() {
        return getConnectivityMgr().getActiveNetworkInfo();
    }

    private boolean haveNetwork(int typ) {
        final NetworkInfo info = getNetworkInfo();
        if (info == null)
            return false;
        return (info.getType() == typ && info.isConnectedOrConnecting());
    }

    protected boolean haveWifiNetwork() {
        return haveNetwork(ConnectivityManager.TYPE_WIFI);
    }

    protected boolean haveMobileNetwork() {
        return haveNetwork(ConnectivityManager.TYPE_MOBILE);
    }

    private class ToastRunnable implements Runnable {
        private Toast t;

        public ToastRunnable(String msg) {
            this(msg, Toast.LENGTH_SHORT);
        }

        public ToastRunnable(String msg, int len) {
            t = Toast.makeText(BaseIntentService.this, msg, len);
        }

        @Override
        public void run() {
            t.show();
        }
    }
}
