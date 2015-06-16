package com.badou.mworking.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * Created by Administrator on 2015/5/26.
 */
public class MyScrollListenerScrollView extends ScrollView {

    private OnScrollChangedListener mOnScrollChangedListener;

    public interface OnScrollChangedListener {
        void onScrollChanged(int y);

        void onScrollStopped();
    }

    public MyScrollListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.mOnScrollChangedListener = onScrollChangedListener;
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {

        public int lastY;

        @Override
        public void run() {
            if (lastY == getScrollY()) {
                //停止了
                if (mOnScrollChangedListener != null) {
                    mOnScrollChangedListener.onScrollStopped();
                }
            }
        }
    };
}
