package com.sctw.bonniedraw.utility;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.sctw.bonniedraw.paint.Brushes;

/**
 * Created by Fatorin on 2017/10/23.
 */

public class InitApp extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Brushes.loadBrushList(getApplicationContext());
    }
}
