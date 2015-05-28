package com.badou.mworking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的学习 类: MyStudyProgressAct 时间:2014年8月26日 | 下午1:52:16
 */
public class MyStudyProgressAct extends BaseNoTitleActivity implements OnClickListener, OnRefreshListener2<ListView> {


    public ImageView ivLeft;  // action 左侧iv
    public TextView tvTitle;  // action 中间tv
    private TextView tvRank;
    private TextView tvText;

    public static final String VALUE_STUDY = "VALUE_STUDY";

    private int beginIndex = 0;
    private TrainAdapter trainAdapter;
    private PullToRefreshListView pullToRefreshListView;

    private Dialog dialog;//
    private ProgressBar pro;// 文件下载的进度条
    private Train train;
    private AlertDialog.Builder loadDialog;// 显示的提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_study_progerss);
        //页面滑动关闭
        layout.attachToActivity(this);
        initAction(this);
        initView();
        initListener();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void initView() {
        tvRank = (TextView) this.findViewById(R.id.tv_PaiMing);
        UserDetail userDetail = (UserDetail) getIntent().getSerializableExtra(
                UserCenterActivity.KEY_USERINFO);
        if (userDetail != null) {
            int studyRank = userDetail.getStudy_rank();
            String str1 = " <font color=\'#ffffff\'><b>"
                    + mContext.getResources().getString(R.string.study_fir)
                    + "</b></font>";// 你的学习进度
            String str2 = " <font color=\'#ffffff\'><b>"
                    + mContext.getResources().getString(R.string.study_sec)
                    + "</b></font>";// 的用户
            String html1 = str1 + " <font color=\'#f7ab32\'><b>" + studyRank
                    + "%</b></font>" + str2;
            String str3 = "";

            if (studyRank >= 0 && studyRank <= 50) {
                str3 = " <font color=\'#ffffff\'><b>"
                        + "小小学渣" + "</b></font>";
            } else if (studyRank > 50 && studyRank <= 80) {
                str3 = " <font color=\'#ffffff\'><b>"
                        + "淡淡学屌" + "</b></font>";
            } else if (studyRank > 80 && studyRank <= 100) {
                str3 = " <font color=\'#ffffff\'><b>"
                        + "五星学霸" + "</b></font>";
            }
            tvRank.setText(Html.fromHtml(html1));
            tvText = (TextView) this.findViewById(R.id.tv_PaiMingtext);
            tvText.setText(Html.fromHtml(str3));
        } else {
            // return 直接返回，不在进行其他操作，应为这回对空对象操作，下面都会报错
            return;
        }
    }

    /**
     * 初始化action 布局
     *
     * @param onclick
     */
    private void initAction(OnClickListener onclick) {
        ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
        tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
        ivLeft.setOnClickListener(onclick);
        tvTitle.setText(getResources().getString(R.string.user_center_my_study_progress));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
        if (loadDialog == null) {
            // 初始化对话框
            loadDialog = new AlertDialog.Builder(this);
        }
        pullToRefreshListView.setMode(Mode.BOTH);
        pullToRefreshListView.setVisibility(View.VISIBLE);
        pullToRefreshListView.setOnRefreshListener(this);
        if (trainAdapter == null) {
            trainAdapter = new TrainAdapter(mContext, true);
        }
        pullToRefreshListView.setAdapter(trainAdapter);
        pullToRefreshListView.setRefreshing();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_actionbar_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        beginIndex = 0;
        updataListView(beginIndex);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        updataListView(beginIndex);
    }


