package com.hrithikvish.cbsm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

//tab layout adapter
public class ViewPagerPostsAndSavedAdapter extends FragmentStatePagerAdapter {
    public ViewPagerPostsAndSavedAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new ProfilePostsFragment();
        } else {
            return new ProfileSavedFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return "Posts";
        } else {
            return "Saved";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}