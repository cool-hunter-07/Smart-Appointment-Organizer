package com.cool.smartappointmentorganizer.model;

public class User {
    public String id;
    public String name;
    public String mobile;
    public String email;
    public String avatar;

    public User() {
    }

    public User(String id, String mobile, String email, String avatar, String name) {
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.avatar = avatar;
        this.name = name;
    }
}
