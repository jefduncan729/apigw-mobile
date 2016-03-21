package com.axway.apigw.android.view;

import android.view.View;
import android.widget.CheckBox;

import com.axway.apigw.android.R;
import com.axway.apigw.android.model.ServiceConfig;

/**
 * Created by su on 3/15/2016.
 */
public class RecCfgHolder implements ViewHolder {

    private CheckBox edIn;
    private CheckBox edOut;
    private CheckBox edPath;
    private CheckBox edTrace;

    public RecCfgHolder(View v) {
        this(v, null);
    }

    public RecCfgHolder(View v, ServiceConfig sc) {
        super();
        edIn = (CheckBox)v.findViewById(R.id.edit_rec_inbound);
        edOut = (CheckBox)v.findViewById(R.id.edit_rec_outbound);
        edPath = (CheckBox)v.findViewById(R.id.edit_rec_path);
        edTrace = (CheckBox)v.findViewById(R.id.edit_rec_trace);
        if (sc != null) {
            edIn.setChecked(sc.recordInbound());
            edOut.setChecked(sc.recordOutbound());
            edPath.setChecked(sc.recordCircuitPath());
            edTrace.setChecked(sc.recordTrace());
        }
    }

    private void _set(CheckBox cb, boolean newVal) {
        if (cb == null)
            return;
        cb.setChecked(newVal);
    }
    public RecCfgHolder recordInbound(boolean newVal) {
        _set(edIn, newVal);
        return this;
    }

    public RecCfgHolder recordOutbound(boolean newVal) {
        _set(edOut, newVal);
        return this;
    }

    public RecCfgHolder recordPath(boolean newVal) {
        _set(edPath, newVal);
        return this;
    }

    public RecCfgHolder recordTrace(boolean newVal) {
        _set(edTrace, newVal);
        return this;
    }

    public boolean _get(CheckBox cb) {
        return (cb != null && cb.isChecked());
    }

    public boolean recordInbound() {
        return _get(edIn);
    }

    public boolean recordOutbound() {
        return _get(edOut);
    }

    public boolean recordPath() {
        return _get(edPath);
    }

    public boolean recordTrace() {
        return _get(edTrace);
    }
}
