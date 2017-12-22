package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Fatorin on 2017/12/20.
 */

public class LoadMoreFooter implements View.OnClickListener {
    public static final int STATE_DISABLED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_ENDLESS = 3;
    public static final int STATE_FAILED = 4;
    private boolean mbIsScrolling = false;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textView_footer_refresh) {
            checkLoadMore();
        }
    }

    @IntDef({STATE_DISABLED, STATE_LOADING, STATE_FINISHED, STATE_ENDLESS, STATE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private ProgressBar mProgressBar;
    private TextView mTvHint;

    @State
    private int state = STATE_DISABLED;
    private final OnLoadMoreListener loadMoreListener;

    public LoadMoreFooter(@NonNull final Context context, @NonNull HeaderAndFooterRecyclerView recyclerView, @NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        View footerView = LayoutInflater.from(context).inflate(R.layout.item_footer_refresh, recyclerView.getFooterContainer(), false);
        recyclerView.addFooterView(footerView);
        mProgressBar = footerView.findViewById(R.id.progressBar_footer_refresh);
        mTvHint = footerView.findViewById(R.id.textView_footer_refresh);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mbIsScrolling = true;
                    GlideApp.with(context).resumeRequests();
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (mbIsScrolling == true) {
                        GlideApp.with(context).pauseRequests();
                    }
                    mbIsScrolling = false;
                }
                if (!recyclerView.canScrollVertically(1)) {
                    checkLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case STATE_DISABLED:
                    mProgressBar.setVisibility(View.GONE);
                    mTvHint.setVisibility(View.GONE);
                    mTvHint.setText(null);
                    mTvHint.setClickable(false);
                    break;
                case STATE_LOADING:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText("載入中");
                    mTvHint.setClickable(false);
                    break;
                case STATE_FINISHED:
                    mProgressBar.setVisibility(View.GONE);
                    mTvHint.setVisibility(View.GONE);
                    mTvHint.setText(null);
                    mTvHint.setClickable(false);
                    break;
                case STATE_ENDLESS:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTvHint.setVisibility(View.INVISIBLE);
                    mTvHint.setText(null);
                    mTvHint.setClickable(true);
                    break;
                case STATE_FAILED:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText("載入失敗");
                    mTvHint.setClickable(true);
                    break;
                default:
                    throw new AssertionError("Unknow load more state.");
            }
        }
    }

    private void checkLoadMore() {
        if (getState() == STATE_ENDLESS || getState() == STATE_FAILED) {
            setState(STATE_LOADING);
            loadMoreListener.onLoadMore();
        }
    }
}