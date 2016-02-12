package com.axway.apigw.android.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.axway.apigw.android.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ServerInfo {
	
	private static final String TAG = ServerInfo.class.getSimpleName();
	
	private long id;
	private int status;
	private String host;
	private int port;
	private boolean ssl;
    private boolean certTrusted;
	private String user;
	private String pwd;

	public ServerInfo() {
		super();
		id = 0;
		status = Constants.STATUS_ACTIVE;
		host = null;
		port = 8090;
		ssl = true;
        certTrusted = false;
		user = null;
		pwd = null;
	}
	
	public static ServerInfo from(Cursor c) {
		ServerInfo rv = null;
		if (c == null)
			return rv;
		rv = new ServerInfo();
//		rv.setStatus(c.getInt(DbHelper.ConnMgrColumns.IDX_STATUS));
//        rv.setCertTrusted(c.getInt(DbHelper.ConnMgrColumns.IDX_FLAG) == Constants.FLAG_CERT_TRUSTED);
//		rv.setId(c.getLong(DbHelper.ConnMgrColumns.IDX_ID));
//		rv.setHost(c.getString(DbHelper.ConnMgrColumns.IDX_HOST));
//		rv.setPort(c.getInt(DbHelper.ConnMgrColumns.IDX_PORT));
//		rv.setSsl(c.getInt(DbHelper.ConnMgrColumns.IDX_USE_SSL) == 1);
//		rv.setUser(c.getString(DbHelper.ConnMgrColumns.IDX_USER));
//		rv.setPasswd(c.getString(DbHelper.ConnMgrColumns.IDX_PASS));
		return rv;
	}
	
	public ServerInfo(String host) {
		this();
		this.host = host;
	}
	
	public ServerInfo(String host, int port) {
		this(host);
		this.port = port;
	}
	
	public ServerInfo(String host, int port, boolean ssl) {
		this(host);
		this.port = port;
		this.ssl = ssl;
	}
	
	public ServerInfo(String host, int port, boolean ssl, String user, String pwd) {
		this(host, port, ssl);
		this.user = user;
		this.pwd = pwd;
	}
	
	public ServerInfo(String host, int port, String user, String pwd) {
		this(host, port, true, user, pwd);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return pwd;
	}

	public void setPasswd(String pwd) {
		this.pwd = pwd;
	}
	
	public String displayString() {
		StringBuilder sb = new StringBuilder("http");
		if (ssl)
			sb.append("s");
		sb.append("://").append(host);
		if (port != 0)
			sb.append(":").append(port);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean rv = false;
		if (!(o instanceof ServerInfo))
			return false;
		ServerInfo si = (ServerInfo)o;
		rv = (status == si.getStatus());
		if (!rv)
			return rv;
		rv = (id == si.getId());
		if (!rv)
			return rv;
		rv = (port == si.getPort());
		if (!rv)
			return rv;
		rv = (ssl != si.isSsl());
		if (!rv)
			return rv;
        if (ssl) {
            rv = (certTrusted != si.isCertTrusted());
            if (!rv)
                return rv;
        }
		if (host == null)
			rv = !TextUtils.isEmpty(si.getHost());
		else
			rv = (host.equals(si.getHost()));
		if (!rv)
			return rv;
		if (user == null)
			rv = !TextUtils.isEmpty(si.getUser());
		else
			rv = (user.equals(si.getUser()));
		if (!rv)
			return rv;
		if (pwd == null)
			rv = !TextUtils.isEmpty(si.getPasswd());
		else
			rv = (pwd.equals(si.getPasswd()));
		if (!rv)
			return rv;
		return true;
	}

	@Override
	public int hashCode() {
		int rv = 0;
		if (host != null)
			rv += host.hashCode();
		if (user != null)
			rv += user.hashCode();
		if (pwd != null)
			rv += pwd.hashCode();
		rv += port;
		if (ssl)
			rv += 1;
		return rv;
	}
	
	public static ServerInfo fromBundle(Bundle args) {
        if (args == null)
            return null;
//		long id = args.getLong(DbHelper.ConnMgrColumns._ID, 0);
//        if (id == 0) {
//            return null;
//        }
		ServerInfo rv = new ServerInfo();
//        rv.setStatus(args.getInt(DbHelper.ConnMgrColumns.STATUS, Constants.STATUS_ACTIVE));
//        rv.setId(id);
//        rv.setHost(args.getString(DbHelper.ConnMgrColumns.HOST, null));
//        rv.setUser(args.getString(DbHelper.ConnMgrColumns.USER, null));
//        rv.setPasswd(args.getString(DbHelper.ConnMgrColumns.PASS, null));
//        rv.setPort(args.getInt(DbHelper.ConnMgrColumns.PORT, 0));
//        rv.setSsl(args.getBoolean(DbHelper.ConnMgrColumns.USE_SSL, true));
//        rv.setCertTrusted(args.getBoolean(DbHelper.ConnMgrColumns.FLAG, true));
		return rv;
	}
	
	public Bundle toBundle() {
		Bundle args = new Bundle();
//		args.putInt(DbHelper.ConnMgrColumns.STATUS, getStatus());
//		args.putLong(DbHelper.ConnMgrColumns._ID, getId());
//		args.putString(DbHelper.ConnMgrColumns.HOST, getHost());
//		args.putString(DbHelper.ConnMgrColumns.USER, getUser());
//		args.putString(DbHelper.ConnMgrColumns.PASS, getPasswd());
//		args.putInt(DbHelper.ConnMgrColumns.PORT, getPort());
//		args.putBoolean(DbHelper.ConnMgrColumns.USE_SSL, isSsl());
//        args.putBoolean(DbHelper.ConnMgrColumns.FLAG, isCertTrusted());
		return args;
	}

    public ContentValues toValues() {
        ContentValues args = new ContentValues();
//        args.put(DbHelper.ConnMgrColumns.STATUS, getStatus());
//        args.put(DbHelper.ConnMgrColumns._ID, getId());
//        args.put(DbHelper.ConnMgrColumns.HOST, getHost());
//        args.put(DbHelper.ConnMgrColumns.USER, getUser());
//        args.put(DbHelper.ConnMgrColumns.PASS, getPasswd());
//        args.put(DbHelper.ConnMgrColumns.PORT, getPort());
//        args.put(DbHelper.ConnMgrColumns.USE_SSL, isSsl());
//        args.put(DbHelper.ConnMgrColumns.FLAG, isCertTrusted());
        return args;
    }

	public String getBaseUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("http");
		if (ssl)
			sb.append("s");
		sb.append("://").append(host).append(":").append(port);	//.append("/api/");
		return sb.toString();
	}
	
	public String buildUrl(String endpoint, String... params) {
		String base = getBaseUrl();
		StringBuilder sb = new StringBuilder(base);
		if (endpoint.startsWith("/")) {
			if (base.endsWith("/"))
				sb.append(endpoint.substring(1));
			else
				sb.append(endpoint);
		}
		else
			sb.append(endpoint);
		if (params != null) {
			if (!endpoint.endsWith("/"))
				sb.append("/");
			String val = null;
			int i = 0;
			for (String k: params) {
				try {
					val = URLEncoder.encode(k, Constants.UTF8);
					if (i++ > 0)
						sb.append("/");
					sb.append(val);
				} 
				catch (UnsupportedEncodingException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}
		}
		return sb.toString();
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	public void copy(ServerInfo other) {
		if (other == null)
			return;
		setStatus(other.getStatus());
		setId(other.getId());
		setHost(other.getHost());
		setPort(other.getPort());
		setSsl(other.isSsl());
        setCertTrusted(other.isCertTrusted());
		setUser(other.getUser());
		setPasswd(other.getPasswd());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public boolean isCertTrusted() {
        return certTrusted;
    }

    public void setCertTrusted(boolean newVal) {
        if (isSsl()) {
            certTrusted = newVal;
        }
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerInfo [id=");
		builder.append(id);
		builder.append(", status=");
		builder.append(status);
		builder.append(", host=");
		builder.append(host);
		builder.append(", port=");
		builder.append(port);
		builder.append(", ssl=");
		builder.append(ssl);
        builder.append(", certTrusted=");
        builder.append(certTrusted);
		builder.append(", user=");
		builder.append(user);
		builder.append(", pwd=");
		builder.append(pwd);
		builder.append("]");
		return builder.toString();
	}
}
