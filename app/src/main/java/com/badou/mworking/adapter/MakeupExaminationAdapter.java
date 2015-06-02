package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.category.Exam;

import java.util.ArrayList;

/**
 * @author gejianfeng
 * 
 */
public class MakeupExaminationAdapter extends BaseAdapter{
	
	private ArrayList<Exam> exams;
	private LayoutInflater layoutInflater;

	public MakeupExaminationAdapter(Context context,ArrayList<Exam> exams) {
		super();
		this.exams = exams;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return exams.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		convertView = layoutInflater.inflate(R.layout.makeupexaminationadapter, null);
		TextView examTitleTv= (TextView) convertView.findViewById(R.id.exam_title_tv);
		Exam exam = exams.get(position);
		examTitleTv.setText(exam.subject);
		return convertView;
	}
}
