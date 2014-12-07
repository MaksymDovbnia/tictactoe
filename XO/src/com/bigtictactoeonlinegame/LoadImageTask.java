package com.bigtictactoeonlinegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.bigtictactoeonlinegame.activity.R;
import com.utils.FileUtil;
import com.utils.ImageUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Maksym on 07.12.2014.
 */
public class LoadImageTask extends AsyncTask<String, byte[], Bitmap> {

    private ImageView imageView;
    private static final String IMAGE_NAME = "user_image.png";
    private int imageSize = 90;

    public LoadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0];
//        if (FileUtil.isFileExist(IMAGE_NAME)) {
//            return ImageUtils.loadBitmapFromLocalStorage(IMAGE_NAME, imageSize, imageSize);
//        }
        XOSharedPreferenceHelper.getInstance().saveUserImageName(urlString);
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            if (url != null) {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
            return ImageUtils.decodeCircleBitmap(bmp, 0, imageSize, R.color.white);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            if (!FileUtil.isFileExist(IMAGE_NAME)) {
                ImageUtils.saveImageToFile(bitmap, IMAGE_NAME);
            }
        }


    }
}
