package com.axway.apigw.android.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by su on 11/3/2014.
 */
public class BasicViewHolder {

    private static final int DEF_TEXT1_ID = android.R.id.text1;
    private static final int DEF_TEXT2_ID = android.R.id.text2;
    private static final int DEF_IMAGE_ID = android.R.id.icon;

    private TextView txt01;
    private TextView txt02;
    private ImageView img01;
    private int viewType;
    private Object data;

    public BasicViewHolder(View rv) {
        this(rv, DEF_TEXT1_ID, DEF_TEXT2_ID, DEF_IMAGE_ID);
    }

    public BasicViewHolder(View rv, int txt1Id) {
        this(rv, txt1Id, DEF_TEXT2_ID, DEF_IMAGE_ID);
    }

    public BasicViewHolder(View rv, int txt1Id, int txt2Id) {
        this(rv, txt1Id, txt2Id, DEF_IMAGE_ID);
    }

    public BasicViewHolder(View rv, int txt1Id, int txt2Id, int imgId) {
        super();
        viewType = 0;
        txt01 = (TextView) rv.findViewById(txt1Id);
        txt02 = (TextView) rv.findViewById(txt2Id);
        img01 = (ImageView) rv.findViewById(imgId);
        data = null;
    }

    public int getViewType() {
        return viewType;
    }

    public BasicViewHolder setViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }

    public BasicViewHolder setText1(String txt) {
        if (txt01 != null)
            txt01.setText(txt);
        return this;
    }

    public BasicViewHolder setText2(String txt) {
        if (txt02 != null)
            txt02.setText(txt);
        return this;
    }

    public TextView getTextView1() {
        return txt01;
    }

    public TextView getTextView2() {
        return txt02;
    }

    public ImageView getImageView() {
        return img01;
    }

    public BasicViewHolder setText1Visibility(int v) {
        if (txt01 != null)
            txt01.setVisibility(v);
        return this;
    }

    public BasicViewHolder setText2Visibility(int v) {
        if (txt02 != null)
            txt02.setVisibility(v);
        return this;
    }

    public BasicViewHolder setImageVisibility(int v) {
        if (img01 != null)
            img01.setVisibility(v);
        return this;
    }

    public BasicViewHolder setImageDrawable(Drawable d) {
        if (img01 != null)
            img01.setImageDrawable(d);
        return this;
    }

    public BasicViewHolder setImageResource(int resId) {
        if(img01 != null)
            img01.setImageResource(resId);
        return this;
    }

    public BasicViewHolder setImageBitmap(Bitmap b) {
        if (img01 != null)
            img01.setImageBitmap(b);
        return this;
    }

    public Object getData() {
        return data;
    }

    public BasicViewHolder setData(Object data) {
        this.data = data;
        return this;
    }

    public BasicViewHolder setText1Tag(Object o) {
        if (txt01 != null)
            txt01.setTag(o);
        return this;
    }

    public BasicViewHolder setText2Tag(Object o) {
        if (txt02 != null)
            txt02.setTag(o);
        return this;
    }

    public BasicViewHolder setImageTag(Object o) {
        if (img01 != null)
            img01.setTag(o);
        return this;
    }

    public Object getText1Tag() {
        if (txt01 == null)
            return null;
        return txt01.getTag();
    }

    public Object getText2Tag() {
        if (txt02 == null)
            return null;
        return txt02.getTag();
    }

    public Object getImageTag() {
        if (img01 == null)
            return null;
        return img01.getTag();
    }
}
