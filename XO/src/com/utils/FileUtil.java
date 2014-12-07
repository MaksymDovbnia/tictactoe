package com.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import net.mtechlab.mfamily.MFamilyApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by deerhunter on 03.06.14.
 */
public class FileUtil {
    private static final String LOG_TAG = FileUtil.class.getSimpleName();

    private FileUtil() {
    }

    public static void writeToInternalStorageFile(String localFileName, byte[] packet, boolean append) {
        try {
            int mode = Context.MODE_PRIVATE;
            if (append)
                mode |= Context.MODE_APPEND;
            FileOutputStream file = MFamilyApplication.getInstance().openFileOutput(localFileName, mode);
            file.write(packet);
            file.flush();
            file.getFD().sync();
            file.close();
        } catch (Exception ex) {
            DebugLog.d(LOG_TAG, Log.getStackTraceString(ex));
        }
    }

    public static void writeToExternalStorageFile(String fileName, byte[] packet, boolean append) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard.getAbsolutePath(), fileName);
            FileOutputStream outStream = new FileOutputStream(file, append);
            outStream.write(packet);
            outStream.flush();
            outStream.close();
        } catch (Exception ex) {
            DebugLog.d(LOG_TAG, Log.getStackTraceString(ex));
        }
    }

    public static String getAbsoluteLocalPath(String fileName) {
        File localFile = new File(MFamilyApplication.getInstance().getFilesDir(), fileName);
        return localFile.getAbsolutePath();
    }

    public static boolean isFileExist(String fileName){
        File localFile = new File(MFamilyApplication.getInstance().getFilesDir(), fileName);
        return localFile.exists();
    }

}
