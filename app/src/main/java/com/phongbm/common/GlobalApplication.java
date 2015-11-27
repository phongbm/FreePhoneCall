package com.phongbm.common;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.phongbm.freephonecall.ActiveFriendItem;
import com.phongbm.freephonecall.AllFriendItem;

import java.util.ArrayList;

public class GlobalApplication extends Application {
    private static final String TAG = GlobalApplication.class.getSimpleName();
    public static int widthScreen;
    public static int heightScreen;
    public static float densityDPI;

    private ArrayList<AllFriendItem> allFriendItems;
    private ArrayList<ActiveFriendItem> activeFriendItems;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()...");
        this.initializeParseServer();
        this.initializeComponent();

        allFriendItems = new ArrayList<>();
        activeFriendItems = new ArrayList<>();
    }

    private void initializeParseServer() {
        Parse.initialize(this, ServerInfo.PARSE_APPLICATION_ID, ServerInfo.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    private void initializeComponent() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        widthScreen = metrics.widthPixels;
        heightScreen = metrics.heightPixels;
        densityDPI = metrics.densityDpi;
    }

    public ArrayList<AllFriendItem> getAllFriendItems() {
        return allFriendItems;
    }

    public void setAllFriendItems(ArrayList<AllFriendItem> allFriendItems) {
        this.allFriendItems = allFriendItems;
    }

    public ArrayList<ActiveFriendItem> getActiveFriendItems() {
        return activeFriendItems;
    }

    public void setActiveFriendItems(ArrayList<ActiveFriendItem> activeFriendItems) {
        this.activeFriendItems = activeFriendItems;
    }

}