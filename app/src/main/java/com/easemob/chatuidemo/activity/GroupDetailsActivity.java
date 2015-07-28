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
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.user.UserInfo;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.DefaultHXSDKModel;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.badou.mworking.R;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.adapter.MessageAdapter;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.widget.ExpandGridView;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;

public class GroupDetailsActivity extends BaseBackActionBarActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
    private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;

    String longClickUsername = null;

    private ExpandGridView userGridview;
    private String groupId;
    private Button exitBtn;
    private Button deleteBtn;
    private EMGroup group;
    private GridAdapter adapter;
    private int referenceWidth;
    private int referenceHeight;

    private RelativeLayout rl_switch_block_groupmsg;
    /**
     * 屏蔽群消息imageView
     */
    private ImageView iv_switch_block_groupmsg;
    /**
     * 关闭屏蔽群消息imageview
     */
    private ImageView iv_switch_unblock_groupmsg;

    private TextView tv_group_member_number;

    public static GroupDetailsActivity instance;

    String st = "";
    // 清空所有聊天记录
    private RelativeLayout clearAllHistory;
    //private RelativeLayout blacklistLayout;
    private RelativeLayout changeGroupNameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLeft(R.drawable.button_title_bar_back_grey);
        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if (group == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_group_details);
        instance = this;
        st = getResources().getString(R.string.people);
        clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        userGridview = (ExpandGridView) findViewById(R.id.gridview);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
        tv_group_member_number = (TextView) findViewById(R.id.tv_group_member_number);
        //blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
        changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);

        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);

        iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);

        rl_switch_block_groupmsg.setOnClickListener(this);

        Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
        referenceWidth = referenceDrawable.getIntrinsicWidth();
        referenceHeight = referenceDrawable.getIntrinsicHeight();

        tv_group_member_number.setText(group.getMembers().size() + "/" + group.getMaxUsers());
        if (group.getOwner() == null || "".equals(group.getOwner())
                || !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            //blacklistLayout.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
        }
        // 如果自己是群主，显示解散按钮
        if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
        setGroupTitle(group);

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        adapter = new GridAdapter(this, R.layout.grid, members);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        // 设置OnTouchListener
        userGridview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            //adapter.isInDeleteMode = false;
                            //adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        clearAllHistory.setOnClickListener(this);
        //blacklistLayout.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);

    }

    private void sendTipMessage(String groupId, String content, String attribute, String value) {
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(groupId);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MessageAdapter.KEY_HELLO_MESSAGE, "1");
        if (!TextUtils.isEmpty(attribute)) {
            message.setAttribute(attribute, value);
        }
        TextMessageBody txtBody = new TextMessageBody(content);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(groupId);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, null);
    }

    private void sendTipMessage(String groupId, String content) {
        sendTipMessage(groupId, content, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
        String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
        final String st9 = getResources().getString(R.string.failed_to_move_into);

        final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
        if (resultCode == RESULT_OK) {
            mProgressDialog.setMessage(st1);
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
                    mProgressDialog.setMessage(st1);
                    mProgressDialog.show();
                    addMembersToGroup(newmembers);
                    break;
                case REQUEST_CODE_EXIT: // 退出群
                    mProgressDialog.setMessage(st2);
                    mProgressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
                    mProgressDialog.setMessage(st3);
                    mProgressDialog.show();
                    deleteGrop();
                    break;
                case REQUEST_CODE_CLEAR_ALL_HISTORY:
                    // 清空此群聊的聊天记录
                    mProgressDialog.setMessage(st4);
                    mProgressDialog.show();
                    clearGroupHistory();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData)) {
                        mProgressDialog.setMessage(st5);
                        mProgressDialog.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    EMGroupManager.getInstance().changeGroupName(groupId, returnData);
                                    StringBuilder body = new StringBuilder();
                                    body.append("群主将群名称改为 " + returnData);
                                    sendTipMessage(groupId, body.toString(), "groupname", returnData);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            group.setGroupName(returnData);
                                            setGroupTitle(group);
                                            mProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    break;
                case REQUEST_CODE_ADD_TO_BALCKLIST:
                    mProgressDialog.setMessage(st8);
                    mProgressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMGroupManager.getInstance().blockUser(groupId, longClickUsername);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        refreshMembers();
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), stsuccess, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HXSDKHelper.getInstance().getNotifier().reset();
    }

    private void setGroupTitle(EMGroup group) {
        setActionbarTitle(group.getGroupName());
        /*if (group.getGroupName().length() > 10) {
            setActionbarTitle(group.getGroupName().substring(0, 11) + "...(" + group.getAffiliationsCount() + st);
        } else {
            setActionbarTitle(group.getGroupName() + "(" + group.getAffiliationsCount() + st);
        }*/
    }

    private void refreshMembers() {
        adapter.clear();
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        adapter.addAll(members);
        tv_group_member_number.setText(group.getMembers().size() + "/" + group.getMaxUsers());
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
                REQUEST_CODE_EXIT_DELETE);

    }

    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {

        EMChatManager.getInstance().clearConversation(group.getGroupId());
        mProgressDialog.dismiss();
        // adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));

    }

    /**
     * 退出群组
     */
    private void exitGrop() {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitFromGroup(groupId);
                    String name = UserInfo.getUserInfo().getName();
                    sendTipMessage(groupId, name + " 退出了该群");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     */
    private void deleteGrop() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        new Thread(new Runnable() {

            public void run() {
                try {
                    // 创建者调用add方法
                    if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                        EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
                    } else {
                        // 一般成员调用invite方法
                        EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
                    }
                    if (newmembers != null && newmembers.length > 0) {
                        String username = UserInfo.getUserInfo().getName();
                        StringBuilder body = new StringBuilder(username);
                        body.append("邀请了");
                        for (int ii = 0; ii < newmembers.length; ii++) {
                            User user = EMChatEntity.getInstance().getContactList().get(newmembers[ii]);
                            body.append(user == null ? newmembers[ii] : user.getNick());
                            body.append("、");
                        }
                        body.deleteCharAt(body.length() - 1);
                        sendTipMessage(groupId, body.toString());
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                            setGroupTitle(group);
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st6 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        String st6 = getResources().getString(R.string.Is_unblock);
        final String st7 = getResources().getString(R.string.remove_group_of);
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg: // 屏蔽群组
                if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                    EMLog.d(TAG, "change to unblock group msg");
                    mProgressDialog.setMessage(st6);
                    mProgressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                List<String> current = ((DefaultHXSDKModel) DemoHXSDKHelper.getInstance().getModel()).getDisabledGroups();
                                if (current == null)
                                    current = new ArrayList<>();
                                current.remove(groupId);
                                ((DefaultHXSDKModel) DemoHXSDKHelper.getInstance().getModel()).setDisabledGroups(current);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                                        iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }
                    }).start();

                } else {
                    String st8 = getResources().getString(R.string.group_is_blocked);
                    final String st9 = getResources().getString(R.string.group_of_shielding);
                    EMLog.d(TAG, "change to block group msg");
                    mProgressDialog.setMessage(st8);
                    mProgressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                List<String> current = ((DefaultHXSDKModel) DemoHXSDKHelper.getInstance().getModel()).getDisabledGroups();
                                if (current == null)
                                    current = new ArrayList<>();
                                current.add(groupId);
                                ((DefaultHXSDKModel) DemoHXSDKHelper.getInstance().getModel()).setDisabledGroups(current);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                                        iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        if (group.getOwner().equals(EMChatEntity.getInstance().getUserName())) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.group_of_shielding_owner), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                        }
                    }).start();
                }
                break;

            case R.id.clear_all_history: // 清空聊天记录
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                Intent intent = new Intent(GroupDetailsActivity.this, AlertDialog.class);
                intent.putExtra("cancel", true);
                intent.putExtra("titleIsCancel", true);
                intent.putExtra("msg", st9);
                startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
                break;

            /*case R.id.rl_blacklist: // 黑名单列表
                startActivity(new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class).putExtra("groupId", groupId));
                break;*/

            case R.id.rl_change_group_name: // 修改群名称
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
                break;

            default:
                break;
        }

    }

    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     */
    private class GridAdapter extends ArrayAdapter<String> {

        private int res;
        public boolean isInDeleteMode;
        private List<String> objects;

        public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            res = textViewResourceId;
            isInDeleteMode = false;
        }

        public void addAll(List<String> members) {
            objects.addAll(members);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 最后一个item，减人按钮
            if (position == super.getCount() + 1) {
                holder.textView.setText("");
                // 设置成删除按钮
                holder.imageView.setImageResource(R.drawable.smiley_minus_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
                if (isInDeleteMode) {
                    // 正处于删除模式下，隐藏删除按钮
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    // 正常模式
                    convertView.setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                }
                final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
                holder.imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EMLog.d(TAG, st10);
                        isInDeleteMode = true;
                        notifyDataSetChanged();
                    }
                });
            } else if (position == super.getCount()) { // 添加群组成员按钮
                holder.textView.setText("");
                holder.imageView.setImageResource(R.drawable.smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
                // 如果不是创建者或者没有相应权限
                if (!group.isAllowInvites() && !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    // 正处于删除模式下,隐藏添加按钮
                    if (isInDeleteMode) {
                        convertView.setVisibility(View.INVISIBLE);
                    } else {
                        convertView.setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                    }
                    final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                    holder.imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            startActivityForResult(
                                    (new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER);
                        }
                    });
                }
            } else { // 普通item，显示群组成员
                final String username = getItem(position);
                convertView.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);
