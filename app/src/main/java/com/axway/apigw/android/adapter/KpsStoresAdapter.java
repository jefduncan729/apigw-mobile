package com.axway.apigw.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.axway.apigw.android.R;
import com.axway.apigw.android.model.Kps;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.view.BasicViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by su on 2/19/2016.
 */
public class KpsStoresAdapter extends BaseAdapter {

    public static final int TYPE_STORE = 1;
    public static final int TYPE_PACKAGE = 2;

    private LayoutInflater inflater;
    private List<Entry> entries;
    //private ImageFactory bmpFactory;
    private int flags;
    private Kps kps;

    public KpsStoresAdapter(Context ctx, Kps kps, boolean showInternal) {
        super();
        this.kps = kps;
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //bmpFactory = ImageFactory.getInstance(ctx);
        createEntries(showInternal);
    }

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

    @Override
    public int getCount() {
        if (entries == null)
            return 0;
        return entries.size();
    }

    @Override
    public Object getItem(int pos) {
        if (entries == null || pos < 0 || pos >= entries.size())
            return null;
        return entries.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private int getLayoutId(Entry e) {
        if (e.type == TYPE_STORE)
            return android.R.layout.simple_list_item_activated_2;
        if (e.type == TYPE_PACKAGE)
            return R.layout.list_hdr;
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        Entry e = (Entry)getItem(pos);
        if (e == null) {
            return null;
        }
        BasicViewHolder holder = (view == null ? null : (BasicViewHolder)view.getTag());
        if (view == null || (holder != null && holder.getViewType() != e.type)) {
            view = inflater.inflate(getLayoutId(e), null);
            holder = new BasicViewHolder(view);
            holder.setViewType(e.type);
            view.setTag(holder);
        }
        else {
            holder = (BasicViewHolder) view.getTag();
        }
        if (e.type == TYPE_STORE) {
            KpsStore store = kps.getStoreById(e.id);    //BaseApplication.getKpsClient().getKps().getStoreById(e.id);
            if (store != null) {
                holder.setText1(e.name);
                holder.setText2(buildDetail(store));
            }
        }
        else if (e.type == TYPE_PACKAGE) {
            view.setFocusable(false);
            view.setClickable(false);
            holder.setText1(e.name);
        }
        return view;
    }

    private String buildDetail(KpsStore store) {
        StringBuilder sb = new StringBuilder();
        if (store == null)
            return sb.toString();
        sb.append(store.getDescription());
//        KpsType t = KpsModel.getInstance().getKps().getType(store.getTypeId());
//        Map<String, String> props = t.getProperties();
//        sb.append("\n").append(props.size()).append(" field").append(props.size() == 1 ? "" : "s");
        return sb.toString();
    }
}
