package com.axway.apigw.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.axway.apigw.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/3/2016.
 */
public class ProgressFragment extends Fragment {

    @Bind(android.R.id.progress) ProgressBar progressBar;
    @Bind(android.R.id.text1) TextView textView;

    private String msg;

    public ProgressFragment() {
        super();
        msg = null;
    }

    public static ProgressFragment newInstance() {
        ProgressFragment rv = new ProgressFragment();
        return rv;
    }

    public static ProgressFragment newInstance(String msg) {
        ProgressFragment rv = newInstance();
        rv.msg = msg;
        return rv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.prog_frag, null);
        ButterKnife.bind(this, rv);
        return rv;  //super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        if (msg == null) {
            textView.setVisibility(View.GONE);
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(msg);
        }
    }
}
