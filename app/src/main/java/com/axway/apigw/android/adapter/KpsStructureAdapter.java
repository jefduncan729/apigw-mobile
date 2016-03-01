package com.axway.apigw.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.axway.apigw.android.model.KpsField;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.view.BasicViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by su on 2/22/2016.
 */
public class KpsStructureAdapter extends BaseAdapter {

    private List<KpsField> fields;
    private LayoutInflater inflater;
    private KpsStore kpsStore;
    private KpsType kpsType;

    public KpsStructureAdapter(Context ctx, KpsStore store, KpsType type) {
        super();
        inflater = LayoutInflater.from(ctx);
        kpsStore = store;
        kpsType = type;
        createEntries();
    }

    @Override
    public int getCount() {
        if (fields == null)
            return 0;
        return fields.size();
    }

    @Override
    public Object getItem(int i) {
        if (fields == null)
            return null;
        return fields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BasicViewHolder vh = null;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
            vh = new BasicViewHolder(view);
            view.setTag(vh);
        }
        else {
            vh = (BasicViewHolder)view.getTag();
        }
        KpsField fld = (KpsField)getItem(i);
        vh.setText1(fld.fldName);
        vh.setText2(buildDetails(fld));
        return view;
    }

    private void createEntries() {
        fields = new ArrayList<>();
        Set<Map.Entry<String, String>> props = kpsType.getProperties().entrySet();
        for (Map.Entry<String, String> p: props) {
            String key = p.getKey();
            KpsField fld = new KpsField(key, p.getValue());
            fld.isSecure = kpsStore.isEncryptedField(key);
            fld.isGenerated = kpsStore.isGeneratedField(key);
            fld.isIndex = kpsStore.isIndex(key);
            fld.isPrimaryKey = kpsStore.isPrimaryKey(key);
            fields.add(fld);
        }
    }

    private String buildDetails(KpsField fld) {
        StringBuilder dtls = new StringBuilder();
        if (fld == null)
            return dtls.toString();
        if (fld.isPrimaryKey)
            dtls.append("Primary Key");
        if (fld.isIndex) {
            if (dtls.length() > 0)
                dtls.append(", ");
            dtls.append("Index");
        }
        if (fld.isGenerated) {
            if (dtls.length() > 0)
                dtls.append(", ");
            dtls.append("Generated");
        }
        if (fld.isSecure) {
            if (dtls.length() > 0)
                dtls.append(", ");
            dtls.append("Encrypted");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(fld.fldType);
        if (dtls.length() > 0)
            sb.append(" - ");
        sb.append(dtls);
        return sb.toString();
    }
}
