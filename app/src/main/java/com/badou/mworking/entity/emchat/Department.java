package com.badou.mworking.entity.emchat;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Department {
    @SerializedName("dpt")
    long id;
    @SerializedName("name")
    String name;
    @SerializedName("son")
    List<Department> sonList;

    transient long parent;
    transient long[] sons;

    public Department(long id, String name, long parent, String sons) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.sons = string2LongArray(sons);
    }

    public long getParent() {
        return parent;
    }

    public long getId() {
        return id;
    }

    public long getTopId() {
        int level = 0;
        long top = this.id;
        for (level = 0; top % 100 == 0; level++, top /= 100) ;
        top++;
        for (int jj = 0; jj < level; jj++, top *= 100) ;
        return top;
    }

    public String getName() {
        return name;
    }

    public long[] getSons() {
        if (sons == null) {
            sons = new long[sonList.size()];
            for (int ii = 0; ii < sonList.size(); ii++) {
                sons[ii] = sonList.get(ii).getId();
            }
        }
        return sons;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public List<Department> getSonList() {
        return sonList;
    }

    public String getSonString() {
        return longArray2String(getSons());
    }

    private static long[] string2LongArray(String string) {
        if (TextUtils.isEmpty(string))
            return new long[0];
        String[] temp = string.split(",");
        long[] array = new long[temp.length];
        for (int ii = 0; ii < temp.length; ii++) {
            array[ii] = Long.parseLong(temp[ii]);
        }
        return array;
    }

    private static String longArray2String(long[] array) {
        if (array == null || array.length == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < array.length; ii++) {
            builder.append(array[ii]);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
