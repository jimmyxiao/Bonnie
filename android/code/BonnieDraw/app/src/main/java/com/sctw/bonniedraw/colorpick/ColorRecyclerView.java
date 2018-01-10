package com.sctw.bonniedraw.colorpick;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by jimmyxiao on 2018/1/9.
 */

public class ColorRecyclerView extends RecyclerView {

    private Context mContext = null;

    private ColorTicketAdapter mColorTicketAdapter = null;

    private int shortestDistance; // 滑動距離限制
    private float slideDistance = 0; // 滑動距離
    private float scrollX = 0;

    private int spanRow = 2; //排數
    private int spanColumn = 2; //每排的數量
    private int totalPage = 0; //總頁數
    private int currentPage = 1; //當前頁號

    private int pageMargin = 0;

    //private PageIndicatorView mIndicatorView = null; //指示器


    /*  暫不處理
     * 0: 停止滾動且手指移開; 1: 開始滾動; 2: 手指做了拋的動作（手指離開屏幕前，用力滑了一下）
     */
    private int scrollState = 0; // 滾動狀態

    private AutoGridLayoutManager mAutoGridLayoutManager;

    public ColorRecyclerView(Context context) {
        this(context, null);
    }

    public ColorRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defaultInit(context);
    }


    private void defaultInit(Context context) {
        this.mContext = context;
        this.mAutoGridLayoutManager = new AutoGridLayoutManager(
                mContext, spanRow, AutoGridLayoutManager.HORIZONTAL, false);
        setLayoutManager(mAutoGridLayoutManager);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    //設定 spanColumn , spanRow 及 mAutoGridLayoutManager
    public void setPageSize(int spanRow, int spanColumn) {
        this.spanRow = spanRow <= 0 ? this.spanRow : spanRow;
        this.spanColumn = spanColumn <= 0 ? this.spanColumn : spanColumn;

        this.mAutoGridLayoutManager = new AutoGridLayoutManager(
                mContext, this.spanRow, AutoGridLayoutManager.HORIZONTAL, false);
        setLayoutManager(mAutoGridLayoutManager);
    }


    public void setPageMargin(int pageMargin) {
        this.pageMargin = pageMargin;
    }

    /*
    public void setIndicator(PageIndicatorView indicatorView) {
        this.mIndicatorView = indicatorView;
    }
    */

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        shortestDistance = getMeasuredWidth() / 2;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        this.mColorTicketAdapter = (ColorTicketAdapter) adapter;
        update();
    }

    private void update() {

        int temp = ((int) Math.ceil(mColorTicketAdapter.getColorsSize() / (double) (spanRow * spanColumn)));
        if (temp != totalPage) {
            // 檢量是不是最後一頁,如果不是才可以滾動
            if (temp < totalPage && currentPage == totalPage) {
                currentPage = temp;
                smoothScrollBy(-getWidth(), 0);
            }
        //    mIndicatorView.setSelectedPage(currentPage - 1);
            totalPage = temp;
        }

        //頁數顯示可以在這裡處理

        mAutoGridLayoutManager.setTotalPages(temp);
    }

    //分頁,處理滑動多少滑到下一頁

/*
    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case 2:
                scrollState = 2;
                break;
            case 1:
                scrollState = 1;
                break;
            case 0:
                if (slideDistance == 0) {
                    break;
                }
                scrollState = 0;
                if (slideDistance < 0) { // 上頁
                    currentPage = (int) Math.ceil(scrollX / getWidth());
                    if (currentPage * getWidth() - scrollX < shortestDistance) {
                        currentPage += 1;
                    }
                } else { // 下頁
                    currentPage = (int) Math.ceil(scrollX / getWidth()) + 1;
                    if (currentPage <= totalPage) {
                        if (scrollX - (currentPage - 2) * getWidth() < shortestDistance) {
                            // 如果這一頁滑出距離不足，則定位到前一頁
                            currentPage -= 1;
                        }
                    } else {
                      //  currentPage = totalPage;
                    }
                }
                // 執行自動滾動
                smoothScrollBy((int) ((currentPage - 1) * getWidth() - scrollX), 0);
                // 修改指示器選中項
              //  mIndicatorView.setSelectedPage(currentPage - 1);
                slideDistance = 0;
                break;
        }
        super.onScrollStateChanged(state);
    }
    */

    @Override
    public void onScrolled(int dx, int dy) {
        scrollX += dx;
        if (scrollState == 1) {
            slideDistance += dx;
        }

        super.onScrolled(dx, dy);
    }

}
