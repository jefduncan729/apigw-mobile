package com.axway.apigw.android.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.fragment.SettingsFragment;

/**
 * Created by su on 2/17/2016.
 */
public class SettingsActivity extends BaseActivity implements SettingsFragment.Callback {

    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_pane);
/*
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!swipeRefresh.isRefreshing())
                        swipeRefresh.setRefreshing(true);
                    postEmptyMessage(Constants.MSG_REFRESH);
                }
            });
            swipeRefresh.setRefreshing(true);
        }
*/
        setResult(RESULT_CANCELED);
        postEmptyMessage(Constants.MSG_REFRESH);
    }

    @Override
    public void onSettingChanged(String key) {
        setResult(RESULT_OK);
/*
        if (swipeRefresh != null)
            swipeRefresh.setRefreshing(true);
        postEmptyMessage(Constants.MSG_REFRESH, 2000);
*/
    }

    @Override
    protected boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_REFRESH:
                replaceFragment(R.id.container01, SettingsFragment.newInstance(), Constants.TAG_SINGLE_PANE);
//                if (swipeRefresh != null)
//                    swipeRefresh.setRefreshing(false);
                return true;
        }
        return super.onHandleMessage(msg);
    }
}
