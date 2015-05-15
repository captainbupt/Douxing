package com.badou.mworking.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.badou.mworking.database.MTrainingDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 类:  <code> Subject </code>
 * 功能描述:  答题卡实体类，保存答题的信息
 * 创建人:  葛建锋
 * 创建日期: 2014年8月19日 下午7:46:30
 * 开发环境: JDK7.0
 */
public class Subject {
	
	public final static String SEPERATOR_OPTIONS = "\t";
	public final static String SEPERATOR_ANSWERS = ",";
	private int id;        
	private String content;      // 标题内容
	private List<String> options;
	private int score = 0;
	private List<Integer> answers;     //自己选择的答案
	private boolean isCorrect = false;       //该题是否选择正确
	private List<Integer> rightAnswers;   //正确答案
	
	public List<Integer> getRightAnswers() {
		return rightAnswers;
	}

	public void setRightAnswers(List<Integer> rightAnswers) {
		this.rightAnswers = rightAnswers;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public void addAnswers(int answer) {
		if (this.answers == null) {
			this.answers = new ArrayList<>();
		}
		this.answers.add(answer);
	}

	public void setAnswers(List<Integer> answers) {
		this.answers = answers;
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(MTrainingDBHelper.EXAM_ID_CONTENT, content);
		values.put("choice", stringList2String(options));
		values.put("isCorrect", isCorrect ? 1 : 0);
		values.put("answer", intList2String(answers));
		values.put("score", score);
		values.put("right",
				intList2String(rightAnswers));
		return values;
	}

	public Subject() {
		super();
	}

	public Subject(Cursor c) {
		super();
		this.id = c.getInt(c.getColumnIndex(MTrainingDBHelper.PRIMARY_ID));
		this.content = c.getString(c
				.getColumnIndex(MTrainingDBHelper.EXAM_ID_CONTENT));
		setOptions(c.getString(c
				.getColumnIndex("choice")));
		this.isCorrect = c.getInt(c
				.getColumnIndex("isCorrect")) > 0;
		this.score = c
				.getInt(c.getColumnIndex("score"));
		setAnswers(c.getString(c
				.getColumnIndex("answer")));
		setRightAnswers(c.getString(c
				.getColumnIndex("right")));
	}

	public Subject(int id, String content, String options, int score) {
		super();
		this.id = id;
		this.content = content;
		setOptions(options);
		this.score = score;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOptionsString() {
		return stringList2String(this.options);
	}

	public List<String> getOptions() {
		if (options == null)
			options = new ArrayList<>();
		return options;
	}

	public void addRightAnswers(int rightAnswer) {
		if (this.rightAnswers == null) {
			this.rightAnswers = new ArrayList<>();
		}
		this.rightAnswers.add(rightAnswer);
	}

	public void addOptions(String option) {
		if (this.options == null) {
			this.options = new ArrayList<>();
		}
		this.options.add(option);
	}

	public void setOptions(String optionString) {
		this.options = string2ArrayString(optionString);
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getRightAnswerString() {
		return intList2String(rightAnswers);
	}

	public String getAnswerString() {
		return intList2String(answers);
	}

	public List<Integer> getAnswers() {
		return answers;
	}

	public void setRightAnswers(String answerString) {
		this.rightAnswers = string2ArrayInt(answerString);
	}

	public void setAnswers(String answerString) {
		this.answers = string2ArrayInt(answerString);
	}

	public static List<Integer> string2ArrayInt(String orgString) {
		if (TextUtils.isEmpty(orgString))
			return null;
		String[] orgStringArray;
		if (orgString.contains("\t")) {
			orgStringArray = orgString.split("\t");
		} else {
			orgStringArray = orgString.split(SEPERATOR_ANSWERS);
		}
		int length = orgStringArray.length;
		List<Integer> resultList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			resultList.add(Integer.parseInt(orgStringArray[i]));
		}
		return resultList;
	}

	public static List<String> string2ArrayString(String orgString) {
		if (TextUtils.isEmpty(orgString))
			return null;
		String[] orgStringArray = orgString.split(SEPERATOR_OPTIONS);
		int length = orgStringArray.length;
		List<String> resultList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			resultList.add(orgStringArray[i]);
		}
		return resultList;
	}

	public static String stringList2String(List<String> orgList) {
		if (orgList == null)
			return "";
		String resultString = "";
		for (Object temp : orgList) {
			if (resultString.equals("")) {
				resultString = temp.toString();
			} else {
				resultString += SEPERATOR_OPTIONS + temp;
			}
		}
		return resultString;
	}

	public static String intList2String(List<Integer> orgList) {
		if (orgList == null)
			return "";
		String resultString = "";
		for (Object temp : orgList) {
			if (resultString.equals("")) {
				resultString = temp.toString();
			} else {
				resultString += SEPERATOR_ANSWERS + temp;
			}
		}
		return resultString;
	}
}
