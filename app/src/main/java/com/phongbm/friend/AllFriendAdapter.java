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

public class AllFriendAdapter extends BaseAdapter {
    private ArrayList<AllFriendItem> allFriendItems;
    private LayoutInflater layoutInflater;
    private OnShowPopupMenu onShowPopupMenu;

    public AllFriendAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        allFriendItems = Friend.getInstance().getAllFriendItems();
    }

    @Override
    public int getCount() {
        return allFriendItems.size();
    }

    @Override
    public AllFriendItem getItem(int position) {
        return allFriendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_all_friend, parent, false);
            viewHolder.imgAvatar = (CircleImageView) convertView.findViewById(R.id.img_avatar);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.menu = (ImageView) convertView.findViewById(R.id.menu);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imgAvatar.setImageBitmap(allFriendItems.get(position).getAvatar());
        viewHolder.txtName.setText(allFriendItems.get(position).getFullName());
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