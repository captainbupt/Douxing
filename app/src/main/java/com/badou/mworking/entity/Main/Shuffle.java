package com.badou.mworking.entity.main;

import android.content.Context;

import com.badou.mworking.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shuffle {

    public static final String BUTTON_NOTICE = "button_notice";//通知公告icon
    public static final String BUTTON_TRAINING = "button_training";//微培训
    public static final String BUTTON_SHELF = "button_shelf";//微培训
    public static final String BUTTON_EXAM = "button_exam";//考试
    public static final String BUTTON_SURVEY = "button_survey";//培训调研
    public static final String BUTTON_TASK = "button_task";//任务签到
    public static final String BUTTON_CHATTER = "button_chatter";//同事圈
    public static final String BUTTON_ASK = "button_ask";// 问答
    public static final String BUTTON_CENTER = "button_center";// 问答
    public static final String BUTTON_CHAT = "button_chat";// 问答

    // 所有的MainIcon都应该是通过Key来进行访问的，所以通过给Key来定义顺序，实现主界面的排序效果
    private static final String[] MAP_ACCESS_KEY = new String[]{BUTTON_NOTICE, BUTTON_TRAINING, BUTTON_EXAM, BUTTON_TASK, BUTTON_SURVEY, BUTTON_CHATTER, BUTTON_SHELF, BUTTON_ASK};
    private final Map<String, MainIcon> MAP_ACCESS;

    public Shuffle() {
        MAP_ACCESS = new HashMap<String, MainIcon>(8) {{
            put(BUTTON_NOTICE, buttonNotice);
            put(BUTTON_TRAINING, buttonTraining);
            put(BUTTON_EXAM, buttonExam);
            put(BUTTON_TASK, buttonTask);
            put(BUTTON_SURVEY, buttonSurvey);
            put(BUTTON_CHATTER, buttonChatter);
            put(BUTTON_SHELF, buttonShelf);
            put(BUTTON_ASK, buttonAsk);
        }};
    }

    public List<Object> getMainIconList(Context context, int access) {
        List<Object> mainIconList = new ArrayList<>();
        for (int ii = 0; ii < MAP_ACCESS_KEY.length; ii++, access /= 2) {
            if (access % 2 == 1) {
                mainIconList.add(getMainIcon(context, MAP_ACCESS_KEY[ii]));
            }
        }
        Collections.sort(mainIconList, new Comparator<Object>() {
            @Override
            public int compare(Object t1, Object t2) {
                return Integer.valueOf(((MainIcon) t1).getPriority()).compareTo(Integer.valueOf(((MainIcon) t2).getPriority()));
            }
        });
        return mainIconList;
    }

    public MainIcon getMainIcon(Context context, String key) {
        if (BUTTON_SHELF.equals(key)) {
            if (buttonShelf == null)
                buttonShelf = new MainIcon("1", context.getString(R.string.module_default_title_ask));
            buttonShelf.setResId(R.drawable.button_shelf);
            buttonShelf.setKey(BUTTON_SHELF);
            return buttonShelf;
        } else if (BUTTON_CENTER.equals(key)) {
            if (buttonCenter == null)
                buttonCenter = new MainIcon("1", context.getString(R.string.module_default_title_center));
            buttonCenter.setResId(R.drawable.button_shelf);
            buttonCenter.setKey(BUTTON_CENTER);
            return buttonCenter;
        } else if (BUTTON_TRAINING.equals(key)) {
            if (buttonTraining == null)
                buttonTraining = new MainIcon("1", context.getString(R.string.module_default_title_training));
            buttonTraining.setResId(R.drawable.button_training);
            buttonTraining.setKey(BUTTON_TRAINING);
            return buttonTraining;
        } else if (BUTTON_CHATTER.equals(key)) {
            if (buttonChatter == null)
                buttonChatter = new MainIcon("1", context.getString(R.string.module_default_title_chatter));
            buttonChatter.setResId(R.drawable.button_chatter);
            buttonChatter.setKey(BUTTON_CHATTER);
            return buttonChatter;
        } else if (BUTTON_CHAT.equals(key)) {
            if (buttonChat == null)
                buttonChat = new MainIcon("1", context.getString(R.string.module_default_title_chat));
            buttonChat.setResId(R.drawable.button_chatter);
            buttonChat.setKey(BUTTON_CHAT);
            return buttonChat;
        } else if (BUTTON_NOTICE.equals(key)) {
            if (buttonNotice == null)
                buttonNotice = new MainIcon("1", context.getString(R.string.module_default_title_notice));
            buttonNotice.setResId(R.drawable.button_notice);
            buttonNotice.setKey(BUTTON_NOTICE);
            return buttonNotice;
        } else if (BUTTON_TASK.equals(key)) {
            if (buttonTask == null)
                buttonTask = new MainIcon("1", context.getString(R.string.module_default_title_task));
            buttonTask.setResId(R.drawable.button_task);
            buttonTask.setKey(BUTTON_TASK);
            return buttonTask;
        } else if (BUTTON_EXAM.equals(key)) {
            if (buttonExam == null)
                buttonExam = new MainIcon("1", context.getString(R.string.module_default_title_exam));
            buttonExam.setResId(R.drawable.button_exam);
            buttonExam.setKey(BUTTON_EXAM);
            return buttonExam;
        } else if (BUTTON_ASK.equals(key)) {
            if (buttonAsk == null)
                buttonAsk = new MainIcon("1", context.getString(R.string.module_default_title_ask));
            buttonAsk.setResId(R.drawable.button_ask);
            buttonAsk.setKey(BUTTON_ASK);
            return buttonAsk;
        } else if (BUTTON_SURVEY.equals(key)) {
            if (buttonSurvey == null)
                buttonSurvey = new MainIcon("1", context.getString(R.string.module_default_title_survey));
            buttonSurvey.setResId(R.drawable.button_survey);
            buttonSurvey.setKey(BUTTON_SURVEY);
            return buttonSurvey;
        }
        return null;
    }

    @SerializedName(BUTTON_SHELF)
    @Expose
    private MainIcon buttonShelf;
    @SerializedName(BUTTON_CENTER)
    @Expose
    private MainIcon buttonCenter;
    @SerializedName(BUTTON_TRAINING)
    @Expose
    private MainIcon buttonTraining;
    @SerializedName(BUTTON_CHATTER)
    @Expose
    private MainIcon buttonChatter;
    @SerializedName(BUTTON_CHAT)
    @Expose
    private MainIcon buttonChat;
    @SerializedName(BUTTON_NOTICE)
    @Expose
    private MainIcon buttonNotice;
    @SerializedName(BUTTON_TASK)
    @Expose
    private MainIcon buttonTask;
    @SerializedName(BUTTON_EXAM)
    @Expose
    private MainIcon buttonExam;
    @SerializedName(BUTTON_ASK)
    @Expose
    private MainIcon buttonAsk;
    @SerializedName(BUTTON_SURVEY)
    @Expose
    private MainIcon buttonSurvey;

}

