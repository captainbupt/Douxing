package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.entity.Audit;
import com.badou.mworking.widget.CornerRadiusButton;
import com.idunnololz.widgets.AnimatedExpandableListView;

import java.util.BitSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AuditExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    Context mContext;
    List<Audit> mItemList;
    View.OnClickListener mConfirmListener;
    View.OnClickListener mRefuseListener;

    public AuditExpandableAdapter(Context context, View.OnClickListener confirmListener, View.OnClickListener refuseListener) {
        mContext = context;
        mConfirmListener = confirmListener;
        mRefuseListener = refuseListener;
    }

    public List<Audit> getParentList() {
        return mItemList;
    }

    public void setData(List<Audit> items) {
        this.mItemList = items;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mItemList.size()) {
            mItemList.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public Audit getChild(int groupPosition, int childPosition) {
        return mItemList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        Audit item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_audit_item_child, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.nameTextView.setText(mContext.getString(R.string.audit_info_name) + item.getName());
        holder.departmentTextView.setText(mContext.getString(R.string.audit_info_department) + item.getDepartment());
        holder.titleTextView.setText(mContext.getString(R.string.audit_info_title) + item.getRole());
        holder.phoneTextView.setText(mContext.getString(R.string.audit_info_phone) + item.getPhone());
        holder.confirmRadiusButton.setTag(groupPosition);
        holder.refuseRadiusButton.setTag(groupPosition);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Audit getGroup(int groupPosition) {
        return mItemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder;
        Audit item = getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_audit_item_parent, parent, false);
            holder = new ParentViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ParentViewHolder) convertView.getTag();
        }
        holder.titleTextView.setText(String.format(mContext.getString(R.string.audit_item_content), item.getName()));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    class ParentViewHolder {
        @Bind(R.id.title_text_view)
        TextView titleTextView;

        public ParentViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    class ChildViewHolder {

        @Bind(R.id.name_text_view)
        TextView nameTextView;
        @Bind(R.id.department_text_view)
        TextView departmentTextView;
        @Bind(R.id.title_text_view)
        TextView titleTextView;
        @Bind(R.id.phone_text_view)
        TextView phoneTextView;
        @Bind(R.id.refuse_radius_button)
        CornerRadiusButton refuseRadiusButton;
        @Bind(R.id.confirm_radius_button)
        CornerRadiusButton confirmRadiusButton;

        public ChildViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            refuseRadiusButton.setOnClickListener(mRefuseListener);
            confirmRadiusButton.setOnClickListener(mConfirmListener);
        }
    }
}
