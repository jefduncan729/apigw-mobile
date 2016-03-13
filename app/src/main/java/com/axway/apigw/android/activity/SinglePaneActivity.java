package com.axway.apigw.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toolbar;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.EmptyFragment;
import com.axway.apigw.android.view.FloatingActionButton;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/26/2016.
 */
public class SinglePaneActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args;
        if (savedInstanceState == null) {
            args = getIntent().getExtras();
        }
        else {
            args = savedInstanceState;
        }
        setContentView(R.layout.toolbar_pane);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container01, createFragment(args), Constants.TAG_SINGLE_PANE).commit();
    }

    protected Fragment createFragment(Bundle args) {
        return EmptyFragment.newInstance();
    }


    @Override
    protected void setupFab(FloatingActionButton fab) {
        fab.setVisibility(View.GONE);
    }
}
