package com.phongbm.friend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Friend implements Cloneable, Serializable {
    private static final String TAG = Friend.class.getSimpleName();

    private static class LazyInit {
        private static final Friend INSTANCE = new Friend();
    }

    public static Friend getInstance() {
        return LazyInit.INSTANCE;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException();
    }

    protected Object readResolve() {
        return Friend.getInstance();
    }

    private ArrayList<AllFriendItem> allFriendItems;
    private ArrayList<ActiveFriendItem> activeFriendItems;

    private Friend() {
        allFriendItems = new ArrayList<>();
        activeFriendItems = new ArrayList<>();
    }

    public void getData() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Log.i(TAG, "ParseUser.getCurrentUser() NULL");
            return;
        }
        final ArrayList<String> listFriendId = (ArrayList<String>) currentUser.get("listFriend");
        if (listFriendId == null || listFriendId.size() == 0) {
            return;
        }
        if (allFriendItems != null) {
            allFriendItems.clear();
        }
        if (activeFriendItems != null) {
            activeFriendItems.clear();
        }
        for (int i = 0; i < listFriendId.size(); i++) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(listFriendId.get(i), new GetCallback<ParseUser>() {
                @Override
                public void done(final ParseUser parseUser, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    ParseFile avatarFile = (ParseFile) parseUser.get("avatar");
                    avatarFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                            String id = parseUser.getObjectId();
                            String phoneNumber = parseUser.getUsername();
                            String fullName = parseUser.getString("fullName");
                            Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            allFriendItems.add(new AllFriendItem(id, phoneNumber, fullName, avatar));
                            Collections.sort(allFriendItems);
                            if (parseUser.getBoolean("online")) {
                                activeFriendItems.add(new ActiveFriendItem(id, phoneNumber, fullName, avatar));
                            }
                        }
                    });
                }
            });
        }
    }

    public ArrayList<AllFriendItem> getAllFriendItems() {
        return allFriendItems;
    }

    public void setAllFriendItems(ArrayList<AllFriendItem> allFriendItems) {
        this.allFriendItems = allFriendItems;
    }

    public ArrayList<ActiveFriendItem> getActiveFriendItems() {
        return activeFriendItems;
    }

    public void setActiveFriendItems(ArrayList<ActiveFriendItem> activeFriendItems) {
        this.activeFriendItems = activeFriendItems;
    }

}