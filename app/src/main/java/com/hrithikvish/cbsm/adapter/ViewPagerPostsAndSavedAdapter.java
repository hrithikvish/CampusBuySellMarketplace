package com.hrithikvish.cbsm.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hrithikvish.cbsm.ProfilePostsFragment;
import com.hrithikvish.cbsm.ProfileSavedFragment;

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
            return "Your Items";
        } else {
            return "Saved Items";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}