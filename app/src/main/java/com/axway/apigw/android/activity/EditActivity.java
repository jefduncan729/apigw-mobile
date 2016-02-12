package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.fragment.EditFrag;

/**
 * Created by su on 2/10/2016.
 */
abstract public class EditActivity<T> extends BaseActivity {

    protected T item;
    protected EditFrag<T> editFrag;

    abstract protected EditFrag<T> createFragment(T item);

    abstract protected T createItem(Intent intent);
    abstract protected T loadItem(Intent intent);
    abstract protected boolean saveItem(T item);

    private Toolbar toolbar;
    protected boolean isInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        isInsert = false;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (Intent.ACTION_INSERT.equals(getIntent().getAction())){
            isInsert = true;
            item = createItem(getIntent());
        }
        else {
            item = loadItem(getIntent());
        }
        if (toolbar != null) {
            setupToolbar(toolbar);
            setActionBar(toolbar);
        }
        editFrag = createFragment(item);
        replaceFragment(R.id.container01, editFrag, Constants.TAG_SINGLE_PANE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                save();
                return true;
            case R.id.action_cancel:
                cancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.bus().unregister(this);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save() {
        try {
            editFrag.validate();
            editFrag.collect(item);
            if (saveItem(item)) {
                setResult(RESULT_OK);
                finish();
            }
        }
        catch (ValidationException ve) {
            showToast(ve.getMessage());
        }
    }

    protected void setupToolbar(final Toolbar toolbar) {
        if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
            toolbar.setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
        }
        else {
            toolbar.setTitle(String.format("%s Connection", (isInsert ? "Add" : "Edit")));
        }
    }
}
