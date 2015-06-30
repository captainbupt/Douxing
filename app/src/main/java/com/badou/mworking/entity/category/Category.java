package com.badou.mworking.entity.category;

import android.content.Context;

import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
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
    public int top; // 是否置顶 top 默认为0，1表示置顶
    public long time; // 发布时间
    public String url; // 资源url 对应不同类型
    public int subtype; // 资源类型
    public int read; // 是否完成

    public Category(JSONObject jsonObject) {
        this.rid = jsonObject.optString(ResponseParameters.CATEGORY_RID);
        this.subject = jsonObject.optString(ResponseParameters.CATEGORY_SUBJECT);
        this.department = jsonObject
                .optString(ResponseParameters.CATEGORY_DEPARTMENT);
        this.time = jsonObject.optLong(ResponseParameters.CATEGORY_TIME) * 1000;
        this.top = jsonObject.optInt(ResponseParameters.CATEGORY_TOP);
        this.tag = jsonObject
                .optInt(ResponseParameters.CATEGORY_TAG);
        this.url = jsonObject.optString(ResponseParameters.CATEGORY_URL);
        this.subtype = jsonObject.optInt(ResponseParameters.CATEGORY_SUBTYPE);
        // 为空的时候为已完成
        this.read = jsonObject.optInt(ResponseParameters.CATEGORY_UNREAD, 1);
    }

    public abstract int getCategoryType();

    public abstract String getCategoryKeyName();

    public abstract String getCategoryKeyUnread();

    public boolean isRead() {
        return read == Constant.FINISH_YES;
    }

    public boolean isTop() {
        return top == Constant.TOP_YES;
    }

    public String getClassificationName(Context context) {
        return getClassificationName(context, getCategoryType(), tag);
    }

    public static String getClassificationName(Context context, int type, int tag) {
        return SP.getStringSP(context, CATEGORY_KEY_NAMES[type], tag + "", "");
    }

    public String getCategoryName(Context context) {
        Shuffle shuffle = UserInfo.getUserInfo().getShuffle();
        int type = getCategoryType();
        if (type == CATEGORY_NOTICE) {
            return shuffle.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_NOTICE).getName();
        } else if (type == CATEGORY_EXAM) {
            return shuffle.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_EXAM).getName();
        } else if (type == CATEGORY_TASK) {
            return shuffle.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TASK).getName();
        } else if (type == CATEGORY_TRAINING) {
            return shuffle.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TRAINING).getName();
        } else if (type == CATEGORY_SHELF) {
            return shuffle.getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_SHELF).getName();
        }
        return null;
    }

}
