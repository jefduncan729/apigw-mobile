package com.axway.apigw.android.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.model.StatusObserver;

/**
 * Created by su on 2/3/2016.
 */
public class ServiceViewHolder extends BasicViewHolder implements StatusObserver {

    ProgressBar progress;
    ImageView statImg;

    public ServiceViewHolder(View rv) {
        super(rv);
        progress = (ProgressBar)rv.findViewById(android.R.id.progress);
        statImg = (ImageView)rv.findViewById(android.R.id.icon1);
    }

    public int getStatus() {
        if (progress == null)
            return TopologyModel.GATEWAY_STATUS_UNKNOWN;
        Object o = progress.getTag();
        if (o == null || !(o instanceof Integer))
            return 0;
        return (Integer)o;
    }

    public ServiceViewHolder setStatus(int newVal) {
        if (progress == null || getStatus() == newVal)
            return this;
        progress.setTag(newVal);
        if (newVal == TopologyModel.GATEWAY_STATUS_CHECKING) {
            progress.setVisibility(View.VISIBLE);
        }
        else {
            progress.setVisibility(View.GONE);
        }
        if (statImg == null)
            return this;
        switch (newVal) {
            case TopologyModel.GATEWAY_STATUS_RUNNING:
            case TopologyModel.GATEWAY_STATUS_NOT_RUNNING:
                statImg.setVisibility(View.VISIBLE);
                statImg.setImageDrawable(BaseApp.getInstance().statusDrawable(newVal));
            break;
            default:
                statImg.setVisibility(View.GONE);
        }
        return this;
    }

    public ServiceViewHolder setStatClickListener(String instId, View.OnClickListener l) {
        if (statImg != null) {
            statImg.setTag(instId);
            statImg.setOnClickListener(l);
        }
        return this;
    }

    @Override
    public void onStatusChange(String instId, int newVal) {
        setStatus(newVal);
    }

    public ImageView getStatusImage() {
        return statImg;
    }
}
