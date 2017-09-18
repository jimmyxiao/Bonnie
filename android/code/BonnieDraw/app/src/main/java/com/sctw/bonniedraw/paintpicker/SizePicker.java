package com.sctw.bonniedraw.paintpicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sctw.bonniedraw.R;


public class SizePicker extends Dialog implements SizeSelectedListener {

    private OnSizeChangedListener mListener;
    private int mInitialSize;

    public SizePicker(Context context, OnSizeChangedListener listener, String key, int initialSize) {
        super(context);
        mListener = listener;
        mInitialSize = initialSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_size_picker);

        RecyclerView rv=(RecyclerView) findViewById(R.id.paint_size_recyclerview);
        LinearLayoutManager lm=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(lm);
        SizeAdapter adapter = new SizeAdapter(getContext().getResources().getIntArray(R.array.sizes), this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onSizeSelected(int size) {
        mListener.sizeChanged(size);
        dismiss();
    }
}
