package com.phongbm.home;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class MainFragment extends AppCompatActivity {
    private HomeFragment homeFragment = new HomeFragment();
    private SignInFragment signInFragment = new SignInFragment();
    private SignUpFragment signUpFragment = new SignUpFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window statusBar = this.getWindow();
            statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            statusBar.setStatusBarColor(Color.parseColor("#1976d2"));
        }
        this.showHomeFragment();
    }

    public void showHomeFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, homeFragment).commit();
    }

    public void showSignInFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, signInFragment).commit();
    }

    public void showSigUpFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, signUpFragment).commit();
    }

    public void showProfileFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, profileFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (signInFragment.isVisible() || signUpFragment.isVisible()) {
            this.showHomeFragment();
        } else {
            if (!profileFragment.isVisible()) {
                super.onBackPressed();
            }
        }
    }

}