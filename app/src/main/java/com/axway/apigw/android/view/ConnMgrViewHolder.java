package com.axway.apigw.android.view;

import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.model.ServerInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/10/2016.
 */
public class ConnMgrViewHolder {

    @Bind(R.id.edit_host) EditText edHost;
    @Bind(R.id.edit_port) EditText edPort;
    @Bind(R.id.edit_use_ssl) CheckBox edSsl;
    @Bind(R.id.edit_user) EditText edUser;
    @Bind(R.id.edit_passwd) EditText edPass;
    @Bind(R.id.edit_enabled) CheckBox edEnabled;

    public ConnMgrViewHolder(View v) {
        super();
        ButterKnife.bind(this, v);
    }

    public ConnMgrViewHolder populateUi(ServerInfo info) {
        if (info == null)
            return this;
        edHost.setText(info.getHost());
        edPort.setText(String.format("%d", info.getPort()));
        edSsl.setChecked(info.isSsl());
        edUser.setText(info.getUser());
        edPass.setText(info.getPasswd());
        edEnabled.setChecked(info.getStatus() == Constants.STATUS_ACTIVE);
        return this;
    }

    public ConnMgrViewHolder setTextWatcher(TextWatcher w) {
        edHost.addTextChangedListener(w);
        edPort.addTextChangedListener(w);
        edUser.addTextChangedListener(w);
        edPass.addTextChangedListener(w);
        return this;
    }

    public ConnMgrViewHolder setClickListener(View.OnClickListener l) {
        edSsl.setOnClickListener(l);
        edEnabled.setOnClickListener(l);
        return this;
    }

    public void validate() throws ValidationException {
        String empty = null;
        if (TextUtils.isEmpty(edHost.getText()))
            empty = "host";
        if (TextUtils.isEmpty(edUser.getText()))
            empty = "username";
        if (TextUtils.isEmpty(edPass.getText()))
            empty = "password";
        if (TextUtils.isEmpty(edPort.getText()))
            empty = "port";
        if (empty != null) {
            throw new ValidationException(empty + " is required");
        }
        int p = 0;
        try {
            p = Integer.parseInt(edPort.getText().toString());
        }
        catch (NumberFormatException e) {
            p = 0;
        }
        if (p < 1025 || p > 65535)
            throw new ValidationException(String.format("port %d is an invalid port", p));
//        return null;
    }

    public void collect(ServerInfo info) {
        info.setHost(edHost.getText().toString());
        info.setPort(Integer.parseInt(edPort.getText().toString()));
        info.setSsl(edSsl.isChecked());
        info.setUser(edUser.getText().toString());
        info.setPasswd(edPass.getText().toString());
        if (edEnabled.getVisibility() == View.VISIBLE) {
            info.setStatus(edEnabled.isChecked() ? Constants.STATUS_ACTIVE : Constants.STATUS_INACTIVE);
        }
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        }
        catch (ValidationException ve) {
            return false;
        }
    }
}
