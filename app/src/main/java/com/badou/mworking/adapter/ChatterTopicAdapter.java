package com.badou.mworking.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.ChatterTopic;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

/**
 * Created by Administrator on 2015/6/11.
 */
public class ChatterTopicAdapter extends MyBaseAdapter {

    private LinearLayout mEditLayout;
    private OnConfirmClickListener mOnConfirmClickListener;

    public interface OnConfirmClickListener {
        void onConfirm(String content);
    }

    public ChatterTopicAdapter(Context context, OnConfirmClickListener onConfirmClickListener) {
        super(context);
        mOnConfirmClickListener = onConfirmClickListener;
        mEditLayout = (LinearLayout) mInflater.inflate(R.layout.layout_adapter_chatter_topic_edit, null);
        final EditText contentEditText = (EditText) mEditLayout.findViewById(R.id.et_adapter_chatter_topic_edit);
        mEditLayout.findViewById(R.id.tv_adapter_chatter_topic_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onConfirm(contentEditText.getText().toString().replace("#", "").replace(" ", "").trim());
                }
                contentEditText.setText("");
            }
        });
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == 0) {
            return mEditLayout;
        }
        if (view == null || !view.getClass().equals(TextView.class)) {
            int smallSize = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_small);
            int mediumSize = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_medium);
            view = new TextView(mContext);
            view.setPadding(mediumSize, smallSize, mediumSize, smallSize);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_medium));
            ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.color_text_black));
        }
        ((TextView) view).setText("#" + ((ChatterTopic) getItem(i - 1)).key + "#");
        return view;
    }
}
