package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;

import rx.Observable;

public class CategoryUseCase extends UseCase {

    public static final int TYPE_ALL = -1;
    public static final int TYPE_UNREAD = 0;
    public static final int TYPE_READ = 1;

    private int done;
    private int type;
    private int pageNum;
    private int tag;
    private int itemNum;

    public CategoryUseCase(int type) {
        this.type = type;
        this.done = TYPE_ALL;
        this.itemNum = Constant.LIST_ITEM_NUM;
        this.tag = 0;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getCategory(UserInfo.getUserInfo().getUid(), Category.CATEGORY_KEY_NAMES[type], tag, (pageNum - 1) * itemNum, itemNum, done);
    }
}
