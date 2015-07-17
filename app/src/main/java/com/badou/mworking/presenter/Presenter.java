package com.badou.mworking.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.view.BaseView;

/**
 * Base class representing a Presenter in a model view presenter (MVP) pattern.
 */
public abstract class Presenter {

    Context mContext;

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public abstract void attachView(BaseView v);

    public void finish() {

    }
}
