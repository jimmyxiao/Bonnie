package com.sctw.bonniedraw.utility;

import android.app.Application;

import com.sctw.bonniedraw.paint.Brushes;

/**
 * Created by Fatorin on 2017/10/23.
 */

public class LoadImageApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Brushes.loadBrushList(getApplicationContext());
    }
}
