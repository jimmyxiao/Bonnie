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
    private static String mKey;

    public ColorPicker(Context context, OnColorChangedListener listener, String key, int initialColor) {
        super(context);
        mKey = key;
        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_picker);

        RecyclerView recyclerViewColors = (RecyclerView) findViewById(R.id.recyclerViewColors);
        LinearLayoutManager lm=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewColors.setLayoutManager(lm);
        ColorsAdapter adapter = new ColorsAdapter(getContext().getResources().getIntArray(R.array.colors), this);
        recyclerViewColors.setAdapter(adapter);

        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(String key, int color) {
                mListener.colorChanged(key, color);
                dismiss();
            }
        };

    }

    @Override
    public void onOvalColorSelected(int color) {
        mListener.colorChanged(mKey, color);
        dismiss();
    }
}
