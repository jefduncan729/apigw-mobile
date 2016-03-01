package com.axway.apigw.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.axway.apigw.android.ValidationException;

/**
 * Created by su on 2/10/2016.
 */
abstract public class EditFrag<T> extends Fragment {

    protected T item;
    private boolean dirty;
//    protected EditCallbacks<T> callbacks;

//    @Nullable @Bind(R.id.action_save) Button btnSave;

    protected EditFrag() {
        super();
        item = null;
        dirty = false;
    }
//
//    protected EditFrag(T item) {
//        this();
//        this.item = item;
//    }

    public boolean isValid() {
        try {
            validate();
            return true;
        }
        catch (ValidationException ve) {
            return false;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean newVal) {
        dirty = newVal;
//        if (btnSave != null) {
//            if (dirty) {
//                btnSave.setEnabled(isValid(false));
//            } else {
//                btnSave.setEnabled(false);
//            }
//        }
    }

    abstract public void populate(View view);
    abstract public void validate() throws ValidationException;
    abstract public void collect(T item, Bundle extras);
}
