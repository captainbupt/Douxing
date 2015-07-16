package com.badou.mworking.view;

import com.badou.mworking.entity.comment.Comment;

public interface CommentView extends BaseListView<Comment> {
    void setBottomSend();

    void setBottomReply(String name);

    void setCommentCount(int count);
}
