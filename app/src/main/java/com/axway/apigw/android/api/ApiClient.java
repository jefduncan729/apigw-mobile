package com.axway.apigw.android.api;

import android.util.Log;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.model.ServerInfo;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by su on 1/25/2016.
 */
public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();

    private ServerInfo srvr;
    private OkHttpClient httpClient;
    private boolean forceTrailingSlash;

    protected ApiClient() {
        super();
        httpClient = null;
        srvr = null;
        forceTrailingSlash = false;
    }

    protected ApiClient(ServerInfo srvr) {
        this();
        this.srvr = srvr;
    }

    public static ApiClient from(ServerInfo srvr) {
        ApiClient rv = new ApiClient(srvr);
        return rv;
    }

    protected OkHttpClient getHttpClient() {
        assert srvr != null;
        if (httpClient == null) {
            OkHttpClient.Builder bldr = new OkHttpClient.Builder()
                    .readTimeout(300, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .hostnameVerifier(BaseApp.hostVerifier());
            if (srvr.isSsl()) {
                bldr.sslSocketFactory(BaseApp.sslSocketFactory());
            }
            httpClient = bldr.build();
        }
        return httpClient;
    }

    protected String buildUrl(String endpoint) {
        assert srvr != null;
        StringBuilder sb = new StringBuilder();
        sb.append(srvr.isSsl() ? Constants.HTTPS_SCHEME : Constants.HTTP_SCHEME).append("://");
        sb.append(srvr.getHost()).append(":").append(srvr.getPort()).append("/").append(endpoint);
        String rv = sb.toString();
        if (forceTrailingSlash && !rv.endsWith("/"))
            rv += "/";
        return rv;
    }

    public boolean isForceTrailingSlash() {
        return forceTrailingSlash;
    }

    public void setForceTrailingSlash(boolean forceTrailingSlash) {
        this.forceTrailingSlash = forceTrailingSlash;
    }

//    public void get(String endpoint, ResponseHandler<JsonObject> handler) {
//        sendRequest(endpoint, handler);
//        String cred = Credentials.basic(srvr.getUser(), srvr.getPasswd());
//        Request req = new Request.Builder()
//                .url(buildUrl(endpoint))
//                .header("Authorization", cred)
//                .build();
//        GetTask<JsonObject> t = new GetTopologyTask(handler);
//        t.execute(req);
//    }

    public Request createRequest(final String endpoint) {
        String cred = Credentials.basic(srvr.getUser(), srvr.getPasswd());
        final Request req = new Request.Builder()
                .url(buildUrl(endpoint))
                .header("Authorization", cred)
                .build();
        return req;
    }

    public void reset() {
        BaseApp.resetSocketFactory();
        httpClient = null;
    }

    public Request createRequest(final String endpoint, JsonObject obj) {
        return createRequest(endpoint, "POST", obj);
    }

    public Request createRequest(final String endpoint, String method, JsonObject obj) {
        String cred = Credentials.basic(srvr.getUser(), srvr.getPasswd());
        final Request.Builder bldr = new Request.Builder();
        bldr.url(buildUrl(endpoint))
            .header("Authorization", cred)
            .method(method, RequestBody.create(MediaType.parse("application/json"), (obj == null ? "": obj.toString())));
        return bldr.build();
    }

    public Response executeRequest(final Request req) throws IOException {
        Log.d(TAG, String.format("executeRequest: %s", req));
        Response resp = getHttpClient().newCall(req).execute();
        Log.d(TAG, String.format("response: %s", resp));
        return resp;
    }

    public void executeAsyncRequest(final Request req, final Callback callback) {
        Log.d(TAG, String.format("executeRequestAsync: %s", req));
        getHttpClient().newCall(req).enqueue(callback);
    }

    public void checkCert(Callback callback) {
        if (srvr == null)
            return;
        executeAsyncRequest(createRequest("api"), callback);
    }

    public ServerInfo getServerInfo() {
        return srvr;
    }
//    protected void sendRequest(final String endpoint, final ResponseHandler handler) {
//        String cred = Credentials.basic(srvr.getUser(), srvr.getPasswd());
//        final Request req = new Request.Builder()
//                .url(buildUrl(endpoint))
//                .header("Authorization", cred)
//                .build();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Log.d(TAG, String.format("execute request: %s", req.toString()));
//                    Response resp = getHttpClient().newCall(req).execute();
//                    Log.d(TAG, String.format("data: %s", resp.toString()));
//                    if (resp.isSuccessful()) {
//                        if (handler != null) {
//                            JsonObject obj = JsonHelper.getInstance().parseAsObject(resp.body().string());
//                            if (obj != null && obj.has("result")) {
//                                obj = obj.getAsJsonObject("result");
//                            }
//                            handler.onSuccess(resp.code(), resp.headers(), obj);
//                        }
//                    }
//                }
//                catch (IOException e) {
//                    if (handler != null) handler.onFailure(-1, e);
//                }
//            }
//        }.start();
//    }
//
//    private final class GetTopologyTask extends GetTask<JsonObject> {
//
//        public GetTopologyTask(ResponseHandler<JsonObject> handler) {
//            super(handler, "result");
//        }
//
//        @Override
//        protected JsonObject parseResponse(Response resp) {
//            JsonObject rv = null;
//            try {
//                String s = resp.body().string();
//                rv = JsonHelper.getInstance().parseAsObject(s);
//                if (rv != null && !TextUtils.isEmpty(attrName)) {
//                    rv = rv.getAsJsonObject(attrName);
//                }
//            }
//            catch (Exception e) {
//                if (handler != null)
//                    handler.onFailure(code, e);
//            }
//            return rv;
//        }
//
//        @Override
//        protected OkHttpClient getClient() {
//            return getHttpClient();
//        }
//    }
}
