package com.badou.mworking.listener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.badou.mworking.ChatterTopicActivity;
import com.badou.mworking.base.BaseActionBarActivity;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/6/11.
 */
public class TopicClickableSpan extends ClickableSpan {
    private String mTopic;
    private Context mContext;

    public TopicClickableSpan(Context context, String topic) {
        this.mContext = context;
        this.mTopic = topic;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, ChatterTopicActivity.class);
        intent.putExtra(ChatterTopicActivity.KEY_TOPIC, mTopic);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, mTopic);
        mContext.startActivity(intent);
        if (mContext.getClass().equals(ChatterTopicActivity.class)) {
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false); //去掉下划线
    }

    public static void setClickTopic(Context context, TextView textView, String content) {
        SpannableString spannableString = new SpannableString(content);
        Pattern pattern = Pattern.compile("#[\\s\\S]*#");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new TopicClickableSpan(context, matcher.group().replace("#", "")), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannableString);
    }
}