package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/11/24.
 */

public class ExtraUtil {

    public static void Share(Context context, String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //當用戶選擇短信時使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定義選擇框的標題
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
        //系統默認標題
    }
}
