package com.badou.mworking.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 功能描述: 图片处理工具类
 */
public class BitmapUtil {

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static Bitmap narrowBitMap(Bitmap bm, int newWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        if (newbm != bm && bm != null && !bm.isRecycled())
            bm.recycle();
        return newbm;
    }

    /**
     * 缩放图片
     *
     * @param orgBitmap
     * @return
     */
    public static Bitmap zoom(Bitmap orgBitmap, float zf) {
        Matrix matrix = new Matrix();
        matrix.postScale(zf, zf);
        Bitmap resizedBitmap = Bitmap.createBitmap(orgBitmap, 0, 0,
                orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true);
        if (orgBitmap != null && resizedBitmap != orgBitmap)
            orgBitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 缩放图片
     *
     * @return
     */
    public static Bitmap zoom(Bitmap orgBitmap, float wf, float hf) {
        Matrix matrix = new Matrix();
        matrix.postScale(wf, hf);
        Bitmap resizedBitmap = Bitmap.createBitmap(orgBitmap, 0, 0,
                orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true);
        if (orgBitmap != null && resizedBitmap != orgBitmap)
            orgBitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 图片圆角处理
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
        // RCB means
        // Rounded
        // Corner Bitmap
        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(dstbmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (bitmap != null && dstbmp != bitmap)
            bitmap.recycle();
        return dstbmp;
    }

    public static int calculateRatio(int orgWidth, int orgHeight, int reqWidth,
                                     int reqHeight) {
        // 源图片的高度和宽度
        int ratio = 1;
        if (orgHeight > reqHeight || orgWidth > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) orgHeight
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) orgWidth
                    / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return ratio;
    }

    /**
     * 功能描述:
     *
     * @param orgBitmap
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap narrowBitmap(Bitmap orgBitmap, int reqWidth,
                                      int reqHeight) {
        float ratio = 1.0f;
        if (orgBitmap == null)
            return null;
        if (orgBitmap.getHeight() > reqHeight
                && orgBitmap.getWidth() > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            float heightRatio = (float) reqHeight
                    / (float) orgBitmap.getHeight();
            float widthRatio = (float) reqWidth / (float) orgBitmap.getWidth();
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap resizedBitmap = Bitmap.createBitmap(orgBitmap, 0, 0,
                orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true);
        if (orgBitmap != null && resizedBitmap != orgBitmap)
            orgBitmap.recycle();
        return resizedBitmap;

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = res.openRawResource(resId);
        BitmapFactory.decodeStream(is, null, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateRatio(options.outWidth,
                options.outHeight, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        is = res.openRawResource(resId);
        return narrowBitmap(BitmapFactory.decodeStream(is, null, options),
                reqWidth, reqHeight);
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri,
                                                    int reqWidth, int reqHeight) throws FileNotFoundException {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        ContentResolver cr = context.getContentResolver();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);

        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateRatio(options.outWidth,
                options.outHeight, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

        return narrowBitmap(BitmapFactory.decodeStream(cr.openInputStream(uri),
                null, options), reqWidth, reqHeight);
    }

    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory
                .decodeByteArray(b, 0, b.length);
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri)
            throws FileNotFoundException {
        ContentResolver cr = context.getContentResolver();
        return BitmapFactory.decodeStream(cr.openInputStream(uri), null, null);
    }

    /**
     * 功能描述:从文件中获取图片
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
                                                     int reqHeight) {
        File file = new File(path);
        if (!file.exists())
            return null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateRatio(options.outWidth,
                options.outHeight, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap == null) {
            return null;
        } else {
            return narrowBitmap(bitmap, reqWidth, reqHeight);
        }
    }

    /**
     * 功能描述:从网络上获取图片
     *
     * @param Url
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws Exception
     */
    public static Bitmap decodeSampledBitmapFromUrl(String Url, int reqWidth,
                                                    int reqHeight) throws Exception {

        URL url = new URL(Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(6 * 1000);
        // 别超过10秒。
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = readStream(inputStream);
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateRatio(options.outWidth,
                    options.outHeight, reqWidth, reqHeight);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } else {
            throw new Exception("err: responseCode " + conn.getResponseCode());
        }
    }


    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        inStream.close();

        return outstream.toByteArray();
    }

    /**
     * 功能描述:获取圆形图片
     *
     * @param orgBitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap getCirlBitmp(Bitmap orgBitmap, int targetWidth,
                                      int targetHeight) {
        if (orgBitmap == null || orgBitmap.isRecycled())
            return null;
        // 防止失真
        targetWidth += 10;
        targetHeight += 10;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(orgBitmap, new Rect(0, 0, orgBitmap.getWidth(),
                orgBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);
        return targetBitmap;
    }

    public static void saveBitmap(Bitmap bmp, String fileName) {
        if (bmp == null || bmp.isRecycled())
            return;
        File f = new File(fileName);
        if (f.exists())
            f.delete();
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);

                baos.flush();

                byte[] bitmapBytes = baos.toByteArray();
                result = new String(Base64.encodeBase64(bitmapBytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 40, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中 
            options -= 10;//每次都减少10 
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中 
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片 
        return bitmap;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap compressBmp(String filePath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            // 先设置为TRUE不加载到内存中，但可以得到宽和高
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 480, 800);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
