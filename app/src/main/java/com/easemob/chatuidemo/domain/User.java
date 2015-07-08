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

import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.entity.emchat.Department;
import com.badou.mworking.util.Cn2Spell;
import com.easemob.chat.EMContact;

public class User extends EMContact {
    private int unreadMsgCount;
    private String header;
    private String avatar;
    private long department;
    private int role;
    private String spell;

    public User() {
    }

    public User(String username) {
        this.username = username;
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

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    @Override
    public void setUsername(String s) {
        super.setUsername(s);
    }

    public Department getDepartment() {
        return EMChatResManager.getDepartment( department);
    }

    public String getSpell() {
        return spell;
    }

    @Override
    public void setNick(String s) {
        super.setNick(s);
        setSpell(s);
    }

    public long getDepartmentId() {
        return department;
    }

    public int getRole() {
        return role;
    }

    public void setDepartment(long department) {
        this.department = department;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
