package com.axway.apigw.android;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import com.axway.apigw.android.model.ServerInfo;
import com.squareup.otto.Bus;
import com.vordel.api.topology.model.Topology;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by su on 1/22/2016.
 */
public class BaseApp extends Application {

    public static final String TAG = BaseApp.class.getSimpleName();

    private static final boolean DRAW_RECT = true;
    private static final int[] BG_COLOR_IDS = {android.R.color.holo_blue_dark, android.R.color.holo_purple, android.R.color.holo_green_dark, android.R.color.holo_red_dark};

    private static SSLSocketFactory _socketFactory = null;
    private static KeystoreManager _keystoreManager = null;
    private static HostnameVerifier _hostnameVerifier = null;
    private static BaseApp _inst = null;
    private static Bus _bus = null;
    private SparseArray<Drawable> _statDrawables;
    private int[] _colors;
    private int colorNdx;

    @Override
    public void onCreate() {
        super.onCreate();
        _statDrawables = new SparseArray<>();
        Resources res = getResources();
//        _statDrawables.put(TopologyModel.GATEWAY_STATUS_RUNNING, res.getDrawable(R.mipmap.thumbs_up, null));
//        _statDrawables.put(TopologyModel.GATEWAY_STATUS_NOT_RUNNING, res.getDrawable(R.mipmap.thumbs_down, null));
        _colors = new int[BG_COLOR_IDS.length];
        for (int i = 0; i < BG_COLOR_IDS.length; i++) {
            _colors[i] = res.getColor(BG_COLOR_IDS[i]);
        }
        _inst = this;
        colorNdx = 0;
    }

    public static SSLSocketFactory sslSocketFactory() {
        if (_socketFactory == null) {
            Log.d(TAG, "create SSLSocketFactory");
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keystoreManager().loadKeystore());
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                _socketFactory = sslContext.getSocketFactory();
                Log.d(TAG, "SSLSocketFactory created");
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithmException", e);
            } catch (KeyStoreException e) {
                Log.e(TAG, "KeyStoreException", e);
            } catch (KeyManagementException e) {
                Log.e(TAG, "KeyManagementException", e);
            }
        }
        return _socketFactory;
    }

    public static KeystoreManager keystoreManager() {
        if (_keystoreManager == null) {
            _keystoreManager = KeystoreManager.from(_inst);
        }
        return _keystoreManager;
    }

    public static HostnameVerifier hostVerifier() {
        if (_hostnameVerifier == null) {
            _hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
        }
        return _hostnameVerifier;
    }

    public static Bus bus() {
        if (_bus == null) {
            _bus = new Bus();
        }
        return _bus;
    }

    public static void post(Object evt) {
        bus().post(evt);
    }

    public static void resetSocketFactory() {
        _socketFactory = null;
    }

    public static boolean addTrustedCert(ServerInfo info, CertPath cp) {
        boolean rv = keystoreManager().addTrustedCert(info, cp);
        if (rv)
            resetSocketFactory();
        return rv;
    }

    public static CertPath certPathFromThrowable(Throwable e) {
        CertPath rv = null;
        CertPathValidatorException cpve = null;
        Throwable cause = e.getCause();
        while (cpve == null && cause != null) {
            if (cause instanceof CertPathValidatorException) {
                cpve = (CertPathValidatorException)cause;
            }
            else
                cause = cause.getCause();
        }
        if (cpve != null)
            rv = cpve.getCertPath();
        return rv;
    }

    public static BaseApp getInstance() {
        return _inst;
    }

    public Drawable statusDrawable(int stat) {
        return _statDrawables.get(stat);
    }

    public Bitmap drawBitmapForState(Topology.EntityType typ, String name) {
        Bitmap rv = null;
//        String name = null;
//        if (typ == Topology.EntityType.Gateway)
//            name = "Instance";
//        else
//            name = typ.name();
        Paint p = new Paint();
        p.setStrokeWidth(4.0f);
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        int h = 36; //(int)(dimens[DIMEN_IMGSIZE]);	//*scale
        int w = h;
        rv = Bitmap.createBitmap(h, w, config);
        Canvas canvas = new Canvas(rv);
        if (DRAW_RECT) {
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.FILL);
//            canvas.drawOval(0, 0, w, h, p);
            canvas.drawRect(0, 0, w, h, p);	//rv.getWidth(), rv.getHeight(), p);
            p.setColor(nextColor());    //typeColor(typ));
            p.setStyle(Paint.Style.FILL);
//            canvas.drawOval(0, 0, w-4, h-4, p);
			canvas.drawRect(2, 2, w-2, h-2, p);	//rv.getWidth(), rv.getHeight(), p);
//			canvas.drawLine(0, 0, w-1, 0, p);
//			canvas.drawLine(w-1, 0, w-1, h-1, p);
//			canvas.drawLine(w-1, h-1, 0, h-1, p);
//			canvas.drawLine(0, h-1, 0, 0, p);
        }
        else {
            float pad = (rv.getWidth()*0.01f);
            float cx = (rv.getWidth()/2) - (pad);
            float cy = (rv.getHeight()/2) - (pad) + 2;
            float r = (rv.getWidth()/2) - pad;
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setColor(nextColor());
            canvas.drawCircle(cx, cy, r, p);
        }
        p.setColor(Color.WHITE);
        p.setTextSize(30.0f);   //dimens[DIMEN_IMGFONTSIZE]);	// * scale);
        String text = name.substring(0,1);
        Rect bounds = new Rect();
        p.getTextBounds(text, 0, text.length(), bounds);
        p.setShadowLayer(1f, 0f, 1f, Color.BLACK);
        int x = ((rv.getWidth() - bounds.width())/2) - 3;
        int y = (rv.getHeight() + bounds.height()) / 2;
        canvas.drawText(text, x, y, p);
        return rv;
    }

    private int nextColor() {
        if (colorNdx >= _colors.length)
            colorNdx = 0;
        return _colors[colorNdx++];
    }

    public void resetColors() {
        colorNdx = 0;
    }
}
