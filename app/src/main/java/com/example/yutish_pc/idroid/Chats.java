package com.example.yutish_pc.idroid;
//
/**
 * Created by Yutish-pc on 18-02-2018.
 */

public class Chats {                                                                                //required for chats activity

    private String user_name;

    public Chats() {
    }

    public Chats(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
