package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import org.holoeverywhere.widget.LinearLayout;

/**
 * Created by Administrator on 2015/5/26.
 */
public class TopFadeScrollView extends ScrollView {

    private int originHeight = -1;
    private int originWeight = -1;
    private float lastY;
    private int currentHeight;
    private Context mContext;
    private View mTopView;

    public TopFadeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopViewId(int id) {
        mTopView = findViewById(id);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if(lastY == -1){
                lastY = ev.getY();
                return true;
            }
            float currentY = ev.getY();
            float offset = currentY - lastY;
            if (originHeight == -1 || originWeight == -1) {
                originHeight = mTopView.getHeight();
                originWeight = mTopView.getWidth();
                currentHeight = originHeight;
            }
            if (offset < 0 && currentHeight > 0) {
                currentHeight += offset;
                if (currentHeight <= 0) {
                    currentHeight = 0;
                    mTopView.setVisibility(View.GONE);
                } else {
                    mTopView.setVisibility(View.VISIBLE);
                    int height = currentHeight;
                    int width = originWeight * currentHeight / originHeight;
                    mTopView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                }
            } else if (offset > 0 && currentHeight < originHeight) {
                System.out.println("motion down");
                currentHeight += offset;
                if (currentHeight >= originHeight) {
                    currentHeight = originHeight;
                }
                mTopView.setVisibility(View.VISIBLE);
                int height = currentHeight;
                int width = originWeight * currentHeight / originHeight;
                mTopView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            }
            System.out.println("offset: " + offset + "last:" + lastY + ", current:" + currentY + ", height:" + currentHeight);
            lastY = currentY;
            return true;
        } else if(ev.getAction() == MotionEvent.ACTION_UP){
            lastY = -1;
        }
        return super.onTouchEvent(ev);
    }
}
