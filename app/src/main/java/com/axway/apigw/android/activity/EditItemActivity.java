package com.axway.apigw.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.api.DeploymentModel;
import com.axway.apigw.android.event.ItemSelectedEvent;
import com.axway.apigw.android.fragment.EditFrag;
import com.axway.apigw.android.fragment.ManagePropsFragment;
import com.axway.apigw.android.model.DeploymentDetails;
import com.axway.apigw.android.model.ObservableJsonObject;
import com.axway.apigw.android.util.NameValuePair;
import com.axway.apigw.android.view.FloatingActionButton;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by su on 2/29/2016.
 */
public abstract class EditItemActivity<T> extends BaseActivity implements Observer {

    private static final String TAG = EditItemActivity.class.getSimpleName();
    protected T item;
    protected EditFrag<T> editFrag;
    protected boolean isInsert;
    private boolean dirty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        obsItem = null;
        Bundle args;
        boolean saved = false;
        if (savedInstanceState == null) {
            isInsert = Intent.ACTION_INSERT.equals(getIntent().getAction());
            args = getIntent().getExtras();
        }
        else {
            isInsert = savedInstanceState.getBoolean(Intent.EXTRA_LOCAL_ONLY);
            args = savedInstanceState;
            saved = true;
        }
        createFromArgs(args, saved);
        setContentView(R.layout.toolbar_pane);
        showProgressBar(true);
        refreshFrag();
    }

    abstract protected void createFromArgs(Bundle args, boolean saved);

    abstract protected Observable getObservable();

    abstract protected EditFrag<T> createFragment();
    abstract protected void performSave();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (item != null)
            outState.putString(Constants.EXTRA_JSON_ITEM, item.toString());
        outState.putBoolean(Intent.EXTRA_LOCAL_ONLY, isInsert);
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
        switch (item.getItemId()) {
            case R.id.action_save:
                onSave();
                return true;
            case R.id.action_cancel:
                onCancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void refreshFrag() {
        Observable observable = getObservable();
        if (observable == null)
            throw new IllegalStateException("must provide Observable object");
        observable.addObserver(this);
        editFrag = createFragment();    //ManagePropsFragment.newInstance(observable);
        showProgressBar(false);
        replaceFragment(R.id.container01, editFrag, Constants.TAG_SINGLE_PANE);
    }

    protected void addItem() {
        Log.d(TAG, "addItem");
    }

    protected boolean isDirty() {
//        return (obsItem != null && obsItem.hasChangedKeys());
        return dirty;
    }

    protected void onSave() {
        if (!isDirty()) {
            Log.d(TAG, "onSave: nothing changed");
            performCancel();
            return;
        }
        try {
            onValidate();
        }
        catch (ValidationException e) {
            showToast(e.getMessage());
            return;
        }
//        Log.d(TAG, String.format("onSave: %s\n%s", item, obsItem));
        performSave();
    }

    protected void onCancel() {
        if (isDirty()) {
            confirmCancel();
            return;
        }
        performCancel();
    }

    protected void onValidate() throws ValidationException {
        if (editFrag == null)
            throw new ValidationException("no edit fragment");
        editFrag.validate();
    }

    protected void confirmCancel() {
        confirmDialog("Touch OK to discard changes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performCancel();
            }
        });
    }

    protected void performCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void putNotNull(Map<String, String> map, String key, String val) {
        if (TextUtils.isEmpty(val))
            return;
        map.put(key, val);
    }

    @Override
    protected void setupFab(FloatingActionButton f) {
        f.setVisibility(View.GONE);
    }
/*

    @Subscribe
    public void onSelected(ItemSelectedEvent<NameValuePair> evt) {
        final NameValuePair item = evt.data;
        String title = String.format("Edit %s", item.name);
        customDialog(title, R.layout.name_dlg, new CustomDialogCallback() {
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
//                performDestAction(R.id.action_add, k, s);
            }
            @Override
            public boolean validate(AlertDialog dlg) {
                EditText ed = (EditText) dlg.findViewById(R.id.edit_name);
                String s = ed.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    showToast("Provide a value");
                    return false;
                }
                return true;
            }
        });
    }
*/

    @Override
    public void onClicked(FloatingActionButton fab) {
        addItem();
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, String.format("update: %s, %s", observable, data));
        dirty = true;
    }
}
