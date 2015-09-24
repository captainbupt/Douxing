package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.util.TimeTransfer;

public class UserProgressAdapter extends MyBaseRecyclerAdapter<Category, UserProgressAdapter.MyViewHolder> {

    private int mType;
    private View.OnClickListener mOnItemClickListener;

    public UserProgressAdapter(Context context, int type, View.OnClickListener onClickListener) {
        super(context);
        this.mType = type;
        mOnItemClickListener = onClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(mInflater.inflate(R.layout.adapter_user_progress, parent, false));
        if (mType == Category.CATEGORY_EXAM) {
            viewHolder.scoreTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.scoreTextView.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = getItem(position);
        holder.subjectTextView.setText(category.getSubject() + "");
        holder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, category.getTime()));
        if (mType == Category.CATEGORY_EXAM) {
            holder.scoreTextView.setText(((Exam) category).getScore() + mContext.getResources().getString(R.string.text_score));
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView timeTextView;
        TextView scoreTextView;

        MyViewHolder(View view) {
            super(view);
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_subject);
            timeTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_time);
            scoreTextView = (TextView) view.findViewById(R.id.tv_adapter_user_progress_score);
        }
    }
}
