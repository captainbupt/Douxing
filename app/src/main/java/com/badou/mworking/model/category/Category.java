package com.badou.mworking.model.category;

import android.content.Context;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 功能描述:  通知公告，在线考试，微培训，任务签到分类信息
 */
public abstract class Category implements Serializable {

    public static final int CATEGORY_NOTICE = 0;     //通知公告
    public static final int CATEGORY_TRAINING = 1;   //微培训
    public static final int CATEGORY_EXAM = 2;        //在线考试
    public static final int CATEGORY_TASK = 3;        //任务签到
    public static final int CATEGORY_SHELF = 4;   //橱窗

    public static final String[] CATEGORY_KEY_NAMES = new String[]{"notice", "training", "exam", "task", "shelf"};
    public static final String[] CATEGORY_KEY_UNREADS = new String[]{"noticeUnreadNum", "trainUnreadNum", "examUnreadNum", "taskUnreadNum", "shelfUnreadNum"};
    public static final String[] CATEGORY_KEY_ICONS = new String[]{RequestParameters.CHK_UPDATA_PIC_NOTICE, RequestParameters.CHK_UPDATA_PIC_TRAINING, RequestParameters.CHK_UPDATA_PIC_EXAM, RequestParameters.CHK_UPDATA_PIC_TASK, RequestParameters.CHK_UPDATA_PIC_SHELF};

    public int tag; // 类别id
    public String subject; // 标题
    public String rid; // 资源唯一标识 主键id ，资源id
    public String department;// 部门
    public boolean isTop; // 是否置顶 top 默认为0，1表示置顶
    public long time; // 发布时间
    public String url; // 资源url 对应不同类型
    public int subtype; // 资源类型
    public boolean isRead; // 是否完成
    public boolean isStore;

    public Category() {
    }

    public Category(Context context, JSONObject jsonObject) {
        this.rid = jsonObject.optString(ResponseParameters.CATEGORY_RID);
        this.subject = jsonObject.optString(ResponseParameters.CATEGORY_SUBJECT);
        this.department = jsonObject.optString(ResponseParameters.CATEGORY_DEPARTMENT);
        this.time = jsonObject.optLong(ResponseParameters.CATEGORY_TIME) * 1000;
        this.isTop = jsonObject.optInt(ResponseParameters.CATEGORY_TOP) == Constant.TOP_YES;
        this.tag = jsonObject.optInt(ResponseParameters.CATEGORY_TAG);
        this.url = jsonObject.optString(ResponseParameters.CATEGORY_URL) + "&uid=" + ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
        this.subtype = jsonObject.optInt(ResponseParameters.CATEGORY_SUBTYPE);
        // 为空的时候为已完成
        this.isRead = jsonObject.optInt(ResponseParameters.CATEGORY_UNREAD, 1) == Constant.FINISH_YES;
        this.isStore = jsonObject.optBoolean("store");
    }

    public abstract int getCategoryType();

    public abstract String getCategoryKeyName();

    public abstract String getCategoryKeyUnread();

    public boolean isAvailable() {
        return !isRead;
    }

    public String getClassificationName(Context context) {
        return getClassificationName(context, getCategoryType(), tag);
    }

    public static String getClassificationName(Context context, int type, int tag) {
        return SP.getStringSP(context, CATEGORY_KEY_NAMES[type], tag + "", "");
    }

    public String getCategoryName(Context context) {
        int type = getCategoryType();
        if (type == CATEGORY_NOTICE) {
            return MainIcon.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_NOTICE).name;
        } else if (type == CATEGORY_EXAM) {
            return MainIcon.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_EXAM).name;
        } else if (type == CATEGORY_TASK) {
            return MainIcon.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TASK).name;
        } else if (type == CATEGORY_TRAINING) {
            return MainIcon.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TRAINING).name;
        } else if (type == CATEGORY_SHELF) {
            return MainIcon.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_SHELF).name;
        }
        return null;
    }

    public static Category getCategoryFromDetail(CategoryDetail categoryDetail) {
        if (categoryDetail.type == CATEGORY_NOTICE) {
            Notice notice = new Notice();
            notice.subject = categoryDetail.subject;
            notice.rid = categoryDetail.rid;
            notice.commentNumber = categoryDetail.commentNum;
            notice.url = categoryDetail.url;
            notice.subtype = categoryDetail.format;
            notice.isRead = categoryDetail.isSign;
            notice.isStore = categoryDetail.isStore;
            return notice;
        } else if (categoryDetail.type == CATEGORY_TRAINING || categoryDetail.type == CATEGORY_SHELF) {
            Train train;
            if (categoryDetail.type == CATEGORY_TRAINING) {
                train = new Train(true);
            } else {
                train = new Train(false);
            }
            train.subject = categoryDetail.subject;
            train.rid = categoryDetail.rid;
            train.commentNum = categoryDetail.commentNum;
            train.ecnt = categoryDetail.ratingNum;
            train.eval = categoryDetail.ratingTotal;
            train.rating = categoryDetail.rating;
            train.url = categoryDetail.url;
            train.subtype = categoryDetail.format;
            train.isRead = categoryDetail.isSign;
            train.isStore = categoryDetail.isStore;
            return train;
        } else if (categoryDetail.type == CATEGORY_EXAM) {
            Exam exam = new Exam();
            exam.subject = categoryDetail.subject;
            exam.rid = categoryDetail.rid;
            exam.isRead = categoryDetail.isSign;
            exam.isStore = categoryDetail.isStore;
            exam.subtype = categoryDetail.format;
            return exam;
        } else if (categoryDetail.type == CATEGORY_TASK) {
            Task task = categoryDetail.task;
            return task;
        } else {
            return null;
        }
    }
}
