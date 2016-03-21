package com.axway.apigw.android.view;

import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by su on 2/23/2016.
 */
public class LabeledEditHolder implements ViewHolder {

    private TextView label;
    private EditText edit;
    private int viewType;
    private Object data;

    public LabeledEditHolder(View rv) {
        this(rv, BasicViewHolder.DEF_TEXT1_ID, BasicViewHolder.DEF_TEXT2_ID);
    }

    public LabeledEditHolder(View rv, int labelId) {
        this(rv, labelId, BasicViewHolder.DEF_TEXT2_ID);
    }

    public LabeledEditHolder(View rv, int labelId, int editId) {
        super();
        viewType = 0;
        label = (TextView) rv.findViewById(labelId);
        edit = (EditText) rv.findViewById(editId);
        if (edit != null)
            edit.setTag(this);
        data = null;
    }

    public LabeledEditHolder setLabel(String newVal) {
        if (label == null)
            return this;
        label.setText(newVal);
        return this;
    }

    public LabeledEditHolder setTextWatcher(TextWatcher w) {
        if (edit == null)
            return this;
        edit.addTextChangedListener(w);
        return this;
    }

    public LabeledEditHolder setEditValue(String newVal) {
        if (edit == null)
            return this;
        edit.setText(newVal);
        return this;
    }

    public String getEditValue() {
        if (edit == null)
            return null;
        return edit.getText().toString();
    }

    public LabeledEditHolder setData(Object o) {
        data = o;
        return this;
    }

    public Object getData() {
        return data;
    }

    public int getViewType() {
        return viewType;
    }

    public LabeledEditHolder setViewType(int viewType) {
        this.viewType = viewType;
        if (edit == null)
            return this;
        edit.setRawInputType(InputType.TYPE_CLASS_TEXT);
        return this;
    }
}
