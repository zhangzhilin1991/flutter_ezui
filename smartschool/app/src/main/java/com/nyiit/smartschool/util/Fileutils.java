package com.nyiit.smartschool.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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

    public static List<File> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");return null;}
        List<File> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i]);
        }
        return s;
    }

    private void openAssignFolder(Context context, String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            context.startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
