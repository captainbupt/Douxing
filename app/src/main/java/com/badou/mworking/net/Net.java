package com.badou.mworking.net;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.util.SP;

/**
 * 功能描述:  接口工具类
 */
public class Net {

    public static final String HTTP_IP = "ip_address";  // Sp中保存的ip

    public static final String DATA = "data";
    public static final String CODE = "errcode";
    public static final int SUCCESS = 0;
    public static final int LOGOUT = 50002;

    public static final String FORGET_PASSWORD = "/faq.html#wjmm";
    public static final String FAQ = "/faq.html";

    public static String MARK_READ(String rid, String uid) {
        return "/markread?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&rid=" + rid;
    }

    public static String KNOWLEDGE_LIBIRARY(String uid) {
        return "/getlibrary?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid;
    }

    public static final String Http_Host_ip = "http://115.28.138.79";

    public static String getRunHost(Context context) {
        String ip = SP.getStringSP(context, SP.DEFAULTCACHE, Net.HTTP_IP, "");
        if ("".equals(ip)) {
            return Http_Host_ip + "/badou";
        } else {
            return ip + "/badou";
        }

    }

    /**
     * 1 .登录
     */
    public static String LOGIN = "/login?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;

    /**
     * 功能描述: 2. 发送短信获取验证码
     *
     * @return
     */
    public static String VERIFICATION_CODE() {
        return "/sendsms?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion;
    }

    /**
     * 3.忘记密码重置
     *
     * @return
     */
    public static String FORGET_PASS() {
        return "/rstpwd?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion;
    }

    /**
     * 功能描述: 4 体验账号登录
     *
     * @return
     */
    public static String EXPERIENCE() {
        return "/floatin?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion;
    }

    /**
     * 功能描述: 5  修改密码
     *
     * @return
     */
    public static String CHANGE_PASSWORD() {
        return "/chgpwd?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion;
    }

    /**
     * 6.崩溃日志
     *
     * @param appversion
     * @return
     */
    public static String SUBMIT_ERROR(String appversion) {
        return "/crashlog?sys=android" + AppApplication.SYSVERSION + "&ver="
                + appversion;
    }

    /**
     * 7.设置头像
     */
    public static String UPDATE_HEAD_ICON(String uid) {
        String iconString = "/setimg?sys=android" + AppApplication.SYSVERSION
                + "&ver=" + AppApplication.appVersion + "&uid=" + uid;
        return iconString;
    }

    /**
     * 8.用户反馈
     *
     * @param uid
     * @param type
     * @param content
     * @return
     */
    public static String SUBMIT_FEEDBACK(String uid, String type, String content) {
        return "/feedback?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&type=" + type
                + "&content=" + content;
    }

    /**
     * 9.获取分类列表
     *
     * @param uid
     * @return
     */
    public static String GET_TAG(String uid, String type) {
        return "/gettaglist?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&fmt=nest&type=" + type;
    }


    /**
     * 10.升级资源包
     *
     * @param uid
     * @return
     */
    public static String CHECK_UPDATE(String uid, String screen) {
        return "/chkupd?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&screen=" + screen;
    }

    /**
     * 11.同步云端数据
     *
     * @param uid
     * @param type
     * @param ts
     * @param tag
     * @return
     */
    public static String UPDATE_RESOURCES(String uid, String type, String ts,
                                          int tag) {
        return "/sync?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&type=" + type
                + "&ts=" + ts;
    }

    /**
     * 12.获取用户详情
     */
    public static String USER_DETAIL(String uid) {
        String userDetail = "/viewusr?sys=android" + AppApplication.SYSVERSION
                + "&ver=" + AppApplication.appVersion + "&uid=" + uid;
        return userDetail;
    }

    /**
     * 14.任务签到
     *
     * @param rid
     * @param uid
     * @return
     */
    public static String SIGN(String rid, String uid, String lat, String lon) {
        String taskSign = "/checkin_v2?sys=android" + AppApplication.SYSVERSION
                + "&ver=" + AppApplication.appVersion + "&rid=" + rid + "&uid="
                + uid + "&lat=" + lat + "&lon=" + lon;
        return taskSign;
    }

