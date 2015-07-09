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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.model.emchat.Department;
import com.badou.mworking.model.emchat.Role;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.badou.mworking.R;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.adapter.MessageAdapter;
import com.easemob.chatuidemo.adapter.PickContactsAdapter;
import com.easemob.chatuidemo.adapter.PickContactsAutoCompleteAdapter;
import com.easemob.chatuidemo.db.DemoDBManager;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.widget.Sidebar;
import com.easemob.exceptions.EaseMobException;

import org.json.JSONArray;
import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class GroupPickContactsActivity extends BaseBackActionBarActivity {
    private StickyListHeadersListView listView;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;

    private PickContactsAdapter contactAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers;
    private TextView selectedNumberTextView;

    ImageButton clearSearch;
    AutoCompleteTextView query;

    List<Department> departments;
    List<Role> roles;
    List<User> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts);
        setActionbarTitle(R.string.title_name_emchat_contact);


        listView = (StickyListHeadersListView) findViewById(R.id.list);
        ((Sidebar) findViewById(R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getAdapter().getItem(position);
                if (!exitingMembers.contains(user.getUsername())) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    checkBox.toggle();
                }

            }
        });

        initSearch();
        initHeader();

        setRightText(R.string.emchat_contact_title_right, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> members = contactAdapter.getToBeAddMembers();
                if (exitingMembers.size() > 0) {
                    save(members.toArray(new String[members.size()]));
                } else if (members.size() > 0) {
                    createGroup(members.toArray(new String[members.size()]));
                } else {
                    ToastUtil.showToast(mContext, R.string.group_member_empty);
                }
            }
        });

        departments = EMChatResManager.getDepartments(mContext);
        roles = EMChatResManager.getRoles(mContext);
        contacts = EMChatResManager.getContacts(mContext);
        for (int ii = 0; ii < contacts.size(); ii++) {
            if (contacts.get(ii).getUsername().equals(EMChatManager.getInstance().getCurrentUser())) {
                contacts.remove(ii);
                break;
            }
        }
        initContactList(contacts);
    }

    private void initHeader() {
        ((CheckBox) findViewById(R.id.filter_selected)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                contactAdapter.setSelected(b);
            }
        });
        selectedNumberTextView = (TextView) findViewById(R.id.selected_number);
        setSelectedNumber(0);
    }

    private void setSelectedNumber(int number) {
        String numberStr = number + "";
        SpannableString spannableString = new SpannableString(String.format(getString(R.string.filter_selected), number));
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_blue)), 2, 2 + numberStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        selectedNumberTextView.setText(spannableString);
    }

    private void initSearch() {
        //搜索框
        query = (AutoCompleteTextView) findViewById(R.id.query);
        query.setHint(R.string.search);
        query.setThreshold(0);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
    }

    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void initContactList(List<User> contacts) {
        query.setAdapter(new PickContactsAutoCompleteAdapter(mContext, contacts, departments, roles, new ArrayList<>()));
        query.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object object = adapterView.getAdapter().getItem(i);
                if (object instanceof Role) {
                    Role role = (Role) object;
                    contactAdapter.setRole(role);
                    query.setText(role.getName());
                } else if (object instanceof Department) {
                    Department department = (Department) object;
                    contactAdapter.setDepartment(department);
                    query.setText(department.getName());
                } else if (object instanceof User) {
                    User user = (User) object;
                    contactAdapter.setUser(user);
                    query.setText(user.getNick());
                }
            }
        });
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    contactAdapter.showAll();
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// 创建群组
            isCreatingNewGroup = true;
        } else {
            // 获取此群组的成员列表
            EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
            exitingMembers = group.getMembers();
        }
        if (exitingMembers == null)
            exitingMembers = new ArrayList<>();
        // 对list进行排序
        Collections.sort(contacts, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return (lhs.getSpell().compareTo(rhs.getSpell()));

            }
        });

        contactAdapter = new PickContactsAdapter(this, contacts, exitingMembers);
        listView.setAdapter(contactAdapter);
        contactAdapter.setOnSelectedCountChangeListener(new PickContactsAdapter.OnSelectedCountChangeListener() {
            @Override
            public void onSelectedCountChange(int count) {
                setSelectedNumber(count);
            }
        });
    }

    public void save(final String[] members) {
        setResult(RESULT_OK, new Intent().putExtra("newmembers", members));
        finish();
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
                String account = ((AppApplication) mContext.getApplicationContext()).getUserInfo().account;
                String groupName = account + "创建的群组聊天";
                String desc = "";
                try {
                    EMGroup emGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, true, 200);
                    //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
                    EMConversation conversation = EMChatManager.getInstance().getConversation(emGroup.getGroupId());
                    //创建一条文本消息
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    //如果是群聊，设置chattype,默认是单聊
                    message.setChatType(EMMessage.ChatType.GroupChat);
                    message.setAttribute(MessageAdapter.KEY_HELLO_MESSAGE, "1");
                    //设置消息body
                    StringBuilder body = new StringBuilder(account);
                    body.append("邀请了");
                    for (int ii = 0; ii < members.length; ii++) {
                        body.append(AppApplication.getInstance().getContactList().get(members[ii]).getNick() + "、");
                    }
                    body.deleteCharAt(body.length() - 1);
                    TextMessageBody txtBody = new TextMessageBody(body.toString());
                    message.addBody(txtBody);
                    //设置接收人
                    message.setReceipt(emGroup.getGroupId());
                    //把消息加入到此会话对象中
                    conversation.addMessage(message);
                    //发送消息
                    EMChatManager.getInstance().sendMessage(message, null);
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
}
