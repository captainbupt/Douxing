package com.badou.mworking.services;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.badou.mworking.error.FileAlreadyExistException;
import com.badou.mworking.error.NoMemoryException;
import com.badou.mworking.http.AndroidHttpClient;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetworkUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类:  <code> DownloadTask </code>
 * 功能描述: 
 * 创建人: 葛建锋
 * 创建日期: 2013-12-2 上午9:17:22
 * 开发环境: JDK6.0
 */
public class DownloadTask extends AsyncTask<Void, Integer, Long> {

    public final static int TIME_OUT = 30000;  //超时为30秒
    private final static int BUFFER_SIZE = 1024 * 8;
    
    private static final String TAG = "DownloadTask";
    private static final boolean DEBUG = true;
    private static final String TEMP_SUFFIX = ".download";    //临时后缀

    private URL URL;
    private File file;
    private File tempFile;
    private String url;
    /**
     * 此类的实例支持对随机访问文件的读取和写入。随机访问文件的行为类似存储在文件系统中的一个大型 byte 数组。
     * 存在指向该隐含数组的光标或索引，称为文件指针；输入操作从文件指针开始读取字节，并随着对字节的读取而前移此文件指针。
     * 如果随机访问文件以读取/写入模式创建，则输出操作也可用；输出操作从文件指针开始写入字节，并随着对字节的写入而前移此文件指针。
     * 写入隐含数组的当前末尾之后的输出操作导致该数组扩展。该文件指针可以通过 getFilePointer 方法读取，并通过 seek 方法设置。
     * 通常，如果此类中的所有读取例程在读取所需数量的字节之前已到达文件末尾，则抛出 EOFException（是一种 IOException）。
     * 如果由于某些原因无法读取任何字节，而不是在读取所需数量的字节之前已到达文件末尾，则抛出 IOException，而不是 EOFException。
     * 需要特别指出的是，如果流已被关闭，则可能抛出 IOException。
     **/
    private RandomAccessFile outputStream;
    private DownloadTaskListener listener;
    private Context context;

    private long downloadSize;   
    private long previousFileSize; 
    private long totalSize;           
    private long downloadPercent;      //下载百分比   
    private long networkSpeed;        //网络速度
    private long previousTime;
    private long totalTime;
    private Throwable error = null;
    private boolean interrupt = false;

    /**
     * 
     **/
    private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
        private int progress = 0;

        public ProgressReportingRandomAccessFile(File file, String mode)
                throws FileNotFoundException {

            super(file, mode);
        }

        /* 将 len 个字节从指定 byte 数组写入到此文件，并从偏移量 offset 处开始。
         * @see java.io.RandomAccessFile#write(byte[], int, int)
         * @param buffer  数据
         * @param offset  数据的初始偏移量
         * @param count   要写入的字节数
         * @throws IOException
         */
        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
        
