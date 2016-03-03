package com.axway.apigw.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.activity.DeployDetailsActivity;
import com.axway.apigw.android.util.SafeAsyncTask;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by su on 3/3/2016.
 */
public class DriveService extends Service {
    private static final String TAG = DriveService.class.getSimpleName();

    private NotificationManager notificationMgr;
    private Notification.Builder notificationBldr;

    private final Binder binder = new LocalBinder();
    private DummyTask curTask;
    private GoogleApiClient googleApiClient;

    public class LocalBinder extends Binder {
        public DriveService getService() {
            return DriveService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, String.format("onBind %s", intent));
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, String.format("onUnbind %s", intent));
        if (curTask != null) {
            Log.d(TAG, "taak is running");
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, String.format("onStartCommand %s, %d, %d", intent, flags, startId));
//        registerBroadcastReceiver();
        return START_STICKY;    //super.onStartCommand(intent, flags, startId);
    }

    public void uploadArchive(int action, String instId) {
        String w = "unknown";
        switch (action) {
            case R.id.action_save_deploy_archive:
                w = "deployment";
                break;
            case R.id.action_save_policy_archive:
                w = "policy";
                break;
            case R.id.action_save_env_archive:
                w = "environment";
                break;
        }
        Log.d(TAG, String.format("upload %s archive for %s", w, instId));
        if (curTask != null) {
            curTask.cancel(true);
            curTask = null;
        }
        curTask = new DummyTask(this, instId);
        curTask.execute(10000L);
    }

    private void onTaskComplete(String res) {
        Log.d(TAG, String.format("OnTaskComplete: %s", res));
        curTask = null;
        Intent actIntent = new Intent(this, DeployDetailsActivity.class);
//        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actIntent.setAction(Intent.ACTION_VIEW);
        actIntent.putExtra(Constants.EXTRA_INSTANCE_ID, res);
        PendingIntent pi = PendingIntent.getActivity(this, 0, actIntent, PendingIntent.FLAG_ONE_SHOT);
        String subj = "Upload Complete";
        String msg = String.format("finished upload - %s", res);
        Notification n = getNotificationBuilder()
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_action_upload_holo_dark)
                .setStyle(new Notification.BigTextStyle().bigText(msg))
                .setContentTitle(subj)
                .setContentText(msg)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        getNotificationManager().notify(42, n);
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

    private class DummyTask extends SafeAsyncTask<Long, Void, String> {
        private String instId;

        protected DummyTask(Context ctx, String instId) {
            super(ctx);
            this.instId = instId;
        }

        @Override
        protected String run(Long... params) {
            long ms = params[0];
            try {
                Thread.sleep(ms);
            }
            catch (InterruptedException e) {
                //e.printStackTrace();
            }
            return instId;
        }

        @Override
        protected void doPostExecute(String s) {
            onTaskComplete(s);
        }
    }
}
