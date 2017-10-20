package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by Fatorin on 2017/10/20.
 */

public class Thumbnail {
    public static Bitmap getBitmap(Context context, Uri uri) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);

            // 第一次 decode,只取得圖片長寬,還未載入記憶體
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, opts);
            in.close();

            // 取得動態計算縮圖長寬的 SampleSize (2的平方最佳)
            int sampleSize = computeSampleSize(opts, -1, 512 * 512);

            // 第二次 decode,指定取樣數後,產生縮圖
            in = context.getContentResolver().openInputStream(uri);
            opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;

            Bitmap bmp = BitmapFactory.decodeStream(in, null, opts);
            in.close();

            return bmp;
        } catch (Exception err) {
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;

        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