    /**
     * 15.课件点赞
     *
     * @param uid
     * @param rid
     * @param mark
     * @return
     */
    public static String SUMIT_TRAIN_MARK(String uid, String rid, int mark) {
        return "/mark?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&rid=" + rid
                + "&mark=" + mark;
    }

    /**
     * 17.课件评论
     *
     * @param uid
     * @param rid
     * @return
     */
    public static String SUBMIT_COMMENT(String uid, String rid) {
        return "/comment?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&rid=" + rid;
    }

    /**
     * 通知公告和微培训 回复某人的信息
     *
     * @param uid
     * @param rid
     * @param whom
     * @return
     */
    public static String SUBMIT_PERSON_COMMENT(String uid, String rid, String whom) {
        return "/comment?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&rid=" + rid + "&whom=" + whom;
    }


    /**
     * 18.获取课件评论
     */
    public static String UPDATE_COMMENT = "/getcomment?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;

    /**
     * 19. 发布同事圈
     */
    public static String CHATTER_PUBLISH = "/publish?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;


    /**
     * 20.获取同事圈
     */
    public static String QUESTION_GET = "/getpublish?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;


    /**
     * 20.获取某人的同事圈
     */
    public static String CHATTER_GET_USER = "/viewqas_v2?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;

    /**
     * 20.获取同事圈话题
     */
    public static String CHATTER_GET_TOPIC(String uid, String topic, int pageNum, int itemNum) {
        return "/getTopicInfo?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion + "&uid="
                + uid + "&topic=" + topic + "&page_no=" + pageNum + "&item_per_page=" + itemNum;
    }

    /**
     * 20.获取同事圈话题
     */
    public static String CHATTER_GET_HOT(String uid, int pageNum, int itemNum) {
        return "/getdaren?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion + "&uid="
                + uid + "&page_no=" + pageNum + "&item_per_page=" + itemNum;
    }

    /**
     * 21. 同事圈圈 评论删除
     */
    public static String MYGROUP_DEL(String uid, String qid) {
        return "/delpublish?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&qid=" + qid;
    }

    public static String CHATTER_GET_BY_ID(String uid, String qid) {
        return "/getonepub?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&qid=" + qid;
    }

    public static String ASK_GET_BY_ID = "/getoneask?sys=android" + AppApplication.SYSVERSION + "&ver="
            + AppApplication.appVersion;

    /**
     * 22.回复问题同事圈
     */
    public static String CHATTER_REPLY = "/reply?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;

    /**
     * 23.获取同事圈答复
     */
    public static String CHATTER_REPLY_GET = "/getreply?sys=android"
            + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;

    /**
     * 获取资源的点赞数／评论数／评分
     *
     * @param uid
     * @return
     */
    public static String GET_MAC_POST(String uid) {
        return "/getmc2?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid;
    }

    /**
     * 26 同事圈点赞
     *
     * @param uid
     * @param qid
     * @return
     */
    public static String SET_CREDIT(String uid, String qid) {
        return "/setCredit?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&qid=" + qid;
    }


    /**
     * 27  获取聊天用户列表
     *
     * @param uid
     * @param qid
     * @return
     */
    public static String GET_CHAT_LIST(String uid, String qid) {
        return "/getChatList?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid;
    }


    /**
     * 28  获取用户聊天记录
     *
     * @param uid
     * @return
     */
    public static String GET_CHAT_Info(String uid, String whom) {
        return "/getChatInfo?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&whom=" + whom;
    }

    /**
     * 29  发送消息
     *
     * @return
     */
    public static String SEND_MSG() {
        return "/chat?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion;
    }


    /**
     * 功能描述: 微培训课件评分
     *
     * @param uid    用户id
     * @param rid    资源id
     * @param credit 打分   分值在0-5之间
     * @return
     */
    public static String COURSEWARE_SCORING(String uid, String rid, String credit) {
        return "/credit?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&rid=" + rid + "&credit=" + credit;
    }


