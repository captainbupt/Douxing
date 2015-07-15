package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.MessageCenterActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.util.TimeTransfer;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chatuidemo.Constant;
import com.badou.mworking.R;
import com.easemob.chatuidemo.adapter.ChatAllHistoryAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.swipe.delete.SwipeLayout;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends Fragment {

    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    public RelativeLayout errorItem;

    public TextView errorText;
    private boolean hidden;
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private View headView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.row_chat_history, listView, false);
        ((TextView) headView.findViewById(R.id.name)).setText(R.string.title_name_message_center);
        ((TextView) headView.findViewById(R.id.unread_msg_number)).setVisibility(View.GONE);
        headView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MessageCenterActivity.class));
            }
        });
        ((SwipeLayout) headView.findViewById(R.id.sl_adapter_message_center)).setSwipeEnabled(false);
        updateHeadView();
        listView.addHeaderView(headView);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList, this);
        // 设置adapter
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                EMConversation conversation = adapter.getItem(position - 1);
                String username = conversation.getUserName();
                // 进入聊天页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                intent.putExtra("groupId", username);
                startActivity(intent);
            }
        });

    }

    private void updateHeadView() {
        List<Object> messageCenters = MessageCenterResManager.getAllItem(getActivity());
        if (messageCenters.size() > 0) {
            ((ImageView) headView.findViewById(R.id.avatar)).setImageResource(R.drawable.icon_emchat_message_center_unread);
            ((TextView) headView.findViewById(R.id.message)).setText(String.format("你有%d条未读消息", messageCenters.size()));
            ((TextView) headView.findViewById(R.id.time)).setText(TimeTransfer.long2StringDetailDate(getActivity(), ((MessageCenter) messageCenters.get(0)).ts));
        } else {
            ((ImageView) headView.findViewById(R.id.avatar)).setImageResource(R.drawable.icon_emchat_message_center_read);
            ((TextView) headView.findViewById(R.id.message)).setText("暂无未读消息");
            ((TextView) headView.findViewById(R.id.time)).setText("");
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        updateHeadView();
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<String> illegalKey = new ArrayList<>();
        for (String key : conversations.keySet()) {
            EMConversation conversation = conversations.get(key);
            if (!conversation.isGroup() || EMChatManager.getInstance().getGroup(conversation.getUserName()) == null) {
                illegalKey.add(key);
            }
        }
        for (String key : illegalKey) {
            conversations.remove(key);
        }
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
}