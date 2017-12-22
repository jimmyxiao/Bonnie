package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/11/15.
 */
@GlideModule
public final class GlideAppModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setMemoryCache(new LruResourceCache(10 * 1024 * 1024));
    }

    public static RequestOptions getWorkOptions() {
        return new RequestOptions()
                .skipMemoryCache(true)
                .fitCenter()
                .placeholder(new ColorDrawable(Color.WHITE))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    public static RequestOptions getUserOptions() {
        return new RequestOptions()
                .placeholder(R.drawable.photo_round)
                .error(R.drawable.photo_round)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
