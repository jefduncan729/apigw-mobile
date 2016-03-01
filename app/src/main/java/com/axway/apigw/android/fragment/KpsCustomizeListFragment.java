package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.axway.apigw.android.R;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.model.DisplayPrefs;
import com.axway.apigw.android.model.KpsStore;
import com.axway.apigw.android.model.KpsType;
import com.axway.apigw.android.view.BasicViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by su on 2/19/2016.
 */
public class KpsCustomizeListFragment extends EditFrag<DisplayPrefs> implements AdapterView.OnItemSelectedListener {
    private static final String TAG = KpsCustomizeListFragment.class.getSimpleName();
    public static final int[] SPINNER_IDS = {R.id.spinner01, R.id.spinner02, R.id.spinner03};  //, R.id.spinner04, R.id.spinner05, R.id.spinner06, R.id.spinner07, R.id.spinner08, R.id.spinner09};

    @Bind(R.id.container01) ViewGroup parentView;

    private KpsStore kpsStore;
    private KpsType kpsType;
    private DisplayPrefs layout;
    private List<String> fldNames;
    private boolean viewFilled;

    private SpinnerAdapter fldNameAdapter;
    private View[] rows;
    private TextView[] labels;
    private Spinner[][] spinners;

    private int[] selPos;

    private Map<String, View> views;

    public static KpsCustomizeListFragment newInstance(KpsStore store, KpsType type, DisplayPrefs layout) {
        KpsCustomizeListFragment rv = new KpsCustomizeListFragment();
        rv.kpsStore = store;
        rv.kpsType = type;
        rv.layout = layout;
        return rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.scrollable, null);
        ButterKnife.bind(this, rv);
        viewFilled = false;
        buildView(inflater);

        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populate(view);
        setDirty(false);
        viewFilled = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void buildView(LayoutInflater inflater) {
        viewFilled = false;
        rows = new View[DisplayPrefs.MAX_ROWS];
        labels = new TextView[DisplayPrefs.MAX_ROWS];
        selPos = new int[DisplayPrefs.MAX_ROWS * SPINNER_IDS.length];
        for (int i = 0; i < DisplayPrefs.MAX_ROWS; i++)
            rows[i] = inflater.inflate(R.layout.spinner_row, null);
        spinners = new Spinner[DisplayPrefs.MAX_ROWS][SPINNER_IDS.length];
        views = new HashMap<>();
        for (int i = 0; i < DisplayPrefs.MAX_ROWS; i++) {
            View row = rows[i];
            row.setTag(i);
            labels[i] = (TextView)row.findViewById(android.R.id.text1);
            if (labels[i] != null) {
                labels[i].setText(String.format("Row %d", i+1));
            }
            for (int j = 0; j < SPINNER_IDS.length; j++) {
                selPos[(i*DisplayPrefs.MAX_ROWS)+j] = -1;
                spinners[i][j] = (Spinner) row.findViewById(SPINNER_IDS[j]);
                spinners[i][j].setTag((i*DisplayPrefs.MAX_ROWS)+j);
                spinners[i][j].setVisibility((j==0 ? View.VISIBLE : View.GONE));
            }
            row.setVisibility(View.VISIBLE);
            addView("row" + Integer.toString(i), row);
        }
        View samp = inflater.inflate(R.layout.kps_cust_sample, null);
        samp.setTag(new BasicViewHolder(samp));
        addView("sample", samp);

    }

    protected void addView(String nm, View v) {
        if (!views.containsKey(nm)) {
            views.put(nm, v);
            parentView.addView(v);
        }
    }

    protected Map<String, View> getViews() {
        return views;
    }

    protected View getView(String nm) {
        if (views == null || TextUtils.isEmpty(nm))
            return null;
        return views.get(nm);
    }

