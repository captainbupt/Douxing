package com.badou.mworking.view.chatter;

import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.view.CommentView;

public interface ChatterDetailView extends CommentView {
    void setData(Chatter chatter);

}
