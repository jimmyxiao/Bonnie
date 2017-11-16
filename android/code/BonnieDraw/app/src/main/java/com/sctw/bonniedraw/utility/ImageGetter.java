package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by Fatorin on 2017/11/16.
 */

public class ImageGetter implements Html.ImageGetter {
    private URLDrawable urlDrawable = null;
    private TextView textView;
    private Context context;

    public ImageGetter(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        urlDrawable = new URLDrawable();

        GlideApp.with(context).asBitmap().load(source).fitCenter().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                urlDrawable.bitmap = resource;
                Log.d("Loading", "圖片，Width：" + resource.getWidth() + "，Height：" + resource.getHeight());
                urlDrawable.setBounds(0, 0, 48, 48);
                textView.invalidate();
                textView.setText(textView.getText());
            }
        });
        return urlDrawable;
    }

    public class URLDrawable extends BitmapDrawable {
        public Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }
}
