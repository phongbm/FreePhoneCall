package com.phongbm.freephonecall;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.phongbm.common.CommonMethod;
import com.phongbm.common.CommonValue;
import com.phongbm.libraries.CallingRippleView;
import com.phongbm.libraries.CircleImageView;

public class OutGoingCallActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = OutGoingCallActivity.class.getSimpleName();
    private static final int MESSAGE_UPDATE_TIME_CALL = 111;
    private static final int NOTIFICATION_CALLING = 0;

    private ImageButton btnEndCall;
    private TextView txtTime;
    private TextView txtFullName;
    private TextView txtPhoneNumber;
    private CircleImageView imgAvatar;
    private CallingRippleView callingRipple;
    private OutGoingCallReceiver outGoingCallReceiver;
    private int timeCall = 0;
    private String id;
    private String time;
    private String fullName;
    private String phoneNumber;
    private String date = null;
    private boolean isCalling = false;
    private boolean isPressBtnEndCall = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_TIME_CALL:
                    txtTime.setText("Time call: " + time);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_outgoing_call);
        this.initializeComponent();

        Intent intent = new Intent(CommonValue.ACTION_OUTGOING_CALL);
        intent.putExtra(CommonValue.INCOMING_CALL_ID, id);
        this.sendBroadcast(intent);

        this.registerOutGoingCallReceiver();

        CommonMethod.getInstance().pushNotification(this, MainActivity.class,
                "Calling...", NOTIFICATION_CALLING, R.drawable.ic_calling, true);
    }

    private void initializeComponent() {
        btnEndCall = (ImageButton) this.findViewById(R.id.btn_end_call);
        btnEndCall.setOnClickListener(this);

        this.findViewById(R.id.btn_speaker).setOnClickListener(this);

        txtTime = (TextView) this.findViewById(R.id.txt_time);
        txtFullName = (TextView) this.findViewById(R.id.txt_full_name);
        txtPhoneNumber = (TextView) this.findViewById(R.id.txt_phone_number);
        imgAvatar = (CircleImageView) this.findViewById(R.id.img_avatar);
        callingRipple = (CallingRippleView) this.findViewById(R.id.calling_ripple);

        Intent intent = this.getIntent();
        id = intent.getStringExtra(CommonValue.INCOMING_CALL_ID);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("objectId", id);
        parseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null || e != null) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                    return;
                }
                fullName = (String) parseUser.get("fullName");
                txtFullName.setText(fullName);
                phoneNumber = parseUser.getUsername();
                txtPhoneNumber.setText("Mobile " + phoneNumber);
                ParseFile avatarFile = (ParseFile) parseUser.get("avatar");
                if (avatarFile == null) {
                    return;
                }
                avatarFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                            Log.i(TAG, e.getMessage());
                            return;
                        }
                        Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgAvatar.setImageBitmap(avatar);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_end_call:
                isCalling = false;
                date = CommonMethod.getInstance().getCurrentDateTime();
                isPressBtnEndCall = true;
                btnEndCall.setEnabled(false);
                this.sendBroadcast(new Intent(CommonValue.ACTION_END_CALL));
                break;

            case R.id.btn_speaker:
                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                        AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                break;
        }
    }

    private void registerOutGoingCallReceiver() {
        if (outGoingCallReceiver == null) {
            outGoingCallReceiver = new OutGoingCallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(CommonValue.STATE_PICK_UP);
            filter.addAction(CommonValue.STATE_END_CALL);
            this.registerReceiver(outGoingCallReceiver, filter);
        }
    }

    private class OutGoingCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CommonValue.STATE_PICK_UP:
                    isCalling = true;
                    new Thread(runnableTimeCall).start();
                    OutGoingCallActivity.this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    break;

                case CommonValue.STATE_END_CALL:
                    if (timeCall != 0) {
                        isCalling = false;
                        txtTime.setText("End Call: " + time);
                    } else {
                        if (isPressBtnEndCall) {
                            txtTime.setText("Call Ended");
                        } else {
                            txtTime.setText("Missed Call");
                        }
                    }
                    if (date == null) {
                        date = CommonMethod.getInstance().getCurrentDateTime();
                    }
                    btnEndCall.setEnabled(false);
                    txtTime.setBackgroundColor(ContextCompat.getColor(OutGoingCallActivity.this, R.color.red_500));
                    callingRipple.setVisibility(RelativeLayout.GONE);
                    OutGoingCallActivity.this.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OutGoingCallActivity.this.finish();
                        }
                    }, 3000);
                    break;
            }
        }
    }

    private Runnable runnableTimeCall = new Runnable() {
        @Override
        public void run() {
            while (isCalling) {
                timeCall += 1000;
                time = CommonMethod.getInstance().convertTimeToString(timeCall);
                handler.sendEmptyMessage(MESSAGE_UPDATE_TIME_CALL);
                SystemClock.sleep(1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(outGoingCallReceiver);
        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_CALLING);
        super.onDestroy();
    }

    @Override
    public void finish() {
        this.setResult(Activity.RESULT_OK);
        super.finish();
    }

}