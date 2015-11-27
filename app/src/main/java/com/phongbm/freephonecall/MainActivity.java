package com.phongbm.freephonecall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import com.phongbm.common.CommonMethod;
import com.phongbm.common.CommonValue;
import com.phongbm.common.Profile;
import com.phongbm.libraries.CircleImageView;
import com.phongbm.libraries.FadingBackgroundView;
import com.phongbm.libraries.FloatingActionButton;
import com.phongbm.libraries.FloatingActionMenu;
import com.phongbm.libraries.FloatingActionToggleButton;
import com.phongbm.libraries.OnFloatingActionMenuSelectedListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADDITION_FRIEND = 0;

    private ParseUser currentUser;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tab;
    private FriendItem newFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = ParseUser.getCurrentUser();
        CommonMethod.getInstance().loadListFriend(currentUser, this);

        this.setContentView(R.layout.activity_main);
        this.initializeToolbar();
        this.initializeDrawerLayout();
        this.initializeNavigationView();
        this.initializeMyProfile();
        this.initializeFloatingActionMenu();

        this.startService();

        viewPagerAdapter = new ViewPagerAdapter(this, this.getSupportFragmentManager());
        viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        tab = (TabLayout) this.findViewById(R.id.tab);
        tab.setupWithViewPager(viewPager);
    }

    private void startService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setClassName(CommonValue.PACKAGE_NAME, CommonValue.PACKAGE_NAME + ".FreePhoneCallService");
        this.startService(serviceIntent);
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

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinator_layout);
    }

    private void initializeNavigationView() {
        navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeMyProfile() {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        final CircleImageView imgAvatar = (CircleImageView) headerView.findViewById(R.id.img_avatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        final TextView txtFullName = (TextView) headerView.findViewById(R.id.txt_fullName);
        final TextView txtEmail = (TextView) headerView.findViewById(R.id.txt_email);

        imgAvatar.setImageBitmap(Profile.getInstance().getAvatar());
        txtFullName.setText(Profile.getInstance().getFullName());
        txtEmail.setText(Profile.getInstance().getEmail());
    }

    private void initializeFloatingActionMenu() {
        FadingBackgroundView fbv = (FadingBackgroundView) this.findViewById(R.id.fading_background);
        final FloatingActionToggleButton fatb = (FloatingActionToggleButton)
                this.findViewById(R.id.floating_action_toggle_button);
        FloatingActionMenu fam = (FloatingActionMenu) this.findViewById(R.id.floating_action_menu);
        fam.setFadingBackgroundView(fbv);
        fam.setOnFloatingActionMenuSelectedListener(new OnFloatingActionMenuSelectedListener() {
            @Override
            public void onFloatingActionMenuSelected(FloatingActionButton fab) {
                switch (fab.getId()) {
                    case R.id.btn_add_friend:
                        fatb.toggleOff();
                        Intent addFriendIntent = new Intent(MainActivity.this, AdditionFriend.class);
                        MainActivity.this.startActivityForResult(addFriendIntent, REQUEST_ADDITION_FRIEND);
                        break;

                    case R.id.btn_new_voice_call:
                        break;
                }
            }
        });
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
            case R.id.nav_camara:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FriendItem getNewFriend() {
        return newFriend;
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
                    newFriend = new FriendItem(id, avatar, parseUser.getUsername(),
                            parseUser.getString("fullName"));
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
                case REQUEST_ADDITION_FRIEND:
                    if (data == null) {
                        return;
                    }
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    if (phoneNumber.equals(Profile.getInstance().getPhoneNumber())) {
                        Snackbar.make(coordinatorLayout, "You can not make friends with yourself", Snackbar.LENGTH_LONG)
                                .setAction("ACTION", null)
                                .show();
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
                                Snackbar.make(coordinatorLayout, "Error", Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null)
                                        .show();
                                return;
                            }
                            if (parseUser == null) {
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayout, "That account does not exist", Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null)
                                        .show();
                                return;
                            }
                            ArrayList<String> listFriend = (ArrayList<String>) currentUser.get("listFriend");
                            String newUserId = parseUser.getObjectId();
                            if (listFriend == null) {
                                listFriend = new ArrayList<>();
                            } else {
                                /*if (listFriend.contains(newUserId)) {
                                    progressDialog.dismiss();
                                    Snackbar.make(coordinatorLayout, "That account has been identical", Snackbar.LENGTH_LONG)
                                            .setAction("ACTION", null)
                                            .show();
                                    return;
                                }*/
                            }
                            listFriend.add(newUserId);
                            currentUser.put("listFriend", listFriend);
                            currentUser.saveInBackground();
                            MainActivity.this.createNewFriend(parseUser);
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Addition friend successfully", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.parseColor("#4caf50"));
                            snackbar.show();
                        }
                    });
                    break;
            }
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