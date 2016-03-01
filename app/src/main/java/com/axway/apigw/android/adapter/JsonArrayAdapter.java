package com.axway.apigw.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by su on 2/18/2016.
 */
abstract public class JsonArrayAdapter extends BaseAdapter {

    private JsonArray array;
    protected LayoutInflater inflater;

    public JsonArrayAdapter(Context ctx, JsonArray a) {
        super();
        this.array = a;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        if (array == null)
            return 0;
        return array.size();
    }

    @Override
    public Object getItem(int i) {
        if (array == null)
            return null;
        if (i < 0 || i >= array.size())
            return null;
        return array.get(i).getAsJsonObject();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rv = view;
        JsonObject j = (JsonObject)getItem(i);
        if (rv == null) {
            rv = createView(i);
            BasicViewHolder vh = new BasicViewHolder(rv);
            rv.setTag(vh);
        }
        populateView(rv, j);
        return rv;
    }

    abstract protected View createView(int i);

    abstract protected void populateView(View rv, JsonObject j);
}
