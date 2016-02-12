package com.axway.apigw.android.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by su on 1/28/2016.
 */
abstract public class ApiLoader<B, T> extends AsyncTaskLoader<T> {

    private static final String TAG = ApiLoader.class.getSimpleName();
    protected ApiClient client;

    protected ApiLoader(Context context, ApiClient client) {
        super(context);
        this.client = client;
    }

    abstract protected Request createRequest(ApiClient client);
    abstract protected B consumeBody(Response resp) throws IOException;
    abstract protected T transformBody(B body) throws IOException;

    protected void requestFailed(Response resp, Request req, Throwable t) {
        Log.e(TAG, "request failed", t);
    }

    @Override
    public T loadInBackground() {
        assert client != null;
        Log.d(TAG, "loadInBackground");
        T rv = null;
        Request req = createRequest(client);    //.createRequest("api/topology");
        Response resp = null;
        try {
            resp = client.executeRequest(req);
            if (resp.isSuccessful()) {
                rv = transformBody(consumeBody(resp));
            }
            else {
                requestFailed(resp, req, null);
            }
        }
        catch (IOException e) {
            requestFailed(resp, req, e);
        }
        return rv;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading");
        cancelLoad();
    }
}
