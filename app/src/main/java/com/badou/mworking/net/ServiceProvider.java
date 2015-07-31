package com.badou.mworking.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.FileUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 网络获取
 */
public class ServiceProvider {

    private static AsyncHttpClient client = new AsyncHttpClient();


    /**
     * 功能描述: 2. 发送短信获取验证码
     */
    public static void getVerificationCode(Context context, String phoneNum, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.cp_USER_PHONE, phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue()
                .add(new JsonObjectRequest(Request.Method.POST, Net
                        .getRunHost() + Net.VERIFICATION_CODE(), jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 3.忘记密码重置
     *
     * @param context
     * @param serial         手机号
     * @param vcode          验证码
     * @param newpwd         新密码
     * @param volleyListener
     */
    public static void doForgetPassword(Context context, String serial, String vcode, String newpwd, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.cp_USER_PHONE, serial);
            jsonObject.put(RequestParameters.cp_VCODE, vcode);
            jsonObject.put(RequestParameters.cp_NEW_PASSWORD, newpwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost()
                        + Net.FORGET_PASS(), jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }


    public static void doUpdateMTraning(final Context context, final String url, final RangeFileAsyncHttpResponseHandler httpResponseHandler) {
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

    public static void getContacts(Context context, VolleyListener volleyListener) {
        String uid = UserInfo.getUserInfo().getUid();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.USER_ID, uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, Net.getRunHost() + Net.getContactList(),
                jsonObject, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void registerAccount(Context context, String username, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hxusr", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, Net.getRunHost() + Net.registerAccount(),
                jsonObject, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

}
