package com.phongbm.freephonecall;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseUser;
import com.phongbm.common.Profile;
import com.phongbm.friend.Friend;
import com.phongbm.home.MainFragment;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window statusBar = this.getWindow();
            statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            statusBar.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Friend.getInstance().getData();
            Profile.getInstance().getData(this);
            currentUser.put("online", true);
            currentUser.saveInBackground();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainFragment.class));
                }
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}