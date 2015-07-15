package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.util.TimeTransfer;

/**
 * Created by Administrator on 2015/5/29.
 */
public class UserProgressAdapter extends MyBaseAdapter {

    private int mType;

    public UserProgressAdapter(Context context, int type) {
        super(context);
        this.mType = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        /** 学习进度显示的布局 **/
        ViewHolder holder;
        Category category = (Category) getItem(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_user_progress,
                    viewGroup, false);
            holder = new ViewHolder(convertView, mType);
            convertView.setTag(holder);
        }
        holder.subjectTextView.setText(category.getSubject() + "");
        holder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, category.getTime()));
        if (mType == Category.CATEGORY_EXAM) {
            holder.scoreTextView.setText(((Exam) category).getScore() + mContext.getResources().getString(R.string.text_score));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView subjectTextView;
        TextView timeTextView;
        TextView scoreTextView;

        ViewHolder(View view, int type) {
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_subject);
            timeTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_time);
            scoreTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_score);
            if (type == Category.CATEGORY_EXAM) {
                scoreTextView.setVisibility(View.VISIBLE);
            } else {
                scoreTextView.setVisibility(View.GONE);
            }
        }
    }
}
