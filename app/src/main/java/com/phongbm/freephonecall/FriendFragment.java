package com.phongbm.freephonecall;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.phongbm.libraries.CircleImageView;

import java.util.Collections;

@SuppressLint("ValidFragment")
public class FriendFragment extends Fragment implements View.OnClickListener, OnShowPopupMenu {
    private static final String TAG = FriendFragment.class.getSimpleName();

    private View view;
    private View divider;
    private ListView listViewFriend;
    private RelativeLayout layoutNote;
    private RelativeLayout layoutMyProfile;
    private TextView btnTabActive;
    private TextView btnTabAllFriends;
    private TextView txtFullName;
    private TextView txtStatus;
    private CircleImageView imgAvatar;
    private Switch switchOnline;
    private boolean tabActive = true;
    private boolean isOnline = true;
    private boolean enableTabAllFriend = false;
    private AllFriendAdapter allFriendAdapter;
    private ActiveFriendAdapter activeFriendAdapter;
    private BroadcastUpdateListFriend broadcastUpdateListFriend = new BroadcastUpdateListFriend();

    @SuppressLint("InflateParams")
    public FriendFragment(Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.fragment_friend, null);
        this.initializeComponent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerUpdateListFriend();
        activeFriendAdapter = new ActiveFriendAdapter(this.getActivity(), this.getActivity());
        activeFriendAdapter.setOnShowPopupMenu(this);
        listViewFriend.setAdapter(activeFriendAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.initializeProfile();
        return view;
    }

    private void initializeComponent() {
        divider = view.findViewById(R.id.divider);

        listViewFriend = (ListView) view.findViewById(R.id.list_view_friend);

        layoutNote = (RelativeLayout) view.findViewById(R.id.layout_note);

        btnTabActive = (TextView) view.findViewById(R.id.tab_active);
        btnTabActive.setOnClickListener(this);
        btnTabAllFriends = (TextView) view.findViewById(R.id.tab_all_friends);
        btnTabAllFriends.setOnClickListener(this);

        layoutMyProfile = (RelativeLayout) view.findViewById(R.id.layout_profile);

        listViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void initializeProfile() {
        imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        imgAvatar.setImageBitmap(Profile.getInstance().getAvatar());

        txtFullName = (TextView) view.findViewById(R.id.txt_fullName);
        txtFullName.setText(Profile.getInstance().getFullName());

        txtStatus = (TextView) view.findViewById(R.id.txt_status);

        switchOnline = (Switch) view.findViewById(R.id.switch_online);
        switchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtStatus.setText("ONLINE");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green_500));
                    listViewFriend.setAdapter(activeFriendAdapter);
                    isOnline = true;
                    listViewFriend.setVisibility(View.VISIBLE);
                    layoutNote.setVisibility(View.GONE);

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("online", true);
                    currentUser.saveInBackground();
                } else {
                    txtStatus.setText("OFFLINE");
                    txtStatus.setTextColor(ContextCompat.getColor(FriendFragment.this.getContext(), R.color.textPrimary));
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
                this.changeStateShow(btnTabActive);
                this.changeStateHide(btnTabAllFriends);
                listViewFriend.setAdapter(activeFriendAdapter);
                if (isOnline) {
                    listViewFriend.setVisibility(View.VISIBLE);
                    layoutNote.setVisibility(View.GONE);
                } else {
                    listViewFriend.setVisibility(View.GONE);
                    layoutNote.setVisibility(View.VISIBLE);
                }
                layoutMyProfile.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                tabActive = true;
                break;
            case R.id.tab_all_friends:
                if (!enableTabAllFriend) {
                    enableTabAllFriend = true;
                    allFriendAdapter = new AllFriendAdapter(this.getActivity(), this.getActivity());
                    allFriendAdapter.setOnShowPopupMenu(this);
                }
                this.changeStateShow(btnTabAllFriends);
                this.changeStateHide(btnTabActive);
                listViewFriend.setAdapter(allFriendAdapter);
                listViewFriend.setVisibility(View.VISIBLE);
                layoutNote.setVisibility(View.GONE);
                layoutMyProfile.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                tabActive = false;
                break;
        }
    }

    private void changeStateShow(TextView textView) {
        textView.setBackgroundResource(R.color.green_500);
        textView.setTextColor(Color.WHITE);
    }

    private void changeStateHide(TextView textView) {
        textView.setBackgroundResource(R.drawable.div_stroke_green);
        textView.setTextColor(Color.parseColor("#4caf50"));
    }

    public void registerUpdateListFriend() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.ACTION_ADD_FRIEND);
        this.getActivity().registerReceiver(broadcastUpdateListFriend, filter);
    }

    @Override
    public void onShowPopupMenuListener(int position, View view) {
        final String inComingId, inComingFullName;
        if (tabActive) {
            inComingId = activeFriendAdapter.getItem(position).getId();
            inComingFullName = activeFriendAdapter.getItem(position).getFullName();
        } else {
            inComingId = allFriendAdapter.getItem(position).getId();
            inComingFullName = allFriendAdapter.getItem(position).getFullName();
        }
        PopupMenu popup = new PopupMenu(this.getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_voice_call:
                        Intent intentCall = new Intent(getActivity(), OutGoingCallActivity.class);
                        intentCall.putExtra(CommonValue.INCOMING_CALL_ID, inComingId);
                        getActivity().startActivity(intentCall);
                        break;
                    case R.id.action_view_profile:
                        /*Intent intentProfile = new Intent(getActivity(), DetailActivity.class);
                        intentProfile.putExtra(CommonValue.USER_ID, inComingId);
                        TabFriendFragment.this.getActivity().startActivity(intentProfile);*/
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private class BroadcastUpdateListFriend extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonValue.ACTION_ADD_FRIEND)) {
                FriendItem newFriend = ((MainActivity) getActivity()).getNewFriend();
                AllFriendItem allFriendItem = new AllFriendItem(newFriend.getId(),
                        newFriend.getAvatar(), newFriend.getPhoneNumber(), newFriend.getFullName());
                if (allFriendAdapter == null) {
                    allFriendAdapter = new AllFriendAdapter(getActivity(), getActivity());
                }
                allFriendAdapter.getAllFriendItems().add(allFriendItem);
                Collections.sort(allFriendAdapter.getAllFriendItems());
                allFriendAdapter.notifyDataSetChanged();

                if (intent.getBooleanExtra("online", true)) {
                    ActiveFriendItem activeFriendItem = new ActiveFriendItem(newFriend.getId(),
                            newFriend.getAvatar(), newFriend.getPhoneNumber(), newFriend.getFullName());
                    activeFriendAdapter.getActiveFriendItems().add(activeFriendItem);
                    activeFriendAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}