    @Override
    public void populate(View view) {
        if (spinners != null) {
            for (int i = 0; i < DisplayPrefs.MAX_ROWS; i++) {
                for (int j = 0; j < SPINNER_IDS.length; j++) {
                    spinners[i][j].setAdapter(getFieldNameAdapter());
                    spinners[i][j].setOnItemSelectedListener(this);
                    if (layout != null && !TextUtils.isEmpty(layout.getCell(i,j))) {
                        int p = getPosition(layout.getCell(i,j));
                        if (p != -1) {
                            selPos[(i*DisplayPrefs.MAX_ROWS)+j] = p;
                            spinners[i][j].setSelection(p);
                            spinners[i][j].setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public void collect(DisplayPrefs item, Bundle extras) {
        for (int r = 0; r < DisplayPrefs.MAX_ROWS; r++) {
            for (int c = 0; c < DisplayPrefs.MAX_COLS; c++) {
                String s = getSelectedField(r, c);
                item.setCell(r, c, TextUtils.isEmpty(s) ? "" : s);
            }
        }
        Log.d(TAG, String.format("collected: %s", item));
    }

    private int getPosition(String fname) {
        if (fldNames == null)
            return -1;
        return fldNames.indexOf(fname);
    }

    private SpinnerAdapter getFieldNameAdapter() {
        if (fldNameAdapter == null) {
            if (fldNames == null) {
                fldNames = new ArrayList<>();
                Set<String> keys = kpsType.getProperties().keySet();
                fldNames.addAll(keys);
                fldNames.add(0, DisplayPrefs.NOT_USED);
            }
            fldNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, fldNames);
        }
        return fldNameAdapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!viewFilled)
            return;
        if (adapterView instanceof Spinner) {
            setDirty(true);
            Spinner s = (Spinner)adapterView;
            int ndx = -1;
            if (s.getTag() != null && (s.getTag() instanceof Integer))
                ndx = (Integer)s.getTag();
            if (ndx < 0 || ndx >= (DisplayPrefs.MAX_ROWS * 3))
                return;
            selPos[ndx] = i;
            int r = (ndx < 3 ? 0: (ndx < 5 ? 1 : 2));
            int c = (ndx < 3 ? ndx: (ndx % DisplayPrefs.MAX_ROWS));
            Log.d(TAG, "r, c of selected item: " + Integer.toString(r) + ", " + Integer.toString(c));
            String fld = fldNames.get(i);
            if (fld == null || DisplayPrefs.NOT_USED.equals(fld))
                return;
            if (c < 2) {
                spinners[r][c+1].setVisibility(View.VISIBLE);
            }
            buildSampleView();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String[][] getDisplayFields() {
        String[][] rv = new String[DisplayPrefs.MAX_ROWS][SPINNER_IDS.length];
        for (int r = 0; r < DisplayPrefs.MAX_ROWS; r++) {
            for (int c = 0; c < 3; c++) {
                String fldName = getSelectedField(r, c);
                if (TextUtils.isEmpty(fldName) || DisplayPrefs.NOT_USED.equals(fldName)) {
                    rv[r][c] = null;
                }
                else {
                    rv[r][c] = fldName;
                }
            }
        }
        return rv;
    }

    private void buildSampleView() {
        View v = getView("sample");
        if (v == null)
            return;
        BasicViewHolder vh = (BasicViewHolder)v.getTag();
//        TextView t1 = (TextView)v.findViewById(android.R.id.text1);
//        TextView t2 = (TextView)v.findViewById(android.R.id.text2);
//        if (t1 == null || t2 == null)
//            return;
        StringBuilder[] rows = new StringBuilder[DisplayPrefs.MAX_ROWS];
        for (int i = 0; i < DisplayPrefs.MAX_ROWS; i++)
            rows[i] = new StringBuilder();

        String[][] flds = getDisplayFields();
        for (int r = 0; r < DisplayPrefs.MAX_ROWS; r++) {
            StringBuilder sb = rows[r];
            for (int c = 0; c < 3; c++) {
                String fldName = flds[r][c];
                if (!TextUtils.isEmpty(fldName)) {
                    if (sb.length() > 0)
                        sb.append(" ");
                    sb.append(fldName);
                }
            }
        }
        vh.setText1(rows[0].toString());
        vh.setText2(new StringBuilder(rows[1].toString()).append("\n").append(rows[2].toString()).toString());
    }

    private String getSelectedField(int r, int c) {
        if (spinners == null || (r < 0 || r >= DisplayPrefs.MAX_ROWS) || (c < 0 || c >= 3))
            return null;
        Spinner s = spinners[r][c];
        SpinnerAdapter a = s.getAdapter();
        int p = selPos[(r*DisplayPrefs.MAX_ROWS) + c];
        if (p >= 0 && p < a.getCount())
            return (String)a.getItem(p);
        return null;
    }
}
