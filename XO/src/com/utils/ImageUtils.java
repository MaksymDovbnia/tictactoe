package com.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import net.mtechlab.mfamily.MFamilyApplication;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by deerhunter on 03.06.14.
 */
public class ImageUtils {
    public static final int USER_IMAGE_SIZE = 200;

    private static Map<BitmapParameters, SoftReference<Bitmap>> bitmapCache = new HashMap<>();
    private static Map<ResBitmapParameters, SoftReference<Bitmap>> resBitmapCache = new HashMap<>();
    private static Map<String, List<BitmapParameters>> filenameToParametersMap = new HashMap<>();

    private ImageUtils() {
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeRotatedSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight, int orientation) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        if (orientation == 90 || orientation == 270) {
            options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
        }else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filename, options);
        if (orientation > 0)
        {
            Matrix matrix = new Matrix();
            matrix.preRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] buffer,
                                                          int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
    }

    public static Bitmap decodeCircleBitmap(Bitmap bitmap, int border, int size, int borderColor) {
        Bitmap output = Bitmap.createBitmap(size,
                size, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        final Rect rect = new Rect(0, 0, size, size);
        final Rect rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final float roundPx = ((float) size - border) / 2;
        final float center = (float) size / 2;

        canvas.drawARGB(0, 0, 0, 0);

        paint.setAntiAlias(true);
        paint.setDither(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center, center, center, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rectSrc, rect, paint);

        if (border > 0) {
            paint.setXfermode(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(border);
            paint.setColor(borderColor);
            canvas.drawCircle(center, center, roundPx, paint);
        }

        return output;
    }

    public static Bitmap decodeRectBitmap(Bitmap bitmap, int border, int width, int height, int borderColor) {
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        final Rect bgrBmpRect = new Rect(0, 0, width, height);
        final Rect mainBmpRect = new Rect(border, border, width - border, height - border);
        final Rect rectSrc = calculateSrcRest(bitmap.getWidth(), bitmap.getHeight(), width - 2 * border, height - 2 * border);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setAntiAlias(true);
        paint.setDither(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(bgrBmpRect, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rectSrc, mainBmpRect, paint);

        if (border > 0){
            paint.setXfermode(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(border);
            paint.setColor(borderColor);
            canvas.drawRect(bgrBmpRect, paint);
        }

        return output;
    }

    private static Rect calculateSrcRest(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) dstWidth / (float) dstHeight;

        if (srcAspect > dstAspect) {
            final int srcRectWidth = (int) (srcHeight * dstAspect);
            final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
            return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
        } else {
            final int srcRectHeight = (int) (srcWidth / dstAspect);
            final int scrRectTop = (srcHeight - srcRectHeight) / 2;
            return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
        }
    }

    public static Bitmap decodeSampledBitmapFromDescriptor(
            FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    public static Bitmap loadBitmapFromLocalStorage(String fileName, int rectWidth, int rectHight) {
        byte[] data = loadImageFromFile(fileName);
        if (data != null) {
            Bitmap bitmap = decodeSampledBitmapFromByteArray(data, rectWidth, rectHight);
            return bitmap;
        }

        return null;
    }

    public static boolean renameImage(String oldName, String newName) {
        File oldFile = new File(FileUtil.getAbsoluteLocalPath(oldName));
        File newFile = new File(FileUtil.getAbsoluteLocalPath(newName));
        return oldFile.renameTo(newFile);
    }

    /**
     * Load image from url to byte array
     *
     * @param urlString - url of image
     * @return byte array of image
     */
    public static byte[] loadImageFromUrl(String urlString) {
        URL url;
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        boolean isDownloadSuccessfull = true;
        try {
            url = new URL(urlString);
            is = url.openConnection().getInputStream();
            bis = new BufferedInputStream(is);
            bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }

        } catch (Exception e) {
            isDownloadSuccessfull = false;
            e.printStackTrace();
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        if (!isDownloadSuccessfull || bos == null || bos.size() == 0) return null;

        return bos.toByteArray();
    }

    public static byte[] bitmapToByteArray(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(MFamilyApplication.getInstance().getResources(), resId);
        return bitmapToByteArray(bitmap);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;
        // Create the buffer with the correct size
        int iBytes = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(iBytes);

        bitmap.copyPixelsToBuffer(buffer);
        bitmap.recycle();
        return buffer.array();
    }

    /**
     * Saves file
     *
     * @param data     - byte array
     * @param fileName - name of file
     * @return success or not
     */
    public static String saveFile(byte[] data, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = MFamilyApplication.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data);
            fos.flush();
            fos.getFD().sync();
            return FileUtil.getAbsoluteLocalPath(fileName);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static boolean saveImageToFile(Bitmap bitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = MFamilyApplication.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
            fos.flush();
            fos.getFD().sync();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    /**
     * Convert base64 string to image and save to file
     *
     * @param base64Image - base64 string representation of image
     * @param fileName    - name of image file
     * @return success or not
     */
    public static boolean convertBase64StringToImageFile(String base64Image, String fileName) {
        byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);

        return (saveFile(imageData, fileName) != null);
    }


    /**
     * Load image from file
     *
     * @param fileName - name of image file
     * @return byte array of image file
     */
    public static byte[] loadImageFromFile(String fileName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(fileName);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        if (bos.size() == 0) return null;

        return bos.toByteArray();
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);
        return stream.toByteArray();
    }

    /**
     * Convert image saved in file to base64 string
     *
     * @param fileName - name of image file
     * @return base64 string or NULL (in case of error or empty file)
     */
    public static String convertImageToBase64(String fileName) {
        byte[] image = loadImageFromFile(fileName);
        if (image == null)
            return null;
        return Base64.encodeToString(image, Base64.DEFAULT);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight && width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = (int) ((float) height / (float) reqHeight);
            final int widthRatio = (int) ((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap getBitmap(BitmapParameters params) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> bitmapRef = bitmapCache.get(params);
        if (bitmapRef != null){
            bitmap = bitmapRef.get();
        }
        if (bitmap == null) {
            Bitmap tmp = null;
            int imageWidth = params.size.x;
            int imageHeight = params.size.y;
            boolean imageExists = ((params.path != null) && (new File(params.path).exists()));
            if (imageExists) {
                tmp = decodeRotatedSampledBitmapFromFile(params.path, imageWidth, imageHeight, params.orientation);
            } else {
                if (params.noImageRes == Integer.MIN_VALUE)
                    return null;
                SoftReference<Bitmap> resBitmapRef = resBitmapCache.get(new ResBitmapParameters(params));
                if (resBitmapRef != null)
                    tmp = resBitmapRef.get();
                if (tmp != null)
                    return tmp;
                tmp = decodeSampledBitmapFromResource(MFamilyApplication.getInstance().getResources(), params.noImageRes, imageWidth, imageHeight);
            }

            if (params.isGrayscaled)
                tmp = toGrayscale(tmp);

            int borderSize = params.borderSize;

            if (params.isImageRound) {
                int size = (imageWidth > imageHeight) ? imageHeight : imageWidth;
                bitmap = decodeCircleBitmap(tmp, borderSize, size, params.borderColor);
                tmp.recycle();
            } else {
                bitmap = decodeRectBitmap(tmp, borderSize, imageWidth, imageHeight, params.borderColor);
                tmp.recycle();
            }

            if (params.shouldDrawGlow)
                drawGlow(bitmap, borderSize, params.isImageRound);

            if (imageExists) {
                bitmapCache.put(params, new SoftReference<>(bitmap));
                List<BitmapParameters> bitmapParameters = filenameToParametersMap.get(params.path);
                if (bitmapParameters == null){
                    bitmapParameters = new ArrayList<>();
                    filenameToParametersMap.put(params.path, bitmapParameters);
                }
                bitmapParameters.add(params);
            } else {
                resBitmapCache.put(new ResBitmapParameters(params), new SoftReference<>(bitmap));
            }
        }
        return bitmap;
    }

    private static void drawGlow(Bitmap bitmap, int borderSize, boolean isImageRound) {
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(30, 255, 255, 255));
        RectF rect = new RectF(borderSize, borderSize, bitmap.getWidth() - borderSize, bitmap.getHeight() - borderSize);
        if (isImageRound) {
            path.moveTo(rect.centerX(), rect.centerY());
            path.addArc(rect, -135, 180);
            path.lineTo(rect.centerX(), rect.centerY());
        } else {
            path.moveTo(0, 0);
            path.lineTo(rect.right, rect.bottom);
            path.lineTo(rect.right, 0);
            path.lineTo(0, 0);
        }
        canvas.drawPath(path, paint);
    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static void deleteImage(String photoFileName) {
        File userImage = new File(photoFileName);
        userImage.delete();
        List<BitmapParameters> bitmapParametersList = filenameToParametersMap.get(photoFileName);
        if (bitmapParametersList != null){
            for (BitmapParameters bp : bitmapParametersList){
                SoftReference<Bitmap> bitmapSoftReference = bitmapCache.get(bp);
                if (bitmapSoftReference != null) {
                    Bitmap bitmap = bitmapSoftReference.get();
                    if (bitmap != null)
                        bitmap.recycle();
                }
                bitmapCache.remove(bp);
            }
        }
    }
    public static String getImageNameFromUrl(String imageUrl){
        int start = imageUrl.lastIndexOf("/") + 1;
        return  imageUrl.substring(start, imageUrl.length());
    }

    public static class BitmapParameters {
        private String path;
        private Point size;
        private boolean isImageRound;
        private int borderSize;
        private int noImageRes;
        private int borderColor;
        private boolean shouldDrawGlow;
        private boolean isGrayscaled;
        private int orientation;

        private BitmapParameters(String filePath, Point size, int noImageRes, boolean isImageRound, int borderSize, int borderColor, boolean shouldDrawGlow, boolean isGrayscaled, int orientation) {
            this.path = filePath;
            this.size = size;
            this.noImageRes = noImageRes;
            this.isImageRound = isImageRound;
            this.borderSize = borderSize;
            this.borderColor = borderColor;
            this.shouldDrawGlow = shouldDrawGlow;
            this.isGrayscaled = isGrayscaled;
            this.orientation = orientation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BitmapParameters that = (BitmapParameters) o;

            if (borderColor != that.borderColor) return false;
            if (borderSize != that.borderSize) return false;
            if (isGrayscaled != that.isGrayscaled) return false;
            if (isImageRound != that.isImageRound) return false;
            if (noImageRes != that.noImageRes) return false;
            if (orientation != that.orientation) return false;
            if (shouldDrawGlow != that.shouldDrawGlow) return false;
            if (path != null ? !path.equals(that.path) : that.path != null) return false;
            if (!size.equals(that.size)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = path != null ? path.hashCode() : 0;
            result = 31 * result + size.hashCode();
            result = 31 * result + (isImageRound ? 1 : 0);
            result = 31 * result + borderSize;
            result = 31 * result + noImageRes;
            result = 31 * result + borderColor;
            result = 31 * result + (shouldDrawGlow ? 1 : 0);
            result = 31 * result + (isGrayscaled ? 1 : 0);
            result = 31 * result + orientation;
            return result;
        }

        public static class Builder{
            private String path = "";
            private Point size = new Point(0, 0);
            private boolean isImageRound = false;
            private int borderSize = 0;
            private int noImageRes = Integer.MIN_VALUE;
            private int borderColor = Color.TRANSPARENT;
            private boolean shouldDrawGlow = false;
            private boolean isGrayscaled = false;
            private int orientation;

            public Builder setPath(String path){
                this.path = path;
                return this;
            }

            public Builder setSize(Point size){
                this.size = size;
                return this;
            }

            public Builder setIsImageRound(boolean isImageRound){
                this.isImageRound = isImageRound;
                return this;
            }

            public Builder setBorderSize(int borderSize){
                this.borderSize = borderSize;
                return this;
            }

            public Builder setNoImageRes(int noImageRes){
                this.noImageRes = noImageRes;
                return this;
            }

            public Builder setBorderColor(int borderColor){
                this.borderColor = borderColor;
                return this;
            }

            public Builder setShouldDrawGlow(boolean shouldDrawGlow){
                this.shouldDrawGlow = shouldDrawGlow;
                return this;
            }

            public Builder setIsGrayscaled(boolean isGrayscaled){
                this.isGrayscaled = isGrayscaled;
                return this;
            }

            public Builder setOrientation(int orientation) {
                this.orientation = orientation;
                return this;
            }

            public BitmapParameters build(){
                return new BitmapParameters(path, size, noImageRes, isImageRound, borderSize, borderColor, shouldDrawGlow, isGrayscaled, orientation);
            }
        }
    }

    public static class ResBitmapParameters {
        private int resId;
        private Point size;
        private boolean isImageRound;
        private int borderSize;
        private int borderColor;
        private boolean shouldDrawGlow;
        private boolean isGrayscaled;
        private int orientation;

        public ResBitmapParameters(BitmapParameters params) {
            resId = params.noImageRes;
            size = params.size;
            isImageRound = params.isImageRound;
            borderSize = params.borderSize;
            borderColor = params.borderColor;
            shouldDrawGlow = params.shouldDrawGlow;
            isGrayscaled = params.isGrayscaled;
            orientation = params.orientation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ResBitmapParameters that = (ResBitmapParameters) o;

            if (borderColor != that.borderColor) return false;
            if (borderSize != that.borderSize) return false;
            if (isGrayscaled != that.isGrayscaled) return false;
            if (isImageRound != that.isImageRound) return false;
            if (orientation != that.orientation) return false;
            if (resId != that.resId) return false;
            if (shouldDrawGlow != that.shouldDrawGlow) return false;
            if (!size.equals(that.size)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = resId;
            result = 31 * result + size.hashCode();
            result = 31 * result + (isImageRound ? 1 : 0);
            result = 31 * result + borderSize;
            result = 31 * result + borderColor;
            result = 31 * result + (shouldDrawGlow ? 1 : 0);
            result = 31 * result + (isGrayscaled ? 1 : 0);
            result = 31 * result + orientation;
            return result;
        }
    }
}

