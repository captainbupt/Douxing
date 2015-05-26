package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2015/5/26.
 */
public class TopFadeScrollView extends ScrollView {

    private Context mContext;
    private View mTopView;

    public TopFadeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopViewId(int id){
        mTopView = findViewById(id);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // t 为已滚动的y值
        if(mTopView!=null){
            int height = mTopView.getHeight();
/*            PropertyValuesHolder pvhW = PropertyValuesHolder.ofFloat("width", ball.getWidth(),
                    ball.getWidth() * 2);
            PropertyValuesHolder pvhH = PropertyValuesHolder.ofFloat("height", ball.getHeight(),
                    ball.getHeight() * 2);
            PropertyValuesHolder pvTX = PropertyValuesHolder.ofFloat("x", ball.getX(),
                    ball.getX() - BALL_SIZE/2f);
            PropertyValuesHolder pvTY = PropertyValuesHolder.ofFloat("y", ball.getY(),
                    ball.getY() - BALL_SIZE/2f);
            ObjectAnimator whxyBouncer = ObjectAnimator.ofPropertyValuesHolder(ball, pvhW, pvhH,
                    pvTX, pvTY).setDuration(DURATION / 2);
            whxyBouncer.setRepeatCount(1);*/
        }
    }
}
