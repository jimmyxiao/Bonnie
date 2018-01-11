package com.sctw.bonniedraw.utility;

/**
 * Created by jimmyxiao on 2018/1/11.
 */

import java.io.File;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
public class SingleMediaScanner implements MediaScannerConnectionClient {
    private MediaScannerConnection mMs;
    private File mFile;
    public SingleMediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }
    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }
    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }
}