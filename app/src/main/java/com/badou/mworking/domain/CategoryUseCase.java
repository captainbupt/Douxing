package com.badou.mworking.domain;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.entity.category.Task;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;

import rx.Observable;

public class CategoryUseCase extends UseCase {

    public static final int TYPE_ALL = -1;
    public static final int TYPE_UNREAD = 0;
    public static final int TYPE_READ = 1;

    private int done;
    private int type;
    private int begin;
    private int tag;

    public CategoryUseCase(int type) {
        this.type = type;
        this.done = TYPE_ALL;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getCategory(UserInfo.getUserInfo().getUid(), Category.CATEGORY_KEY_NAMES[type], tag, begin, Constant.LIST_ITEM_NUM, done);
    }
}
