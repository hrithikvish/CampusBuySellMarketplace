package com.hrithikvish.cbsm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hrithikvish.cbsm.ProfilePostsFragment
import com.hrithikvish.cbsm.ProfileSavedFragment

//tab layout adapter
class ViewPagerPostsAndSavedAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            ProfilePostsFragment()
        } else {
            ProfileSavedFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            "Your Items"
        } else {
            "Saved Items"
        }
    }

    override fun getCount(): Int {
        return 2
    }
}