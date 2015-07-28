package com.badou.mworking.view.category;

import com.badou.mworking.view.category.CategoryBaseView;
import com.baidu.location.BDLocation;

public interface TaskSignView extends CategoryBaseView {

    int STATUS_UNSIGN = 0;
    int STATUS_SIGN = 1;
    int STATUS_OFFLINE = 2;

    void setStatus(int status);

    void setLocation(BDLocation location);

    void takeImage();
}
