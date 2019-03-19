package com.iseasoft.iseagoals.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iseasoft.iseagoals.ChannelListFragment;
import com.iseasoft.iseagoals.FullMatchFragment;
import com.iseasoft.iseagoals.HighlightFragment;
import com.iseasoft.iseagoals.LiveFragment;
import com.iseasoft.iseagoals.WebViewFrament;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_HIGHLIGHT = 0;
    private static final int TAB_LIVE = TAB_HIGHLIGHT + 1;
    private static final int TAB_LIVE_SCORE = TAB_LIVE + 1;
    private static final int TAB_SPORT_TV = TAB_LIVE_SCORE + 1;
    private static final int TAB_FULL_MATCH = TAB_SPORT_TV + 1;
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
            case TAB_HIGHLIGHT:
                frag = HighlightFragment.newInstance();
                break;
            case TAB_SPORT_TV:
                frag = ChannelListFragment.newInstance();
                break;
            case TAB_LIVE_SCORE:
                frag = WebViewFrament.newInstance();
                break;
            case TAB_FULL_MATCH:
                frag = FullMatchFragment.newInstance();
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
                title = "EVENTS";
                break;
            case TAB_LIVE_SCORE:
                title = "LIVE SCORE";
                break;
            case TAB_HIGHLIGHT:
                title = "HIGHLIGHTS";
                break;
            case TAB_SPORT_TV:
                title = "SPORT TV";
                break;
            case TAB_FULL_MATCH:
                title = "FULL MATCH";
                break;
        }
        return title;
    }
}