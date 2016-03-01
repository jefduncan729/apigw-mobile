package com.axway.apigw.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.R;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vordel.kps.json.Json;

/**
 * Created by su on 2/18/2016.
 */
abstract public class JsonArrayFragment extends ListFragment implements LoaderManager.LoaderCallbacks<JsonArray> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(hasOptsMenu());
        getLoaderManager().initLoader(1, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (hasContextMenu())
            getListView().setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onLoadFinished(Loader<JsonArray> loader, JsonArray jsonElements) {
        setListAdapter(createAdapter(jsonElements));
    }

    @Override
    public void onLoaderReset(Loader<JsonArray> loader) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        JsonObject jsonObject = (JsonObject)l.getItemAtPosition(position);
        BaseApp.post(new ItemSelectedEvent<JsonObject>(jsonObject));
        super.onListItemClick(l, v, position, id);
    }

    protected boolean hasOptsMenu() {
        return false;
    }

    protected int getLayoutId() {
        return android.R.layout.list_content;
    }

    abstract protected ListAdapter createAdapter(JsonArray a);

    protected boolean hasContextMenu() {
        return false;
    }
}
