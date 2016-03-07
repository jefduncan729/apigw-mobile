package com.axway.apigw.android.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.db.DbHelper;
import com.axway.apigw.android.event.ActionEvent;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.view.BasicViewHolder;

public class ConnMgrListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener{
	private static final String TAG = ConnMgrListFragment.class.getSimpleName();

	private static final int[] DRAWABLE_IDS = { R.mipmap.non_ssl, R.mipmap.ssl_trusted, R.mipmap.ssl_not_trusted };
	private static final int IDX_NON_SSL = 0;
	private static final int IDX_TRUSTED = 1;
	private static final int IDX_NOT_TRUSTED = 2;

	private CursorAdapter adapter;
	private Uri ctxUri;
	private String ctxName;
	private Drawable[] drawables;

	public static ConnMgrListFragment newInstance() {
		ConnMgrListFragment rv = new ConnMgrListFragment();
		return rv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
        drawables = new Drawable[DRAWABLE_IDS.length];
        for (int i = 0; i < DRAWABLE_IDS.length; i++)
            drawables[i] = getActivity().getResources().getDrawable(DRAWABLE_IDS[i], null);
		adapter = new ConnMgrAdapter(getActivity(), null, 0);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnCreateContextMenuListener(this);
		setEmptyText("Add an Admin Node Manager connection");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.conn_mgr, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean rv = true;
		switch (item.getItemId()) {
			case R.id.action_add:
            case R.id.action_remove_trust:
                BaseApp.post(new ActionEvent(item.getItemId()));
			break;
			default:
				rv = super.onOptionsItemSelected(item); 
		}
		return rv;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int pos, long id) {
		Cursor c = (Cursor)listView.getItemAtPosition(pos);
		Uri uri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, c.getLong(DbHelper.ConnMgrColumns.IDX_ID));
        BaseApp.post(new ActionEvent(R.id.action_edit, new Intent(Intent.ACTION_EDIT, uri)));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (ctxUri == null)
			return false;
		boolean rv = true;
        Intent i = new Intent();
        i.setData(ctxUri);
		switch (item.getItemId()) {
			case R.id.action_delete:
                i.putExtra(Constants.EXTRA_ITEM_NAME, ctxName);
			break;
			case R.id.action_enable:
                i.putExtra(Constants.EXTRA_GATEWAY_STATUS, Constants.STATUS_ACTIVE);
			break;
			case R.id.action_disable:
                i.putExtra(Constants.EXTRA_GATEWAY_STATUS, Constants.STATUS_INACTIVE);
			break;
			case R.id.action_check_cert:
			break;
			case R.id.action_view_cert:
				break;
			default:
				rv = false;
		}
        if (!rv)
            return super.onContextItemSelected(item);
        BaseApp.post(new ActionEvent(item.getItemId(), i));
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ctxUri = null;
		ctxName = null;
		AdapterContextMenuInfo cmi = (AdapterContextMenuInfo)menuInfo;
		Cursor c = (Cursor)getListView().getItemAtPosition(cmi.position);
		if (c == null)
			return;
        ServerInfo si = ServerInfo.from(c);
		ctxUri = ContentUris.withAppendedId(DbHelper.ConnMgrColumns.CONTENT_URI, si.getId());    //c.getLong(ConnMgrColumns.IDX_ID));
		ctxName = si.getHost() + ":" + Integer.toString(si.getPort());    //c.getString(ConnMgrColumns.IDX_HOST) + ":" + Integer.toString(c.getInt(ConnMgrColumns.IDX_PORT));
		menu.setHeaderTitle(si.getHost());  //c.getString(ConnMgrColumns.IDX_HOST));
		menu.add(0, R.id.action_delete, 1, R.string.action_delete);
		if (si.getStatus() == Constants.STATUS_ACTIVE)
			menu.add(0, R.id.action_disable, 2, R.string.action_disable);
		else
			menu.add(0, R.id.action_enable, 3, R.string.action_enable);
		if (si.isSsl()) {
			if (si.isCertTrusted()) {
				menu.add(0, R.id.action_view_cert, 4, R.string.action_view_cert);
			}
			else {
				menu.add(0, R.id.action_check_cert, 4, R.string.action_check_cert);
			}
		}
	}

	public void refresh() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), DbHelper.ConnMgrColumns.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	
	private class ConnMgrAdapter extends CursorAdapter {

		public ConnMgrAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			BasicViewHolder holder = (BasicViewHolder)view.getTag();
			if (holder == null)
				return;
            ServerInfo si = ServerInfo.from(cursor);
            holder.setText1(si.getHost() + ":" + Integer.toString(si.getPort()));    //h + ":" + Integer.toString(p));
            int pf = holder.getTextView1().getPaintFlags();
            if (si.getStatus() == Constants.STATUS_ACTIVE)
                pf = pf & ~Paint.STRIKE_THRU_TEXT_FLAG;    //pf holder.getTextView1().setPaintFlags(holder.getTextView1().getPaintFlags()  & ~Paint.STRIKE_THRU_TEXT_FLAG);
            else
                pf = pf | Paint.STRIKE_THRU_TEXT_FLAG;  //holder.getTextView1().setPaintFlags(holder.getTextView1().getPaintFlags()  | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.getTextView1().setPaintFlags(pf);
		    holder.setText2(buildDetails(si));   //cursor));
			if (holder.getImageView() != null) {
				int id = -1;
				if (si.isSsl()) {   //cursor.getInt(ConnMgrColumns.IDX_USE_SSL) == 1) {
					if (si.isCertTrusted()) //cursor.getInt(ConnMgrColumns.IDX_FLAG) == Constants.FLAG_CERT_NOT_TRUSTED)
						id = IDX_TRUSTED;
					else
						id = IDX_NOT_TRUSTED;
				}
				else
					id = IDX_NON_SSL;
				holder.setImageDrawable(drawables[id]);
			}
		}

		private String buildDetails(ServerInfo si) {    //Cursor cursor) {
			StringBuilder sb = new StringBuilder();
			sb.append("SSL: ");
			if (si.isSsl()){    //cursor.getInt(ConnMgrColumns.IDX_USE_SSL) == 1) {
				sb.append("yes");
//				boolean trusted = (cursor.getInt(ConnMgrColumns.IDX_FLAG) == Constants.FLAG_CERT_TRUSTED);
				sb.append(" (").append(si.isCertTrusted() ? "" : "NOT ").append("trusted)");
			}
			else
				sb.append("no");
//			String usr = cursor.getString(ConnMgrColumns.IDX_USER);
			if (!TextUtils.isEmpty(si.getUser()))
				sb.append(", user: ").append(si.getUser());
			return sb.toString();
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View rv = getActivity().getLayoutInflater().inflate(R.layout.listitem_2, null);
			BasicViewHolder holder = new BasicViewHolder(rv);
			rv.setTag(holder);
			return rv;
		}
	}
}
