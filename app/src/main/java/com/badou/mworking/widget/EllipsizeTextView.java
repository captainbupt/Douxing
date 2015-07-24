package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/**
 * 在缩略图的情况下，当文件名超过长度时显示省略号
 */
public class EllipsizeTextView extends TextView {


    //要显示的省略号
    private static final String ELLIPSIS = "...";

    private static final String TAG = "EllipsizeText";

    private float lineSpacingMultiplier = 1.0f;
    private float lineAdditionalVerticalPadding = 0.0f;

    private boolean isEllipsized;
    private boolean isStale;
    private boolean programmaticChange;
    private String fullText;
    private int maxLines = -1;

    public EllipsizeTextView(Context context) {
        super(context);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //是否会在文字过多的时候显示省略符号
    public boolean isEllipsized() {
        return isEllipsized;
    }


    //重写setMaxLines的方法，因为只有在代码中setMaxLine才有效
    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
        isStale = true;
    }


    public int getMaxLines() {
        return maxLines;
    }


    @Override
    public void setLineSpacing(float add, float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!programmaticChange) {
            fullText = text.toString();
            isStale = true;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "onDraw");
        if (isStale) {
            super.setEllipsize(null);
            resetText();
            Log.v(TAG, "resetText");
        }
        super.onDraw(canvas);
    }

//最关键的部分在这里，其实就是将用要显示的文本创建一个text layout然后在这个layout的maxline大于咱们设定的的maxline的时候就截取字符串，并加上省略号

//然后再用截取加上省略号的文字再创建text layout比较是否是大于maxline，然后大于的话就每次将字符串-1，知道等于maxline，这个时候就是咱们要显示的文字了

    private void resetText() {
        int maxLines = getMaxLines();
        String workingText = fullText;
        Log.v(TAG, "workingText=" + workingText);
        boolean ellipsized = false;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {
//获取一行显示字符个数，然后截取字符串数
                workingText = fullText.substring(0, layout.getLineEnd(maxLines - 1)).trim() + ELLIPSIS;
                Layout layout2 = createWorkingLayout(workingText);
                while (layout2.getLineCount() > maxLines) {
                    int lastSpace = workingText.length() - 1;
                    Log.v(TAG, "lastSpace=" + lastSpace);
                    if (lastSpace == -1) {
                        break;
                    }
                    workingText = workingText.substring(0, lastSpace);
                    layout2 = createWorkingLayout(workingText + ELLIPSIS);
                    Log.v(TAG, "lastSpace workingText=" + workingText);
                }
                workingText = workingText + ELLIPSIS;
                ellipsized = true;
            }
        }
        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setText(workingText);
                invalidate();
            } finally {
                programmaticChange = false;
            }
        }
        isStale = false;
        if (ellipsized != isEllipsized) {
            isEllipsized = ellipsized;
        }
    }

    //返回textview的显示区域的layout，该textview的layout并不会显示出来，只是用其宽度来比较要显示的文字是否过长
    private Layout createWorkingLayout(String workingText) {
        //字符串资源，画笔，layout的宽度，Layout的样式，字体的大小，行间距
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
    }


}