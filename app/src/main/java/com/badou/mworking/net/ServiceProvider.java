package com.badou.mworking.net;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.EncryptionByMD5;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * 功能描述: 网络获取
 */
public class ServiceProvider {

    /**
     * 功能描述:1 .登录
     *
     * @param context
     * @param username
     * @param password
     * @param volleyListener
     */
    public static void doLogin(Context context, String username,
                               String password, JSONObject LocationJson, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.SERIAL, username);
            jsonObject.put(RequestParameters.l_PASSWORD,
                    EncryptionByMD5.getMD5(password.getBytes()));
            jsonObject.put(RequestParameters.LOCATION, LocationJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue()
                .add(new JsonObjectRequest(Request.Method.POST, Net
                        .getRunHost(context) + Net.LOGIN, jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

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
                        .getRunHost(context) + Net.VERIFICATION_CODE(), jsonObject, volleyListener,
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
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.FORGET_PASS(), jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述:  4 体验账号登录
     *
     * @param context
     * @param phoneNum       电话号码
     * @param vcode          验证码
     * @param company        公司
     * @param office         职位
     * @param volleyListener
     */
    public static void Isexperience(Context context, String phoneNum, String vcode, String company, String office, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.cp_USER_PHONE, phoneNum);
            jsonObject.put(RequestParameters.cp_VCODE, vcode);
            jsonObject.put("company", company);
            jsonObject.put("title", office);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue()
                .add(new JsonObjectRequest(Request.Method.POST, Net
                        .getRunHost(context) + Net.EXPERIENCE(), jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 5.修改密码
     *
     * @param context
     * @param originalPassword
     * @param newPassword
     * @param volleyListener
     */
    public static void doChangePassword(Context context,
                                        String originalPassword, String newPassword,
                                        VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.USER_ID, ((AppApplication) context
                    .getApplicationContext()).getUserInfo().userId);
            jsonObject
                    .put(RequestParameters.cp_ORIGINAL_PASSWORD, originalPassword);
            jsonObject.put(RequestParameters.cp_NEW_PASSWORD, newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.CHANGE_PASSWORD(), jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }


    public static void doUpdateLocalResource(Context context, String type,
                                             String ts, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        if (ts == null)
            return;
        long tsInSecond = Long.parseLong(ts) / 1000;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.UPDATE_RESOURCES(uid, type, tsInSecond + "", 0),
                        null, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void doSubmitTrainMark(Context context, String rid, int mark,
                                         VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.SUMIT_TRAIN_MARK(uid, rid, mark), null,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述: 16  获取课件点赞数
     *
     * @param context
     * @param rids
     * @param volleyListener
     */
    public static void doUpdateFeedbackCount(Context context, String[] rids,
                                             VolleyListener volleyListener) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < rids.length; i++) {
            array.put(rids[i]);
        }
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new MyJsonRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.GET_MAC_POST(uid), array.toString(),
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void doUpdateComment(Context context, String rid,
                                       int pageNumber, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.USER_ID, uid);
            jsonObject.put(RequestParameters.RESOURCE_ID, rid);
            jsonObject.put(RequestParameters.cm_PAGENUMBER, pageNumber);
            jsonObject.put(RequestParameters.cm_ITEMPERPAGE, 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.UPDATE_COMMENT, jsonObject, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void doSubmitComment(Context context, String rid,
                                       String content, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new MyJsonRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.SUBMIT_COMMENT(uid, rid), content,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }


    /**
     * 通知公告和微培训 回复指定某人的评论
     *
     * @param context
     * @param rid
     * @param whom
     * @param content
     * @param volleyListener
     */
    public static void doReplyComment(Context context, String rid, String whom,
                                      String content, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new MyJsonRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.SUBMIT_PERSON_COMMENT(uid, rid, whom), content,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void doCheckUpdate(Context context, JSONObject jsonObject,
                                     VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        try {
            if (jsonObject == null) {
                jsonObject = new JSONObject();
                jsonObject.put(RequestParameters.CHK_UPDATA_PIC_COMPANY_LOGO, "");
                jsonObject.put(RequestParameters.CHK_UPDATA_PIC_NEWVER, "");
                jsonObject.put(RequestParameters.CHK_UPDATA_BANNER, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.CHECK_UPDATE(uid, AppApplication.screenlg), jsonObject, volleyListener,
                        volleyListener));

        MyVolley.getRequestQueue().start();
    }

    public static void doUpdateMTraning(final Context context, final String url) {
        final ProgressDialog mProgressDialog = new WaitProgressDialog(context,
                R.string.action_update_download_ing);
        mProgressDialog.setTitle(R.string.action_update_download_ing);
        if (null != mProgressDialog && context != null
                && !((Activity) context).isFinishing()) {
            mProgressDialog.show();
        }
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {

                } else if (msg.what == 1) {

                } else if (msg.what == 2) {
                    if (null != mProgressDialog && context != null
                            && !((Activity) context).isFinishing()) {
                        mProgressDialog.dismiss();
                        String path = (String) msg.obj;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + path),
                                "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }
        };
        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                String path = context.getExternalFilesDir(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                        + File.separator
                        + context.getResources().getString(R.string.app_name)
                        + Calendar.getInstance().getTimeInMillis() + ".apk";
                HttpDownloader.downFile(url, path, new DownloadListener() {

                    @Override
                    public void onDownloadSizeChange(int downloadSize) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = downloadSize;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onGetTotalSize(int totalSize) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.arg1 = totalSize;
                        handler.sendMessage(msg);
                    }
                });
                Message msg = new Message();
                msg.what = 2;
                msg.obj = path;
                handler.sendMessage(msg);
            }
        };
        thread.start();
    }

    public static void doSubmitError(Context context, String log, String appversion,
                                     VolleyListener volleyListener) {
        MyVolley.getRequestQueue().add(
                new MyJsonRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.SUBMIT_ERROR(appversion), log, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * chygt 12  获取用户详情
     */
    public static void doOptainUserDetail(Context context, String uid,
                                          VolleyListener volleyListener) {
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.USER_DETAIL(uid), null, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 发布问题/分享
     */
    public static void doPublishQuestionShare(final Context context,
                                              final String type, final String content, Bitmap bitmap, int anonymous,
                                              final VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        try {
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID,
                    uid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_TYPE,
                    type);
            jsonObject.put(
                    RequestParameters.PUBLISH_QUSETION_SHARE_CONTENT,
                    content);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_ANONYMOUS, anonymous);
            if (bitmap != null) {
                jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_PICTURE, BitmapUtil.bitmapToBase64(bitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.QUESTION_PUBLISH, jsonObject, volleyListener, volleyListener));
    }

    /**
     * 功能描述: 摄像上传
     */
    public static void doUploadVideo(final Context context, String qid, String filePath, final VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        final String url = Net.getRunHost(context) + Net.PUBVIDEO(uid, qid);
        RequestParams params = new RequestParams();
        FileEntity entity = new FileEntity(new File(filePath),
                "binary/octet-stream");
        params.setBodyEntity(entity);
        System.out.println("url:" + url);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                System.out.println(responseInfo.result);
                try {
                    volleyListener.onResponse(new JSONObject((String) responseInfo.result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    volleyListener.onResponse(new JSONObject());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                volleyListener.onErrorResponse(new ParseError(new NetworkResponse(s.getBytes())));
            }
        });
    }

    /**
     * 功能描述: 图片上传
     */
    public static void doUploadImage(final Context context, String qid, int index, Bitmap bitmap, final VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        final String url = Net.getRunHost(context) + Net.PUBIMAGE(uid, qid, index);
        final String tempFilePath = FileUtils.getChatterDir(context) + "temp.jpg";
        FileUtils.writeBitmap2SDcard(bitmap, tempFilePath);
        RequestParams params = new RequestParams();
        FileEntity entity = new FileEntity(new File(tempFilePath),
                "binary/octet-stream");
        params.setBodyEntity(entity);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                //volleyListener.onResponse(responseInfo.result);
                System.out.println("result: " + responseInfo.result);
                try {
                    volleyListener.onResponse(new JSONObject("{\"errcode\":0}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                volleyListener.onErrorResponse(new ParseError(new NetworkResponse(s.getBytes())));
            }
        });
    }

    public static void doGetTopicList(Context context, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        final String url = Net.getRunHost(context) + Net.TOPICLIST(uid);
        MyVolley.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url, null, volleyListener, volleyListener));
    }

    /*
     * 发布问题
     * */
    public static void doPublishAsk(final Context context, final String subject, final String content, final Bitmap bitmap,
                                    final VolleyListener volleyListener) {

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                volleyListener.onResponse(msg.obj);
            }

            ;
        };

        new Thread() {
            public void run() {
                final String url = Net.getRunHost(context) + Net.pubAsk();
                JSONObject jsonObject = new JSONObject();
                String uid = ((AppApplication) context.getApplicationContext())
                        .getUserInfo().userId;
                try {
                    jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID,
                            uid);
                    jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_SUBJECT, subject);
                    jsonObject.put(
                            RequestParameters.PUBLISH_QUSETION_SHARE_CONTENT,
                            content);
                    if (bitmap != null) {
                        jsonObject.put(
                                RequestParameters.PUBLISH_QUSETION_SHARE_PICTURE,
                                BitmapUtil.bitmapToBase64(bitmap));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HttpPost request = new HttpPost(url);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response;
                JSONObject result = null;
                try {
                    ByteArrayEntity arrayEntity = new ByteArrayEntity(
                            jsonObject.toString().getBytes());
                    request.setEntity(arrayEntity);
                    response = httpClient.execute(request);
                    // 如果返回状态为200，获得返回的结果
                    if (response != null
                            && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        // 图片上传成功
                        HttpEntity entity2 = response.getEntity();
                        if (entity2 != null) {
                            String string = EntityUtils.toString(entity2);
                            result = new JSONObject(string);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.obtainMessage(1, result).sendToTarget();
            }

            ;
        }.start();
    }

    /*
     * 发布回答
     * */
    public static void doPublishAnswer(final Context context, final String content, final String aid, final Bitmap bitmap,
                                       final VolleyListener volleyListener) {

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                volleyListener.onResponse(msg.obj);
            }

            ;
        };

        new Thread() {
            public void run() {
                final String url = Net.getRunHost(context) + Net.pubAsnswer();
                JSONObject jsonObject = new JSONObject();
                String uid = ((AppApplication) context.getApplicationContext())
                        .getUserInfo().userId;
                try {
                    jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID,
                            uid);
                    jsonObject.put(
                            RequestParameters.PUBLISH_QUSETION_SHARE_CONTENT,
                            content);
                    jsonObject.put("aid", aid);
                    if (bitmap != null) {
                        jsonObject.put(
                                RequestParameters.PUBLISH_QUSETION_SHARE_PICTURE,
                                BitmapUtil.bitmapToBase64(bitmap));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HttpPost request = new HttpPost(url);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response;
                JSONObject result = null;
                try {
                    ByteArrayEntity arrayEntity = new ByteArrayEntity(
                            jsonObject.toString().getBytes());
                    request.setEntity(arrayEntity);
                    response = httpClient.execute(request);
                    // 如果返回状态为200，获得返回的结果
                    if (response != null
                            && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        // 图片上传成功
                        HttpEntity entity2 = response.getEntity();
                        if (entity2 != null) {
                            String string = EntityUtils.toString(entity2);
                            result = new JSONObject(string);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.obtainMessage(1, result).sendToTarget();
            }

            ;
        }.start();
    }

    /**
     * 回复问题／分享
     */
    public static void doAnswerQuestionShare(Context context, String qid,
                                             String content, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        try {
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID, uid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_QID, qid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_CONTENT,
                    content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Net.getRunHost(context) + Net.QUESTION_REPLY;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        volleyListener, volleyListener));
    }

    /**
     * 获取最新问题／分享的列表
     */
    public static void doQuestionShareList(Context context, String type, String topic,
                                           int page_no, int item_per_page, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        // 正常查询页面
        if (TextUtils.isEmpty(topic)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID, uid);
                jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_TYPE, type);
                jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_PAGE_NO,
                        page_no);
                jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_ITEM_PER_PAGE,
                        item_per_page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Net.getRunHost(context) + Net.QUESTION_GET;
            MyVolley.getRequestQueue().add(
                    new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                            volleyListener, volleyListener));
        } else {// 话题查询页面
            try {
                topic = URLEncoder.encode(topic, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = Net.getRunHost(context) + Net.QUESTION_GET_TOPIC(uid, topic, page_no, item_per_page);
            MyVolley.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url, null, volleyListener, volleyListener));
        }
    }

    /**
     * 获取最新问题／分享的列表
     */
    public static void doGetChatterHot(Context context, int page_no, int item_per_page, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        String url = Net.getRunHost(context) + Net.QUESTION_GET_HOT(uid, page_no, item_per_page);
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, null,
                        volleyListener, volleyListener));
    }

    /**
     * 获取某一特定用户的同事圈列表
     */
    public static void doGetUserChatterList(Context context, String type, String uid, int page_no, int item_per_page, VolleyListener volleyListener) {
        String selfuid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        // 正常查询页面
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID, uid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_TYPE, type);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_PAGE_NO,
                    page_no);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_ITEM_PER_PAGE,
                    item_per_page);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_SELF_UID, selfuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Net.getRunHost(context) + Net.QUESTION_GET_USER;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        volleyListener, volleyListener));
    }

    /**
     * 获取最新问题／分享的答复
     */
    public static void doQuestionShareAnswer(Context context, String qid,
                                             int page_no, int item_per_page, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        try {
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID, uid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_QID, qid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_PAGE_NO,
                    page_no);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_ITEM_PER_PAGE,
                    item_per_page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Net.getRunHost(context) + Net.QUESTION_REPLY_GET;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        volleyListener, volleyListener));
    }

    /**
     * 功能描述:已读打点
     *
     * @param context
     * @param rid
     */
    public static void doMarkRead(final Context context, String rid) {
        VolleyListener volleyListener = new VolleyListener(context) {

            @Override
            public void onResponse(Object arg0) {
                try {
                    JSONObject response = (JSONObject) arg0;
                    int code = response.optInt(Net.CODE);
                    if (code == Net.LOGOUT) {
                        AppApplication.logoutShow(context);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        };
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.MARK_READ(rid, uid), null, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 9.获取分类列表
     *
     * @param context
     * @param type
     */
    public static void doGetCategorys(Context context, String type,
                                      VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.GET_TAG(uid, type), null, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    public static void doUpdateBitmap(Context context, Bitmap bitmap,
                                      final String url, final VolleyListener volleyListener) {
        if (bitmap != null) {
            final String tempImgPath = context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "temp.png";
            FileUtils.writeBitmap2SDcard(bitmap, tempImgPath);
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    volleyListener.onResponse(msg.obj);
                }

                ;
            };
            new Thread() {
                public void run() {
                    JSONObject jsonObject = null;
                    HttpPost request = new HttpPost(url);
                    HttpClient httpClient = new DefaultHttpClient();
                    FileEntity entity = new FileEntity(new File(tempImgPath),
                            "binary/octet-stream");
                    HttpResponse response;
                    try {
                        request.setEntity(entity);
                        entity.setContentEncoding("binary/octet-stream");
                        response = httpClient.execute(request);
                        // 如果返回状态为200，获得返回的结果
                        if (response != null
                                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            // 图片上传成功
                            HttpEntity entity2 = response.getEntity();
                            if (entity2 != null) {
                                String string = EntityUtils.toString(entity2);
                                jsonObject = new JSONObject(string);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.obtainMessage(1, jsonObject).sendToTarget();
                    new File(tempImgPath).delete();
                }

                ;
            }.start();
        } else {
            MyVolley.getRequestQueue().add(
                    new JsonObjectRequest(Request.Method.POST, url, null,
                            volleyListener, volleyListener));
            MyVolley.getRequestQueue().start();
        }
    }

    public static void doGetLibrary(Context context,
                                    VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.KNOWLEDGE_LIBIRARY(uid), null, volleyListener,
                        volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 我的圈评论删除
     */
    public static void doMyGroup_del(Context context, String qid, VolleyListener volleyListener) {
        JSONObject jsonObject = new JSONObject();
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        try {
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_UID, uid);
            jsonObject.put(RequestParameters.PUBLISH_QUSETION_SHARE_QID, qid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Net.getRunHost(context) + Net.MYGROUP_DEL(uid, qid);
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 26 同事圈点赞
     *
     * @param context
     * @param qid
     * @param volleyListener
     */
    public static void doSetCredit(Context context, String qid, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        String url = Net.getRunHost(context) + Net.SET_CREDIT(uid, qid);
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, url, null,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }


    /**
     * 27.获取聊天⽤用户列表
     *
     * @param context
     * @param volleyListener
     */
    public static void dogetChatList(Context context, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        String url = Net.getRunHost(context) + Net.GET_CHAT_LIST(uid, "");
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, url, null,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 28.获取⽤用户聊天记录
     *
     * @param context
     * @param whom
     * @param volleyListener
     */
    public static void dogetChatInfo(Context context, String whom, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        String url = Net.getRunHost(context) + Net.GET_CHAT_Info(uid, whom);
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, url, null,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 29. 发送消息(聊天)
     */
    public static void doSendChat(Context context, String content, String whom, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        String url = Net.getRunHost(context) + Net.SEND_MSG();
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("whom", whom);
            json.put("content", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, url, json,
                        volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述:   资源更新v2 接口          2014-12-15
     */
    public static void doUpdateLocalResource2(Context context, String type, int tag,
                                              int begin, int limit, String searchStr, String done, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        // url 编码，    url请求不支持中问，需要将中文进行url编码
        if (!TextUtils.isEmpty(searchStr)) {
            try {
                searchStr = URLEncoder.encode(searchStr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.UPDATE_RESOURCES_2(uid, type, tag, begin, limit, searchStr, done),
                        null, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述:   搜索 接口          2014-12-15
     */
    public static void doSearch(Context context, String key, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        // url 编码，    url请求不支持中问，需要将中文进行url编码
        key = key.replace("\n", "");
        key = key.replace(" ", "");
        if (!TextUtils.isEmpty(key)) {
            try {
                key = URLEncoder.encode(key, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context)
                        + Net.SEARCH(uid, key), null, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述: 课件打分
     */
    public static void coursewareScoring(Context context, String rid, String credit, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.GET, Net.getRunHost(context) + Net.COURSEWARE_SCORING(uid, rid, credit),
                        null, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }


    /**
     * 功能描述:  评论回复
     *
     * @param context
     * @param qid            问题／分享的唯一id
     * @param content        答复内容。
     * @param whom           回复的uid(取getreply的结果的uid字段)
     * @param volleyListener
     */
    public static void ReplyComment(Context context, String qid, String content, String whom, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("qid", qid);
            json.put("content", content);
            json.put("whom", whom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.ReplyComment(), json,
                        volleyListener, volleyListener));

        System.out.println(Net.getRunHost(context)
                + Net.ReplyComment());

        MyVolley.getRequestQueue().start();
    }

    /**
     * 功能描述:  删除评论回复
     */
    public static void deleteReplyComment(Context context, String qid, int floor, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("qid", qid);
            json.put("no", floor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Net.getRunHost(context)
                + Net.DeleteReplyComment());

        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context)
                        + Net.DeleteReplyComment(), json,
                        volleyListener, volleyListener));

        MyVolley.getRequestQueue().start();
    }

    /**
     * 获取问答列表
     *
     * @param context
     * @param page           第几页
     * @param limit          每次请求条目数
     * @param searchStr      搜索的内容
     * @param volleyListener
     */
    public static void updateAskList(Context context, int page, int limit, String searchStr, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        // url 编码，    url请求不支持中问，需要将中文进行url编码
        if (!TextUtils.isEmpty(searchStr)) {
            try {
                searchStr = URLEncoder.encode(searchStr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("key", searchStr);  // 搜索关键字，无则为空
            json.put("page_no", page);
            json.put("item_per_page", limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.getask(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 获取回答列表
     */
    public static void updateAnswerList(Context context, int page, int limit, String aid, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("aid", aid);  // 搜索关键字，无则为空
            json.put("page_no", page);
            json.put("item_per_page", limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.getAnswer(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }


    /**
     * 删除问答
     */
    public static void deleteAsk(Context context, String aid, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("aid", aid);  // 搜索关键字，无则为空
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.delAsk(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 回答点赞
     *
     * @param context
     * @param aid
     * @param ts
     * @param volleyListener
     */
    public static void pollAnswer(Context context, String aid, String ts, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("aid", aid);  // 搜索关键字，无则为空
            json.put("ts", ts);  // 搜索关键字，无则为空
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.pollAnswer(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 获取用户等级信息
     *
     * @param context
     * @param tag            列表tag
     * @param volleyListener
     */
    public static void getViewrank(Context context, String tag, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("tag", tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.getViewrank(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 获取用户历史等级考试
     */
    public static void getPastrank(Context context, String tag, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("tag", tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.getPastrank(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }


    /**
     * 删除会话
     *
     * @param context
     * @param whom
     * @param volleyListener
     */
    public static void delChat(Context context, String whom, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("whom", whom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.delchat(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

    /**
     * 获取资源
     */
    public static void getResourceDetail(Context context, String rid, VolleyListener volleyListener) {
        String uid = ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        JSONObject json = new JSONObject();
        try {
            json.put("uid", uid);
            json.put("rid", rid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.getRequestQueue().add(
                new JsonObjectRequest(Request.Method.POST, Net.getRunHost(context) + Net.viewResourceDetail(),
                        json, volleyListener, volleyListener));
        MyVolley.getRequestQueue().start();
    }

}
