package com.iseasoft.iseafootball.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iseasoft.iseafootball.FullMatchFragment;
import com.iseasoft.iseafootball.HighlightFragment;
import com.iseasoft.iseafootball.IptvFragment;
import com.iseasoft.iseafootball.LiveFragment;
import com.iseasoft.iseafootball.WebViewFrament;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_HIGHLIGHT = 0;
    private static final int TAB_LIVE = TAB_HIGHLIGHT + 1;
    private static final int TAB_IPTV = TAB_LIVE + 1;
    private static final int TAB_LIVE_SCORE = TAB_IPTV + 1;
    private static final int TAB_FULL_MATCH = TAB_LIVE_SCORE + 1;
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
            case TAB_IPTV:
                frag = IptvFragment.newInstance();
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
            case TAB_IPTV:
                title = "IPTV";
                break;
            case TAB_FULL_MATCH:
                title = "FULL MATCH";
                break;
        }
        return title;
    }
}