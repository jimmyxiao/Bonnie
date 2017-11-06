package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Fatorin on 2017/11/1.
 */

public class OpacityPopup extends BasePopupWindow {
    private TextView mTextView;

    public OpacityPopup(Context context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mTextView = (TextView) findViewById(R.id.textView_opacity);
        setPopupGravity(Gravity.CENTER);
    }

    @Override
    protected Animation initShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.popup_opacity);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.main_paint_layout);
    }

    public void setText(int i){
        mTextView.setText(i+"%");
    }
}
