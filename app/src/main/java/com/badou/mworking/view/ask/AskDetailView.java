package com.badou.mworking.view.ask;

import com.badou.mworking.entity.Ask;
import com.badou.mworking.view.BaseListView;
import com.badou.mworking.view.StoreItemView;

public interface AskDetailView extends BaseListView<Ask>,StoreItemView {
    void setData(Ask ask);

    void setReplyCount(int count);

    int getAllCount();
}
