package com.badou.mworking.model.emchat;

import android.text.TextUtils;

public class Department {
    private long id;
    private String name;
    private long parent;
    private long[] sons;

    public Department(long id, String name, long parent, long[] sons) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.sons = sons;
    }

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

    public String getName() {
        return name;
    }

    public long[] getSons() {
        return sons;
    }

    public String getSonString() {
        return longArray2String(sons);
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
