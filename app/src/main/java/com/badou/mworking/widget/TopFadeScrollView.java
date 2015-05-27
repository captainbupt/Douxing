package com.badou.mworking.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.holoeverywhere.widget.LinearLayout;

/**
 * Created by Administrator on 2015/5/26.
 */
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            int y = getScrollY();
            int animTime = 100;
            int height = originHeight - y;
            if (height < originHeight / 2) {
/*                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(mTopView, "scaleX", 0),
                        ObjectAnimator.ofFloat(mTopView, "scaleY", 0),
                        ObjectAnimator.ofFloat(mTopView, "y", originHeight / 2),
                        ObjectAnimator.ofFloat(mTopView, "alpha", 0)
                );
                set.setDuration(animTime).start();*/
                post(new AutoScrollRunnable(animTime, height, originHeight));
            } else {
/*                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(mTopView, "scaleX", 1),
                        ObjectAnimator.ofFloat(mTopView, "scaleY", 1),
                        ObjectAnimator.ofFloat(mTopView, "y", 0),
                        ObjectAnimator.ofFloat(mTopView, "alpha", 1)
                );
                set.setDuration(animTime).start();*/
                post(new AutoScrollRunnable(animTime, height, 0));
            }
        }
        return super.onTouchEvent(ev);
    }

    class AutoScrollRunnable implements Runnable {
        private final int TIME_OFFSET = 10;
        private int end;
        private int totalTime;
        private int offset;

        public AutoScrollRunnable(int totalTime, int start, int end) {
            this.totalTime = totalTime;
            this.end = end;
            this.offset = (end - start) / (totalTime / TIME_OFFSET);
        }

        @Override
        public void run() {
            totalTime -= TIME_OFFSET;
            if (totalTime > 0) {
                TopFadeScrollView.this.scrollBy(0, offset);
                TopFadeScrollView.this.postDelayed(this, TIME_OFFSET);
            } else {
                TopFadeScrollView.this.scrollTo(0, end);
            }
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
}
