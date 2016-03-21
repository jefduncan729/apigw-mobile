package com.axway.apigw.android.view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by su on 2/23/2016.
 */
public class CheckboxHolder implements ViewHolder {

    private TextView label;
    private CheckBox edit;
    private int viewType;
    private Object data;

    public CheckboxHolder(View rv) {
        this(rv, BasicViewHolder.DEF_TEXT1_ID, android.R.id.button1);
    }

    public CheckboxHolder(View rv, int labelId) {
        this(rv, labelId, android.R.id.button1);
    }

    public CheckboxHolder(View rv, int labelId, int editId) {
        super();
        viewType = 0;
        label = (TextView) rv.findViewById(labelId);
        edit = (CheckBox) rv.findViewById(editId);
        if (edit != null)
            edit.setTag(this);
        data = null;
    }

    public CheckboxHolder setLabel(String newVal) {
        if (label == null)
            return this;
        label.setText(newVal);
        return this;
    }

    public CheckboxHolder setChecked(boolean newVal) {
        if (edit == null)
            return this;
        edit.setChecked(newVal);
        return this;
    }

    public boolean isChecked() {
        if (edit == null)
            return false;
        return edit.isChecked();
    }

    public CheckboxHolder setListener(View.OnClickListener l) {
        if (edit == null)
            return this;
        edit.setOnClickListener(l);
        return this;
    }

    public CheckboxHolder setData(Object o) {
        data = o;
        return this;
    }

    public Object getData() {
        return data;
    }
}
