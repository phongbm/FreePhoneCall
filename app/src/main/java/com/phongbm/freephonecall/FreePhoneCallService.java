package com.phongbm.freephonecall;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseUser;
import com.phongbm.common.CommonValue;
import com.phongbm.common.ServerInfo;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.List;

public class FreePhoneCallService extends Service implements SinchClientListener {
    private static final String TAG = FreePhoneCallService.class.getSimpleName();

    private Context context;
    private SinchClient sinchClient;
    private Call outGoingCall;
    private Call inComingCall;
    private MessageListener messageListener;
    private MessageClient messageClient;
    private FreePhoneCallReceiver freePhoneCallReceiver;
    private String outGoingId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        this.registerFreePhoneCallReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        outGoingId = currentUser != null ? currentUser.getObjectId() : null;
        if (intent == null) {
            if (outGoingId != null) {
                this.startSinchService();
            }
            return Service.START_STICKY;
        }
        this.startSinchService();
        return Service.START_STICKY;
    }

    private void startSinchService() {
        if (sinchClient == null) {
            sinchClient = Sinch.getSinchClientBuilder()
                    .context(context)
                    .userId(outGoingId)
                    .applicationKey(ServerInfo.SINCH_APPLICATION_KEY)
                    .applicationSecret(ServerInfo.SINCH_SECRET)
                    .environmentHost(ServerInfo.SINCH_ENVIROMENT)
                    .build();
            sinchClient.setSupportCalling(true);
            sinchClient.setSupportMessaging(true);
            sinchClient.setSupportActiveConnectionInBackground(true);
            sinchClient.addSinchClientListener(this);
            sinchClient.checkManifest();
            sinchClient.start();
        }
    }

    @Override
    public void onClientStarted(SinchClient sinchClient) {
        if (messageClient == null) {
            messageListener = new MessageListener();
            messageClient = this.sinchClient.getMessageClient();
            messageClient.addMessageClientListener(messageListener);
            this.sinchClient.startListeningOnActiveConnection();
            this.sinchClient.getCallClient().addCallClientListener(new InComingCallListener());
        }
    }

    @Override
    public void onClientStopped(SinchClient sinchClient) {
    }

    @Override
    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
    }

    @Override
    public void onLogMessage(int i, String s1, String s2) {
    }

    private class OutGoingCallListener implements CallListener {
        @Override
        public void onCallProgressing(Call call) {
        }

        @Override
        public void onCallEstablished(Call call) {
            FreePhoneCallService.this.sendBroadcast(new Intent(CommonValue.STATE_PICK_UP));
        }

        @Override
        public void onCallEnded(Call call) {
            Log.i(TAG, "onCallEnded...");
            FreePhoneCallService.this.sendBroadcast(new Intent(CommonValue.STATE_END_CALL));
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {
        }
    }

    private class InComingCallListener implements CallClientListener, CallListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            inComingCall = call;
            inComingCall.addCallListener(this);
            Intent inComingCallIntent = new Intent();
            inComingCallIntent.setClassName(CommonValue.PACKAGE_NAME, "com.phongbm.call.InComingCallActivity");
            inComingCallIntent.putExtra(CommonValue.OUTGOING_CALL_ID, call.getRemoteUserId());
            inComingCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(inComingCallIntent);
        }

        @Override
        public void onCallProgressing(Call call) {
        }

        @Override
        public void onCallEstablished(Call call) {
            Intent intentAnswer = new Intent();
            intentAnswer.setAction(CommonValue.STATE_ANSWER);
            FreePhoneCallService.this.sendBroadcast(intentAnswer);
        }

        @Override
        public void onCallEnded(Call call) {
            Intent intentEndCall = new Intent();
            intentEndCall.setAction(CommonValue.STATE_END_CALL);
            FreePhoneCallService.this.sendBroadcast(intentEndCall);
            Log.i(TAG, "onCallEnded 2...");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {
        }
    }

    private class MessageListener implements MessageClientListener {
        @Override
        public void onIncomingMessage(final MessageClient messageClient, final Message message) {
        }

        @Override
        public void onMessageSent(MessageClient messageClient, final Message message, String s) {
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
        }

    }

    private void registerFreePhoneCallReceiver() {
        if (freePhoneCallReceiver == null) {
            freePhoneCallReceiver = new FreePhoneCallReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(CommonValue.ACTION_OUTGOING_CALL);
            intentFilter.addAction(CommonValue.ACTION_END_CALL);
            intentFilter.addAction(CommonValue.ACTION_ANSWER);
            intentFilter.addAction(CommonValue.ACTION_LOGOUT);
            context.registerReceiver(freePhoneCallReceiver, intentFilter);
        }
    }

    private class FreePhoneCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CommonValue.ACTION_OUTGOING_CALL:
                    String inComingCallId = intent.getStringExtra(CommonValue.INCOMING_CALL_ID);
                    outGoingCall = sinchClient.getCallClient().callUser(inComingCallId);
                    outGoingCall.addCallListener(new OutGoingCallListener());
                    break;

                case CommonValue.ACTION_END_CALL:
                    if (outGoingCall != null) {
                        Log.i(TAG, "outGoingCall != null...");
                        outGoingCall.hangup();
                        outGoingCall = null;
                    }
                    if (inComingCall != null) {
                        Log.i(TAG, "inComingCall != null...");
                        inComingCall.hangup();
                        inComingCall = null;
                    }
                    break;

                case CommonValue.ACTION_ANSWER:
                    if (inComingCall != null) {
                        inComingCall.answer();
                    }
                    break;

                case CommonValue.ACTION_LOGOUT:
                    if (messageListener != null) {
                        messageClient.removeMessageClientListener(messageListener);
                        messageClient = null;
                    }
                    sinchClient.stopListeningOnActiveConnection();
                    sinchClient.terminate();
                    sinchClient = null;
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(freePhoneCallReceiver);
        super.onDestroy();
    }

}