package com.axway.apigw.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.axway.apigw.android.model.ServerInfo;

import java.util.List;

public class ServerInfoListAdapter extends BaseListAdapter<ServerInfo> {
	
	public ServerInfoListAdapter(Context ctx, List<ServerInfo> list) {
		super(ctx, list);
	}

	@Override
	protected void populateView(ServerInfo item, View view) {
		if (item == null || view == null)
			return;
		TextView txt = (TextView)view.findViewById(android.R.id.text1);
//		StringBuilder sb = new StringBuilder(item.getHost());
//		sb.append(":").append(item.getPort());
		txt.setText(item.displayString());
	}
}
