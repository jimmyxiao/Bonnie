package com.sctw.bonniedraw.utility;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/11/15.
 */
@GlideModule
public final class GlideAppModule extends AppGlideModule {

    public static RequestOptions getWorkOptions() {
        return new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .dontAnimate();
    }

    public static RequestOptions getUserOptions() {
        return new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.photo_round)
                .error(R.drawable.photo_round);
    }


}
