package com.badou.mworking.entity.emchat;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class ContactList {
    @SerializedName("rolecfg")
    LinkedTreeMap<String, String> roleMap;
    @SerializedName("dptcfg")
    List<Department> departmentList;
    @SerializedName("usrlst")
    List<User> userList;

    List<Role> roleList;

    public List<Role> getRoleList() {
        if (roleList == null) {
            roleList = new ArrayList<>();
            for (String key : roleMap.keySet()) {
                roleList.add(new Role(Integer.parseInt(roleMap.get(key)), key));
            }
        }
        return roleList;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public List<User> getUserList() {
        return userList;
    }
}
