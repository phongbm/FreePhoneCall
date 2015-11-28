package com.phongbm.friend;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public class AllFriendItem extends FriendItem implements Comparable {

    public AllFriendItem(String id, String phoneNumber, String fullName, Bitmap avatar) {
        super(id, phoneNumber, fullName, avatar);
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return this.getFullName().toLowerCase().compareTo(
                ((AllFriendItem) another).getFullName().toLowerCase());
    }

    @Override
    public boolean equals(Object object) {
        return object != null
                && object instanceof AllFriendItem
                && this.getId().equals(((AllFriendItem) object).getId());
    }

}