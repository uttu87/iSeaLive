package com.iseasoft.iseafootball.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iseasoft.iseafootball.LiveFragment;
import com.iseasoft.iseafootball.WebViewFrament;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_LIVE = 0;
    private static final int TAB_LIVE_SCORE = TAB_LIVE + 1;
    private static final int TAB_COUNT = TAB_LIVE_SCORE + 1;

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case TAB_LIVE:
                frag = LiveFragment.newInstance();
                break;
            case TAB_LIVE_SCORE:
                frag = WebViewFrament.newInstance();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case TAB_LIVE:
                title = "LIVE EVENTS";
                break;
            case TAB_LIVE_SCORE:
                title = "LIVE SCORE";
                break;
        }
        return title;
    }
}