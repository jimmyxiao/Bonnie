package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/11/9.
 */

public class ToastUtil {

    private static Toast mToast;

    public static void createToastWindow(Context context, String text) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_toast_opacity, null);
        TextView tv = (TextView) view.findViewById(R.id.textView_opacity);
        tv.setText(text);

        view.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
        if (mToast == null) {
            mToast = new Toast(context);
        }
        mToast.setView(view);
        mToast.setGravity(Gravity.CENTER, 0, 200);
        mToast.show();
    }

    public static void createToastPublish(Context context, String text, boolean isPublish) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_toast_base, null);
        ImageView iv=(ImageView) view.findViewById(R.id.imgView_toast_base);
        TextView tv = (TextView) view.findViewById(R.id.textView_toast_base);
        tv.setText(text);
        if(isPublish){
            iv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_check_circle_black_24dp));
        }else {
            iv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_cancel_black_24dp));
        }

        view.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
        if (mToast == null) {
            mToast = new Toast(context);
        }
        mToast.setView(view);
        mToast.setGravity(Gravity.CENTER, 0, 200);
        mToast.show();
    }

    public static void createToastWindowSize(Context context, float size) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_toast_size, null);
        TextView mTv = (TextView) view.findViewById(R.id.textView_brush_size);
        CircleView mCv = (CircleView) view.findViewById(R.id.view_circle_size);
        mTv.setText(String.format("%d", (int) size));
        mCv.setCircleRadius(size / 2.0f);

        view.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
        if (mToast == null) {
            mToast = new Toast(context);
        }
        mToast.setView(view);
        mToast.setGravity(Gravity.CENTER, 0, 200);
        mToast.show();
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