            super.write(buffer, offset, count);
            progress += count;
            /**
             * This method can be invoked from {@link #doInBackground} to
		     * publish updates on the UI thread while the background computation（计算） is
		     * still running. Each call to this method will trigger（触发） the execution（执行） of
		     * {@link #onProgressUpdate} on the UI thread.
		     *
		     * {@link #onProgressUpdate} will note（注意） be called if the task（任务） has been
		     * canceled.
		     * 这个方法能够调用{@link #doInBackground}来在主线程中发布一个更新，当后台计算仍在进行的时候。
		     * 每次调用这个方法将会触发UI线程中执行{@link #onProgressUpdate}这个方法。
             **/
            publishProgress(progress);
        }
    }

    /**
     * 功能描述: 
     * @param context
     * @param url
     * @param path
     * @throws java.net.MalformedURLException
     * public class MalformedURLException extends IOException
     *                          抛出这一异常指示出现了错误的 URL。或者在规范字符串中找不到任何合法协议，或者无法解析字符串。 
     */
    public DownloadTask(Context context, String url, String path) throws MalformedURLException {

        this(context, url, path, null);
    }

    /**
     * 功能描述:
     * @param context  
     * @param url
     * @param path
     * @param listener
     * @throws java.net.MalformedURLException
     */
    public DownloadTask(Context context, String url, String path, DownloadTaskListener listener)
            throws MalformedURLException {

        super();
        this.url = url;
        this.URL = new URL(url);
        this.listener = listener;
        /**
         * public String getFile()
         * 获取此 URL 的文件名。返回的文件部分将与 getPath() 相同，再加上 getQuery() 值的规范化形式（如果有）。
         * 如果没有查询部分，此方法和 getPath() 将返回相同的结果。 
         * */
//        String fileName = new File(URL.getFile()).getName();
        String fileNames[] = url.split("=");
        /**
         * public File(String parent,String child)
         * 根据 parent 路径名字符串和 child 路径名字符串创建一个新 File 实例。
         * */
        this.file = new File(path, fileNames[fileNames.length-1]);
        this.tempFile = new File(path, fileNames[fileNames.length-1] + TEMP_SUFFIX);
        this.context = context;
    }

    /**
     * 功能描述:  获取下载地址
     * @return
     */
    public String getUrl() {

        return url;
    }

    /**
     * 功能描述: 是否暂停
     * @return
     */
    public boolean isInterrupt() {
     
        return interrupt;
    }

    /**
     * 功能描述:  获取下载百分比
     * @return
     */
    public long getDownloadPercent() {

        return downloadPercent;
    }

    /**
     * 功能描述: 获取下载大小
     * @return    下载大小+先前下载了的文件大小
     */
    public long getDownloadSize() {

        return downloadSize + previousFileSize;
    }

    /**
     * 功能描述: 获取文件的总大小
     * @return
     */
    public long getTotalSize() {

        return totalSize;
    }

    /**
     * 功能描述: 获取下载速度
     * @return
     */
    public long getDownloadSpeed() {

        return this.networkSpeed;
    }

    /**
     * 功能描述:  获取下载总时间
     * @return
     */
    public long getTotalTime() {
       
        return this.totalTime;
    }

    /**
     * 功能描述: 下载栈的监听器
     * @return
     */
    public DownloadTaskListener getListener() {
     
        return this.listener;
    }

    @Override
    protected void onPreExecute() {

        previousTime = System.currentTimeMillis();
        if (listener != null)
            listener.preDownload(this);
    }

    @Override
    protected Long doInBackground(Void... params) {

        long result = -1;
        try {
            result = download();
        } catch (NetworkErrorException e) {
            error = e;
        } catch (FileAlreadyExistException e) {
            error = e;
        } catch (NoMemoryException e) {
            error = e;
        } catch (IOException e) {
            error = e;
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

        if (progress.length > 1) {
            totalSize = progress[1];
            if (totalSize == -1) {
                if (listener != null)
                    listener.errorDownload(this, error);
            } else {

            }
        } else {
            totalTime = System.currentTimeMillis() - previousTime;
            downloadSize = progress[0];
            downloadPercent = (downloadSize + previousFileSize) * 100 / totalSize;
            networkSpeed = downloadSize / totalTime;
            if (listener != null)
                listener.updateProcess(this);
        }
    }

    /* 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     * @param result
     *
     */
    @Override
    protected void onPostExecute(Long result) {

        if (result == -1 || interrupt || error != null) {
            if (DEBUG && error != null) {
                Log.v(TAG, "Download failed." + error.getMessage());
            }
            if (listener != null) {
                listener.errorDownload(this, error);
            }
            return;
        }
        // finish download
        tempFile.renameTo(file);
        if (listener != null)
            listener.finishDownload(this);
    }

    @Override
    public void onCancelled() {

        super.onCancelled();
        interrupt = true;
    }

    private AndroidHttpClient client;
    private HttpGet httpGet;
    private HttpResponse response;

    private long download() throws NetworkErrorException, IOException, FileAlreadyExistException,
            NoMemoryException {

        if (DEBUG) {
            Log.v(TAG, "totalSize: " + totalSize);
        }

        /*
         * check net work
         */
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw new NetworkErrorException("Network blocked.");
        }

        /*
         * check file length
         */
        client = AndroidHttpClient.newInstance("DownloadTask");
        httpGet = new HttpGet(url);
        response = client.execute(httpGet);
        totalSize = response.getEntity().getContentLength();

        if (file.exists() && totalSize == file.length()) {
            if (DEBUG) {
                Log.v(null, "Output file already exists. Skipping download.");
            }

            throw new FileAlreadyExistException("Output file already exists. Skipping download.");
        } else if (tempFile.exists()) {
            httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
            previousFileSize = tempFile.length();

            client.close();
            client = AndroidHttpClient.newInstance("DownloadTask");
            response = client.execute(httpGet);

            if (DEBUG) {
                Log.v(TAG, "File is not complete, download now.");
                Log.v(TAG, "File length:" + tempFile.length() + " totalSize:" + totalSize);
            }
        }

        /*
         * check memory
         */
        long storage = FileUtils.getAvailableStorage();
        if (DEBUG) {
            Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
        }

        if (totalSize - tempFile.length() > storage) {
            throw new NoMemoryException("SD card no memory.");
        }

        /*
         * start download
         */
        outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");

        publishProgress(0, (int) totalSize);

        InputStream input = response.getEntity().getContent();
        int bytesCopied = copy(input, outputStream);

        if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1 && !interrupt) {
            throw new IOException("Download incomplete: " + bytesCopied + " != " + totalSize);
        }

        if (DEBUG) {
            Log.v(TAG, "Download completed successfully.");
        }

        return bytesCopied;

    }

    public int copy(InputStream input, RandomAccessFile out) throws IOException,
            NetworkErrorException {

        if (input == null || out == null) {
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        if (DEBUG) {
            Log.v(TAG, "length" + out.length());
        }

        int count = 0, n = 0;
        long errorBlockTimePreviousTime = -1, expireTime = 0;

        try {

            out.seek(out.length());

            while (!interrupt) {
                n = in.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                count += n;

                /*
                 * check network
                 */
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    throw new NetworkErrorException("Network blocked.");
                }

                if (networkSpeed == 0) {
                    if (errorBlockTimePreviousTime > 0) {
                        expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
                        if (expireTime > TIME_OUT) {
                            throw new ConnectTimeoutException("connection time out.");
                        }
                    } else {
                        errorBlockTimePreviousTime = System.currentTimeMillis();
                    }
                } else {
                    expireTime = 0;
                    errorBlockTimePreviousTime = -1;
                }
            }
        } finally {
            client.close(); // must close client first
            client = null;
            out.close();
            in.close();
            input.close();
        }
        return count;
    }
}
