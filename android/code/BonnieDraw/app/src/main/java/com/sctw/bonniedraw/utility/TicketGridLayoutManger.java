package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Fatorin on 2017/12/22.
 */

public class TicketGridLayoutManger extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public TicketGridLayoutManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TicketGridLayoutManger(Context context, int spanCount) {
        super(context, spanCount);
    }

    public TicketGridLayoutManger(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}