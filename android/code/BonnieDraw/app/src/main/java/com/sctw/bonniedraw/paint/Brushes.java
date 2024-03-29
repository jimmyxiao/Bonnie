package com.sctw.bonniedraw.paint;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import com.sctw.bonniedraw.R;


public class Brushes {
    private static int[] BRUSH_STYLES;
    private static Brush[] mBrushList;
    private static List<Brush> mPhotoBrushList;
    private static List<Brush> mStylishBrushList;

    static {
        mStylishBrushList = new ArrayList();
        mPhotoBrushList = new ArrayList();
        BRUSH_STYLES = new int[]{R.style.Brush_Bamboo1, R.style.Brush_Bamboo2, R.style.Brush_AirBrush,
                R.style.Brush_Pen, R.style.Brush_Calligraphy, R.style.Brush_HardPencil,
                R.style.Brush_SoftPencil, R.style.Brush_InkPen, R.style.Brush_BallpointPen,
                R.style.Brush_HardEraser, R.style.Brush_SoftEraser,
                R.style.Brush_AirBrush, R.style.Brush_Oil, R.style.Brush_FeltPen,
                R.style.Brush_WaterColor, R.style.Brush_OilPastel, R.style.Brush_Pastel,
                R.style.Brush_HardPastel, R.style.Brush_Creyon,
                R.style.Brush_Knife1, R.style.Brush_CombKnife, R.style.Brush_ColorlessOil,
                R.style.Brush_Finger, R.style.Brush_InkSpot,
                R.style.Brush_Flowers, R.style.Brush_Fish, R.style.Brush_Sponge};
    }

    public static List<Brush> get(Context context, int type) {
        if (mBrushList == null) {
            loadBrushList(context);
        }
        return type == 0 ? mStylishBrushList : mPhotoBrushList;
    }

    public static Brush[] get(Context context) {
        if (mBrushList == null) {
            loadBrushList(context);
        }
        return mBrushList;
    }

    public static Brush getNewOneBrush(int ipos) {
        if (mBrushList != null && mBrushList.length>0) {
            Brush brush = null;
            try {
                brush = (Brush) mBrushList[ipos].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return brush;
        }
        return null;
    }


    public static final void loadBrushList(Context context) {
        if (mBrushList == null) {
            long t1 = SystemClock.elapsedRealtime();
            mBrushList = parseStyleData(context, BRUSH_STYLES);
            long t2 = SystemClock.elapsedRealtime();
            Brush[] arr$ = mBrushList;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Brush brush = arr$[i$];
                if (brush.getBrushType() == 0) {
                    mStylishBrushList.add(brush);
                } else {
                    mPhotoBrushList.add(brush);
                }
                i$++;
            }
        }
    }

    public static Brush[] parseStyleData(Context context, int[] styleArray) {
        Brush[] brushes = new Brush[styleArray.length];
        int i = 0;
        while (i < brushes.length) {
            Brush brush = new Brush(i);
            TypedArray a = context.obtainStyledAttributes(styleArray[i], R.styleable.Brush);
            brush.loadFromTypedArray(a);
            a.recycle();
            brushes[i] = brush;
            i++;
        }
        return brushes;
    }


}