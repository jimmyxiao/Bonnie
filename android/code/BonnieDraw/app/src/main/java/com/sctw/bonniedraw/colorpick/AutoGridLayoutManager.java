package com.sctw.bonniedraw.colorpick;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


public class AutoGridLayoutManager extends GridLayoutManager {
    //預計做分頁用, 尚未執行
    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private int totalPages = 2;

    public AutoGridLayoutManager(Context context, AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public AutoGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AutoGridLayoutManager(Context context, int spanCount,
                                 int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /*
    @Override
    public void onMeasure(RecyclerView.Recycler recycler,
                          RecyclerView.State state, int widthSpec, int heightSpec) {

        int itemCount = getItemCount();
        if(itemCount<=0)
            return;
        if (measuredHeight <= 0 && state.getItemCount()>0) {

            View view = recycler.getViewForPosition(0);
            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                measuredWidth = View.MeasureSpec.getSize(widthSpec);
                measuredHeight = view.getMeasuredHeight() * getSpanCount();
            }
        }

        //measuredWidth = 200;
        //measuredHeight = 200;
        setMeasuredDimension(measuredWidth, measuredHeight);

    }
*/
/*
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (totalPages > 1) {
            return super.scrollHorizontallyBy(dx, recycler, state);
        } else {
            return 0;
        }
    }
*/
}