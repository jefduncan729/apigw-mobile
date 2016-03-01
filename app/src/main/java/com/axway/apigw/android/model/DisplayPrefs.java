package com.axway.apigw.android.model;

import android.text.TextUtils;

import java.util.Arrays;

/**
 * Created by su on 2/22/2016.
 */
public class DisplayPrefs {
    public static final String ROW_SEPARATOR = "\n";
    public static final String COL_SEPARATOR = "\t";

    public static final int MAX_ROWS = 3;
    public static final int MAX_COLS = 3;
    public static final String NOT_USED = "(not used)";

    private String[][] layout;

    public DisplayPrefs() {
        super();
        layout = new String[MAX_ROWS][MAX_COLS];
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLS; c++) {
                layout[r][c] = "";
            }
        }
    }

    public String getCell(int r, int c) {
        if (r < 0 || r >= MAX_ROWS || c < 0 || c >= MAX_COLS)
            return null;
        return layout[r][c];
    }

    public DisplayPrefs setCell(int r, int c, String newVal) {
        if (r < 0 || r >= MAX_ROWS || c < 0 || c >= MAX_COLS)
            return this;
        layout[r][c] = newVal;
        return this;
    }

    public static DisplayPrefs inflate(String input) {
        DisplayPrefs rv = new DisplayPrefs();
        if (TextUtils.isEmpty(input))
            return rv;
        String[] rows = input.split(ROW_SEPARATOR);
        if (rows.length == 0)
            return null;
        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            if (TextUtils.isEmpty(row))
                continue;
            String[] flds = row.split(COL_SEPARATOR);
            if (flds.length == 0)
                continue;
            for (int c = 0; c < flds.length; c++)
                rv.setCell(r, c, flds[c]);
        }
        return rv;
    }

    public String deflate() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < MAX_ROWS; r++) {
            if (r > 0)
                sb.append(ROW_SEPARATOR);
            String[] list = layout[r];
            if (list != null && list.length > 0) {
                for (int c = 0; c < list.length; c++) {
                    if (c > 0)
                        sb.append(COL_SEPARATOR);
                    sb.append(list[c]);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
//        final StringBuilder sb = new StringBuilder("DisplayPrefs{");
//        sb.append("layout=").append(Arrays.toString(layout));
//        sb.append('}');
        return deflate();
    }

    public boolean isEmpty() {
        if (layout == null)
            return true;
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLS; c++) {
                String s = getCell(r, c);
                if (!TextUtils.isEmpty(s) && !NOT_USED.equals(s))
                    return false;
            }
        }
        return true;
    }
}
