package com.youtube.android.Fragment1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class FragmentAdapter extends FragmentPagerAdapter {


    FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0 :
                return new StatusFragment();
            case 1 :
                return new ChatFragment();
            case 2 :
                return new FriendsFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "Status";
            case 1:
                return "Message";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}
