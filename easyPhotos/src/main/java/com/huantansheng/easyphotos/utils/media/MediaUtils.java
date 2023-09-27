package com.huantansheng.easyphotos.utils.media;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.uri.UriUtils;

import java.io.File;
import java.io.IOException;

public class MediaUtils {
    private static final String TAG = MediaUtils.class.getSimpleName();

    /**
     * 获取时长
     *
     * @param path path
     * @return duration
     */
    public static long getDuration(String path) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            return Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            Log.e("DurationUtils", e.toString());
        } finally {
            if (mmr != null) {
                try {
                    mmr.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 格式化时长（不足一秒则显示为一秒）
     *
     * @param duration duration
     * @return "MM:SS" or "H:MM:SS"
     */
    public static String format(long duration) {
        double seconds = duration / 1000.0;
        return DateUtils.formatElapsedTime((long) (seconds + 0.5));
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     */
    @Nullable
    public static Uri createImageUri(final Context context) {
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + time);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");
            return context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return context.getContentResolver()
                    .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
    }


    /**
     * 创建一条视频地址uri,用于保存录制的视频
     */
    @Nullable
    public static Uri createVideoUri(final Context context) {
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, "VID_" + time);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATE_TAKEN, time);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/Camera");
            return context.getContentResolver()
                    .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return context.getContentResolver()
                    .insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, values);
        }
    }

    @SuppressLint("Range")
    @Nullable
    public static Pair<String, Photo> getPhoto(File file) {
        Uri uri = UriUtils.getUriByPath(file.getPath());

        String name = file.getName();
        String path = file.getAbsolutePath();
        long size = file.getTotalSpace();
        int[] imgSize = ImageUtils.getSize(file);
        long duration = getDuration(file.getAbsolutePath());

        int width = imgSize[0];
        int height = imgSize[1];
        String type = ImageUtils.getImageType(file).getValue();
        long dateTime = FileUtils.getFileLastModified(file);

        try {
            ExifInterface exif = new ExifInterface(file);
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            if (!StringUtils.isTrimEmpty(date)) {
                dateTime = TimeUtils.date2Millis(TimeUtils.string2Date(date, "yyyy:MM:dd HH:mm:ss"));
            }
        } catch (Exception e) {
            Log.e(TAG, "getPhoto: ", e);
        }

        Photo photo = new Photo(name, path, uri, dateTime, width, height, size, duration, type);
        return new Pair<>("", photo);
    }
}
