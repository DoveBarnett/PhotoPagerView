package com.orient.photopagerviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File utils
 * Author WangJie
 * Created on 2018/10/8.
 */
class PhotoUtils {

    /**
     * 预防OO
     * @param reqWidth 要求的宽度
     * @param reqHeight 要求的高度
     * @return 缩放的倍数
     */
    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 获取屏幕的长和宽
     * @return 屏幕的长和宽
     */
    static int[] getScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels,outMetrics.heightPixels};
    }

    /**
     * 通过指定路径或者指定宽高的图片
     * @param path      路径
     * @param reqWidth  需要的宽度
     * @param reqHeight 需要的高度
     * @return Bitmap
     */
    static Bitmap getSingleBitmapByPath(String path, int reqWidth, int reqHeight) {
        File f = new File(path);
        if (!f.exists())
            return null;

        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            is = new FileInputStream(f);
            BitmapFactory.decodeStream(is, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            is = new FileInputStream(f);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 储存签名路径
     * @param bitmap     Bitmap
     * @param dir 存放的父路径
     * @param name       文件名
     * @return 文件字符串
     */
    static String saveSign(Bitmap bitmap, String dir, String name) {
        ByteArrayOutputStream bao = null;
        FileOutputStream fos = null;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = name + ".jpg";
        File cacheFile = new File(file, fileName);

        // 检查该文件是否存在，如果不存在就直接创建一个
        try {
            cacheFile.createNewFile();
            fos = new FileOutputStream(cacheFile);
            bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] b = bao.toByteArray();
            if (b != null) {
                fos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (bao != null)
                    bao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cacheFile.getPath();
    }

}
