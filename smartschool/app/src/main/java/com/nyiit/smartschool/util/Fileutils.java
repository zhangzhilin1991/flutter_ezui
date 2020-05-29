package com.nyiit.smartschool.util;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.nyiit.smartschool.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_PICTURES;

public class Fileutils {
    //public static final String DEFAULT_FILE_PATH = Environment.getExternalFilesDir().getAbsolutePath();

    //public static final String DEFAULT_PICTURE_PATH = Environment.getExternalStorageDirectory().getPath() + "/com.nyiit.smartschool/picture";
    //public static final String DEFAULT_RECORD_PATH = Environment.getExternalFilesDir().getPath() + "/com.nyiit.smartschool/record";

    public static String getRecordFilePath(Context context, String fileName) {
        return getRecordPath(context) + File.separator + fileName;
    }

    public static String getPictureFilePath(Context context, String fileName) {
        return getPicturePath(context) + File.separator + fileName;
    }

    public static String getExternalFilesDir(Context context) {
        File file = context.getExternalFilesDir(DIRECTORY_PICTURES);
        return file.getAbsolutePath();
    }

    public static String getPicturePath(Context context) {
       File file =  context.getExternalFilesDir(DIRECTORY_PICTURES);
       if (!file.exists()) {
           file.mkdirs();
       }
        return file.getAbsolutePath();
    }

    public static String getRecordPath(Context context) {
        File file = context.getExternalFilesDir(DIRECTORY_MOVIES);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存图片
     * @param context
     * @param file
     */
    public static void saveImage(Context context, File file) {
        ContentResolver localContentResolver = context.getContentResolver();
        ContentValues localContentValues = getImageContentValues(context, file, System.currentTimeMillis());
        localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);

        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        final Uri localUri = Uri.fromFile(file);
        localIntent.setData(localUri);
        context.sendBroadcast(localIntent);
    }

    public static ContentValues getImageContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "image/jpeg");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("orientation", Integer.valueOf(0));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }


    /**
     * 保存视频
     * @param context
     * @param file
     */
    public static void saveVideo(Context context, File file) {
        //是否添加到相册
        ContentResolver localContentResolver = context.getContentResolver();
        ContentValues localContentValues = getVideoContentValues(context, file, System.currentTimeMillis());
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
    }

    public static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }
}
