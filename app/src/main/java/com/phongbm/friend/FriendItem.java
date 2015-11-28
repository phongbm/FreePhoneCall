package com.phongbm.friend;

import android.graphics.Bitmap;

public class FriendItem {
    private String id;
    private String phoneNumber;
    private String fullName;
    private Bitmap avatar;

    public FriendItem() {
    }

    public FriendItem(String id, String phoneNumber, String fullName, Bitmap avatar) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.avatar = avatar;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}