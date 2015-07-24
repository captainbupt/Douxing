package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.view.BaseView;
import com.badou.mworking.widget.ChatterUrlPopupWindow;

/**
 * Base class representing a Presenter in a model view presenter (MVP) pattern.
 */
public abstract class Presenter {

    Context mContext;
    ChatterUrlPopupWindow mPopupWindow;

    public Presenter(Context context) {
        this.mContext = context;
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

    public boolean onBackPressed() {
        return false;
    }

    public abstract void attachView(BaseView v);

    public void comeToForeground() {
        if (mContext instanceof Activity) {
            if (mPopupWindow == null)
                mPopupWindow = new ChatterUrlPopupWindow(mContext);
            mPopupWindow.setUrl("www.baidu.com");
            mPopupWindow.showPopupWindow(((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content));
        }
    }

    public void backToBackground() {
    }
}
