package com.badou.mworking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.GridView;

import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.BannerAdapter;
import com.badou.mworking.adapter.MainGridAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.MainBanner;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BannerGallery;
import com.badou.mworking.widget.SearchLinearView;
import com.badou.mworking.widget.TopFadeScrollView;

import org.holoeverywhere.widget.RadioButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 主页面
 */
public class MainGridActivity extends BaseNoTitleActivity {

    private long mExitTime = 0; // 记录系统时间
    private MainGridAdapter mMainGridAdapter;

    private GridView mMainGridView;

    private ImageView mIconTopImageView; // 企业log imageview
    /**
     * 保存banner的adapter
     */
    private BannerAdapter mBannerAdapter;
    /**
     * 显示bannner*
     */
    private BannerGallery mBannerGallery = null;
    /**
     * 小原点*
     */
    private RadioGroup mIndicatorRadioGroup;
    private List<RadioButton> mIndicatorRadioButtonList;

    private TopFadeScrollView mScrollView;

    private ImageView mUserCentertImageView;    //logo 布局左边图标，点击进入个人中心
    private ImageView mSearchImageView;    //logo 布局右边图标，点击进入搜索页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grid);
        initView();
        initListener();
        initData();
        initJPush();
        int isNewUser;
        isNewUser = SP.getIntSP(mContext, SP.DEFAULTCACHE, ResponseParams.EXPER_IS_NEW_USER, 0);
        if (1 == isNewUser) {
            new AlertDialog.Builder(MainGridActivity.this).setTitle("欢迎回来!").setMessage(getResources().getString(R.string.main_tips_olduser)).setPositiveButton("确定", null).show();
            SP.putIntSP(mContext, SP.DEFAULTCACHE, ResponseParams.EXPER_IS_NEW_USER, 0);
        }

        // 调用缓存中的企业logoUrl图片，这样断网的情况也会显示出来了，如果本地没有的话，网络获取
        String logoUrl = SP.getStringSP(this, SP.DEFAULTCACHE, "logoUrl", "");
        initCompanyLog(logoUrl);
        checkUpdate();

