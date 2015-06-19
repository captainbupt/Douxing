package com.badou.mworking.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.badou.mworking.ChatterTopicActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterListAdapter;
import com.badou.mworking.base.BaseActionBarActivity;

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

    public static void setClickTopic(Context context, final TextView textView, String content, final ChatterListAdapter.ChatterClickListener onItemClickListener) {
        textView.setOnClickListener(onItemClickListener);
        SpannableString spannableString = new SpannableString(content);
/*        spannableString.setSpan(new NormalClickableSpan() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(textView);
            }
        }, 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);*/
        Pattern pattern = Pattern.compile("#[\\s\\S]*#");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            spannableString.setSpan(new TopicClickableSpan(context, matcher.group().replace("#", "")), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_text_blue)), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannableString);
    }

    abstract static class NormalClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false); //去掉下划线
        }
    }
}