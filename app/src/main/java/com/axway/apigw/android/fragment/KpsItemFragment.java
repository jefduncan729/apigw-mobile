package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.text.InputType;
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
public class KpsItemFragment extends EditFrag<JsonObject> {

    private KpsStore kpsStore;
    private KpsType kpsType;
    @Bind(R.id.container01) ViewGroup parentView;

    private Map<String, View> views;

    public static KpsItemFragment newInstance(KpsStore store, KpsType type, JsonObject item) {
        KpsItemFragment rv = new KpsItemFragment();
        rv.kpsStore = store;
        rv.kpsType = type;
        rv.item = item;
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
        vh.setViewType(InputType.TYPE_CLASS_TEXT);
        rv.setTag(vh);
        vh.setLabel(nm);
        if (item.has(nm))
            vh.setEditValue(item.get(nm).getAsString());
        return rv;
    }

    private View editNumber(LayoutInflater inflater, String nm, String cls) {
        View rv = inflater.inflate(R.layout.edit_text, null);
        LabeledEditHolder vh = new LabeledEditHolder(rv);
        vh.setLabel(nm);
        vh.setViewType(InputType.TYPE_CLASS_NUMBER);
        rv.setTag(vh);
        if (item.has(nm))
            vh.setEditValue(String.format("%d", item.get(nm).getAsLong()));
        return rv;
    }

    private View checkbox(LayoutInflater inflater, String nm) {
        View rv = inflater.inflate(R.layout.checkbox, null);
        CheckboxHolder vh = new CheckboxHolder(rv);
        rv.setTag(vh);
        vh.setLabel(nm);
        if (item.has(nm)) {
            vh.setChecked(item.get(nm).getAsBoolean());
        }
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
            View v = views.get(nm);
            Object tag = v.getTag();
            if (tag == null)
                continue;
            if (tag instanceof LabeledEditHolder) {
                LabeledEditHolder vh = (LabeledEditHolder)tag;
                if (vh.getViewType() == 1)
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
        if (item.has(nm))
            item.remove(nm);
        item.addProperty(nm, vh.getEditValue());
    }

    private void collectNumber(LabeledEditHolder vh, String nm, JsonObject item) {
        if (item.has(nm))
            item.remove(nm);
        long val = Long.parseLong(vh.getEditValue());
        item.addProperty(nm, val);
    }

    private void collectBoolean(CheckboxHolder vh, String nm, JsonObject item) {
//        CheckboxHolder vh = (CheckboxHolder)v.getTag();
        if (item.has(nm))
            item.remove(nm);
        item.addProperty(nm, vh.isChecked());
    }
}
