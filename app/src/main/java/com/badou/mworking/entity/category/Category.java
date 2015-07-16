package com.badou.mworking.entity.category;

import android.content.Context;

import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.SPHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.lang.reflect.Type;

/**
 * 功能描述:  通知公告，在线考试，微培训，任务签到分类信息
 */
public abstract class Category implements Serializable {

    public static final int CATEGORY_NOTICE = 0;     //通知公告
    public static final int CATEGORY_TRAINING = 1;   //微培训
    public static final int CATEGORY_EXAM = 2;        //在线考试
    public static final int CATEGORY_TASK = 3;        //任务签到
    public static final int CATEGORY_SHELF = 4;   //橱窗
    public static final int CATEGORY_ENTRY = 5;   //橱窗

    public static final String[] CATEGORY_KEY_NAMES = new String[]{"notice", "training", "exam", "task", "shelf", "entry"};
    public static final String[] CATEGORY_KEY_UNREADS = new String[]{"noticeUnreadNum", "trainUnreadNum", "examUnreadNum", "taskUnreadNum", "shelfUnreadNum", "entryUnreadNum"};
    public static final String[] CATEGORY_KEY_ICONS = new String[]{RequestParameters.CHK_UPDATA_PIC_NOTICE, RequestParameters.CHK_UPDATA_PIC_TRAINING, RequestParameters.CHK_UPDATA_PIC_EXAM, RequestParameters.CHK_UPDATA_PIC_TASK, RequestParameters.CHK_UPDATA_PIC_SHELF, RequestParameters.CHK_UPDATA_PIC_ENTRY};
    public static final Class[] CATEGORY_KEY_CLASSES = new Class[]{Notice.class, Train.class, Exam.class, Task.class, Train.class, Entry.class};
    public static final Type[] CATEGORY_KEY_TYPES = new Type[]{new TypeToken<List<Notice>>() {
    }.getType(), new TypeToken<List<Train>>() {
    }.getType(), new TypeToken<List<Exam>>() {
    }.getType(), new TypeToken<List<Task>>() {
    }.getType(), new TypeToken<List<Train>>() {
    }.getType(), new TypeToken<List<Entry>>() {
    }.getType()};

    @Expose
    int offline; // 过期
    @Expose
    String link_to;
    @Expose
    String ts; // 发布时间
    @Expose
    int read;  // 是否完成
    @Expose
    String rid; // 资源唯一标识 主键id ，资源id
    @Expose
    String tag; // 类别id
    @Expose
    String top; // 是否置顶 top 默认为0，1表示置顶
    @Expose
    String type; // 通知考试培训签到橱窗
    @Expose
    String department; // 部门
    @Expose
    String subject; // 标题
    @Expose
    String url; // 资源url 对应不同类型
    @Expose
    int subtype; // 资源类型
    @Expose
    String img;  // 图片地址
    @Expose
    boolean store;  // 是否收藏

    public Category() {
    }

    public Category(Context context, JSONObject jsonObject) {

    }

    public abstract int getCategoryType();

    public boolean isUnread() {
        return read == 0;
    }

    public boolean isOffline() {
        return offline == 1;
    }

    public boolean isTop() {
        return Integer.parseInt(top) == 1;
    }

    // 服务器的时间以秒为单位，需要换成毫秒
    public long getTime() {
        return Long.parseLong(ts) * 1000;
    }

    public String getRid() {
        return rid;
    }

    public String getTop() {
        return top;
    }

    public String getSubject() {
        return subject;
    }

    public String getUrl() {
        return url;
    }

    public int getSubtype() {
        return subtype;
    }

    public String getImg() {
        return img;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public void setRead(boolean isRead) {
        this.read = isRead ? 1 : 0;
    }

    public String getClassificationName() {
        return getClassificationName(getCategoryType(), Integer.parseInt(tag));
    }

    public static String getClassificationName(int type, int tag) {
        List<Classification> classifications = SPHelper.getClassification(CATEGORY_KEY_NAMES[type]);
        for (Classification classification : classifications) {
            if (classification.getTag() == tag) {
                return classification.getName();
            }
            if (classification.getSon() != null && classification.getSon().size() > 0) {
                for (Classification son : classification.getSon()) {
                    if (son.getTag() == tag) {
                        return son.getName();
                    }
                }
            }
        }
        return "";
    }

    public static String getCategoryName(Context context, int type) {
        Shuffle shuffle = UserInfo.getUserInfo().getShuffle();
        if (type >= 0 && type < CATEGORY_KEY_ICONS.length) {
            return shuffle.getMainIcon(context, CATEGORY_KEY_ICONS[type]).getName();
        }
        return "";
    }

    public abstract void updateData(CategoryDetail categoryDetail);
}
