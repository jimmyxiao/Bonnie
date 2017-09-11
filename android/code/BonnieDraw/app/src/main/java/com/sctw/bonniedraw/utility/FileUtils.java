package com.sctw.bonniedraw.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Fatorin on 2017/9/11.
 */

public class FileUtils {
    /**
     * 由外部 Activity 回傳的資料取得檔案路徑
     *
     * @param context Activity
     * @param uri     Uri
     * @return 檔案路徑
     * @throws URISyntaxException
     */

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("CHECK", "Display Name: " + displayName);
                return displayName;
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    public static String typefaceChecker(String path) {
        if (path == null || path.isEmpty()) return "";

        File file = new File(path);

        String filename = file.getName();
        String ext = getFileExt(filename);

        if (!ext.equalsIgnoreCase(".bdw")) {
            return "";
        }

        return filename;
    }

    public static String getFileExt(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex == -1 ? "" : fileName.substring(dotIndex);
    }
}
