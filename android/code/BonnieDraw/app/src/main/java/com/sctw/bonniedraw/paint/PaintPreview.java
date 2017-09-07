package com.sctw.bonniedraw.paint;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Fatorin on 2017/9/6.
 */

public class PaintPreview extends View {

    private Paint mPaint=new Paint();

    public Paint get_mPaint() {
        return mPaint;
    }

    public void set_mPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public PaintPreview(Context context,Paint mPaint) {
        super(context);
        this.mPaint=mPaint;
    }

    public PaintPreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintPreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaintPreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }



}
