package com.easemob.chatuidemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.emchat.Department;
import com.badou.mworking.model.emchat.Role;
import com.easemob.chatuidemo.domain.User;

import java.util.ArrayList;
import java.util.List;

public class PickContactsAutoCompleteAdapter extends ArrayAdapter<Object> {

    List<User> mUserList;
    List<Department> mDepartmentList;
    List<Role> mRoleList;
    List<Object> mResultList;
    MyFilter mFilter = new MyFilter();
    Context mContext;


    public PickContactsAutoCompleteAdapter(Context context, List<User> users, List<Department> departments, List<Role> roles, List<Object> resultList) {
        super(context, android.R.layout.simple_list_item_1, resultList);
        this.mContext = context;
        this.mUserList = users;
        this.mDepartmentList = departments;
        this.mRoleList = roles;
        this.mResultList = resultList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            TextView textView = new TextView(mContext);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_less));
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            view = textView;
        }
        Object o = getItem(i);
        if (o instanceof Role) {
            ((TextView) view).setText(((Role) o).getName());
        } else if (o instanceof Department) {
            ((TextView) view).setText(((Department) o).getName());
        } else if (o instanceof User) {
            ((TextView) view).setText(((User) o).getNick());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(charSequence)) {
                results.values = null;
                results.count = 0;
            } else {
                mResultList.clear();
                for (Role role : mRoleList) {
                    if (role.getName().contains(charSequence)) {
                        mResultList.add(role);
                    }
                }
                for (Department department : mDepartmentList) {
                    if (department.getName().contains(charSequence)) {
                        mResultList.add(department);
                    }
                }
                for (User user : mUserList) {
                    if (user.getNick().contains(charSequence) || user.getUsername().contains(charSequence)) {
                        mResultList.add(user);
                    }
                }
                results.values = mResultList;
                results.count = mResultList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