    /**
     * 功能描述: 功能描述:同步数据(获取列表)   2014-12-15
     *
     * @param uid   uid 用户id
     * @param type  类型(取值notice/exam/training/task)
     * @param tag   时间戳
     * @param begin 分类编号，0为所有
     * @param limit
     * @param key
     * @param done  done为1  表示请求总学习进度，我的考试中的内容
     * @return
     */
    public static String UPDATE_RESOURCES_2(String uid, String type, int tag, int begin, int limit, String key, String done) {
        if (!TextUtils.isEmpty(done)) {
            return "/sync_v2?sys=android" + AppApplication.SYSVERSION + "&ver="
                    + AppApplication.appVersion + "&uid=" + uid + "&type=" + type
                    + "&tag=" + tag + "&begin=" + begin + "&limit=" + limit + "&key=" + key + "&done=" + done;
        } else {
            return "/sync_v2?sys=android" + AppApplication.SYSVERSION + "&ver="
                    + AppApplication.appVersion + "&uid=" + uid + "&type=" + type
                    + "&tag=" + tag + "&begin=" + begin + "&limit=" + limit + "&key=" + key;
        }
    }

    /**
     * 功能描述: 搜索
     *
     * @param uid 用户id
     * @param key 需要搜索的关键字
     * @return
     */
    public static String SEARCH(String uid, String key) {
        return "/search?sys=android" + AppApplication.SYSVERSION + "&ver="
                + AppApplication.appVersion + "&uid=" + uid + "&key=" + key;
    }

    /**
     * 功能描述:考试item 点击调取的web
     *
     * @param uid
     * @param rid
     * @return
     */
    public static String EXAM_ITEM(String uid, String rid) {
        return "/doexam?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&rid=" + rid;
    }


    /**
     * 功能描述:  android在这儿要传fmt=mp4，服务器会做判断，android上传的视屏，服务器会做转化
     *
     * @param uid
     * @param qid
     * @return
     */
    public static String PUBVIDEO(String uid, String qid) {
        return "/pubvideo?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&qid=" + qid + "&fmt=mp4";
    }

    /**
     * 功能描述:  上传图片
     *
     * @param uid
     * @param qid
     * @return
     */
    public static String PUBIMAGE(String uid, String qid, int index) {
        return "/pubphoto?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&qid=" + qid + "&idx=" + index;
    }

    /**
     * 功能描述:  android在这儿要传fmt=mp4，服务器会做判断，android上传的视屏，服务器会做转化
     *
     * @param uid
     * @return
     */
    public static String TOPICLIST(String uid) {
        return "/getTopicList?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid;
    }

    /**
     * 功能描述: 回复同事圈的某条评论
     *
     * @return
     */
    public static String ReplyComment() {
        return "/replyat?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }


    /**
     * 功能描述: 问答模块提问接口
     *
     * @return
     */
    public static String pubAsk() {
        return "/pubask?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 删除问题
     *
     * @return
     */
    public static String delAsk() {
        return "/delask?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 获取问题列表
     *
     * @return
     */
    public static String getask() {
        return "/getask?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 发布回答
     *
     * @return
     */
    public static String pubAsnswer() {
        return "/pubanswer?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 获取回答列表
     *
     * @return
     */
    public static String getAnswer() {
        return "/getanswer?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 回答点赞
     *
     * @return
     */
    public static String pollAnswer() {
        return "/pollanswer?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 微调研跳转页面
     *
     * @return
     */
    public static String getWeiDiaoYanURl() {
        return "http://mworking.cn/badou/dofeed?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion + "&uid=";
    }

    /**
     * 功能描述: 获取用户等级信息
     *
     * @return
     */
    public static String getViewrank() {
        return "/viewrank?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 获取用户历史等级考试
     *
     * @return
     */
    public static String getPastrank() {
        return "/pastrank?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * @return 获取统计url
     */
    public static String getTongji(String uid, String rid) {
        return "/dostat?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&rid=" + rid;
    }

    /**
     * @return 删除会话
     */
    public static String delchat() {
        return "/delchat?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * 功能描述: 删除同事圈的某条评论
     *
     * @return
     */
    public static String DeleteReplyComment() {
        return "/delreply?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    /**
     * @return 查看资源详情
     */
    public static String viewResourceDetail() {
        return "/viewres?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    public static String addStore() {
        return "/addstore?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    public static String deleteStore() {
        return "/delstore?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

    public static String getStore(String uid, int pageNum, int itemNum) {
        return "/getstore?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion +
                "&uid=" + uid + "&page_no=" + pageNum + "&item_per_page=" + itemNum;
    }

    public static String getContactList() {
        return "/gethxtxl?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion;
    }

}
