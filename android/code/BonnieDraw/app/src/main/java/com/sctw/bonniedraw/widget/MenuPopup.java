package com.sctw.bonniedraw.widget;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.sctw.bonniedraw.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Fatorin on 2017/11/1.
 */

public class MenuPopup extends BasePopupWindow implements View.OnClickListener {
    public static final int PAINT_SETTING_GRID = 1;
    public static final int PAINT_SETTING_BG_COLOR = 2;
    public static final int PAINT_SETTING_SAVE = 3;
    //public static final int PAINT_SETTING_EXTRA = 4;   //paint setting 先不處理
    private MenuPopupOnClick listener;
    private boolean isLand;

    public interface MenuPopupOnClick {
        void onPopupClick(int item);
    }

    public MenuPopup(Activity context, MenuPopupOnClick listener) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.imgBtn_paint_setting_grid).setOnClickListener(this);
        findViewById(R.id.imgBtn_paint_setting_bg_color).setOnClickListener(this);
        findViewById(R.id.imgBtn_paint_setting_save).setOnClickListener(this);
        //findViewById(R.id.imgBtn_paint_setting_extra).setOnClickListener(this);
        this.listener = listener;
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
    public Animator initShowAnimator() {
       /* AnimatorSet set=new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mAnimaView,"scaleX",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"scaleY",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"alpha",0.0f,1.0f).setDuration(300*3/2));*/
        return null;
    }

    @Override
    public void showPopupWindow(View v) {
        //setOffsetX(-(getWidth() - v.getWidth() / 2));
        //setOffsetY(-v.getHeight() / 2);
        setOffsetY(0);
        super.showPopupWindow(v);
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.popup_paint_setting);

    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.main_paint_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_paint_setting_grid:
                listener.onPopupClick(PAINT_SETTING_GRID);
                break;
            case R.id.imgBtn_paint_setting_bg_color:
                listener.onPopupClick(PAINT_SETTING_BG_COLOR);
                break;
            case R.id.imgBtn_paint_setting_save:
                listener.onPopupClick(PAINT_SETTING_SAVE);
                break;
        //    case R.id.imgBtn_paint_setting_extra:
        //        listener.onPopupClick(PAINT_SETTING_EXTRA);
       //         break;
            default:
                break;
        }
    }
}
