package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.EmptyFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/26/2016.
 */
public class SinglePaneActivity extends BaseActivity {

    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        Bundle args;
        if (savedInstanceState == null) {
            args = getIntent().getExtras();
        }
        else {
            args = savedInstanceState;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, createFragment(args), Constants.TAG_SINGLE_PANE).commit();
        if (toolbar != null)
            setupToolbar(toolbar);
    }

    protected Fragment createFragment(Bundle args) {
        return EmptyFragment.newInstance();
    }

    protected void setupToolbar(Toolbar toolbar) {
        String t = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        if (!TextUtils.isEmpty(t))
            toolbar.setTitle(t);
    }
}
