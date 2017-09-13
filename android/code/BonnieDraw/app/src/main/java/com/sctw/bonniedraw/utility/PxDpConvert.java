package com.sctw.bonniedraw.utility;

import android.content.Context;

/**
 * Created by Fatorin on 2017/9/12.
 */

public class PxDpConvert {

    private static final int GlobalPx = 65536;

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float formatToDisplay(float dot, int displayWidth) {
        return dot * displayWidth / GlobalPx;
    }

    public static int displayToFormat(float dot, int displayWidth) {
        return (int) dot * GlobalPx / displayWidth;
    }
}
