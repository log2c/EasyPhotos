package com.huantansheng.easyphotos.utils.uri;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class UriUtils {
    private static final String TAG = "UriUtils";

    @SuppressLint("NewApi")
    public static String getPathByUri(Uri uri) {
        File file = com.blankj.utilcode.util.UriUtils.uri2File(uri);
        Log.i(TAG, "getPathByUri: " + file.toString());
        return file.getAbsolutePath();
    }

    public static Uri getUriByPath(String path) {
        Uri uri = com.blankj.utilcode.util.UriUtils.file2Uri(new File(path));
        Log.i(TAG, "getUriByPath: " + uri.toString());
        return uri;
    }
}