    /**
     * 功能描述:
     *
     * @param beginNum
     */
    private void updataListView(final int beginNum) {
        ServiceProvider.doUpdateLocalResource2(MyStudyProgressAct.this, Train.CATEGORY_KEY_NAME, 0, beginNum, Constant.LIST_ITEM_NUM, "", "1",
                new VolleyListener(MyStudyProgressAct.this) {

                    @Override
                    public void onResponse(Object responseObject) {
                        pullToRefreshListView.onRefreshComplete();
                        final List<Object> list = new ArrayList<>();
                        JSONObject response = (JSONObject) responseObject;
                        try {
                            int code = response.optInt(Net.CODE);
                            if (code != Net.SUCCESS) {
                                return;
                            }
                            JSONObject data = response.optJSONObject(Net.DATA);
                            if (data == null
                                    || data.equals("")) {
                                return;
                            }
                            JSONArray resultArray = data
                                    .optJSONArray(Net.LIST);
                            if (resultArray == null
                                    || resultArray.length() == 0) {
                                if (beginIndex > 0) {
                                    ToastUtil.showUpdateToast(MyStudyProgressAct.this);
                                } else {
                                    pullToRefreshListView.setVisibility(View.GONE);
                                }
                                return;
                            }
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.optJSONObject(i);
                                Train entity = new Train(jsonObject);
                                list.add(entity);
                                beginIndex++;
                            }
                            if (beginNum <= 0) {
                                trainAdapter.setList(updateFeedback(list));
                            } else {
                                trainAdapter.addList(updateFeedback(list));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            trainAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        beginIndex = 0;
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    /**
     * 功能描述:通过网络获取课件点赞数量的list
     */
    private List<Object> updateFeedback(final List<Object> list) {
        int length = list.size();
        String[] rids = new String[length];
        for (int i = 0; i < length; i++) {
            rids[i] = ((Train) list.get(i)).rid;
        }
        // 获取资源的点赞数／评论数／评分
        ServiceProvider.doUpdateFeedbackCount(MyStudyProgressAct.this, rids, new VolleyListener(
                MyStudyProgressAct.this) {

            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                try {
                    int code = response.optInt(Net.CODE);
                    if (code != Net.SUCCESS) {
                        return;
                    }
                    JSONArray resultArray = response
                            .optJSONArray(Net.DATA);
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObject = resultArray.optJSONObject(i);
                        String rid = jsonObject.optString(ResponseParams.CATEGORY_RID);
/*						int feedbackCount = jsonObject
                                .optInt(ResponseParams.RATING_NUM);
						int comment = jsonObject
								.optInt(ResponseParams.COMMENT_NUM);*/
                        int ecnt = jsonObject
                                .optInt(ResponseParams.ECNT); //评分人数
                        int eval = jsonObject
                                .optInt(ResponseParams.EVAL); //评分总分
                        for (int j = 0; j < list.size(); j++) {
                            Train t = (Train) list.get(j);
                            if (rid.equals(t.rid)) {/*
								t.setCommentNum(comment);
								t.setFeedbackCount(feedbackCount);*/
                                t.ecnt = ecnt;
                                t.eval = eval;
                            }
                        }
                    }
                    trainAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pullToRefreshListView.onRefreshComplete();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pullToRefreshListView.onRefreshComplete();
            }
        });
        return list;
    }

    protected void initListener() {
        TextView rlGoAct = (TextView) findViewById(R.id.comment_relat);
        rlGoAct.setText(mContext.getResources().getString(R.string.mystudy_btn));
        rlGoAct.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Category.CLICKMAINICON = Category.CATEGORY_TRAIN;
                Intent intent = new Intent(mContext, TrainActivity.class);
                intent.putExtra(VALUE_STUDY, 1);
                startActivity(intent);
            }
        });

        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                train = (Train) trainAdapter.getItem(position - 1);
                int subtype = train.subtype;
                if (NetUtils.isNetConnected(MyStudyProgressAct.this)) {
                    // 向服务提交课件信息
                    trainAdapter.read(position - 1);
                    ServiceProvider.doMarkRead(MyStudyProgressAct.this, train.rid);
                }
                //返回PDF格式
                if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) {
                    toPDFAndWeb(train);
                    // 返回MP4格式
                } else if (Constant.MWKG_FORAMT_TYPE_MPEG == subtype) {
                    Intent intentToMusic = new Intent(MyStudyProgressAct.this, TrainVideoPlayerAct.class);
                    Bundle bu = new Bundle();
                    bu.putSerializable("train", train);
                    intentToMusic.putExtra("train", bu);
                    startActivity(intentToMusic);
                    // 返回html格式
                } else if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("train", train);
                    intent.putExtra("train", bundle);
                    String url = train.url + "&uid="
                            + ((AppApplication) getApplicationContext())
                            .getUserInfo().getUserId();
                    intent.putExtra(BackWebActivity.VALUE_URL, url);
                    // 获取分类名
                    String title = SP.getStringSP(MyStudyProgressAct.this, SP.TRAINING, train.tag + "", "");
                    intent.putExtra(BackWebActivity.VALUE_TITLE, title);
                    intent.setClass(MyStudyProgressAct.this, BackWebActivity.class);
                    BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
                    MyStudyProgressAct.this.startActivity(intent);
                } else if (Constant.MWKG_FORAMT_TYPE_MP3 == subtype) {
                    Intent intentToMusic = new Intent(MyStudyProgressAct.this, TrainMusicActivity.class);
                    Bundle bu = new Bundle();
                    bu.putSerializable("train", train);
                    intentToMusic.putExtra("train", bu);
                    startActivity(intentToMusic);
                } else {
                    return;
                }
                trainAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     *
     * @param train
     */
    private void toPdfViewer(Train train) {
        if (!MyStudyProgressAct.this.isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("train", train);
                intent.putExtra("train", bundle);
                intent.setClass(MyStudyProgressAct.this, PDFViewerActivity.class);
                startActivity(intent);
                // 设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }
    }

    private void toPDFAndWeb(Train train) {
        /*** 判断api,太小用web **/
        /*** 判断是pdf还是web **/
        if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
            // 声明pdf文件要保存的路径
            if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
                ToastUtil.showToast(MyStudyProgressAct.this, R.string.train_sd_size_);
                return;
            }
            String path = FileUtils.getTrainCacheDir(MyStudyProgressAct.this) + train.rid + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                file.delete();
                // 显示对话框
                dialog = loadPro(loadDialog).show();
                if (NetUtils.isNetConnected(MyStudyProgressAct.this)) {
                    // 开启线程
                    new DownloadThread(train).start();
                } else {
                    if (dialog != null && dialog.isShowing()
                            && !((Activity) MyStudyProgressAct.this).isFinishing()) {
                        // 关闭进度条对话框
                        dialog.dismiss();
                    }
                    ToastUtil.showToast(MyStudyProgressAct.this, R.string.error_service);
                }
            } else {
                // pdf文件已存在 调用
                toPdfViewer(train);
            }
        } else {// web
            Intent intent = new Intent(MyStudyProgressAct.this, PDFViewerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            intent.putExtra("train", bundle);
            //intent.putExtra(TrainActivity.KEY_webView_pdf, TrainActivity.KEY_webView_pdf);
            startActivity(intent);
        }
    }

    /**
     * 功能描述:初始化AlertDialog的布局 初始化progerssBar
     *
     * @param dialog
     * @return
     */
    public AlertDialog.Builder loadPro(AlertDialog.Builder dialog) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(MyStudyProgressAct.this);
        }

        View loadView = new View(MyStudyProgressAct.this);
        // 对话框加载布局文件
        loadView = LayoutInflater.from(MyStudyProgressAct.this).inflate(
                R.layout.load_progerss_layout, null);
        pro = (ProgressBar) loadView.findViewById(R.id.load_progressBar);
        pro.setProgress(0);
        dialog.setView(loadView);
        return dialog;
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 接受线程中传递的消息
            int statu = msg.what;
            Bundle bundle = msg.getData();
            Train train = (Train) bundle.getSerializable("train");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (train != null) {
                if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                    path = FileUtils.getTrainCacheDir(MyStudyProgressAct.this) + train.rid + ".pdf";
                } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.subtype) {
                    path = FileUtils.getTrainCacheDir(MyStudyProgressAct.this) + train.rid + ".mp4";
                }
                if (path == null || path.equals("")) {
                    return;
                }
                File file = new File(path);
                if (statu == -1 || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(MyStudyProgressAct.this,
                            R.string.train_result_download_fail);
                } else {
                    if (statu != 0) {
                        ToastUtil.showToast(MyStudyProgressAct.this,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                        // 下载完成 调用
                        toPdfViewer(train);
                    }
                }
            }
            switch (msg.what) {
			/*case TrainActivity.REFRESH_EXAM_LV:
				if (Constant.setAdapterRefresh) {
					// 刷新 listview
					pullToRefreshListView.setRefreshing();
					Constant.setAdapterRefresh = false;
				} 
				break;
			case TrainActivity.PROGRESS_MAX:
				if (pro != null) {
					pro.setMax((int) msg.obj);
				}
				break;
			case TrainActivity.PROGRESS_CHANGE:
				// 设置进度条改变
				if (pro != null) {
					pro.setProgress((int) msg.obj);
				}
				break;
			case TrainActivity.PROGRESS_FINISH:
				if (dialog != null && dialog.isShowing()
						&& !((Activity) MyStudyProgressAct.this).isFinishing()) {
					// 关闭进度条对话框
					dialog.dismiss();
				}
				break;
*/
                default:
                    break;
            }

        }
    };

    /**
     * 类: <code> DownloadThread </code> 功能描述: 下载pdf文件的线程 创建人:董奇 创建日期: 2014年7月16日
     * 上午9:30:29 开发环境: JDK7.0
     */
    class DownloadThread extends Thread {
        private Train train;
        private String path;

        public DownloadThread(Train train) {
            super();
            this.train = train;
        }

        @Override
        public void run() {
            super.run();
            if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                path = FileUtils.getTrainCacheDir(MyStudyProgressAct.this) + train.rid + ".pdf";
            }
            if (path == null || path.equals("")) {
                return;
            }
            // 通过url下载pdf文件
            int statu = HttpDownloader.downFile(train.url
                            + "&uid="
                            + ((AppApplication) getApplicationContext())
                            .getUserInfo().getUserId(), path,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(handler, TrainActivity.PROGRESS_CHANGE,
                                    downloadSize).sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(handler, TrainActivity.PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            // 下载成功,向handler传递消息
            Message msg = new Message();
            msg.what = statu;
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
