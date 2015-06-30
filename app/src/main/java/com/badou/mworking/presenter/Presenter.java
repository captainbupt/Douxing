package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.view.BaseView;

/**
 * Base class representing a Presenter in a model view presenter (MVP) pattern.
 */
public abstract class Presenter {

    Context mContext;
    Activity mActivity;

    public Presenter(Context context) {
        this.mContext = context;
        this.mActivity = (Activity) context;
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    public void resume() {
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    public void pause() {
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onDestroy() method.
     */
    public void destroy() {
    }

    public abstract void attachView(BaseView v);

    public void attachIncomingIntent(Intent intent) {
    }

}
