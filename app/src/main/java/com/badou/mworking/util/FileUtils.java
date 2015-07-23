package com.badou.mworking.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static int FILESIZE = 4 * 1024;

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDPath(Context context) {
        if (hasSdcard()) {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        } else {
            return context.getFilesDir() + "/";
        }
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param input
     * @return
     */
    public static File write2SDFromInput(String path, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            file = new File(path);
            if (file.exists())
                file.delete();
            file.createNewFile();

            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];

            int totalLength = 0;
            int length;
            while ((length = (input.read(buffer))) > 0) {
                output.write(buffer, 0, length);
                totalLength += length;
            }

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 复制文件
     *
     * @param srcPath  源文件绝对路径
     * @param destPath 目标文件所在目录
     * @return boolean
     */
    private static boolean copyFile(String srcPath, String destPath) {
        boolean flag = false;

        File srcFile = new File(srcPath);
        if (!srcFile.exists()) { // 源文件不存在
            System.out.println("源文件不存在");
            return false;
        }
        if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
            System.out.println("源文件路径和目标文件路径重复!");
            return false;
        }
        File destFile = new File(destPath);
        if (destFile.exists()) { // 该路径下已经有一个同名文件
            deleteGeneralFile(destPath);
        }
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();

            flag = true;
        } catch (IOException e) {
            //
            e.printStackTrace();
        }

        if (flag) {
            System.out.println("复制文件成功!");
        }

        return flag;
    }

    /**
     * 删除文件或文件夹
     *
     * @param path 待删除的文件的绝对路径
     * @return boolean
     */
    public static boolean deleteGeneralFile(String path) {
        boolean flag = false;

        File file = new File(path);
        if (!file.exists()) { // 文件不存在
            System.out.println("要删除的文件不存在！");
        }

        if (file.isDirectory()) { // 如果是目录，则单独处理
            flag = deleteDirectory(file.getAbsolutePath());
        } else if (file.isFile()) {
            flag = deleteFile(file);
        }

        if (flag) {
            System.out.println("删除文件或文件夹成功!");
        }

        return flag;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return boolean
     */
    private static boolean deleteFile(File file) {
        return file.delete();
    }

    /**
     * 删除目录及其下面的所有子文件和子文件夹，注意一个目录下如果还有其他文件或文件夹
     * 则直接调用delete方法是不行的，必须待其子文件和子文件夹完全删除了才能够调用delete
     *
     * @param path path为该目录的路径
     */
    private static boolean deleteDirectory(String path) {
        boolean flag = true;
        File dirFile = new File(path);
        if (!dirFile.isDirectory()) {
            return flag;
        }
        File[] files = dirFile.listFiles();
        for (File file : files) { // 删除该文件夹下的文件和文件夹
            // Delete file.
            if (file.isFile()) {
                flag = deleteFile(file);
            } else if (file.isDirectory()) {// Delete folder
                flag = deleteDirectory(file.getAbsolutePath());
            }
            if (!flag) { // 只要有一个失败就立刻不再继续
                break;
            }
        }
        flag = dirFile.delete(); // 删除空目录
        return flag;
    }

    /**
     * 由上面方法延伸出剪切方法：复制+删除
     *
     * @param destPath 同上
     */
    public static boolean cutGeneralFile(String srcPath, String destPath) {
        if (!copyFile(srcPath, destPath)) {
            System.out.println("复制失败导致剪切失败!");
            return false;
        }
        if (!deleteGeneralFile(srcPath)) {
            System.out.println("删除源文件(文件夹)失败导致剪切失败!");
            return false;
        }

        System.out.println("剪切成功!");
        return true;
    }

    public static File writeBitmap2TmpFile(Context context, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled())
            return null;
        String filePath = context.getExternalCacheDir().getPath() + File.separator + "tmp.jpg";
        FileUtils.writeBitmap2SDcard(bitmap, filePath);
        return new File(filePath);
    }

    public static void writeBitmap2SDcard(Bitmap bitmap, String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        byte[] byteArray = baos.toByteArray();
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 功能描述:获取sd卡的剩余空间
     *
     * @return
     */
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory();// 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
        // (availableBlocks * blockSize)/1024 KIB 单位
        // (availableBlocks * blockSize)/1024 /1024 MIB单位
    }

    /**
     * 功能描述: 通过递归的方法获取文件夹内文件的大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 功能描述: 微培训文件缓存目录
     */
    public static String getTrainCacheDir(Context context) {
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "train";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
            return filePath + File.separator;
        }
        return filePath + File.separator;
    }

    public static String getChatterDir(Context context) {
        String filePath = context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "tongshiquan";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
            return filePath + File.separator;
        }
        return filePath + File.separator;
    }

    public static String getChatterVideoDir(Context context) {
        return getChatterDir(context) + "douxing_paishe.mp4";
    }

    public static long getAvailableStorage() {
        String storageDirectory = null;  //存储目录
        storageDirectory = Environment.getExternalStorageDirectory().toString(); //获取外部存储目录

        try {
            /**
             * StatFs Retrieve overall information about the space on a filesystem.
             * 返回关于文件系统空间的所有信息
             * Android.os下的StatFs类主要用来获取文件系统的状态，能够获取sd卡的大小和剩余空间，获取系统内部空间也就是/system的大小和剩余空间等等。
             * */
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 文件重命名
     *
     * @param path    文件目录
     * @param oldname 原来的文件名
     * @param newname 新文件名
     */
    public static File renameFile(String path, String oldname, String newname) {
        File oldfile = new File(path, oldname);
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File newfile = new File(path, newname);
            if (!oldfile.exists()) {
                return oldfile;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
                return newfile;
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }
        return oldfile;
    }

    public static String readFile2String(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile())
            return null;
        Long fileLength = file.length();     //获取文件长度
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(fileContent);//返回文件内容,默认编码
    }
}
