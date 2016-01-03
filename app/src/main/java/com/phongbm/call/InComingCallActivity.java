package com.phongbm.call;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.phongbm.freephonecall.MainActivity;
import com.phongbm.freephonecall.R;
import com.phongbm.libraries.CallingRippleView;
import com.phongbm.libraries.CircleImageView;
import com.phongbm.music.RingtoneManager;

public class InComingCallActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = InComingCallActivity.class.getSimpleName();
    private static final int MESSAGE_UPDATE_TIME_CALL = 111;
    private static final int NOTIFICATION_CALLING = 0;
    private static final int NOTIFICATION_MISSED_CALL = 1;

    private ImageButton btnAnswer;
    private ImageButton btnEndCall;
    private TextView txtTime;
    private TextView txtFullName;
    private TextView txtPhoneNumber;
    private CircleImageView imgAvatar;
    private CallingRippleView callingRipple;
    private RingtoneManager ringtoneManager;
    private InComingCallReceiver inComingCallReceiver;
    private int timeCall = 0;
    private String state;
    private String id;
    private String fullName;
    private String phoneNumber;
    private String time;
    private String date = null;
    private boolean isCalling = false;
    private Vibrator vibrator;
    private CallLogsDBManager callLogsDBManager;
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

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.setContentView(R.layout.activity_incoming_call);
        this.initializeComponent();

        this.registerInComingCallReceiver();

        this.setVolumeControlStream(AudioManager.STREAM_RING);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long pattern[] = {0, 500, 1000};
        vibrator.vibrate(pattern, 0);

        ringtoneManager = new RingtoneManager(this);
        ringtoneManager.playRingtone();
        callLogsDBManager = new CallLogsDBManager(this);
        CommonMethod.getInstance().pushNotification(this, MainActivity.class,
                "Calling...", NOTIFICATION_CALLING, R.drawable.ic_calling, true);
    }

    private void initializeComponent() {
        btnAnswer = (ImageButton) this.findViewById(R.id.btn_answer);
        btnAnswer.setOnClickListener(this);
        btnEndCall = (ImageButton) this.findViewById(R.id.btn_end_call);
        btnEndCall.setOnClickListener(this);

        txtTime = (TextView) this.findViewById(R.id.txt_time);
        txtFullName = (TextView) this.findViewById(R.id.txt_full_name);
        txtPhoneNumber = (TextView) this.findViewById(R.id.txt_phone_number);
        imgAvatar = (CircleImageView) this.findViewById(R.id.img_avatar);
        callingRipple = (CallingRippleView) this.findViewById(R.id.calling_ripple);

        id = this.getIntent().getStringExtra(CommonValue.OUTGOING_CALL_ID);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("objectId", id);
        parseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                    return;
                }
                fullName = (String) parseUser.get("fullName");
                txtFullName.setText(fullName);
                phoneNumber = parseUser.getUsername();
                txtPhoneNumber.setText("Mobile " + phoneNumber);
                ParseFile parseFile = (ParseFile) parseUser.get("avatar");
                if (parseFile == null) {
                    return;
                }
                parseFile.getDataInBackground(new GetDataCallback() {
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
            case R.id.btn_answer:
                isCalling = true;
                btnAnswer.setEnabled(false);
                ringtoneManager.stopRingtone();
                vibrator.cancel();

                this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                Intent intentAnswer = new Intent(CommonValue.ACTION_ANSWER);
                this.sendBroadcast(intentAnswer);
                break;

            case R.id.btn_end_call:
                isCalling = false;
                date = CommonMethod.getInstance().getCurrentDateTime();
                btnAnswer.setEnabled(false);
                btnEndCall.setEnabled(false);
                ringtoneManager.stopRingtone();
                vibrator.cancel();

                this.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
                Intent intentHangup = new Intent(CommonValue.ACTION_END_CALL);
                this.sendBroadcast(intentHangup);
                break;
        }
    }

    private void registerInComingCallReceiver() {
        if (inComingCallReceiver == null) {
            inComingCallReceiver = new InComingCallReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(CommonValue.STATE_ANSWER);
            intentFilter.addAction(CommonValue.STATE_END_CALL);
            this.registerReceiver(inComingCallReceiver, intentFilter);
        }
    }

    private class InComingCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CommonValue.STATE_ANSWER:
                    new Thread(runnableTimeCall).start();
                    InComingCallActivity.this.setVolumeControlStream(
                            AudioManager.STREAM_VOICE_CALL);
                    break;

                case CommonValue.STATE_END_CALL:
                    if (timeCall != 0) {
                        isCalling = false;
                        txtTime.setText("End Call: " + time);
                        state = "inComingCall";
                    } else {
                        vibrator.cancel();
                        ringtoneManager.stopRingtone();
                        CommonMethod.getInstance().pushNotification(InComingCallActivity.this, MainActivity.class,
                                "Missed Call", NOTIFICATION_MISSED_CALL,
                                R.drawable.ic_call_missed, false);
                        txtTime.setText("Missed Call");
                        state = "missedCall";
                    }
                    if (date == null) {
                        date = CommonMethod.getInstance().getCurrentDateTime();
                    }
                    txtTime.setBackgroundColor(ContextCompat.getColor(InComingCallActivity.this,
                            R.color.red_500));
                    callingRipple.setVisibility(RelativeLayout.GONE);
                    InComingCallActivity.this.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", id);
                    contentValues.put("fullName", fullName);
                    contentValues.put("phoneNumber", phoneNumber);
                    contentValues.put("date", date);
                    contentValues.put("state", state);
                    callLogsDBManager.insertData(contentValues);

                    Intent callIntent = new Intent(CommonValue.ACTION_UPDATE_CALL_LOG);
                    callIntent.putExtra("id", id);
                    callIntent.putExtra("fullName", fullName);
                    callIntent.putExtra("phoneNumber", phoneNumber);
                    callIntent.putExtra("date", date);
                    callIntent.putExtra("state", state);
                    InComingCallActivity.this.sendBroadcast(callIntent);

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InComingCallActivity.this.finish();
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
        this.unregisterReceiver(inComingCallReceiver);
        callLogsDBManager.closeDatabase();
        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_CALLING);
        super.onDestroy();
    }

}