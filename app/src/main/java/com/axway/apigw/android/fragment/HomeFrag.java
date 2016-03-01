package com.axway.apigw.android.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.R;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.view.BasicViewHolder;

/**
 * Created by su on 2/10/2016.
 */
public class HomeFrag extends ListFragment {

    public static final int[] ACTION_IDS = {R.id.action_conn_mgr, R.id.action_connect, R.id.action_cfg_storage };
    public static final int[] DRAWABLE_IDS = {R.mipmap.internet, R.mipmap.server, R.mipmap.preferences };

    public HomeFrag() {
        super();
    }

    public static HomeFrag newInstance() {
        return new HomeFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }
/*

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            BaseApp.post(new ActionEvent(R.id.action_settings));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(android.R.layout.list_content, null);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new HomeAdapter());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (v == null || v.getTag() == null)
            return;
        BasicViewHolder vh = (BasicViewHolder)v.getTag();
        HomeAdapter.Entry e = (HomeAdapter.Entry)vh.getData();
        BaseApp.post(new ActionEvent(e.actionId));
//        super.onListItemClick(l, v, position, id);
    }

    private class HomeAdapter extends BaseAdapter {

        class Entry {
            int actionId;
            int drawableId;

            public Entry(int actionId, int drawableId) {
                super();
                this.actionId = actionId;
                this.drawableId = drawableId;
            }
        }

        Entry[] entries;

        public HomeAdapter() {
            super();
            entries = new Entry[ACTION_IDS.length];

            for (int i = 0; i < ACTION_IDS.length; i++) {
                entries[i] = new Entry(ACTION_IDS[i], DRAWABLE_IDS[i]);
            }
        }

        @Override
        public int getCount() {
            if (entries == null)
                return 0;
            return entries.length;
        }

        @Override
        public Object getItem(int i) {
            if (i < 0 || i >= ACTION_IDS.length)
                return null;
            return entries[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Entry e = (Entry)getItem(i);
            if (e == null)
                return view;
            BasicViewHolder vh = null;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.home_item, null);
                vh = new BasicViewHolder(view);
                vh.setData(e);
                view.setTag(vh);
            }
            else {
                vh = (BasicViewHolder)view.getTag();
            }
            if (e.actionId == R.id.action_conn_mgr) {
                vh.setText1("Connection Manager").setText2("Configure connections to Admin Node Managers");
            }
            else if (e.actionId == R.id.action_connect) {
                vh.setText1("Work with Topology").setText2("Work with the topology of a configured Admin Node Manager");
            }
            else if (e.actionId == R.id.action_cfg_storage) {
                vh.setText1("Configure Cloud Storage").setText2("Configure storage folders in your Drive account");
            }
            vh.setImageResource(e.drawableId);
            return view;
        }
    }
}
