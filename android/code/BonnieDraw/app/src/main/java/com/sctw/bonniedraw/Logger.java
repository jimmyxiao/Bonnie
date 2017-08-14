package com.sctw.bonniedraw;

/**
 * Created by Professor on 4/6/2015.
 */

public class Logger {
    public static boolean DEBUG = true;
    public static String TAG = "com.sctw.bonniedraw";

    public static void v(String message) {
        if (DEBUG && message != null) android.util.Log.v(TAG, message);
    }

    public static void d(String message) {
        if (DEBUG && message != null) android.util.Log.d(TAG, message);
    }

    public static void i(String message) {
        if (DEBUG && message != null) android.util.Log.i(TAG, message);
    }

    public static void w(String message) {
        if (DEBUG && message != null) android.util.Log.w(TAG, message);
    }

    public static void e(String message) {
        if (DEBUG && message != null) android.util.Log.e(TAG, message);
    }

    public static void v(String tag, String message) {
        if (DEBUG && message != null) android.util.Log.v(tag, message);
    }

    public static void d(String tag, String message) {
        if (DEBUG && message != null) android.util.Log.d(tag, message);
    }

    public static void i(String tag, String message) {
        if (DEBUG && message != null) android.util.Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        if (DEBUG && message != null) android.util.Log.w(tag, message);
    }

    public static void e(String tag, String message) {
        if (DEBUG && message != null) android.util.Log.e(tag, message);
    }
}
