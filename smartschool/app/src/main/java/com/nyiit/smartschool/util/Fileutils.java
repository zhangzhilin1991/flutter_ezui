package com.nyiit.smartschool.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

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
}
