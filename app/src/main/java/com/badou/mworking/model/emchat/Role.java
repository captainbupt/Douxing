package com.badou.mworking.model.emchat;

public class Role {
    private int id;
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Role) {
            return getId() == ((Role) o).getId();
        }
        if (o instanceof Long) {
            return getId() == (long) o;
        }
        return false;
    }
}
