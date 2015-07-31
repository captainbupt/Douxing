package com.badou.mworking.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.util.AppManager;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.widget.ChatterUrlPopupWindow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class representing a Presenter in a model view presenter (MVP) pattern.
 */
public abstract class Presenter {

    protected Context mContext;
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


    static final Pattern pattern = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
    static String lastUrl = "";

    public void comeToForeground() {
        Activity activity = AppManager.getAppManager().currentActivity();
        CharSequence content = null;
        if (android.os.Build.VERSION.SDK_INT > 11) {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasPrimaryClip()) {
                content = clipboardManager.getPrimaryClip().getItemAt(0).getText();
            }
        } else {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasText()) {
                content = clipboardManager.getText();
            }
        }
        if (content == null)
            return;
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String url = matcher.group();
            if (url.equals(lastUrl)) {
                return;
            }
            lastUrl = url;
            if (mPopupWindow == null)
                mPopupWindow = new ChatterUrlPopupWindow(activity);
            mPopupWindow.setUrl(url);
            mPopupWindow.showPopupWindow(activity.getWindow().getDecorView().findViewById(android.R.id.content));
        }
    }

    public void backToBackground() {
    }
}
