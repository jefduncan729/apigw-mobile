package com.axway.apigw.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.util.IntegerPreference;

/**
 * Created by su on 2/17/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private CheckBoxPreference chkInternalKps;
    private CheckBoxPreference chkWifiOnly;
    private CheckBoxPreference chkShowAdvisory;
    //    private EditTextPreference txtGoogleAcct;
    private IntegerPreference intBatchSize;

    private Callback callback;

    public interface Callback {
        public void onSettingChanged(String key);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        initUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            callback = (Callback)context;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (callback != null)
            callback.onSettingChanged(key);
        if (Constants.KEY_SHOW_INTERNAL_KPS.equals(key)) {
            setInternalKpsSummary(prefs);
        }
        else if (Constants.KEY_BATCH_SIZE.equals(key)) {
            setBatchSize(prefs);
        }
        else if (Constants.KEY_SHOW_ADVISORIES.equals(key)) {
            setShowAdvisory(prefs);
        }
        else if (Constants.KEY_WIFI_ONLY.equals(key)) {
            setWifiOnly(prefs);
        }
    }

    private void initUi() {
        PreferenceScreen scr = getPreferenceScreen();
        SharedPreferences prefs = scr.getSharedPreferences();
        chkInternalKps = (CheckBoxPreference)scr.findPreference(Constants.KEY_SHOW_INTERNAL_KPS);
        setInternalKpsSummary(prefs);
        chkShowAdvisory = (CheckBoxPreference)scr.findPreference(Constants.KEY_SHOW_ADVISORIES);
        setShowAdvisory(prefs);
        chkWifiOnly = (CheckBoxPreference)scr.findPreference(Constants.KEY_WIFI_ONLY);
        setWifiOnly(prefs);
        intBatchSize = (IntegerPreference)scr.findPreference(Constants.KEY_BATCH_SIZE);
        setBatchSize(prefs);
    }

    public static SettingsFragment newInstance() {
        SettingsFragment rv = new SettingsFragment();
        return rv;
    }

    private void setInternalKpsSummary(SharedPreferences prefs) {
        if (chkInternalKps == null)
            return;
        boolean b = prefs.getBoolean(Constants.KEY_SHOW_INTERNAL_KPS, Constants.DEF_SHOW_INTERNAL_KPS);
        StringBuilder sb = new StringBuilder("Internal KPS stores will");
        if (!b)
            sb.append(" not");
        sb.append(" be shown");
        chkInternalKps.setSummary(sb.toString());
    }

    private void setShowAdvisory(SharedPreferences prefs) {
        if (chkShowAdvisory == null)
            return;
        boolean b = prefs.getBoolean(Constants.KEY_SHOW_ADVISORIES, false);
        StringBuilder sb = new StringBuilder("Advisory Topics will");
        if (!b)
            sb.append(" not");
        sb.append(" be shown");
        chkShowAdvisory.setSummary(sb.toString());
    }

    private void setWifiOnly(SharedPreferences prefs) {
        if (chkWifiOnly == null)
            return;
        boolean b = prefs.getBoolean(Constants.KEY_WIFI_ONLY, true);
        StringBuilder sb = new StringBuilder("Connections will be made ");
        if (b)
            sb.append("only over Wifi network");
        else
            sb.append("on either Wifi or cellular network");
        chkWifiOnly.setSummary(sb.toString());
    }

    private void setBatchSize(SharedPreferences prefs) {
        if (intBatchSize == null)
            return;
        int i = prefs.getInt(Constants.KEY_BATCH_SIZE, Constants.DEF_BATCH_SIZE);
        StringBuilder sb = new StringBuilder("KPS Browsing will ");
        if (i <= 0)
            sb.append("not be batched");
        else
            sb.append("batched in pages of ").append(i).append(" item").append(i == 1 ? "" : "s").append(" at a time");
        intBatchSize.setSummary(sb.toString());
    }
}
