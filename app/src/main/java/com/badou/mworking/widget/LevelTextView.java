package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.badou.mworking.R;

public class LevelTextView extends TextView {
    public LevelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLevel(int level) {
        int bg = 0;
        if (level <= 4) {
            bg = R.drawable.background_lv_fir;
        } else if (level <= 8) {
            bg = R.drawable.background_lv_sec;
        } else if (level <= 12) {
            bg = R.drawable.background_lv_thi;
        } else if (level <= 16) {
            bg = R.drawable.background_lv_fou;
        } else if (level <= 20) {
            bg = R.drawable.background_lv_fif;
        }
        if (level >= 10) {
            setText("LV " + level);
            setPadding(6, 1, 6, 1);
        } else {
            setText(" LV " + level + " ");
            setPadding(7, 1, 7, 1);
        }
        setBackgroundResource(bg);
    }
}
