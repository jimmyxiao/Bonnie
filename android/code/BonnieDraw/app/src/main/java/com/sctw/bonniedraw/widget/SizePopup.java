package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
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

public class SizePopup extends BasePopupWindow {
    private CircleView mCircleView;
    private TextView mTextView;

    public SizePopup(Context context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mTextView = (TextView) findViewById(R.id.sizePopupText);
        this.mCircleView = (CircleView) findViewById(R.id.sizePopupCircle);
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
        return createPopupById(R.layout.popup_size);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.main_paint_layout);
    }

    public void setConvertedValue(float value) {
        this.mTextView.setVisibility(View.VISIBLE);
        this.mTextView.setText(Integer.toString((int) value));
        this.mCircleView.setCircleRadius(value / 2.0f);
    }

    public static class CircleView extends View {
        private Paint mPaint;
        private float mRadius;

        public CircleView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mPaint = new Paint(1);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(3);
            this.mPaint.setColor(Color.WHITE);
        }

        private void setCircleColor(int color) {
            this.mPaint.setColor(color);
            invalidate();
        }

        private void setCircleRadius(float radius) {
            this.mRadius = radius;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle((float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2), this.mRadius, this.mPaint);
        }
    }
}
