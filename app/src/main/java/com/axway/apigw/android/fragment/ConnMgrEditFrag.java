package com.axway.apigw.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.view.ConnMgrViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/10/2016.
 */
public class ConnMgrEditFrag extends EditFrag<ServerInfo> implements View.OnClickListener, TextWatcher {

    private static final String TAG = ConnMgrEditFrag.class.getSimpleName();

    @Bind(R.id.container02) ViewGroup ctr02;
    @Bind(android.R.id.text1) TextView txt01;

//    private EditCallbacks<ServerInfo> callbacks;

    private boolean firstOne;
    private ConnMgrViewHolder cvh;

    public ConnMgrEditFrag() {
        super();
        firstOne = false;
    }

    public static ConnMgrEditFrag newInstance(ServerInfo info) {
        return newInstance(info, false);
    }

    public static ConnMgrEditFrag newInstance(ServerInfo info, boolean firstOne) {
        ConnMgrEditFrag rv = new ConnMgrEditFrag();
        rv.item = info;
        rv.firstOne = firstOne;
        return rv;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof EditCallbacks) {
//            Log.d(TAG, "attaching callbacks");
//            callbacks = (EditCallbacks)context;
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.welcome, null);
        ButterKnife.bind(this, rv);
        cvh = new ConnMgrViewHolder(rv);
        rv.setTag(cvh);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt01.setVisibility(firstOne ? View.VISIBLE : View.GONE);
        populate(view);
//        if (btnSave != null)
//            btnSave.setOnClickListener(this);
        setDirty(!isValid());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_save) {
//            saveChanges();
        }
        else {
            setDirty(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setDirty(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void populate(View view) {
        ConnMgrViewHolder vh = (ConnMgrViewHolder)view.getTag();
        vh.populateUi(item).setTextWatcher(this).setClickListener(this);
        cvh = vh;
    }

    @Override
    public void validate() throws ValidationException {
        cvh.validate();
    }

    @Override
    public void collect(ServerInfo i) {
        cvh.collect(i);
    }


//    public void setCallbacks(EditCallbacks<ServerInfo> cb) {
//        callbacks = cb;
//    }
//
//    private void saveChanges() {
//        if (callbacks != null) {
//            if (isValid(true)) {
//                collect();
//                callbacks.onEditSave(item);
//            }
//        }
//    }
}
