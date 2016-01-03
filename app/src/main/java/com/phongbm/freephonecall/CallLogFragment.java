package com.phongbm.freephonecall;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.phongbm.call.CallLogAdapter;
import com.phongbm.call.CallLogItem;
import com.phongbm.call.CallLogsDBManager;
import com.phongbm.common.CommonValue;

import java.util.ArrayList;

public class CallLogFragment extends Fragment {
    private View view;
    private ListView listViewCallLog;
    private RelativeLayout layoutNoCallLogs;
    private CallLogAdapter callLogAdapter;
    private CallLogsDBManager callLogsDBManager;
    private ArrayList<CallLogItem> callLogItems;
    private boolean canDelete;
    private CallLogsReceiver callLogsReceiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    public CallLogFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerCallLogsReceiver();
        callLogsDBManager = new CallLogsDBManager(this.getActivity());
        callLogItems = callLogsDBManager.getData();
        callLogAdapter = new CallLogAdapter(this.getActivity(), callLogItems, handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call_log, container, false);
        listViewCallLog = (ListView) view.findViewById(R.id.list_view_callLog);
        layoutNoCallLogs = (RelativeLayout) view.findViewById(R.id.layout_no_callLog);

        if (callLogItems.size() == 0) {
            canDelete = false;
            listViewCallLog.setVisibility(RelativeLayout.GONE);
            layoutNoCallLogs.setVisibility(RelativeLayout.VISIBLE);
        } else {
            canDelete = true;
            listViewCallLog.setAdapter(callLogAdapter);
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (canDelete) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
                    alertDialog.setTitle("Delete");
                    alertDialog.setMessage("All call logs will be deleted. Delete?");
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.setCancelable(true);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    canDelete = false;
                                    callLogsDBManager.deleteAllData();
                                    callLogItems.clear();
                                    callLogAdapter.notifyDataSetChanged();
                                    alertDialog.dismiss();
                                    listViewCallLog.setVisibility(RelativeLayout.GONE);
                                    layoutNoCallLogs.setVisibility(RelativeLayout.VISIBLE);
                                }
                            });
                    alertDialog.show();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
                    alertDialog.setTitle("Delete");
                    alertDialog.setMessage("No call logs");
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.setCancelable(true);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onDestroy() {
        this.getActivity().unregisterReceiver(callLogsReceiver);
        callLogsDBManager.closeDatabase();
        super.onDestroy();
    }

    private void registerCallLogsReceiver() {
        callLogsReceiver = new CallLogsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.ACTION_UPDATE_CALL_LOG);
        this.getActivity().registerReceiver(callLogsReceiver, filter);
    }

    private class CallLogsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String fullName = intent.getStringExtra("fullName");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String date = intent.getStringExtra("date");
            String state = intent.getStringExtra("state");
            CallLogItem callLogItem = new CallLogItem(id, fullName, phoneNumber, date, state);
            callLogAdapter.updateCallLogs(callLogItem);
            callLogAdapter.notifyDataSetChanged();

            canDelete = true;
            listViewCallLog.setVisibility(RelativeLayout.VISIBLE);
            layoutNoCallLogs.setVisibility(RelativeLayout.GONE);
        }
    }

}