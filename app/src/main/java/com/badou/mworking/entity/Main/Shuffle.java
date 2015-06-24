package com.badou.mworking.entity.Main;

import android.content.Context;

import com.badou.mworking.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    private final MainIcon[] MAP_ACCESS;

    public Shuffle() {
        MAP_ACCESS = new MainIcon[]{buttonNotice, buttonTraining, buttonExam, buttonTask, buttonSurvey, buttonChatter, buttonShelf, buttonAsk};
    }

    public void initialize(Context context) {
        if (buttonShelf == null)
            buttonShelf = new MainIcon("1", context.getString(R.string.module_default_title_ask));
        buttonShelf.setResId(R.drawable.button_shelf);

        if (buttonCenter == null)
            buttonCenter = new MainIcon("1", context.getString(R.string.module_default_title_center));
        buttonCenter.setResId(R.drawable.button_shelf);

        if (buttonTraining == null)
            buttonTraining = new MainIcon("1", context.getString(R.string.module_default_title_training));
        buttonTraining.setResId(R.drawable.button_training);

        if (buttonChatter == null)
            buttonChatter = new MainIcon("1", context.getString(R.string.module_default_title_chatter));
        buttonChatter.setResId(R.drawable.button_chatter);

        if (buttonChat == null)
            buttonChat = new MainIcon("1", context.getString(R.string.module_default_title_chat));
        buttonChat.setResId(R.drawable.button_chatter);

        if (buttonNotice == null)
            buttonNotice = new MainIcon("1", context.getString(R.string.module_default_title_notice));
        buttonNotice.setResId(R.drawable.button_notice);

        if (buttonTask == null)
            buttonTask = new MainIcon("1", context.getString(R.string.module_default_title_task));
        buttonTask.setResId(R.drawable.button_task);

        if (buttonExam == null)
            buttonExam = new MainIcon("1", context.getString(R.string.module_default_title_exam));
        buttonExam.setResId(R.drawable.button_exam);

        if (buttonAsk == null)
            buttonAsk = new MainIcon("1", context.getString(R.string.module_default_title_ask));
        buttonAsk.setResId(R.drawable.button_ask);

        if (buttonSurvey == null)
            buttonSurvey = new MainIcon("1", context.getString(R.string.module_default_title_survey));
        buttonSurvey.setResId(R.drawable.button_survey);
    }

    public List<Object> getMainIconList(int access) {
        List<Object> mainIconList = new ArrayList<>();
        for (int ii = 0; ii < MAP_ACCESS.length; ii++, access /= 2) {
            if (access % 2 == 1) {
                mainIconList.add(MAP_ACCESS[ii]);
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

    public MainIcon getButtonShelf() {
        return buttonShelf;
    }

    public MainIcon getButtonCenter() {
        return buttonCenter;
    }

    public MainIcon getButtonTraining() {
        return buttonTraining;
    }

    public MainIcon getButtonChatter() {
        return buttonChatter;
    }

    public MainIcon getButtonChat() {
        return buttonChat;
    }

    public MainIcon getButtonNotice() {
        return buttonNotice;
    }

    public MainIcon getButtonTask() {
        return buttonTask;
    }

    public MainIcon getButtonExam() {
        return buttonExam;
    }

    public MainIcon getButtonAsk() {
        return buttonAsk;
    }

    public MainIcon getButtonSurvey() {
        return buttonSurvey;
    }
}

