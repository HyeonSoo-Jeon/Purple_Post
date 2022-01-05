package com.cookandroid.youtubefirebaseprojectmy.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comments {

    private String nickname;
    private String contents;
    @ServerTimestamp
    private Date date;
    private String uid;
    private String commentId;
    private String docId;


    public Comments() {
    }

    public Comments(String nickname, String contents, Date date, String uid, String commentId, String docId) {
        this.nickname = nickname;
        this.contents = contents;
        this.date = date;
        this.uid = uid;
        this.commentId = commentId;
        this.docId = docId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "nickname='" + nickname + '\'' +
                ", contents='" + contents + '\'' +
                ", date=" + date +
                ", uid='" + uid + '\'' +
                ", commentId='" + commentId + '\'' +
                ", docId='" + docId + '\'' +
                '}';
    }
}
