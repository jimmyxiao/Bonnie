package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Fatorin on 2017/11/6.
 */

public class BasePopup extends BasePopupWindow implements View.OnClickListener {
    private TextView mTextView;
    private Button mButton;
    private OnBasePopupClick listener;

    public BasePopup(Context context,OnBasePopupClick listener) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mTextView = (TextView) findViewById(R.id.textView_popup_base);
        this.mButton=(Button) findViewById(R.id.btn_popup_base);
        this.listener=listener;
        mButton.setOnClickListener(this);
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
        return createPopupById(R.layout.popup_base);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.coordinatorLayout_work);
    }

    public void setText(String str) {
        mTextView.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_popup_base:
                listener.onBasePopupClick();
                break;
        }
    }

    public interface OnBasePopupClick{
        void onBasePopupClick();
    }
}
