package com.badou.mworking.view.chatter;

import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.view.CommentView;
import com.badou.mworking.view.StoreItemView;

import java.util.List;

public interface ChatterDetailView extends CommentView, StoreItemView {
    void setData(Chatter chatter);

    void setStore(boolean isStore);

}
