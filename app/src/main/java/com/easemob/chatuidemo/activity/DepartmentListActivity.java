package com.easemob.chatuidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.entity.emchat.Department;
import com.badou.mworking.util.DensityUtil;
import com.easemob.chatuidemo.domain.User;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class DepartmentListActivity extends BaseBackActionBarActivity {
    @InjectView(R.id.bottom_list_view)
    ListView mBottomListView;
    @InjectView(R.id.left_list_view)
    ListView mLeftListView;
    @InjectView(R.id.right_list_view)
    ListView mRightListView;

    DepartmentAdapter mBottomAdapter;
    DepartmentAdapter mLeftAdapter;
    DepartmentAdapter mRightAdapter;

    private int leftWidth;
    private int rightWidth;

    List<User> contacts;

    private final int DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        ButterKnife.inject(this);
        int screeWidth = DensityUtil.getInstance().getScreenWidth();
        leftWidth = (int) (screeWidth * 0.45f);
        rightWidth = screeWidth - leftWidth;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rightWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.RIGHT;
        mRightListView.setLayoutParams(lp);
        contacts = EMChatResManager.getContacts();
        mBottomAdapter = new DepartmentAdapter(mContext);
        mLeftAdapter = new DepartmentAdapter(mContext);
        mRightAdapter = new DepartmentAdapter(mContext);
        mBottomListView.setAdapter(mBottomAdapter);
        mLeftListView.setAdapter(mLeftAdapter);
        mRightListView.setAdapter(mRightAdapter);
        List<Department> rootDepartments = EMChatResManager.getChildDepartments(-1);
        mLeftAdapter.setList(rootDepartments);
        ViewHelper.setTranslationX(mRightListView, rightWidth);
    }

    @OnItemClick(R.id.left_list_view)
    void leftClick(AdapterView<?> adapterView, View view, int i, long l) {
        int origin = mRightAdapter.getCount();
        Department department = (Department) adapterView.getAdapter().getItem(i);
        List<Department> sons = EMChatResManager.getChildDepartments(department.getId());
        mRightAdapter.setList(sons);
        int after = mRightAdapter.getCount();
        if (origin == 0 && after > 0) {
            ValueAnimator rightAnimator = ObjectAnimator.ofFloat(mRightListView, "translationX", rightWidth, 0);
            rightAnimator.setDuration(DURATION);
            rightAnimator.start();
        } else if (origin > 0 && after == 0) {
            ValueAnimator rightAnimator = ObjectAnimator.ofFloat(mRightListView, "translationX", 0, rightWidth);
            rightAnimator.setDuration(DURATION);
            rightAnimator.start();
        }
    }

    @OnItemClick(R.id.right_list_view)
    void rightClick(AdapterView<?> adapterView, View view, int i, long l) {
        Department department = (Department) adapterView.getAdapter().getItem(i);
        List<Department> sons = EMChatResManager.getChildDepartments( department.getId());
        if (sons != null && sons.size() > 0) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mLeftListView, "translationX", leftWidth, 0),
                    ObjectAnimator.ofFloat(mRightListView, "translationX", rightWidth, 0));
            set.start();
            mBottomAdapter.setList(mLeftAdapter.getItemList());
            mLeftAdapter.setList(mRightAdapter.getItemList());
            mRightAdapter.setList(sons);
        }
    }

    private void backLevel() {
        long parentId = mLeftAdapter.getItem(0).getParent();
        if (parentId == -1) {
            if (ViewHelper.getTranslationX(mRightListView) == 0) {
                ValueAnimator rightAnimator = ObjectAnimator.ofFloat(mRightListView, "translationX", 0, rightWidth);
            }
        } else {
            final List<Department> departments = EMChatResManager.getChildDepartments(EMChatResManager.getDepartment(parentId).getParent());
            mBottomAdapter.setList(departments);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mLeftListView, "translationX", 0, leftWidth),
                    ObjectAnimator.ofFloat(mRightListView, "translationX", 0, rightWidth));
            set.setDuration(DURATION);
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mRightAdapter.setList(mLeftAdapter.getItemList());
                    mLeftAdapter.setList(departments);
                    ViewHelper.setTranslationX(mLeftListView, 0);
                    ViewHelper.setTranslationX(mRightListView, 0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        backLevel();
    }

    static class DepartmentAdapter extends MyBaseAdapter<Department> {

        public DepartmentAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.adapter_department_list, viewGroup, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Department department = getItem(i);
            viewHolder.mSubject.setText(department.getName());
            if (department.getSons() != null && department.getSons().length > 0) {
                viewHolder.mMore.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mMore.setVisibility(View.INVISIBLE);
            }
            return view;
        }

        static class ViewHolder {
            @InjectView(R.id.checkbox)
            CheckBox mCheckbox;
            @InjectView(R.id.subject)
            TextView mSubject;
            @InjectView(R.id.more)
            ImageView mMore;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}

