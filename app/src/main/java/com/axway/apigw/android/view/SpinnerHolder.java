package com.axway.apigw.android.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.axway.apigw.android.R;

/**
 * Created by jef on 3/10/16.
 */
public class SpinnerHolder {
    private TextView label;
    private Spinner edit;
    private int viewType;
    private Object data;
    private int curPos;

    public SpinnerHolder(View rv) {
        this(rv, BasicViewHolder.DEF_TEXT1_ID, R.id.spinner01);
    }

    public SpinnerHolder(View rv, int labelId) {
        this(rv, labelId, android.R.id.button1);
    }

    public SpinnerHolder(View rv, int labelId, int editId) {
        super();
        viewType = 0;
        curPos = 0;
        label = (TextView) rv.findViewById(labelId);
        edit = (Spinner) rv.findViewById(editId);
        if (edit != null)
            edit.setTag(this);
        data = null;
    }

    public SpinnerHolder setLabel(String newVal) {
        if (label == null)
            return this;
        label.setText(newVal);
        return this;
    }

    public SpinnerHolder setAdapter(ArrayAdapter adapter) {
        if (edit == null)
            return this;
        edit.setAdapter(adapter);
        return this;
    }

    public int spinnerPosition() {
        return curPos;
    }

    public SpinnerHolder setListener(AdapterView.OnItemSelectedListener l) {
        if (edit == null)
            return this;
        edit.setOnItemSelectedListener(l);
        return this;
    }

    public SpinnerHolder setData(Object o) {
        data = o;
        return this;
    }

    public Object getData() {
        return data;
    }
}
