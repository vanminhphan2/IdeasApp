package com.example.rio.ideasapp.Model;

/**
 * Created by User on 11/9/2017.
 */

public class Message {

    private String key;
    private String uid;
    private String view;

    public Message(){}
    public Message(String Key, String Uid, String View)
    {
        key = Key;
        uid = Uid;
        view = View;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String Uid) {
        uid = Uid;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
