package com.badou.mworking.net.volley;

import com.android.volley.VolleyError;

/**
 * Created by yee on 3/7/14.
 */
public interface VolleyCallback {

    public void onStart();

    public void onSuccess(Object o);

    public void onError(VolleyError e);

}