//				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
//				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
//				button.setCompoundDrawables(null, avatar, null, null);
                User user = EMChatEntity.getInstance().getContactList().get(username);
                if (user == null) {
                    holder.textView.setText(username);
                } else {
                    holder.textView.setText(user.getNick());
                }
                EMChatEntity.setUserAvatar(getContext(), username, holder.imageView);
                // demo群组成员的头像都用默认头像，需由开发者自己去设置头像
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                }
                final String st12 = getResources().getString(R.string.not_delete_myself);
                final String st13 = getResources().getString(R.string.Are_removed);
                final String st14 = getResources().getString(R.string.Delete_failed);
                final String st15 = getResources().getString(R.string.confirm_the_members);
                holder.imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                startActivity(new Intent(GroupDetailsActivity.this, AlertDialog.class).putExtra("msg", st12));
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            EMLog.d("group", "remove user from group:" + username);
                            deleteMembersFromGroup(username);
                        } else {
                            // 正常情况下点击user，可以进入用户详情或者聊天页面等等
                            // startActivity(new
                            // Intent(GroupDetailsActivity.this,
                            // ChatActivity.class).putExtra("userId",
                            // user.getUsername()));

                        }
                    }

                    /**
                     * 删除群成员
                     *
                     * @param username
                     */
                    protected void deleteMembersFromGroup(final String username) {
                        final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
                        deleteDialog.setMessage(st13);
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // 删除被选中的成员
                                    EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
                                    // isInDeleteMode = false;
                                    User user = EMChatEntity.getInstance().getContactList().get(username);
                                    sendTipMessage(groupId, (user == null ? username : user.getNick()) + " 移出了该群");

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            deleteDialog.dismiss();
                                            refreshMembers();
                                            setGroupTitle(group);
                                        }
                                    });
                                } catch (final Exception e) {
                                    deleteDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).start();
                    }
                });

