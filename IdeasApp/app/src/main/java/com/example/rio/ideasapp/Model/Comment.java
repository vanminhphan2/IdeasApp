package com.example.rio.ideasapp.Model;

/**
 * Created by User on 11/5/2017.
 */

public class Comment {
    private String Uid;
    private String CommentText;


    public Comment(){}
    public Comment(String uid, String comment){
        Uid = uid;
        CommentText = comment;
    }

    public void setCommentText(String comment) {
        CommentText = comment;
    }

    public String getCommentText() {
        return CommentText;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
