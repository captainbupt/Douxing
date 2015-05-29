package com.badou.mworking.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.AroundDetailActivity;
import com.badou.mworking.AroundUserActivity;
import com.badou.mworking.BackWebActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.MyGroupAdapter;
import com.badou.mworking.adapter.MyGroupAdapter.OnAdapterItemListener;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Question;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类:  <code> MyGroupFragment </code>
 * 功能描述:  我的圈页面
 * 创建人:   葛建锋
 * 创建日期: 2014年12月25日 下午9:23:49
 * 开发环境: JDK6.0
 */
public class MyGroupFragment extends Fragment implements
        OnRefreshListener<ListView> {

    public static final String BUNDLE_MODE_USER_KEY = "mode_user";
    public static final int MODE_ALL = 201;
    public static final int MODE_USER = 202;

    private int mode = 0;
    private int currentPage = 1;// 当前页码
    private int clickPosition = -1;

    private boolean isFinish = false;
    private boolean isRefreshing = false;
    private boolean lvIsEnable = true;//listview 的 item 可以点击

    private String uid = "";     //用户id

    private MyGroupAdapter myGroupAdapter;
    private PullToRefreshListView pullToRefreshListView;
    private ProgressDialog mProgressDialog;
    private UserDetail userDetail;
    private Context mContext;
    private List<Question> asksList = new ArrayList<Question>();

    private TextView lvTv;

    private View headView;
    TextView headName;
    ImageView headImg;

    public MyGroupFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        lvIsEnable = true;
        if (Constant.is_del && !pullToRefreshListView.isRefreshing()) {
            try {
                Constant.is_del = false;
                if (clickPosition != -1) {
                    asksList.remove(clickPosition);
                    myGroupAdapter.setDatas(asksList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_group, null);
        initView(view);
        initListener();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (pullToRefreshListView != null && pullToRefreshListView.isRefreshing()) {
            pullToRefreshListView.onRefreshComplete();
        }
        isFinish = true;
        if (null != mProgressDialog && mContext != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {

        //获取传过来的uid
        try {
            uid = getActivity().getIntent().getExtras().getString("uid");
            if (TextUtils.isEmpty(uid)) {
                uid = ((AppApplication) mContext.getApplicationContext())
                        .getUserInfo().userId;
            }
        } catch (NullPointerException e) {
            uid = ((AppApplication) mContext.getApplicationContext())
                    .getUserInfo().userId;
            e.printStackTrace();
        }
        pullToRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pullListView);
        pullToRefreshListView.setMode(Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(this);
        headView = LayoutInflater.from(getActivity()).inflate(
                R.layout.base_around_top_layout, null);
        headName = (TextView) headView.findViewById(R.id.tv_user_center_top_name);
        headImg = (ImageView) headView.findViewById(R.id.iv_user_center_top_head);
        lvTv = (TextView) headView.findViewById(R.id.tv_user_center_top_level);

        ListView listview = pullToRefreshListView.getRefreshableView();
        listview.setDividerHeight(0);
        setHeadView();
        listview.addHeaderView(headView, null, false);
        setAdapterData();
        pullToRefreshListView.setAdapter(myGroupAdapter);
    }

    /**
     * 绑定监听
     */
    private void initListener() {
        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (lvIsEnable) {
                    //防止同一个item多次点击但是页面没跳转
                    lvIsEnable = false;
                    //(position - 2) 下拉刷新的view 和 顶部头像布局view
                    clickPosition = position - 2;
                    // 跳转到单条的Item的页面，并传递数据
                    Question question = myGroupAdapter.getItem(clickPosition);
                    if (userDetail != null && question != null) {
                        question.setImgUrl(userDetail.headimg + "");
                    }
                    Intent intent = new Intent(mContext, AroundDetailActivity.class);
                    intent.putExtra(AroundDetailActivity.VALUE_QUESTION, question);
                    mContext.startActivity(intent);
                    // 设置切换动画，从右边进入，左边退出
                    getActivity().overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);
                }

            }
        });
    }

    /**
     * 功能描述:更新数据
     */
    private void setAdapterData() {
        if (myGroupAdapter == null) {
            myGroupAdapter = new MyGroupAdapter(mContext, ((AroundUserActivity) getActivity()).qid);
        }

        if (myGroupAdapter.getCount() == 0) {
            pullToRefreshListView.setRefreshing();
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (isFinish) {
            return;
        }
        // 这里刷新listview数据,只加载第一页的数据
        if (refreshView.getCurrentMode() == Mode.PULL_FROM_START || 0 == currentPage) {
            // 这里刷新listview数据,只加载第一页的数据
            asksList.removeAll(asksList);
            updateDatas(1, uid);
            if (null == userDetail) {
                setHeadView();
            }
        } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
            updateDatas(currentPage + 1, uid);
        }
    }

    /**
     * 功能描述:滚动到最底加载更多
     */
    private void updateDatas(final int page, String uid) {
        String type;
        type = "qas";
        isRefreshing = true;
        // 发起网络请求
        ServiceProvider.doQuestionShareList(mContext, uid, type, page, TongSHQFragments.LOAD_PAGE_NUM, true,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        isRefreshing = false;
                        pullToRefreshListView.onRefreshComplete();
                        JSONObject response = (JSONObject) responseObject;
                        JSONObject contentObject = response
                                .optJSONObject(Net.DATA);
                        if (contentObject == null) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        // 加载到最后时 提示无更新
                        JSONArray resultArray = contentObject
                                .optJSONArray(Net.RESULT);
                        if (resultArray == null || resultArray.length() == 0) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        } else {
                            currentPage++;
                            // 新加载的内容添加到list
                            List<Question> asks = new ArrayList<Question>();
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jo2 = resultArray.optJSONObject(i);
                                if (jo2 == null) {
                                    return;
                                }
                                asks.add(new Question(jo2, mode));
                            }
                            asksList.addAll(asks);
                            if (page == 1) {// 页码为1 重新加载第一页
                                myGroupAdapter.setDatas(asks);
                                currentPage = 1;
                            } else {// 继续加载
                                myGroupAdapter.addDatas(asks);
                            }
                        }

                        myGroupAdapter
                                .setOnAdapterItemListener(new OnAdapterItemListener() {

                                    @Override
                                    public void deleteItem(final int pos,
                                                           final String qid) {
                                        new AlertDialog.Builder(mContext)
                                                .setTitle(R.string.myQuan_dialog_title_tips)
                                                .setMessage(
                                                        getActivity()
                                                                .getResources()
                                                                .getString(
                                                                        R.string.my_group_tishi))
                                                .setPositiveButton(R.string.myQuan_dialog_del_tips,
                                                        new OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface arg0,
                                                                    int arg1) {
                                                                deleteComment(
                                                                        pos,
                                                                        qid);
                                                            }
                                                        })
                                                .setNegativeButton(R.string.text_cancel, null)
                                                .show();

                                    }
                                });
                    }

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        super.onErrorResponse(arg0);
                        isRefreshing = false;
                        pullToRefreshListView.onRefreshComplete();
                    }
                });

    }

    /**
     * 功能描述:删除我的圈中的item
     *
     * @param pos
     * @param qid
     */
    private void deleteComment(final int pos, final String qid) {
        if (mProgressDialog == null)
            mProgressDialog = new WaitProgressDialog(mContext,
                    R.string.action_comment_update_ing);
        mProgressDialog.setTitle(R.string.action_comment_update_ing);
        if (null != mProgressDialog && mContext != null) {
            mProgressDialog.show();
        }

        ServiceProvider.doMyGroup_del(mContext, qid, new VolleyListener(
                mContext) {
            @Override
            public void onResponse(Object responseObject) {
                if (null != mProgressDialog && mContext != null) {
                    mProgressDialog.dismiss();
                }
                isRefreshing = false;
                pullToRefreshListView.onRefreshComplete();
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (responseObject == null) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (Net.SUCCESS != code) {
                    return;
                }
                myGroupAdapter.getDataList().remove(pos);
                myGroupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                super.onErrorResponse(arg0);
                if (null != mProgressDialog && mContext != null) {
                    mProgressDialog.dismiss();
                }
                isRefreshing = false;
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    /**
     * 给下拉刷新listview 添加个headview
     */
    private void setHeadView() {
        chankanLV();
        final int headWidth = getResources().getDimensionPixelSize(
                R.dimen.user_center_image_head_size);
        // 根据uid拿到用户头像的路径
        final String finalImgPath = mContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + uid + ".png";

        // 获取用户详情
        ServiceProvider.doOptainUserDetail(mContext, uid, new VolleyListener(
                mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (null != mProgressDialog && mContext != null
                        && !((Activity) mContext).isFinishing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Object arg0) {
                if (null != mProgressDialog && mContext != null
                        && !((Activity) mContext).isFinishing())
                    mProgressDialog.dismiss();
                JSONObject jsonObject = (JSONObject) arg0;
                int errcode = jsonObject.optInt(Net.CODE);
                if (errcode != 0) {
                    return;
                }
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(jsonObject.optString(Net.DATA));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject == null) {
                    return;
                }

                userDetail = new UserDetail(jObject);
                LVUtil.setTextViewBg(lvTv, userDetail.circle_lv);

                if (!TextUtils.isEmpty(userDetail.name)) {
                    headName.setText(userDetail.name);
                    if ((AroundUserActivity) getActivity() != null && !getActivity().isFinishing()) {
                        ((AroundUserActivity) getActivity()).setActionbarTitle(userDetail.name);
                    }
                }
                Bitmap headBmp = null;
                if (!TextUtils.isEmpty(userDetail.headimg))
                    headBmp = BitmapUtil.getCirlBitmp(
                            BitmapLruCache.getBitmapLruCache().getCircleBitmap(
                                    userDetail.headimg), headWidth,
                            headWidth);
                if (headBmp != null) {
                    headImg.setImageBitmap(headBmp);
                    headBmp = null;
                    return;
                }
                headBmp = BitmapUtil.getCirlBitmp(
                        getUserIconFromFile(finalImgPath, headWidth),
                        headWidth, headWidth);
                if (headBmp != null) {
                    headImg.setImageBitmap(headBmp);
                    BitmapLruCache.getBitmapLruCache().putCircleBitmap(
                            userDetail.headimg, headBmp);
                    return;
                } else {
                    MyVolley.getImageLoader().get(
                            userDetail.headimg,
                            new CircleImageListener(mContext, userDetail
                                    .headimg, headImg, headWidth,
                                    headWidth));
                }
            }
        });
    }

    /**
     * 功能描述: 从文件中获取用户头像
     *
     * @param path
     * @return
     */
    private Bitmap getUserIconFromFile(String path, int wh) {
        if (!FileUtils.hasSdcard()) {
            return null;
        }
        return BitmapUtil.decodeSampledBitmapFromFile(path, wh, wh);
    }

    /**
     * 功能描述: 等级查看
     */
    public void chankanLV() {
        if (lvTv != null) {
            lvTv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String userId = ((AppApplication) mContext.getApplicationContext())
                            .getUserInfo().userId;
                    Intent intent = new Intent(mContext, BackWebActivity.class);
                    intent.putExtra("title", "等级介绍");
                    intent.putExtra(BackWebActivity.VALUE_URL, Constant.LV_URL + userId);
                    startActivity(intent);
                }
            });
        }
    }
}
