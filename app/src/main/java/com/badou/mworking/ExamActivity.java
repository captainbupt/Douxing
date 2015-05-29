package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Exam;
import com.badou.mworking.net.Net;
import com.badou.mworking.receiver.JPushReceiver;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * ExamActivity 考试页面
 */
public class ExamActivity extends BaseProgressListActivity {


    public static String CLASSIFICATIONNAME = "";    // 试题分类名称

    public static String examRid = "";    //考试资源id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Exam.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Exam.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(savedInstanceState);
        try {
            ExamActivity.examRid = "";       //先清空ExamActivity.examRid对象ExamActivity
            // 如果8点提醒点击进入的话，这里会报空，应为极光推送没有收到内容，在这里做个异常捕获
            String JPushBundle = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
            if (JPushBundle != null) {
                JSONObject extraJson = new JSONObject(JPushBundle);
                ExamActivity.examRid = extraJson.getString(JPushReceiver.TYPE_ADD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new ExamAdapter(mContext,false, false);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Exam(jsonObject);
    }

    @Override
    protected void onItemClick(int position) {
        BackWebActivity.PAGEFLAG = BackWebActivity.EXAM;
        Exam exam = (Exam) mCategoryAdapter.getItem(position - 1);
        int subtype = exam.subtype;
        if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
            return;
        }
        // 考试没有联网
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showNetExc(mContext);
            return;
        }
        String uid = ((AppApplication) getApplicationContext()).getUserInfo().userId;
        String url = Net.getRunHost(mContext) + Net.EXAM_ITEM(uid, exam.rid);
        Intent intents = new Intent(mContext, BackWebActivity.class);
        intents.putExtra(BackWebActivity.VALUE_URL, url);
        intents.putExtra(BackWebActivity.ISSHOWTONGJI, true);
        int tag = exam.tag;
        String title = "";
        if (tag >= 0) {
            // 获取分类名
            title = SP.getStringSP(mContext, SP.EXAM, tag + "", "");
        } else {
            title = ExamActivity.CLASSIFICATIONNAME;
        }
        intents.putExtra(BackWebActivity.VALUE_TITLE, title);
        startActivity(intents);
    }

    @Override
    public void clickRight() {
        // tag 值大于 0 ，  代表在线考试，点击跳入搜索，    tag<0, 代表 等级考试， 点击跳入等级考试页面，  tag = 0 表示全部
        if (tag >= 0) {
        } else {
            Intent inten = new Intent(mContext, MyRatingActivity.class);
            startActivity(inten);
        }
    }
}

