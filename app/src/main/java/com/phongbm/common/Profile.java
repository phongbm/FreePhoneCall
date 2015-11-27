package com.phongbm.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.phongbm.freephonecall.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Profile implements Cloneable, Serializable {
    private static final String TAG = Profile.class.getSimpleName();

    private static class LazyInit {
        private static final Profile INSTANCE = new Profile();
    }

    public static Profile getInstance() {
        return LazyInit.INSTANCE;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException();
    }

    protected Object readResolve() {
        return Profile.getInstance();
    }

    private String phoneNumber;
    private String fullName;
    private String email;
    private Bitmap avatar;

    private Profile() {
        phoneNumber = null;
        fullName = null;
        email = null;
        avatar = null;
    }

    public void getData(Context context) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Log.i(TAG, "ParseUser.getCurrentUser() NULL");
            return;
        }

        phoneNumber = currentUser.getUsername();
        fullName = currentUser.getString("fullName");
        if (fullName == null) {
            fullName = "No Name";
            currentUser.put("fullName", "No Name");
            currentUser.saveInBackground();
        }
        email = currentUser.getEmail();
        if (email == null) {
            email = "No Email";
            currentUser.setEmail("No Email");
            currentUser.saveInBackground();
        }

        ParseFile avatarFile = (ParseFile) currentUser.get("avatar");
        if (avatarFile != null) {
            avatarFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                }
            });
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_avatar_default);
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            if (bytes != null) {
                ParseFile parseFile = new ParseFile(bytes);
                currentUser.put("avatar", parseFile);
                currentUser.saveInBackground();
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

}