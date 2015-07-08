package com.easemob.chatuidemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.emchat.Department;
import com.badou.mworking.entity.emchat.Role;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PickContactsAdapter extends MyBaseAdapter<User> implements SectionIndexer, StickyListHeadersAdapter {
    private final int TYPE_ALL = 0;
    private final int TYPE_ROLE = 1;
    private final int TYPE_DEPARTMENT = 2;
    private final int TYPE_USER = 3;

    private int mCurrentType = TYPE_ALL;
    private Object mFilter = null;
    private boolean isSelected = false;

    protected List<User> mOriginUserList;
    private SparseIntArray mPositionOfSection;
    private SparseIntArray mSectionOfPosition;
    private Map<String, Boolean> mIsCheckedMap;
    private List<String> mExitingMembers;
    private OnSelectedCountChangeListener mOnSelectedCountChangeListener;

    public void setOnSelectedCountChangeListener(OnSelectedCountChangeListener mOnSelectedCountChangeListener) {
        this.mOnSelectedCountChangeListener = mOnSelectedCountChangeListener;
    }

    public interface OnSelectedCountChangeListener {
        void onSelectedCountChange(int count);
    }

    public PickContactsAdapter(Context context, List<User> objects, List<String> exitingMembers) {
        super(context, objects);
        mOriginUserList = objects;
        this.mExitingMembers = exitingMembers;
        mIsCheckedMap = new HashMap<>(objects.size());
        for (User user : objects) {
            String username = user.getUsername();
            if (exitingMembers.contains(username))
                mIsCheckedMap.put(username, true);
            else
                mIsCheckedMap.put(username, false);
        }
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new TextView(mContext);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
            int medium = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_medium);
            int micro = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_micro);
            view.setPadding(medium, micro, medium, micro);
            ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_less));
        }
        ((TextView) view).setText(getItem(i).getHeader());
        return view;
    }

    @Override
    public long getHeaderId(int i) {
        String header = getItem(i).getHeader();
        if (!TextUtils.isEmpty(header))
            return getItem(i).getHeader().charAt(0);
        else
            return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_contact_with_checkbox, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = getItem(position);
        //设置nick，demo里不涉及到完整user，用username代替nick显示
        final String username = user.getUsername();
        String header = user.getHeader();
        //显示申请与通知item
        holder.name.setText(user.getNick());
        Department department = user.getDepartment();
        holder.department.setText(department == null ? "暂无" : department.getName());
        //设置用户头像
        UserUtils.setUserAvatar(mContext, username, holder.avatar);
        // 选择框checkbox
        if (mExitingMembers != null && mExitingMembers.contains(username)) {
            holder.checkbox.setButtonDrawable(R.drawable.checkbox_bg_selector);
            holder.checkbox.setClickable(false);
        } else {
            holder.checkbox.setButtonDrawable(R.drawable.checkbox_bg_selector);
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsCheckedMap.put(username, isChecked);
                if (mOnSelectedCountChangeListener != null) {
                    mOnSelectedCountChangeListener.onSelectedCountChange(getToBeAddMembers().size());
                }
            }
        });
        holder.checkbox.setChecked(mIsCheckedMap.get(username));
        return convertView;
    }

    public int getPositionForSection(int section) {
        return mPositionOfSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return mSectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        mPositionOfSection = new SparseIntArray();
        mSectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
        list.add(mContext.getString(R.string.search_header));
        mPositionOfSection.put(0, 0);
        mSectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            String letter = getItem(i).getHeader();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                mPositionOfSection.put(section, i);
            }
            mSectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        switch (mCurrentType) {
            case TYPE_ALL:
                showAll();
                break;
            case TYPE_ROLE:
                setRole((Role) mFilter);
                break;
            case TYPE_DEPARTMENT:
                setDepartment((Department) mFilter);
                break;
            case TYPE_USER:
                setUser((User) mFilter);
                break;
            default:
                showAll();
        }
    }

    public void setRole(Role role) {
        if (role == null) {
            showAll();
        } else {
            mCurrentType = TYPE_ROLE;
            mFilter = role;
            mItemList.clear();
            for (User user : mOriginUserList) {
                if (user.getRole() == role.getId()) {
                    if (!isSelected || mIsCheckedMap.get(user.getUsername()))
                        mItemList.add(user);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setUser(User user) {
        if (user == null) {
            showAll();
        } else {
            mCurrentType = TYPE_USER;
            mFilter = user;
            mItemList.clear();
            if (!isSelected || mIsCheckedMap.get(user.getUsername()))
                mItemList.add(user);
            notifyDataSetChanged();
        }
    }

    public void setDepartment(Department department) {
        if (department == null) {
            showAll();
        } else {
            mCurrentType = TYPE_DEPARTMENT;
            mFilter = department;
            long top = department.getTopId();
            mItemList.clear();
            for (User user : mOriginUserList) {
                if (user.getDepartmentId() >= department.getId() && user.getDepartmentId() < top) {
                    if (!isSelected || mIsCheckedMap.get(user.getUsername()))
                        mItemList.add(user);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void showAll() {
        mItemList.clear();
        if (isSelected) {
            for (User user : mOriginUserList) {
                if (mIsCheckedMap.get(user.getUsername()))
                    mItemList.add(user);
            }
        } else {
            mItemList.addAll(mOriginUserList);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取要被添加的成员
     *
     * @return
     */
    public List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        int length = mOriginUserList.size();
        for (int i = 0; i < length; i++) {
            String username = mOriginUserList.get(i).getUsername();
            if (mIsCheckedMap.get(username) && !mExitingMembers.contains(username)) {
                members.add(username);
            }
        }

        return members;
    }

    static class ViewHolder {
        @InjectView(R.id.checkbox)
        CheckBox checkbox;
        @InjectView(R.id.avatar)
        ImageView avatar;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.department)
        TextView department;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
