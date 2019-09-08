package com.example.yutish_pc.idroid;

/**
 * Created by Yutish-pc on 19-02-2018.
 */

public class Requests {                                                                             //required for request fragment
    private String user_name;

    public Requests() {
    }

    public Requests(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}

