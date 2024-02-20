package com.example.bitmarket.adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bitmarket.Fragments.OutOfDateFragment;
import com.example.bitmarket.Fragments.RunningFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RunningFragment(); // Create the RunningFragment
            case 1:
                return new OutOfDateFragment(); // Create the OutOfDateFragment
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Running"; // Tab title for the first tab
            case 1:
                return "Out of Date"; // Tab title for the second tab
            default:
                return null;
        }
    }
}
