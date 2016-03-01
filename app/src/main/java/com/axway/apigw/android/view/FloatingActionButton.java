package com.axway.apigw.android.view;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

/**
 * Created by su on 4/10/2015.
 */
public class FloatingActionButton extends FrameLayout implements View.OnClickListener { //implements Checkable {

//    public interface CheckedChangedListener {
//        public void onCheckedChanged(FloatingActionButton fab, boolean checked);
//    }
    public interface ClickedListener {
        public void onClicked(FloatingActionButton fab);
    }

//    private static final int[] CHECKED_STATE_SET = {
//            android.R.attr.state_checked
//    };
//
//    private boolean checked;
    private ClickedListener listener;

    public FloatingActionButton(Context context) {
        this(context, null, 0, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, getWidth(), getHeight());
            }
        });
        setClipToOutline(true);
        setOnClickListener(this);
    }
//
    public void setClickedListener(ClickedListener newVal) {
        listener = newVal;
    }
//
//    @Override
//    public void setChecked(boolean checked) {
//        if (this.checked == checked) {
//            return;
//        }
//        this.checked = checked;
//        refreshDrawableState();
//        if (ccListener != null) {
//            ccListener.onCheckedChanged(this, checked);
//        }
//    }
//
//    @Override
//    public boolean isChecked() {
//        return checked;
//    }
//
//    @Override
//    public void toggle() {
//        setChecked(!checked);
//    }
//
//    @Override
//    public boolean performClick() {
//        toggle();
//        return super.performClick();
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateOutline();
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClicked(this);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] rv = super.onCreateDrawableState(extraSpace+1);
//        if (isChecked()) {
//            mergeDrawableStates(rv, CHECKED_STATE_SET);
//        }
        return rv;
    }
}
