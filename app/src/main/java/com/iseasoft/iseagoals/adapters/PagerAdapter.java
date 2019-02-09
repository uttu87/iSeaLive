package com.iseasoft.iseagoals.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iseasoft.iseagoals.ChannelListFragment;
import com.iseasoft.iseagoals.HighlightFragment;
import com.iseasoft.iseagoals.WebViewFrament;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_HIGHLIGHT = 0;
    private static final int TAB_SPORT_TV = 1;
    private static final int TAB_LIVE_SCORE = 2;

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case TAB_HIGHLIGHT:
                frag = HighlightFragment.newInstance();
                break;
            case TAB_SPORT_TV:
                frag = ChannelListFragment.newInstance();
                break;
            case TAB_LIVE_SCORE:
                frag = WebViewFrament.newInstance();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case TAB_LIVE_SCORE:
                title = "LIVE SCORE";
                break;
            case TAB_HIGHLIGHT:
                title = "HIGHLIGHTS";
                break;
            case TAB_SPORT_TV:
                title = "SPORT TV";
                break;
        }
        return title;
    }
}