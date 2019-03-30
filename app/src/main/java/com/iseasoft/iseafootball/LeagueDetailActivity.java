package com.iseasoft.iseafootball;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.iseasoft.iseafootball.models.League;
import com.iseasoft.iseafootball.utils.Utils;

import static com.iseasoft.iseafootball.ISeaLiveConstants.LEAGUE_KEY;

public class LeagueDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_league_detail);
        super.onCreate(savedInstanceState);
        League league = (League) getIntent().getExtras().getSerializable(LEAGUE_KEY);
        if (league != null) {
            Utils.setupToolbar(this, league.getName());
            setupMatchList(league);
        }
    }

    private void setupMatchList(League league) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MatchListFragment matchListFragment = MatchListFragment.newInstance(league);
        ft.replace(R.id.match_list, matchListFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
