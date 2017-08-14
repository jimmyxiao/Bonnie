package com.sctw.bonniedraw;

import android.app.Application;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.sctw.bonniedraw.constant.Common;

import java.util.List;

public class AppDelegate extends Application {
    private static Context context;

    public static SharedPreferences getPreference() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Resources getResource() {
        return context.getResources();
    }

    public static ContentResolver getContentProvider() {
        return context.getContentResolver();
    }

    public static void handleIntent(Context context, Intent intent, String appPackage) {
        PackageManager packageManager = context.getPackageManager();
        if (appPackage != null) {
            intent.setPackage(appPackage);
            if (packageManager.queryIntentActivities(intent, 0).size() > 0)
                context.startActivity(intent);
            else try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Common.MARKET_URI_SCHEME + appPackage)));
            } catch (ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Common.HTTP_URI_SCHEME + appPackage)));
            }
        } else if (packageManager.queryIntentActivities(intent, 0).size() > 0)
            context.startActivity(intent);
        else try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market:")));
            } catch (ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Common.PLAY_STORE_URL)));
            }
    }

    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) width / height;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - height) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - height);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }

    public static boolean hasCamera() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean downloadAppUpdate() {
        Uri uri = Uri.parse(Common.UPDATE_APP_URL);
        final DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING);
        Cursor cursor = downloadManager.query(query);
        while (cursor.moveToNext())
            if (String.valueOf(uri).equals(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))))
                return false;
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(uri.getLastPathSegment()).setDescription(uri.toString()).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
        final long downloadId = downloadManager.enqueue(request);
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                    if (cursor.moveToFirst()) {
                        Intent install = new Intent(Intent.ACTION_VIEW).
                                setDataAndType(Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))), cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))).
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(install);
                    }
                    context.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return true;
    }

    public static Context getContext() {
        return context;
    }

    public static boolean hasNetworkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
