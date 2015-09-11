package com.badou.mworking.entity;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Audit implements ParentObject {

    @SerializedName("employee_id")
    String employee_id;
    @SerializedName("name")
    String name;
    @SerializedName("dpt")
    String department;
    @SerializedName("role")
    String role;

    @Override
    public List<Object> getChildObjectList() {
        return new ArrayList<Object>() {{
            add(Audit.this);
        }};
    }

    @Override
    public void setChildObjectList(List<Object> list) {
    }


    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return employee_id;
    }
}
