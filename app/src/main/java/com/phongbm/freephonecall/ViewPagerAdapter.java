package com.phongbm.freephonecall;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> pages;
    private String[] titles = new String[]{"Friends", "Call Logs"};

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = new ArrayList<>();
        pages.add(new FriendFragment());
        pages.add(new CallLogFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}