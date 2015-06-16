package com.badou.mworking.net.volley;

import com.android.volley.VolleyError;

/**
 * Created by yee on 3/7/14.
 */
public interface VolleyCallback {

    void onStart();

    void onSuccess(Object o);

    void onError(VolleyError e);

}
