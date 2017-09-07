package com.sctw.bonniedraw.paint;

import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

/**
 * Created by Fatorin on 2017/9/4.
 */

public class PathAndPaint implements Serializable {
    private Path mPath;
    private Paint mPaint;

    public PathAndPaint(Path mPath, Paint mPaint) {
        this.mPath = mPath;
        this.mPaint = mPaint;
    }

    public Path getmPath() {
        return mPath;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }
}
