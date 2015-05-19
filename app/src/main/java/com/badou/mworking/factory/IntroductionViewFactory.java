package com.badou.mworking.factory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.IntroductionActivity;
import com.badou.mworking.R;
import com.badou.mworking.widget.OptimizedImageView;

/**
 * Created by Administrator on 2015/5/19.
 * 工厂模式获取每一个页面
 */
public class IntroductionViewFactory {
    private Context mContext;
    private View[] views;
    public static final int COUNT_IMAGE = IntroductionActivity.COUNT_IMAGE;

    public IntroductionViewFactory(Context context){
        this.mContext = context;
        // 如果为null，则创建全部views
        createViews(context);
    }

    public View getViewByPosition(int position) {
        if (position < 0 || position > views.length) {
            return null;
        }
        return views[position];
    }

    private void createViews(Context context) {
        views = new View[COUNT_IMAGE];

        for (int i = 0; i < COUNT_IMAGE; i++) {
            OptimizedImageView imageView = new OptimizedImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResourceFullScreen(R.drawable.background_welcome_1 + i);
            views[i] = imageView;
        }
    }
}
