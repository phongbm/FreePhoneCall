package com.phongbm.loginsignup;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class MainFragment extends AppCompatActivity {
    private HomeFragment homeFragment = new HomeFragment();
    private SignInFragment signInFragment = new SignInFragment();
    private SignupFragment signupFragment = new SignupFragment();
    private ProfileInformationFragment profileInformationFragment = new ProfileInformationFragment();
    private ProfilePictureFragment profilePictureFragment = new ProfilePictureFragment();

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

    public void showLoginFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, signInFragment).commit();
    }

    public void showSigupFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, signupFragment).commit();
    }

    public void showProfileInformationFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, profileInformationFragment).commit();
    }

    public void showProfileInformationFragmentBack() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, profileInformationFragment).commit();
    }

    public void showProfilePictureFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, profilePictureFragment).commit();
    }

    public ProfileInformationFragment getProfileInformationFragment() {
        return profileInformationFragment;
    }

    public SignupFragment getSignUpFragment() {
        return signupFragment;
    }

    @Override
    public void onBackPressed() {
        if (signInFragment.isVisible() || signupFragment.isVisible()) {
            this.showHomeFragment();
        } else {
            if (profileInformationFragment.isVisible()) {
            } else {
                if (profilePictureFragment.isVisible()) {
                    this.showProfileInformationFragmentBack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

}