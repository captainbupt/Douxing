/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.model.emchat.Department;
import com.badou.mworking.model.emchat.Role;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.badou.mworking.R;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.widget.Sidebar;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.DbUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class GroupPickContactsActivity extends BaseBackActionBarActivity {
    private ListView listView;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked = false;
    private PickContactAdapter contactAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts);
        setActionbarTitle(R.string.title_name_emchat_contact);

        listView = (ListView) findViewById(R.id.list);
        ((Sidebar) findViewById(R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });

        setRightText(R.string.emchat_contact_title_right, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> members = getToBeAddMembers();
                createGroup(members.toArray(new String[members.size()]));
            }
        });
        final List<Department> departments = EMChatResManager.getDepartments(mContext);
        final List<Role> roles = EMChatResManager.getRoles(mContext);
        final List<User> contacts = EMChatResManager.getContacts(mContext);

        if (departments.size() == 0 || roles.size() == 0 || contacts.size() == 0) {
            mProgressDialog.show();
            ServiceProvider.getContacts(mContext, new VolleyListener(mContext) {
                @Override
                public void onResponseSuccess(JSONObject response) {
                    JSONObject data = response.optJSONObject(Net.DATA);
                    JSONObject roleJsonObject = data.optJSONObject("rolecfg");
                    JSONArray userArray = data.optJSONArray("usrlst");
                    getDepartmentInfo(departments, data.optJSONArray("dptcfg"), -1l);
                    Iterator<String> iterator = roleJsonObject.keys();
                    while (iterator.hasNext()) {
                        String name = iterator.next();
                        int id = Integer.parseInt(roleJsonObject.optString(name));
                        roles.add(new Role(id, name));
                    }
                    for (int ii = 0; ii < userArray.length(); ii++) {
                        JSONObject userObject = userArray.optJSONObject(ii);
                        User user = new User();
                        user.setUsername(userObject.optString("employee_id"));
                        user.setNick(userObject.optString("name"));
                        user.setDepartment(Long.parseLong(userObject.optString("department")));
                        user.setRole(Integer.parseInt(userObject.optString("role")));
                        user.setAvatar(userObject.optString("imgurl"));
                        user.setHeader(String.valueOf(user.getNick().charAt(0)));
                        contacts.add(user);
                    }
                    EMChatResManager.insertContacts(mContext, contacts);
                    EMChatResManager.insertDepartments(mContext, departments);
                    EMChatResManager.insertRoles(mContext, roles);
                    AppApplication.getInstance().setContactList(null);
                    initContactList(contacts);
                }

                @Override
                public void onCompleted() {
                    mProgressDialog.dismiss();
                }
            });
        } else {
            initContactList(contacts);
        }

    }

    private void getDepartmentInfo(List<Department> departments, JSONArray departmentArray, long parent) {
        if (departmentArray == null || departmentArray.length() == 0) {
            return;
        }
        for (int ii = 0; ii < departmentArray.length(); ii++) {
            JSONObject jsonObject = departmentArray.optJSONObject(ii);
            String name = jsonObject.optString("name");
            long id = jsonObject.optLong("dpt");
            JSONArray sonArray = jsonObject.optJSONArray("son");
            long[] sons;
            if (sonArray == null || sonArray.length() == 0) {
                sons = new long[0];
            } else {
                sons = new long[sonArray.length()];
                for (int jj = 0; jj < sonArray.length(); jj++) {
                    sons[jj] = sonArray.optJSONObject(jj).optLong("dpt");
                }
                getDepartmentInfo(departments, sonArray, id);
            }
            Department department = new Department(id, name, parent, sons);
            departments.add(department);
        }
    }

    private void initContactList(List<User> contacts) {
        // String groupName = getIntent().getStringExtra("groupName");
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// 创建群组
            isCreatingNewGroup = true;
        } else {
            // 获取此群组的成员列表
            EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
            exitingMembers = group.getMembers();
        }
        if (exitingMembers == null)
            exitingMembers = new ArrayList<String>();
        // 获取好友列表
        final List<User> alluserList = contacts;
/*        for (User user : AppApplication.getInstance().getContactList().values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME) & !user.getUsername().equals(Constant.CHAT_ROOM))
                alluserList.add(user);
        }*/

        // 对list进行排序
        Collections.sort(alluserList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return (lhs.getUsername().compareTo(rhs.getUsername()));

            }
        });

        contactAdapter = new PickContactAdapter(this, R.layout.row_contact_with_checkbox, alluserList);
        listView.setAdapter(contactAdapter);
    }

    private void createGroup(final String[] members) {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        //新建群组
        mProgressDialog.setMessage(st1);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用sdk创建群组方法
                String groupName = "群组聊天";
                String desc = "";
                try {
/*                    if (checkBox.isChecked()) {
                        //创建公开群，此种方式创建的群，可以自由加入
                        //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                        EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                    } else {*/
                    //创建不公开群
                    EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, true, 200);
//                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * 获取要被添加的成员
     *
     * @return
     */
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        int length = contactAdapter.isCheckedArray.length;
        for (int i = 0; i < length; i++) {
            String username = contactAdapter.getItem(i).getUsername();
            if (contactAdapter.isCheckedArray[i] && !exitingMembers.contains(username)) {
                members.add(username);
            }
        }

        return members;
    }

    /**
     * adapter
     */
    private class PickContactAdapter extends ContactAdapter {

        private boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<User> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
//			if (position > 0) {
            final String username = getItem(position).getUsername();
            // 选择框checkbox
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            if (exitingMembers != null && exitingMembers.contains(username)) {
                checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
            } else {
                checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
            }
            if (checkBox != null) {
                // checkBox.setOnCheckedChangeListener(null);

                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 群组中原来的成员一直设为选中状态
                        if (exitingMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        }
                        isCheckedArray[position] = isChecked;
                        //如果是单选模式
                        if (isSignleChecked && isChecked) {
                            for (int i = 0; i < isCheckedArray.length; i++) {
                                if (i != position) {
                                    isCheckedArray[i] = false;
                                }
                            }
                            contactAdapter.notifyDataSetChanged();
                        }

                    }
                });
                // 群组中原来的成员一直设为选中状态
                if (exitingMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }
            }
//			}
            return view;
        }
    }

}
