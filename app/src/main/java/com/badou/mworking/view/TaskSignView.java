package com.badou.mworking.view;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;

public interface TaskSignView extends CategoryBaseView {

    int STATUS_UNSIGN = 0;
    int STATUS_SIGN = 1;
    int STATUS_OFFLINE = 2;

    void setStatus(int status);

    void setLocation(BDLocation location);

    void takeImage();
}
