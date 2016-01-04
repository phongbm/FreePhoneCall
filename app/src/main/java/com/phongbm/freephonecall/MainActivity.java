package com.phongbm.freephonecall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.phongbm.call.CallLogsDBManager;
import com.phongbm.common.CommonValue;
import com.phongbm.common.Profile;
import com.phongbm.friend.ActiveFriendItem;
import com.phongbm.friend.AllFriendItem;
import com.phongbm.friend.Friend;
import com.phongbm.home.MainFragment;
import com.phongbm.libraries.CircleImageView;
import com.phongbm.libraries.floatingactionmenu.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ADD_FRIEND = 0;

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setTitle("Free Phone Call");
        loadingDialog.setMessage("Loading data...");
        loadingDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        }, 3000);

        this.setContentView(R.layout.activity_main);
        this.initializeComponent();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tab = (TabLayout) this.findViewById(R.id.tab);
        tab.setupWithViewPager(viewPager);
        this.startService();
    }

    private void initializeComponent() {
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinator_layout);
        this.initializeToolbar();
        this.initializeDrawerLayout();
        this.initializeNavigationView();
        this.initializeMyProfile();
        this.initializeFloatingActionMenu();

        this.findViewById(R.id.btn_add_friend).setOnClickListener(this);
        this.findViewById(R.id.btn_voice_call).setOnClickListener(this);
    }

    private void initializeToolbar() {
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
    }

    private void initializeDrawerLayout() {
        drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeNavigationView() {
        navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeMyProfile() {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        CircleImageView imgAvatar = (CircleImageView) headerView.findViewById(R.id.img_avatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        TextView txtFullName = (TextView) headerView.findViewById(R.id.txt_fullName);
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txt_email);
        imgAvatar.setImageBitmap(Profile.getInstance().getAvatar());
        txtFullName.setText(Profile.getInstance().getFullName());
        txtEmail.setText(Profile.getInstance().getEmail());
    }

    private void initializeFloatingActionMenu() {
        menu = (FloatingActionMenu) this.findViewById(R.id.menu);
        menu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.toggle(true);
            }
        });
        menu.setClosedOnTouchOutside(true);
    }

    private void startService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setClassName(CommonValue.PACKAGE_NAME, CommonValue.PACKAGE_NAME + ".FreePhoneCallService");
        this.startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_about:
                drawer.closeDrawer(GravityCompat.START);
                this.startActivity(new Intent(this, AboutUsActivity.class));
                this.overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
                return true;

            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to Log Out?");
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.put("online", false);
                        currentUser.saveInBackground();
                        ParseUser.logOut();

                        CallLogsDBManager callLogsDBManager = new CallLogsDBManager(MainActivity.this);
                        callLogsDBManager.deleteAllData();
                        callLogsDBManager.closeDatabase();

                        Friend.getInstance().clearData();

                        MainActivity.this.sendBroadcast(new Intent(CommonValue.ACTION_LOGOUT));
                        dialog.dismiss();
                        MainActivity.this.startActivity(new Intent(MainActivity.this, MainFragment.class));
                        MainActivity.this.finish();
                    }
                });

                AlertDialog logoutDialog = builder.create();
                logoutDialog.getWindow().getAttributes().windowAnimations = R.style.AppTheme_Dialog_Animate;
                logoutDialog.show();
                logoutDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(this, R.color.blue_500));
                logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                        ContextCompat.getColor(this, R.color.textPrimary));
                return true;
        }
        return true;
    }

    private void createNewFriend(final ParseUser parseUser) {
        final ParseFile parseFile = (ParseFile) parseUser.get("avatar");
        if (parseFile != null) {
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    final Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    final String id = parseUser.getObjectId();
                    final String phoneNumber = parseUser.getUsername();
                    final String fullName = parseUser.getString("fullName");

                    Friend.getInstance().getAllFriendItems().add(new AllFriendItem(id, phoneNumber, fullName, avatar));
                    Collections.sort(Friend.getInstance().getAllFriendItems());
                    if (parseUser.getBoolean("online")) {
                        Friend.getInstance().getActiveFriendItems().add(new ActiveFriendItem(id, phoneNumber, fullName, avatar));
                    }

                    Intent intentAddFriend = new Intent();
                    intentAddFriend.setAction(CommonValue.ACTION_ADD_FRIEND);
                    boolean isOnline = parseUser.getBoolean("online");
                    intentAddFriend.putExtra("online", isOnline);
                    MainActivity.this.sendBroadcast(intentAddFriend);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_FRIEND:
                    if (data == null) {
                        return;
                    }
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    if (phoneNumber.equals(Profile.getInstance().getPhoneNumber())) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "You can not make friends with yourself", Snackbar.LENGTH_LONG)
                                .setAction("ACTION", null);
                        snackbar.getView().setBackgroundColor(Color.parseColor("#f44336"));
                        snackbar.show();
                        return;
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Addition Friend");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", phoneNumber);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error", Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null);
                                snackbar.getView().setBackgroundColor(Color.parseColor("#f44336"));
                                snackbar.show();
                                return;
                            }
                            if (parseUser == null) {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "That account does not exist", Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null);
                                snackbar.getView().setBackgroundColor(Color.parseColor("#f44336"));
                                snackbar.show();
                                return;
                            }
                            ArrayList<String> listFriend = (ArrayList<String>) currentUser.get("listFriend");
                            String newUserId = parseUser.getObjectId();
                            if (listFriend == null) {
                                listFriend = new ArrayList<>();
                            } else {
                                if (listFriend.contains(newUserId)) {
                                    progressDialog.dismiss();
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "That account has been identical", Snackbar.LENGTH_LONG)
                                            .setAction("ACTION", null);
                                    snackbar.getView().setBackgroundColor(Color.parseColor("#f44336"));
                                    snackbar.show();
                                    return;
                                }
                            }
                            listFriend.add(newUserId);
                            currentUser.put("listFriend", listFriend);
                            currentUser.saveInBackground();
                            MainActivity.this.createNewFriend(parseUser);
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Addition friend successfully", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null);
                            snackbar.getView().setBackgroundColor(Color.parseColor("#2196f3"));
                            snackbar.show();
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_friend:
                menu.close(true);
                Intent newFriendIntent = new Intent(this, AdditionFriend.class);
                this.startActivityForResult(newFriendIntent, REQUEST_CODE_ADD_FRIEND);
                break;

            case R.id.btn_voice_call:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}