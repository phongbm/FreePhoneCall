package com.phongbm.freephonecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseUser;
import com.phongbm.common.CommonValue;
import com.phongbm.common.OnShowPopupMenu;
import com.phongbm.common.Profile;
import com.phongbm.friend.ActiveFriendAdapter;
import com.phongbm.friend.AllFriendAdapter;
import com.phongbm.libraries.CircleImageView;

public class FriendFragment extends Fragment implements View.OnClickListener, OnShowPopupMenu {
    private static final String TAG = FriendFragment.class.getSimpleName();

    private View view;
    private View divider;
    private ListView listViewFriend;
    private RelativeLayout layoutNote;
    private RelativeLayout layoutProfile;
    private TextView tabActive;
    private TextView tabAllFriends;
    private TextView txtStatus;
    private AllFriendAdapter allFriendAdapter;
    private ActiveFriendAdapter activeFriendAdapter;
    private boolean isShowTabActive = true;
    private boolean isOnline = true;
    // private boolean enableTabAllFriend = false;
    private UpdateFriendReceiver updateFriendReceiver;

    public FriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerUpdateFriendReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        this.initializeComponent();
        this.initializeProfile();
        return view;
    }

    private void initializeComponent() {
        divider = view.findViewById(R.id.divider);
        layoutNote = (RelativeLayout) view.findViewById(R.id.layout_note);
        layoutProfile = (RelativeLayout) view.findViewById(R.id.layout_profile);

        tabActive = (TextView) view.findViewById(R.id.tab_active);
        tabActive.setOnClickListener(this);
        tabAllFriends = (TextView) view.findViewById(R.id.tab_all_friends);
        tabAllFriends.setOnClickListener(this);

        allFriendAdapter = new AllFriendAdapter(this.getActivity());
        allFriendAdapter.setOnShowPopupMenu(this);

        listViewFriend = (ListView) view.findViewById(R.id.list_view_friend);
        activeFriendAdapter = new ActiveFriendAdapter(this.getActivity());
        activeFriendAdapter.setOnShowPopupMenu(this);
        listViewFriend.setAdapter(activeFriendAdapter);
        listViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void initializeProfile() {
        CircleImageView imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        imgAvatar.setImageBitmap(Profile.getInstance().getAvatar());

        TextView txtFullName = (TextView) view.findViewById(R.id.txt_fullName);
        txtFullName.setText(Profile.getInstance().getFullName());

        txtStatus = (TextView) view.findViewById(R.id.txt_status);

        Switch switchOnline = (Switch) view.findViewById(R.id.switch_online);
        switchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtStatus.setText("ONLINE");
                    txtStatus.setTextColor(ContextCompat.getColor(
                            FriendFragment.this.getContext(), R.color.green_500));
                    listViewFriend.setAdapter(activeFriendAdapter);
                    isOnline = true;
                    listViewFriend.setVisibility(View.VISIBLE);
                    layoutNote.setVisibility(View.GONE);

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("online", true);
                    currentUser.saveInBackground();
                } else {
                    txtStatus.setText("OFFLINE");
                    txtStatus.setTextColor(ContextCompat.getColor(
                            FriendFragment.this.getContext(), R.color.textPrimary));
                    listViewFriend.setAdapter(null);
                    isOnline = false;
                    layoutNote.setVisibility(View.VISIBLE);
                    listViewFriend.setVisibility(View.GONE);

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("online", false);
                    currentUser.saveInBackground();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_active:
                this.changeStateShow(tabActive);
                this.changeStateHide(tabAllFriends);
                listViewFriend.setAdapter(activeFriendAdapter);
                if (isOnline) {
                    listViewFriend.setVisibility(View.VISIBLE);
                    layoutNote.setVisibility(View.GONE);
                } else {
                    listViewFriend.setVisibility(View.GONE);
                    layoutNote.setVisibility(View.VISIBLE);
                }
                layoutProfile.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                isShowTabActive = true;
                break;

            case R.id.tab_all_friends:
                /*if (!enableTabAllFriend) {
                    enableTabAllFriend = true;
                }*/
                this.changeStateShow(tabAllFriends);
                this.changeStateHide(tabActive);
                listViewFriend.setAdapter(allFriendAdapter);
                listViewFriend.setVisibility(View.VISIBLE);
                layoutNote.setVisibility(View.GONE);
                layoutProfile.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                isShowTabActive = false;
                break;
        }
    }

    private void changeStateShow(TextView textView) {
        textView.setBackgroundResource(R.color.green_500);
        textView.setTextColor(Color.WHITE);
    }

    private void changeStateHide(TextView textView) {
        textView.setBackgroundResource(R.drawable.div_stroke_green);
        textView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.green_500));
    }

    @Override
    public void onShowPopupMenuListener(int position, View view) {
        final String inComingId;
        if (isShowTabActive) {
            inComingId = activeFriendAdapter.getItem(position).getId();
        } else {
            inComingId = allFriendAdapter.getItem(position).getId();
        }
        PopupMenu popup = new PopupMenu(this.getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_voice_call:
                        Intent outGoingCallIntent = new Intent(getActivity(), OutGoingCallActivity.class);
                        outGoingCallIntent.putExtra(CommonValue.INCOMING_CALL_ID, inComingId);
                        FriendFragment.this.getActivity().startActivity(outGoingCallIntent);
                        break;

                    case R.id.action_view_profile:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onDetach() {
        this.getActivity().unregisterReceiver(updateFriendReceiver);
        super.onDetach();
    }

    public void registerUpdateFriendReceiver() {
        updateFriendReceiver = new UpdateFriendReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.ACTION_ADD_FRIEND);
        this.getActivity().registerReceiver(updateFriendReceiver, filter);
    }

    private class UpdateFriendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonValue.ACTION_ADD_FRIEND)) {
                allFriendAdapter.notifyDataSetChanged();
                if (intent.getBooleanExtra("online", true)) {
                    activeFriendAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}