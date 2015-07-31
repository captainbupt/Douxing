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
package com.badou.mworking.entity.emchat;

import com.badou.mworking.database.EMChatResManager;
import com.easemob.chat.EMContact;

import android.text.TextUtils;

import com.easemob.util.HanziToPinyin;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class User extends EMContact {
    @SerializedName("name")
    String nick;
    @SerializedName("employee_id")
    String username;
    @SerializedName("imgurl")
    String avatar;
    @SerializedName("department")
    String department;
    @SerializedName("role")
    String role;

    transient int unreadMsgCount;
    transient String header;
    transient List<HanziToPinyin.Token> spell;
    transient String tag;

    public User() {
    }

    public User(String username, String nick, String avatar, long department, int role) {
        this.username = username;
        this.nick = TextUtils.isEmpty(nick) ? username : nick;
        this.avatar = avatar;
        this.department = department + "";
        this.role = role + "";
        setSpell(nick);
    }

    public void setTag(Map<Long, Department> departmentMap, Map<Integer, Role> roleMap) {
        StringBuilder builder = new StringBuilder();
        builder.append(username);
        builder.append(nick);
        for (long base = 1; base < getDepartmentId(); base *= 100l) {
            long id = getDepartmentId() / base * base;
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
        if (TextUtils.isEmpty(username)) {
            header = "#";
            return;
        }
        this.spell = HanziToPinyin.getInstance().get(username);
        this.header = String.valueOf(spell.get(0).target.charAt(0)).toUpperCase();
        if (header.charAt(0) < 'A' || header.charAt(0) > 'Z') {
            header = "#";
        }
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String s) {
        super.setUsername(s);
    }

    public Department getDepartment() {
        return EMChatResManager.getDepartment(getDepartmentId());
    }

    public List<HanziToPinyin.Token> getSpell() {
        return spell;
    }

    public long getDepartmentId() {
        return Long.parseLong(department);
    }

    public int getRole() {
        return Integer.parseInt(role);
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
