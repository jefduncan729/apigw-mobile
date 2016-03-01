package com.axway.apigw.android.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.JsonHelper;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.activity.BaseActivity;
import com.axway.apigw.android.adapter.BaseListAdapter;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.util.NameValuePair;
import com.axway.apigw.android.view.BasicViewHolder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by su on 2/29/2016.
 */
public class ManagePropsFragment extends EditFrag<JsonObject> implements AdapterView.OnItemClickListener {

    private List<NameValuePair> list;
    public ListView listView;

    public static ManagePropsFragment newInstance(JsonObject j) {
        ManagePropsFragment rv = new ManagePropsFragment();
        if (j == null)
            rv.list = new ArrayList<>();
        else
            rv.list = JsonHelper.getInstance().toNameValuePairs(j);
        return rv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
        listView = (ListView)rv.findViewById(android.R.id.list);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(new PropsAdapter(getActivity(), list));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void populate(View view) {

    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public void collect(JsonObject item, Bundle extras) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BasicViewHolder vh = (BasicViewHolder)view.getTag();
        NameValuePair item = (NameValuePair)vh.getData();
//        BaseApp.post(new ItemSelectedEvent<NameValuePair>(item));
        editItem(item);
    }

    private void editItem(final NameValuePair item) {
//        final NameValuePair item = evt.data;
        final BaseActivity ba = (BaseActivity)getActivity();
        String title = String.format("Edit %s", item.name);
        ba.customDialog(title, R.layout.name_dlg, new BaseActivity.CustomDialogCallback() {
            @Override
            public void populate(AlertDialog dlg) {
                TextView txt = (TextView) dlg.findViewById(R.id.label_name);
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
//              txt.setText(item.name);
                txt.setVisibility(View.GONE);
                ed.setText(item.value);
            }

            @Override
            public void save(AlertDialog dlg) {
                TextView txt = (TextView) dlg.findViewById(R.id.label_name);
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                if (!s.equals(item.value)) {
                    item.value = s;
                    setDirty(true);
                }
//                performDestAction(R.id.action_add, k, s);
            }

            @Override
            public boolean validate(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ba.showToast("Provide a value");
                    return false;
                }
                return true;
            }
        });

    }
    private class PropsAdapter extends BaseListAdapter<NameValuePair> {

        public PropsAdapter(Context ctx, List<NameValuePair> list) {
            super(ctx, list);
        }

        @Override
        protected void populateView(NameValuePair item, View view) {
            BasicViewHolder vh = (BasicViewHolder)view.getTag();
            vh.hideImage()
                    .setText1(String.format("%s: %s", item.name, item.value))
                    .setData(item);
        }

        @Override
        protected void viewCreated(View view) {
            super.viewCreated(view);
            view.setTag(new BasicViewHolder(view));
        }

        /*
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BasicViewHolder vh;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.listitem_1, null);
                vh = new BasicViewHolder(view);
                view.setTag(vh);
            }
            else {
                vh = (BasicViewHolder)view.getTag();
            }
            Entry e = (Entry)getItem(i);
            vh.hideImage()
                .setText1(String.format("%s: %s", e.name, e.val))
                .setData(e);
            return view;
        }
*/
    }
}
