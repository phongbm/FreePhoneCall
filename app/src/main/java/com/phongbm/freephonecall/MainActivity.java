package com.phongbm.freephonecall;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.phongbm.libraries.FadingBackgroundView;
import com.phongbm.libraries.FloatingActionButton;
import com.phongbm.libraries.FloatingActionMenu;
import com.phongbm.libraries.FloatingActionToggleButton;
import com.phongbm.libraries.OnFloatingActionMenuSelectedListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.initializeToolbar();
        this.initializeDrawerLayout();
        this.initializeNavigationView();
        this.initializeFloatingActionMenu();
        // this.initializeFAB();

        viewPagerAdapter = new ViewPagerAdapter(this, this.getSupportFragmentManager());
        viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        tab = (TabLayout) this.findViewById(R.id.tab);
        tab.setupWithViewPager(viewPager);
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
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeFloatingActionMenu() {
        FadingBackgroundView fbv = (FadingBackgroundView) this.findViewById(R.id.fading_background);
        FloatingActionMenu fam = (FloatingActionMenu) this.findViewById(R.id.floating_action_menu);
        fam.setFadingBackgroundView(fbv);
        fam.setOnFloatingActionMenuSelectedListener(new OnFloatingActionMenuSelectedListener() {
            @Override
            public void onFloatingActionMenuSelected(FloatingActionButton fab) {
                switch (fab.getId()) {
                    case R.id.floating_action_toggle_button:
                        FloatingActionToggleButton floatingActionToggleButton =
                                (FloatingActionToggleButton) fab;
                        if (floatingActionToggleButton.isToggleOn()) {
                            Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Open", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.a:
                        Toast.makeText(MainActivity.this, "a", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.b:
                        Toast.makeText(MainActivity.this, "b", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.c:
                        Toast.makeText(MainActivity.this, "c", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /*private void initializeFAB() {
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "OK", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camara:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}