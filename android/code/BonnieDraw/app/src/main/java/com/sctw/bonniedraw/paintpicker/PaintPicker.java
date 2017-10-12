package com.sctw.bonniedraw.paintpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sctw.bonniedraw.R;


public class PaintPicker extends Dialog implements PaintSelectedListener {

    private PaintSelectedListener mListener;
    private int mInitialSize;

    public PaintPicker(Context context, PaintSelectedListener listener, int initialSize) {
        super(context);
        mListener = listener;
        mInitialSize = initialSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_paint_picker);

        RecyclerView rv=(RecyclerView) findViewById(R.id.paint_paint_recyclerview);
        LinearLayoutManager lm=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(lm);
        Drawable[] paints=new Drawable[5];
        paints[0]=getContext().getResources().getDrawable(R.drawable.draw_pen_on_1);
        paints[1]=getContext().getResources().getDrawable(R.drawable.draw_pen_on_2);
        paints[2]=getContext().getResources().getDrawable(R.drawable.draw_pen_on_3);
        paints[3]=getContext().getResources().getDrawable(R.drawable.draw_pen_on_4);
        paints[4]=getContext().getResources().getDrawable(R.drawable.draw_pen_on_5);
        PaintAdapter adapter = new PaintAdapter(paints, this);
        rv.setAdapter(adapter);
    }
    @Override
    public void onPaintSelect(int num) {
        mListener.onPaintSelect(num);
        dismiss();
    }
}
