package com.phongbm.call;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phongbm.common.CommonValue;
import com.phongbm.freephonecall.R;
import com.phongbm.libraries.CircleTextView;

import java.util.ArrayList;
import java.util.Random;

public class CallLogAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<CallLogItem> callLogItems;
    private Handler handler;
    private Random random = new Random();
    private ArrayList<Integer> colors;

    public CallLogAdapter(Context context, ArrayList<CallLogItem> callLogItems, Handler handler) {
        layoutInflater = LayoutInflater.from(context);
        this.callLogItems = callLogItems;
        this.colors = new ArrayList<>();
        for (int i = 0; i < callLogItems.size(); i++) {
            colors.add(-1);
        }
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return callLogItems.size();
    }

    @Override
    public CallLogItem getItem(int position) {
        return callLogItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_call_log, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgAvatar = (CircleTextView) convertView.findViewById(R.id.img_avatar);
            viewHolder.txtFullName = (TextView) convertView.findViewById(R.id.txt_full_name);
            viewHolder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.txt_phone_number);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.imgState = (ImageView) convertView.findViewById(R.id.img_state);
            viewHolder.btnCall = (ImageView) convertView.findViewById(R.id.btn_call);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (colors.get(position) == -1) {
            colors.set(position, Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        viewHolder.imgAvatar.setCircleColor(colors.get(position));
        viewHolder.imgAvatar.setText(callLogItems.get(position).getFullName().substring(0, 1).toUpperCase());
        viewHolder.txtFullName.setText(callLogItems.get(position).getFullName());
        viewHolder.txtPhoneNumber.setText("Mobile " + callLogItems.get(position).getPhoneNumber());
        viewHolder.txtDate.setText(callLogItems.get(position).getDate());
        String state = callLogItems.get(position).getState();
        switch (state) {
            case "outGoingCall":
                viewHolder.imgState.setImageResource(R.drawable.ic_call_log_outgoing_call);
                break;
            case "inComingCall":
                viewHolder.imgState.setImageResource(R.drawable.ic_call_log_incoming_call);
                break;
            case "missedCall":
                viewHolder.imgState.setImageResource(R.drawable.ic_call_log_missed_call);
                break;
        }
        viewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.what = CommonValue.MESSAGE_CALL_BACK;
                message.obj = callLogItems.get(position).getId();
                message.setTarget(handler);
                message.sendToTarget();
                colors.add(-1);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        CircleTextView imgAvatar;
        TextView txtFullName;
        TextView txtPhoneNumber;
        TextView txtDate;
        ImageView imgState;
        ImageView btnCall;
    }

    public void updateCallLogs(CallLogItem callLogItem) {
        callLogItems.add(0, callLogItem);
        colors.add(-1);
    }

}