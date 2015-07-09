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
package com.easemob.chatuidemo.domain;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.model.emchat.Department;
import com.badou.mworking.model.emchat.Role;
import com.badou.mworking.util.Cn2Spell;
import com.easemob.applib.model.DefaultHXSDKModel;
import com.easemob.chat.EMContact;

import java.util.List;
import java.util.Map;

public class User extends EMContact {
    private int unreadMsgCount;
    private String header;
    private String avatar;
    private long department;
    private int role;
    private String spell;
    private String tag;

    public User() {
    }

    public User(String username, String nick, String avatar, long department, int role) {
        this.username = username;
        this.nick = nick;
        this.avatar = avatar;
        this.department = department;
        this.role = role;
        setSpell(username);
    }

    public void setTag(Map<Long, Department> departmentMap, Map<Integer, Role> roleMap) {
        StringBuilder builder = new StringBuilder();
        builder.append(username);
        builder.append(nick);
        for (long base = 1; base < getDepartment(); base *= 100l) {
            long id = getDepartment() / base * base;
            if (departmentMap.containsKey(id)) {
                builder.append(departmentMap.get(id).getName());
            }
        }
        if (roleMap.containsKey(getRole())) {
            builder.append(roleMap.get(getRole()).getName());
        }
        tag = builder.toString();
    }

    public String getTag() {
        return tag;
    }

    public String getHeader() {
        return header;
    }

    public void setSpell(String username) {
        this.spell = Cn2Spell.converterToFirstSpell(username);
        this.header = String.valueOf(spell.charAt(0)).toUpperCase();
        if (header.charAt(0) < 'A' || header.charAt(0) > 'Z') {
            header = "#";
        }
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public Department getDepartment(Context context) {
        return EMChatResManager.getDepartment(context, department);
    }

    public String getSpell() {
        return spell;
    }

    public long getDepartment() {
        return department;
    }

    public int getRole() {
        return role;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getUsername().equals(((User) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}