/*                button.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (EMChatManager.getInstance().getCurrentUser().equals(username))
                            return true;
                        if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                            Intent intent = new Intent(GroupDetailsActivity.this, AlertDialog.class);
                            intent.putExtra("msg", st15);
                            intent.putExtra("cancel", true);
                            startActivityForResult(intent, REQUEST_CODE_ADD_TO_BALCKLIST);
                            longClickUsername = username;
                        }
                        return false;
                    }
                });*/
            }
            return convertView;
        }

        @Override
        public int getCount() {
            // 如果不是创建者或者没有相应权限，不提供加减人按钮
            if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                return super.getCount() + 1;
            } else {
                return super.getCount() + 2;
            }
        }
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
                    // 更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            setGroupTitle(group);
                            refreshMembers();
                            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                                // 显示解散按钮
                                exitBtn.setVisibility(View.GONE);
                                deleteBtn.setVisibility(View.VISIBLE);
                            } else {
                                // 显示退出按钮
                                exitBtn.setVisibility(View.VISIBLE);
                                deleteBtn.setVisibility(View.GONE);
                            }

                            // update block
                            EMLog.d(TAG, "group msg is blocked:" + group.getMsgBlocked());
                            List<String> current = ((DefaultHXSDKModel) DemoHXSDKHelper.getInstance().getModel()).getDisabledGroups();
                            if (current == null)
                                current = new ArrayList<>();
                            if (current.contains(groupId)) {
                                iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                                iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
                            } else {
                                iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                                iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (adapter.isInDeleteMode) {
            adapter.isInDeleteMode = false;
            adapter.notifyDataSetChanged();
        } else {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

}