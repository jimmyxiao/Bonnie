package com.sctw.bonniedraw.paintpicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sctw.bonniedraw.R;


public class ColorPicker extends Dialog implements ColorsSelectedListener {

    private OnColorChangedListener mListener;
    private int mInitialColor;

    public ColorPicker(Context context, OnColorChangedListener listener, String key, int initialColor) {
        super(context);
        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.paint_color_picker);

        RecyclerView recyclerViewColors = (RecyclerView) findViewById(R.id.paint_color_recyclerview);
        LinearLayoutManager lm=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewColors.setLayoutManager(lm);
        ColorsAdapter adapter = new ColorsAdapter(getContext().getResources().getIntArray(R.array.colors), this);
        recyclerViewColors.setAdapter(adapter);
    }

    @Override
    public void onColorSelected(int color) {
        mListener.colorChanged(color);
        dismiss();
    }
}
