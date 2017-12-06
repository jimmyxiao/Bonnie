package com.sctw.bonniedraw.paint;

import android.graphics.Bitmap;

/**
 * Created by Fatorin on 2017/12/6.
 */

public class BackgroundImage {

    private static int tagCode = 0xA002;

    private int imgSize;
    private Bitmap bitmap;

    public static int getTagCode() {
        return tagCode;
    }

    public int getImgSize() {
        return imgSize;
    }

    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
