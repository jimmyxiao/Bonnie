package com.sctw.bonniedraw.utility;

import okhttp3.OkHttpClient;

/**
 * Created by Fatorin on 2017/11/6.
 */

public class OkHttpUtil {
    private static OkHttpClient singleton;

    private OkHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OkHttpUtil.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                }
            }
        }
        return singleton;
    }
}
