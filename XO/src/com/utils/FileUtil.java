package com.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.bigtictactoeonlinegame.XOApplication;


import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by deerhunter on 03.06.14.
 */
public class FileUtil {
    private static final String LOG_TAG = FileUtil.class.getSimpleName();

    private FileUtil() {
    }


    public static String getAbsoluteLocalPath(String fileName) {
        File localFile = new File(XOApplication.getInstance().getFilesDir(), fileName);
        return localFile.getAbsolutePath();
    }

    public static boolean isFileExist(String fileName) {
        File localFile = new File(XOApplication.getInstance().getFilesDir(), fileName);
        return localFile.exists();
    }

}
