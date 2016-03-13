package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.model.ObservableJsonObject;
import com.axway.apigw.android.view.CheckboxHolder;
import com.axway.apigw.android.view.LabeledEditHolder;
import com.google.gson.JsonObject;

import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/23/2016.
 */
public class KpsItemFragment extends EditFrag<JsonObject> implements TextWatcher, View.OnClickListener {
    private static final String TAG = KpsItemFragment.class.getSimpleName();
    private KpsStore kpsStore;
    private KpsType kpsType;
    @Bind(R.id.container01) ViewGroup parentView;
    private ObservableJsonObject observable;

    private Map<String, View> views;

    public static KpsItemFragment newInstance(KpsStore store, KpsType type, ObservableJsonObject item) {
        KpsItemFragment rv = new KpsItemFragment();
        rv.kpsStore = store;
        rv.kpsType = type;
        rv.item = item.jsonObject();
        rv.observable = item;
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.scrollable, null);
        ButterKnife.bind(this, rv);
        buildView(inflater);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populate(view);
        setDirty(false);
//        viewFilled = true;
    }

    private void buildView(LayoutInflater inflater) {
        for (Map.Entry<String, String> p : kpsType.getProperties().entrySet()) {
            String nm = p.getKey();
            String cls = p.getValue();
            if (kpsStore.isGeneratedField(nm))
                continue;
            if ("java.lang.String".equals(cls)) {
                addView(nm, editText(inflater, nm));
            }
            else if ("java.lang.Long".equals(cls) || "java.lang.Integer".equals(cls) || "java.lang.Double".equals(cls)) {
                addView(nm, editNumber(inflater, nm, cls));
            }
            else if ("java.lang.Boolean".equals(cls)) {
                addView(nm, checkbox(inflater, nm));
            }
            else if ("java.lang.Byte".equals(cls)) {
            }
        }
    }

    private void addView(String nm, View view) {
        if (views == null) {
            views = new HashMap<>();
        }
        if (!views.containsKey(nm)) {
            views.put(nm, view);
            parentView.addView(view);
        }
    }

    private View getView(String nm) {
        if (views == null)
            return null;
        return views.get(nm);
    }

    private View editText(LayoutInflater inflater, String nm) {
        View rv = inflater.inflate(R.layout.edit_text, null);
        LabeledEditHolder vh = new LabeledEditHolder(rv);
        rv.setTag(vh);
        vh.setViewType(InputType.TYPE_CLASS_TEXT)
            .setLabel(nm)
            .setData(nm)
            .setEditValue(observable.getString(nm))  //item.get(nm).getAsString());
            .setTextWatcher(new FieldWatcher(vh));
        return rv;
    }

    private View editNumber(LayoutInflater inflater, String nm, String cls) {
        View rv = inflater.inflate(R.layout.edit_text, null);
        LabeledEditHolder vh = new LabeledEditHolder(rv);
        rv.setTag(vh);
        vh.setViewType(InputType.TYPE_CLASS_NUMBER)
            .setLabel(nm)
            .setData(nm)
            .setEditValue(String.format("%d", observable.getLong(nm)))
            .setTextWatcher(new FieldWatcher(vh));
        return rv;
    }

    private View checkbox(LayoutInflater inflater, String nm) {
        View rv = inflater.inflate(R.layout.checkbox, null);
        CheckboxHolder vh = new CheckboxHolder(rv);
        rv.setTag(vh);
        vh.setLabel(nm)
            .setData(nm)
            .setChecked(observable.getBoolean(nm))
            .setListener(this);
        return rv;
    }

    @Override
    public void populate(View view) {

    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public void collect(JsonObject item, Bundle extras) {
        if (views == null)
            return;
        for (String nm: views.keySet()) {
            View v = getView(nm);
            if (v == null)
                continue;
            Object tag = v.getTag();
            if (tag == null)
                continue;
            if (tag instanceof LabeledEditHolder) {
                LabeledEditHolder vh = (LabeledEditHolder)tag;
                if (vh.getViewType() == InputType.TYPE_CLASS_TEXT)
                    collectText(vh, nm, item);
                else
                    collectNumber(vh, nm, item);
            }
            else if (tag instanceof CheckboxHolder) {
                collectBoolean((CheckboxHolder)tag, nm, item);

            }
/*
            String cls = (String)v.getTag();
            if (cls.endsWith("String")) {
                collectText(v, nm, item);
            }
            else if (cls.endsWith("Boolean")) {
                collectBoolean(v, nm, item);
            }
            else {
                collectNumber(v, nm, item);
            }
*/
        }
    }

    private void collectText(LabeledEditHolder vh, String nm, JsonObject item) {
//        LabeledEditHolder vh = (LabeledEditHolder)v.getTag();
//        EditText e = (EditText)v.findViewById(android.R.id.text1);
//        if (e == null)
//            return;
//        if (item.has(nm))
//            item.remove(nm);
//        item.addProperty(nm, vh.getEditValue());
        if (observable == null)
            return;
        observable.setProperty(nm, vh.getEditValue());
    }

    private void collectNumber(LabeledEditHolder vh, String nm, JsonObject item) {
        if (observable == null)
            return;
        long val = Long.parseLong(vh.getEditValue());
        observable.setProperty(nm, val);
    }

    private void collectBoolean(CheckboxHolder vh, String nm, JsonObject item) {
//        CheckboxHolder vh = (CheckboxHolder)v.getTag();
        if (observable == null)
            return;
        observable.setProperty(nm, vh.isChecked());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, String.format("beforeTextChanged: %s, %d, %d, %d", s, start, count, after));
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, String.format("onTextChanged: %s, %d, %d, %d", s, start, before, count));
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG, String.format("afterTextChanged: %s", s));
    }

    @Override
    public void onClick(View v) {
        Object t = v.getTag();
        if (t == null)
            return;
        if (t instanceof CheckboxHolder) {
            CheckboxHolder h = (CheckboxHolder)t;
            observable.setProperty((String)h.getData(), h.isChecked());
            return;
        }
    }

    private class FieldWatcher implements TextWatcher {

        private LabeledEditHolder holder;

        public FieldWatcher(LabeledEditHolder holder) {
            super();
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            String nm = (String)holder.getData();
            if (TextUtils.isEmpty(nm))
                return;
            observable.setProperty(nm, s.toString());
        }
    }
}
