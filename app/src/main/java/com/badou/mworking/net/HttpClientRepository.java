package com.badou.mworking.net;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceActivity;

import com.badou.mworking.util.FileUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.params.ClientPNames;

import java.io.File;
import java.io.IOException;

/**
 * 功能描述: 网络获取
 */
public class HttpClientRepository {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void doUpdateMTraning(final Context context, final String url, final DownloadListener httpResponseHandler) {
        File file;
        try {
            file = File.createTempFile("update.apk", "tmp");
        } catch (IOException e) {
            final String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "update.apk.tmp";
            file = new File(path);
            e.printStackTrace();
        }
        file.deleteOnExit(); // 仅当次登录有效
        client.get(url, new RangeFileAsyncHttpResponseHandler(file) {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                httpResponseHandler.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                httpResponseHandler.onFailure(statusCode, headers, throwable, file);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                FileUtils.renameFile(response.getParent(), "update.apk.tmp", "update.apk");
                httpResponseHandler.onSuccess(statusCode, headers, response);
            }
        });
    }

    public interface DownloadListener {
        void onProgress(long current, long total);

        void onFailure(int statusCode, Header[] headers, Throwable throwable, File file);

        void onSuccess(int statusCode, Header[] headers, File response);
    }

    // 必须传入Context，已便在Activity结束的时候能够停止全部相关请求
    public static void doDownloadTrainingFile(Context context, final String url, final String fullPath, final DownloadListener downloadListener) {
        File file = new File(fullPath + ".tmp");

        isForceStopped = false;
        try {
            client.get(context, url, new RangeFileAsyncHttpResponseHandler(file) {
                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    if (!isForceStopped)
                        downloadListener.onProgress(bytesWritten, totalSize);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    downloadListener.onFailure(statusCode, headers, throwable, file);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File response) {
                    if (!isForceStopped) {
                        FileUtils.renameFile(response.getParent(), response.getName(), response.getName().replace(".tmp", ""));
                        downloadListener.onSuccess(statusCode, headers, new File(fullPath));
                    }
                }
            });
        } catch (AssertionError e) {
            downloadListener.onFailure(400, null, e, file);
        }
    }

    public static boolean isForceStopped = false;

    public static void cancelRequest(Context context) {
        isForceStopped = true;
        client.cancelRequests(context, true);
        // request取消后会导致重定向选项被关闭，比许手动开启
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
    }
}
