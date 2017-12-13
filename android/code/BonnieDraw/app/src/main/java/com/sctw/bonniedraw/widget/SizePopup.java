package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.sctw.bonniedraw.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Fatorin on 2017/11/1.
 */

public class SizePopup extends BasePopupWindow implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private OnSeekChange listener;
    private SeekBar mSeekbar;
    private int miMaxSize;
    private int miMinSize;

    @Override
    public void onClick(View v) {
        int progress = mSeekbar.getProgress();
        switch (v.getId()) {
            case R.id.imgBtn_paint_base_add:
                if (progress < miMaxSize) progress++;
                listener.onSizeAdd(progress);
                mSeekbar.setProgress(progress);
                break;
            case R.id.imgBtn_paint_base_decrease:
                if (progress > miMinSize) progress--;
                listener.onSizeDecrease(progress);
                mSeekbar.setProgress(progress);
                break;
        }
    }

    public interface OnSeekChange {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onSizeAdd(int progress);

        void onSizeDecrease(int progress);

        void onSetSize(int progress);
    }

    public void changeProgress(int progress, int max, int min) {
        mSeekbar.setProgress(progress);
        mSeekbar.setMax(max);
        this.miMaxSize = max;
        this.miMinSize = min;
    }

    public SizePopup(Context context, OnSeekChange listener, int max, int min) {
        super(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.listener = listener;
        this.miMaxSize = max;
        this.miMinSize = min;
        mSeekbar = (SeekBar) findViewById(R.id.seekbar_paint_base);
        mSeekbar.setOnSeekBarChangeListener(this);
        ImageButton mBtnAdd = (ImageButton) findViewById(R.id.imgBtn_paint_base_add);
        ImageButton mBtnDecrease = (ImageButton) findViewById(R.id.imgBtn_paint_base_decrease);
        mBtnAdd.setOnClickListener(this);
        mBtnDecrease.setOnClickListener(this);
    }

    @Override
    protected Animation initShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
        //return null;
    }

    @Override
    public void showPopupWindow(View v) {
        //setOffsetX(-(getWidth() - v.getWidth() / 2));
        setOffsetY(-v.getHeight() * 2);
        super.showPopupWindow(v);
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.popup_paint_base);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.main_paint_layout);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress >= miMinSize) {
            seekBar.setProgress(progress);
            listener.onProgressChanged(seekBar, progress, fromUser);
        } else {
            seekBar.setProgress(miMinSize);
            listener.onSetSize(seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


}