        AlarmUtil alarmUtil = new AlarmUtil();
        alarmUtil.OpenTimer(this);

    }

    private void initJPush() {

        JPushInterface.init(getApplicationContext());
        //push 推送默认开启，如果用户关闭掉推送的话，在这里停掉推送
        Boolean isOpenPush = SP.getBooleanSP(this, SP.DEFAULTCACHE, Constant.PUSH_NOTIFICATIONS, true);
        if (!isOpenPush) {
            JPushInterface.stopPush(getApplicationContext());
        }
    }


    protected void initView() {
        mUserCentertImageView = (ImageView) findViewById(R.id.iv_actionbar_left);
        mSearchImageView = (ImageView) findViewById(R.id.iv_actionbar_right);
        mIconTopImageView = (ImageView) findViewById(R.id.iv_actionbar_logo);
        mMainGridView = (GridView) findViewById(R.id.gv_main_grid_second);
        mIndicatorRadioGroup = (RadioGroup) findViewById(R.id.rg_main_focus_indicator_container);
        mBannerGallery = (BannerGallery) findViewById(R.id.gallery_main_banner);
        int height = AppApplication.getScreenWidth(mContext) * 400 / 720; // 按屏幕宽度，计算banner高度
        mBannerGallery.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
        mScrollView = (TopFadeScrollView) findViewById(R.id.tfsv_main_grid);
        mScrollView.setTopViewId(R.id.rl_main_grid_banner);
    }

    /**
     * 功能描述:设置view的监听
     */
    protected void initListener() {
        mMainGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                MainIcon mainIcon = (MainIcon) mMainGridAdapter.getItem(arg2);
                switch (mainIcon.mainIconId) {
                    case RequestParams.CHK_UPDATA_PIC_NOTICE: // 通知公告
                        intent.setClass(mContext, NoticeActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_TRAIN: // 微培训
                        intent.setClass(mContext, TrainActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_EXAM: // 在线考试
                        intent.setClass(mContext, ExamActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_SURVEY: // 培训调研
                        String uid = ((AppApplication) getApplicationContext())
                                .getUserInfo().getUserId();
                        String url = Net.getWeiDiaoYanURl() + uid;
                        intent.setClass(mContext, BackWebActivity.class);
                        intent.putExtra(BackWebActivity.VALUE_URL, url);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_TASK: // 任务签到
                        intent.setClass(mContext, TaskActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_CHATTER: // 同事圈
                        intent.setClass(mContext, AroundActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_ASK: //问答
                        intent.setClass(mContext, WenDActivity.class);
                        break;
                    case RequestParams.CHK_UPDATA_PIC_SHELF: //问答
                        intent.setClass(mContext, TrainActivity.class);
                        break;
                }
                intent.putExtra(BaseActionBarActivity.KEY_TITLE, mainIcon.name);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        mUserCentertImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                intent.putExtra(BaseActionBarActivity.KEY_TITLE, getResources().getString(R.string.title_name_user_center));
                startActivity(intent);
            }
        });
        mSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchLinearView searchView = new SearchLinearView(mContext);
                FrameLayout container = (FrameLayout) findViewById(R.id.fm_main_grid_container);
                container.addView(searchView);
            }
        });
    }

    protected void initData() {
        // 取出SP中的banner内容
        mBannerAdapter = new BannerAdapter(mContext);
        List<Object> mBannerList = new ArrayList<>();
        String bannerStr = SP.getStringSP(this, SP.DEFAULTCACHE, "banner", "");
        if (bannerStr != null && !bannerStr.equals("")) {
            String[] bannerInfos = bannerStr.split(",");
            for (String string : bannerInfos) {
                String[] bannerInfo = string.split("@");
                MainBanner mainBanner = new MainBanner(bannerInfo[0],
                        bannerInfo[1], bannerInfo[2]);
                mBannerList.add(mainBanner);
            }
        }
        mBannerGallery.setAdapter(mBannerAdapter);
        updateIndicator(mBannerList);
        updateBanner(mBannerList);
        mMainGridAdapter = new MainGridAdapter(mContext, getMainIconList());
        mMainGridView.setAdapter(mMainGridAdapter);
        mScrollView.scrollTo(0, 0);
    }

    /**
     * 功能描述: 更新显示的banner
     */
    private void updateBanner(final List<Object> bList) {
        mBannerAdapter.setList(bList);
        mBannerGallery.setFocusable(true);
        if (bList.size() <= 0) { // 如果没有banner，则取消点击事件
            mBannerGallery.setOnItemSelectedListener(null);
            mBannerGallery.setOnItemClickListener(null);
            return;
        }
        mBannerGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selIndex, long arg3) {
                selIndex = selIndex % bList.size();
                mIndicatorRadioButtonList.get(selIndex).setChecked(true);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                mIndicatorRadioButtonList.get(0).setChecked(true);
            }
        });
        /**
         * 点击跳转到webview页
         */
        mBannerGallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = position % bList.size();
                Intent intent = new Intent(mContext, BackWebActivity.class);
                intent.putExtra(BackWebActivity.VALUE_URL, ((MainBanner) bList.get(pos)).getBannerContentURL() + "");
                BackWebActivity.PAGEFLAG = BackWebActivity.BANNER;
                startActivity(intent);
            }
        });
    }

    /**
     * 功能描述: 定义底部滑动的小点
     */
    private void updateIndicator(List<Object> bannerList) {
        this.mIndicatorRadioGroup.removeAllViews();
        if (bannerList == null || bannerList.size() <= 0) {
            return;
        }
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size_main_grid_rb);
        mIndicatorRadioButtonList = new ArrayList<>();
        for (int i = 0; i < bannerList.size(); i++) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setId(i);
            RadioGroup.LayoutParams localLayoutParams = new RadioGroup.LayoutParams(
                    size, size);
            localLayoutParams.setMargins(size / 2, 0, size / 2, 0);
            radioButton.setLayoutParams(localLayoutParams);
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton
                    .setBackgroundResource(R.drawable.background_rb_welcome);
            mIndicatorRadioButtonList.add(radioButton);
            this.mIndicatorRadioGroup.addView(radioButton);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMainGridAdapter != null) {
            mMainGridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 功能描述: 点击两次返回键退出应用程序，通过记录按键时间计算时间差实现
     */
    @Override
    public void onBackPressed() {
        // 应为系统当前的系统毫秒数一定小于2000
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.showToast(mContext, R.string.main_exit_tips);
            mExitTime = System.currentTimeMillis();
        } else {
            AppManager.getAppManager().AppExit(this, false);
        }
    }

    /**
     * 功能描述:网络请求更新资源包，         这里是上传MD5来进行匹配，应为本地icon图片已经缓存，每次上传null，会把完整信息请求下来，
     * 如果url匹配，不会再下载图片内容
     */
    private void checkUpdate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RequestParams.CHK_UPDATA_PIC_COMPANY_LOGO, "");
            jsonObject.put(RequestParams.CHK_UPDATA_BANNER, "");
            jsonObject.put(RequestParams.CHK_UPDATA_PIC_NEWVER, "");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        /**
         * 发起请求
         */
        ServiceProvider.doCheckUpdate(mContext, jsonObject, new VolleyListener(
                mContext) {

            @Override
            public void onResponse(Object responseObject) {
                List<Object> list = new ArrayList<>();
                JSONObject response = (JSONObject) responseObject;
                try {
                    int code = response.optInt(Net.CODE);
                    if (code == Net.LOGOUT) {
                        AppApplication.logoutShow(mContext);
                        return;
                    }
                    if (code != Net.SUCCESS) {
                        return;
                    }
                    JSONObject data = response.optJSONObject(Net.DATA);
                    apkUpdate(data);

                    JSONObject jSONObject = data
                            .optJSONObject(RequestParams.CHK_UPDATA_PIC_COMPANY_LOGO);
                    if (jSONObject != null) {
                        String logoUrl = jSONObject
                                .optString(MainBanner.CHK_URL);
                        SP.putSP(MainGridActivity.this, SP.DEFAULTCACHE, "logoUrl", logoUrl);
                        initCompanyLog(logoUrl);
                    }

                    JSONArray arrBanner = data.getJSONArray("banner");

                    String bannerInfo = "";

                    for (int i = 0; i < arrBanner.length(); i++) {
                        JSONObject jo = (JSONObject) arrBanner.get(i);
                        String img = jo.optString(MTrainingDBHelper.CHK_IMG);
                        String url = jo.optString(MainBanner.CHK_URL);
                        String md5 = jo.optString(MainBanner.CHK_RES_MD5);
                        MainBanner banner = new MainBanner(img, url, md5);
                        list.add(banner);
                        bannerInfo = bannerInfo
                                + banner.bannerToString(img, url, md5);
                    }
                    updateBanner(list);
                    // 保存banner信息数据到sp
                    SP.putStringSP(MainGridActivity.this, SP.DEFAULTCACHE, "banner", bannerInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    /**
     * 在主页验证是否有软件更新
     */
    private void apkUpdate(JSONObject dataJson) {
        JSONObject newVerjson = dataJson
                .optJSONObject(RequestParams.CHK_UPDATA_PIC_NEWVER);
        boolean hasNew = newVerjson.optInt(ResponseParams.CHECKUPDATE_NEW) == 1;
        if (hasNew) {
            final String info = newVerjson
                    .optString(ResponseParams.CHECKUPDATE_INFO);
            final String url = newVerjson
                    .optString(ResponseParams.CHECKUPDATE_URL);
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.main_tips_update_title)
                    .setMessage(info)
                    .setPositiveButton(R.string.main_tips_update_btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    ServiceProvider.doUpdateMTraning(mActivity,
                                            url);
                                }
                            }).setNegativeButton(R.string.text_cancel, null)
                    .create().show();
        }
    }

    /**
     * 功能描述: 初始化企业logo布局
     */
    private void initCompanyLog(String logoUrl) {
        Bitmap logBmp = BitmapLruCache.getBitmapLruCache().get(logoUrl);
        if (logBmp != null && logBmp.isRecycled()) {
            mIconTopImageView.setImageBitmap(logBmp);
        } else {
            MyVolley.getImageLoader().get(
                    logoUrl,
                    new IconLoadListener(mContext, mIconTopImageView, logoUrl,
                            R.drawable.logo));
        }
    }

    /**
     * 功能描述:初始化MainIcon的数据
     */
    private List<Object> getMainIconList() {
        List<Object> mainIconList = new ArrayList<>();

        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_NOTICE, R.drawable.button_notice, R.string.module_default_title_notice));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_TRAIN, R.drawable.button_training, R.string.module_default_title_training));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_EXAM, R.drawable.button_exam, R.string.module_default_title_exam));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_TASK, R.drawable.button_task, R.string.module_default_title_task));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_SURVEY, R.drawable.button_survey, R.string.module_default_title_survey));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_CHATTER, R.drawable.button_chatter, R.string.module_default_title_chatter));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_SHELF, R.drawable.button_shelf, R.string.module_default_title_shelf));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_ASK, R.drawable.button_ask, R.string.module_default_title_ask));

        /**
         * 权限， 设置隐藏显示
         * @param access 后台返回的十进制权限制
         */
        int access = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().getAccess();

        char[] accessArray = Integer.toBinaryString(access).toCharArray();
        for (int i = accessArray.length - 1; i >= 0; i--) {
            if (accessArray[i] == '0')
                mainIconList.remove(i);
        }

        Collections.sort(mainIconList, new Comparator<Object>() {
            @Override
            public int compare(Object t1, Object t2) {
                return Integer.valueOf(((MainIcon) t1).priority) < Integer.valueOf(((MainIcon) t2).priority) ? 1 : -1;
            }
        }); //对list进行排序
        return mainIconList;
    }

    /**
     * @param key               icon键值
     * @param resId             本地图片
     * @param defaultTitleResId 默认名称
     */
    private MainIcon getMainIcon(String key, int resId, int defaultTitleResId) {
        JSONObject mainIconJSONObject = getMainIconJSONObject(key);
        String title = mainIconJSONObject.optString("name");
        String priority = mainIconJSONObject.optString("priority");
        if (TextUtils.isEmpty(title)) {
            title = mContext.getResources().getString(defaultTitleResId);
        }
        return new MainIcon(key, resId, title, priority);
    }

    /**
     * 功能描述: 更新数据库中mainIcon的name 字段和 priority 字段
     */
    private JSONObject getMainIconJSONObject(String key) {
        String shuffleStr = SP.getStringSP(mContext, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, "");
        if (TextUtils.isEmpty(shuffleStr)) {
            return null;
        }
        try {
            JSONObject shuffle = new JSONObject(shuffleStr);
            Iterator it = shuffle.keys();
            while (it.hasNext()) {
                String IconKey = (String) it.next();
                if (key.equals(IconKey)) {
                    return shuffle.optJSONObject(IconKey);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
