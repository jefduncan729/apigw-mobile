package com.axway.apigw.android.activity;

import android.os.Bundle;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.SettingsFragment;

/**
 * Created by su on 2/17/2016.
 */
public class SettingsActivity extends BaseActivity implements SettingsFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_pane);
        setResult(RESULT_CANCELED);
        replaceFragment(R.id.container01, SettingsFragment.newInstance(), Constants.TAG_SINGLE_PANE);
    }

    @Override
    public void onSettingChanged(String key) {
        setResult(RESULT_OK);
    }
}
