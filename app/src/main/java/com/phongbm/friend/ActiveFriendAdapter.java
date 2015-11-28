package com.phongbm.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phongbm.common.OnShowPopupMenu;
import com.phongbm.freephonecall.R;
import com.phongbm.libraries.CircleImageView;

import java.util.ArrayList;

public class ActiveFriendAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<ActiveFriendItem> activeFriendItems;
    private OnShowPopupMenu onShowPopupMenu;

    public ActiveFriendAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        activeFriendItems = Friend.getInstance().getActiveFriendItems();
    }

    @Override
    public int getCount() {
        return activeFriendItems.size();
    }

    @Override
    public ActiveFriendItem getItem(int position) {
        return activeFriendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_active_friend, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgAvatar = (CircleImageView) convertView.findViewById(R.id.img_avatar);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.menu = (ImageView) convertView.findViewById(R.id.menu);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imgAvatar.setImageBitmap(activeFriendItems.get(position).getAvatar());
        viewHolder.txtName.setText(activeFriendItems.get(position).getFullName());
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowPopupMenu.onShowPopupMenuListener(position, viewHolder.menu);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        CircleImageView imgAvatar;
        TextView txtName;
        ImageView menu;
    }

    public void setOnShowPopupMenu(OnShowPopupMenu onShowPopupMenu) {
        this.onShowPopupMenu = onShowPopupMenu;
    }

}