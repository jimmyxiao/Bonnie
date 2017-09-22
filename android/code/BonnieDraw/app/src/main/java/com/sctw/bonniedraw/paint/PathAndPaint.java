package com.sctw.bonniedraw.paint;

import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

/**
 * Created by Fatorin on 2017/9/22.
 */

public class PathAndPaint implements Serializable {
    private Path mPath;
    private Paint mPaint;

    public PathAndPaint(Path mPath, Paint mPaint) {
        this.mPath = mPath;
        this.mPaint = mPaint;
    }

    public Path get_mPath() {
        return mPath;
    }

    public void set_mPath(Path mPath) {
        this.mPath = mPath;
    }

    public Paint get_mPaint() {
        return mPaint;
    }

    public void set_mPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

}
