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
 * ������ͼ������£����ļ�����������ʱ��ʾʡ�Ժ�
 */
public class EllipsizeTextView extends TextView {


    //Ҫ��ʾ��ʡ�Ժ�
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


    //�Ƿ�������ֹ����ʱ����ʾʡ�Է���
    public boolean isEllipsized() {
        return isEllipsized;
    }


    //��дsetMaxLines�ķ�������Ϊֻ���ڴ�����setMaxLine����Ч
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

//��ؼ��Ĳ����������ʵ���ǽ���Ҫ��ʾ���ı�����һ��text layoutȻ�������layout��maxline���������趨�ĵ�maxline��ʱ��ͽ�ȡ�ַ�����������ʡ�Ժ�

//Ȼ�����ý�ȡ����ʡ�Ժŵ������ٴ���text layout�Ƚ��Ƿ��Ǵ���maxline��Ȼ����ڵĻ���ÿ�ν��ַ���-1��֪������maxline�����ʱ���������Ҫ��ʾ��������

    private void resetText() {
        int maxLines = getMaxLines();
        String workingText = fullText;
        Log.v(TAG, "workingText=" + workingText);
        boolean ellipsized = false;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {
//��ȡһ����ʾ�ַ�������Ȼ���ȡ�ַ�����
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

    //����textview����ʾ�����layout����textview��layout��������ʾ������ֻ�����������Ƚ�Ҫ��ʾ�������Ƿ����
    private Layout createWorkingLayout(String workingText) {
        //�ַ�����Դ�����ʣ�layout�Ŀ�ȣ�Layout����ʽ������Ĵ�С���м��
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
    }


}