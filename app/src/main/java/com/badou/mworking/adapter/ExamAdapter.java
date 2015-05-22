package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Exam;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述:  在线考试列表页适配器
 */
public class ExamAdapter extends MyBaseAdapter {

    private boolean isUserCenter = false;    // 判断是不是个人中心进入

    public static boolean isHistory = false;         //判断判断是不是历史考试模块

    public ExamAdapter(Context context, boolean userCenter, boolean history) {
        super(context);
        this.isUserCenter = userCenter;
        this.isHistory = history;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Exam exam = (Exam) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_exam_item, parent, false);
        }

        TextView subject = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_subject);
        TextView department_time = ViewHolder.getVH(convertView, R.id.tv_adapter_item_dpt_date);
        TextView myscoreTv = ViewHolder.getVH(convertView, R.id.myscore);
        TextView isFinsh = ViewHolder.getVH(convertView, R.id.tv_unread);
        ImageView top = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_top);
        RelativeLayout rl_isReadbg = ViewHolder.getVH(convertView, R.id.rl_item_bg_isread);

        // 判断read字段， 已考完
        if (exam.isFinish()) {
            if (exam.isDaiPiYue()) {
                //显示:待批阅
                isFinsh.setText(mContext.getResources().getString(R.string.exam_daipiyue));
                myscoreTv.setVisibility(View.INVISIBLE);
            } else {
                //显示:已考完(判断是不是是不是个人中心进入的)
                if (!isUserCenter) {
                    isFinsh.setText(mContext.getResources().getString(R.string.exam_isFinish));
                    myscoreTv.setVisibility(View.VISIBLE);
                } else {
                    isFinsh.setText(exam.score + mContext.getResources().getString(R.string.text_score));
                    myscoreTv.setVisibility(View.INVISIBLE);
                }
            }
            isFinsh.setTextColor(mContext.getResources().getColor(R.color.color_exam_orange));
            // 未考试
        } else {
            myscoreTv.setVisibility(View.INVISIBLE);
            //显示:已过期
            if (exam.offline == Constant.OVERDUE_YES) {
                isFinsh.setText(mContext.getResources().getString(R.string.isDeadtime));
                isFinsh.setTextColor(mContext.getResources()
                        .getColor(R.color.color_exam_grey));
                //显示:未考试
            } else {
                isFinsh.setText(mContext.getResources().getString(R.string.exam_notExam));
                isFinsh.setTextColor(mContext.getResources()
                        .getColor(R.color.color_exam_grey));
            }
        }
        if (exam.top == Constant.TOP_YES && !isUserCenter) {
            top.setVisibility(View.VISIBLE);
        } else {
            top.setVisibility(View.INVISIBLE);
        }
        // 考试已完成或者已过期
        if (exam.isFinish() || (exam.offline == Constant.OVERDUE_YES)) {
            rl_isReadbg.setBackgroundResource(R.drawable.icon_read_);
        } else {
            rl_isReadbg
                    .setBackgroundResource(R.drawable.icon_unread_orange);
        }
        // 着重显示  有推送时显示已经批阅完成的试卷
        /*if(!TextUtils.isEmpty(ExamActivity.examRid)){
			if(exam.getExamId().equals(ExamActivity.examRid)){
				convertView.setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
			//设置透明的色值
			}else{
				convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
			}
		}*/
        //我的分数 * 5 / 总分 = ratBar的长度
        myscoreTv.setText(exam.score + "分");
        // 个人中心进入不显示星星
        if (isUserCenter) {
            myscoreTv.setVisibility(View.INVISIBLE);
        }
        subject.setText(exam.subject);
        if (ExamAdapter.isHistory) {
            department_time.setText(exam.credit + "学分  "
                    + TimeTransfer.long2StringDetailDate(mContext, exam.time));
        } else {
            department_time.setText(TimeTransfer.long2StringDetailDate(mContext, exam.time));
        }
        return convertView;
    }
}
