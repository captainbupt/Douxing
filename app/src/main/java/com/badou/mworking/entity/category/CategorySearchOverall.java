package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategorySearchOverall {
    @SerializedName("notice")
    List<CategorySearch> notice;
    @SerializedName("training")
    List<CategorySearch> training;
    @SerializedName("exam")
    List<CategorySearch> exam;
    @SerializedName("task")
    List<CategorySearch> task;
    @SerializedName("shelf")
    List<CategorySearch> shelf;
    @SerializedName("entry")
    List<CategorySearch> entry;
    @SerializedName("plan")
    List<CategorySearch> plan;
    @SerializedName("survey")
    List<CategorySearch> survey;

    public List<CategorySearch> getNotice() {
        initType(Category.CATEGORY_NOTICE, notice);
        return notice;
    }

    public List<CategorySearch> getTraining() {
        initType(Category.CATEGORY_TRAINING, training);
        return training;
    }

    public List<CategorySearch> getExam() {
        initType(Category.CATEGORY_EXAM, exam);
        return exam;
    }

    public List<CategorySearch> getTask() {
        initType(Category.CATEGORY_TASK, task);
        return task;
    }

    public List<CategorySearch> getShelf() {
        initType(Category.CATEGORY_SHELF, shelf);
        return shelf;
    }

    public List<CategorySearch> getEntry() {
        initType(Category.CATEGORY_ENTRY, entry);
        return entry;
    }

    public List<CategorySearch> getPlan() {
        initType(Category.CATEGORY_PLAN, plan);
        return plan;
    }

    public List<CategorySearch> getSurvey() {
        initType(Category.CATEGORY_SURVEY, survey);
        return survey;
    }

    private void initType(int type, List<CategorySearch> list) {
        for (CategorySearch item : list) {
            item.type = type;
        }
    }
}
