package com.axway.apigw.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.axway.apigw.android.R;
import com.axway.apigw.android.model.DisplayPrefs;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by su on 2/19/2016.
 */
public class KpsTableAdapter extends BaseAdapter {

    public static final int TYPE_STORE = 1;
    public static final int TYPE_PACKAGE = 2;

    private LayoutInflater inflater;
//    private List<Entry> entries;
    //private ImageFactory bmpFactory;
    private int flags;
    private KpsType kpsType;
    private KpsStore kpsStore;
    private JsonArray data;
    private DisplayPrefs layout;

    public KpsTableAdapter(Context ctx, KpsStore kpsStore, KpsType kpsType, DisplayPrefs layout, JsonArray data) {
        super();
        this.data = data;
        this.kpsType = kpsType;
        this.kpsStore = kpsStore;
        this.layout = layout;
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //bmpFactory = ImageFactory.getInstance(ctx);
//        createEntries(showInternal);
    }
/*

    public class Entry {
        public int type;
        public String id;
        public String name;
        public int data;

        public Entry(int type, String id, String name) {
            super();
            this.type = type;
            this.id = id;
            this.name = name;
        }

        public Entry(int type, String id, String name, int data) {
            this(type, id, name);
            this.data = data;
        }
    }

    private void createEntries(final boolean showInternal) {
        entries = null;
        //Kps kps = BaseApplication.getKpsClient().getKps();
        if (kps == null)
            return;
        entries = new ArrayList<Entry>();
        List<String> pkgs = kps.getPackageNames();
        if (pkgs == null || pkgs.size() == 0)
            return;
        for (String pkg: pkgs) {
            List<KpsStore> stores = kps.getStoresInPackage(pkg, showInternal);
            if (stores == null || stores.size() == 0)
                continue;
            entries.add(new Entry(TYPE_PACKAGE, pkg, pkg));
            for (KpsStore s: stores) {
                entries.add(new Entry(TYPE_STORE, s.getIdentity(), s.getAlias()));
            }
        }
    }
*/

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    @Override
    public Object getItem(int pos) {
        if (data == null || pos < 0 || pos >= data.size())
            return null;
        return data.get(pos).getAsJsonObject();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private int getLayoutId() {
        return R.layout.listitem_2;    //android.R.layout.simple_list_item_activated_2;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        JsonElement e = (JsonElement)getItem(pos);
        if (e == null || !e.isJsonObject()) {
            return null;
        }
        BasicViewHolder vh;
        JsonObject j = e.getAsJsonObject();
        if (view == null) {
            view = inflater.inflate(getLayoutId(), null);
            vh = new BasicViewHolder(view);
            vh.setImageVisibility(View.GONE);
            view.setTag(vh);
        }
        else {
            vh = (BasicViewHolder)view.getTag();
        }
        if (j != null) {
            vh.setText1(buildTitle(j));
            vh.setText2(buildDetails(j));
        }
        return view;
    }

    private String buildTitle(JsonObject j) {
        StringBuilder sb = new StringBuilder();
        if (layout.isEmpty()) {
            String key = kpsStore.getConfig().getKey();
            if (kpsStore.isGeneratedField(key)) {
                List<String> ndxs = kpsStore.getConfig().getIndexes();
                if (ndxs.size() > 0) {
                    key = ndxs.get(0);
                }
            }
            if (j.has(key))
                sb.append(j.get(key).getAsString());
        }
        else {
            for (int c = 0; c < DisplayPrefs.MAX_COLS; c++) {
                String key = layout.getCell(0, c);
                if (TextUtils.isEmpty(key) || DisplayPrefs.NOT_USED.equals(key))
                    continue;
                if (j.has(key))
                    sb.append(j.get(key).getAsString());
            }
        }
        return sb.toString();
    }

    private String buildDetails(JsonObject j) {
        StringBuilder sb = new StringBuilder();
        if (layout.isEmpty()) {
            String key = kpsStore.getConfig().getKey();
            Set<Map.Entry<String, JsonElement>> props = j.entrySet();
            for (Map.Entry<String, JsonElement> p : props) {
                if (p.getKey().equals(key))
                    continue;
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(p.getKey()).append(": ").append(p.getValue().getAsString());
            }
        }
        else {
            for (int r = 1; r < DisplayPrefs.MAX_ROWS; r++) {
                if (sb.length() > 0)
                    sb.append("\n");
                for (int c = 0; c < DisplayPrefs.MAX_COLS; c++) {
                    String key = layout.getCell(r, c);
                    if (TextUtils.isEmpty(key) || DisplayPrefs.NOT_USED.equals(key))
                        continue;
                    if (j.has(key)) {
                        if (sb.length() > 0)
                            sb.append(" ");
                        sb.append(j.get(key).getAsString());
                    }
                }
            }
        }
        return sb.toString();
    }
}
