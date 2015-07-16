package com.badou.mworking.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;

public class TopFadeScrollView extends ScrollView {

    private int originHeight = -1;
    private int originWidth = -1;
    private View mTopView;

    public TopFadeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopViewId(int id) {
        mTopView = findViewById(id);
    }

    Handler handler = new Handler();

    StopDetectRunnable runnable = new StopDetectRunnable();

    class StopDetectRunnable implements Runnable {

        public int lastY;

        @Override
        public void run() {
            if (lastY == getScrollY() && originHeight > getScrollY()) {
                //停止了
                int animSpeed = 20;
                int height = originHeight - getScrollY();
                if (height < originHeight / 2) {
                    post(new AutoScrollRunnable(animSpeed, getScrollY(), originHeight));
                } else {
                    post(new AutoScrollRunnable(animSpeed, getScrollY(), 0));
                }
            } else {
                handler.removeCallbacks(runnable);
                runnable.lastY = getScrollY();
                handler.postDelayed(runnable, 100);
            }
        }
    }

    class AutoScrollRunnable implements Runnable {
        private final int TIME_OFFSET = 10;
        private int end;
        private int offset;
        private int start;

        public AutoScrollRunnable(int offset, int start, int end) {
            this.end = end;
            this.start = start;
            if (end < start)
                this.offset = -offset;
            else
                this.offset = offset;
        }

        @Override
        public void run() {
            if (offset > 0 && start + offset > end) {
                offset = end - start;
            } else if (offset < 0 && start + offset < end) {
                offset = end - start;
            }
            start += offset;
            TopFadeScrollView.this.scrollBy(0, offset);
            if (start != end)
                TopFadeScrollView.this.postDelayed(this, TIME_OFFSET);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // 首次使用获取banner初始高度和宽度
        if (originHeight == -1 || originWidth == -1) {
            originHeight = mTopView.getHeight();
            originWidth = mTopView.getWidth();
        }

        // t为ScrollView向上滑动的量， 两者相减即为当前banner所需的高度
        int height = originHeight - t;
        // 计算缩放比例
        float newRatio = (float) height / (float) originHeight;

        // 修改banner的属性
        if (height >= 0) {
            ViewHelper.setScaleX(mTopView, newRatio);
            ViewHelper.setScaleY(mTopView, newRatio);
            ViewHelper.setY(mTopView, t / 2);
            ViewHelper.setAlpha(mTopView, newRatio);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(runnable);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            handler.removeCallbacks(runnable);
            runnable.lastY = getScrollY();
            handler.postDelayed(runnable, 100);
        }
        return super.dispatchTouchEvent(ev);
    }
}
