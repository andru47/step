package com.google.sps.classes;

public class Comment {
    private String comment;
    private String nickname;
    public Comment(String comment, String nickname){
        this.comment = comment;
        this.nickname = nickname;
    }
    public String getComment(){
        return this.comment;
    }
    public String getNickName(){
        return this.nickname;
    }
}
