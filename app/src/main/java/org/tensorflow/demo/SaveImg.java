package org.tensorflow.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;

public class SaveImg {
    private static SaveImg instance;

    public static SaveImg getInstance() {
        if (instance == null) {
            instance = new SaveImg();
        }
        return instance;
    }

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator;

    public static boolean saveImg(Bitmap bitmap, String fileName) {
        Log.i(TAG, "saveImg: "+fileName);
        try {
            // 创建文件流，指向该路径，文件名叫做fileName
            File file = new File(FILE_PATH, fileName+".jpg");
            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }
            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
