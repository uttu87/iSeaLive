package com.iseasoft.iseagoals;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iseasoft.iseagoals.adapters.MatchAdapter;
import com.iseasoft.iseagoals.api.APIListener;
import com.iseasoft.iseagoals.api.ISeaLiveAPI;
import com.iseasoft.iseagoals.listeners.OnMatchListener;
import com.iseasoft.iseagoals.models.League;
import com.iseasoft.iseagoals.models.Match;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

import static com.iseasoft.iseagoals.ISeaLiveConstants.LEAGUE_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.MATCH_KEY;

public class MatchListFragment extends BaseFragment implements OnMatchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = MatchListFragment.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.tv_league_name)
    TextView tvLeagueName;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.rv_match_list)
    RecyclerView rvMatchList;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    ArrayList<Match> matches = new ArrayList<>();
    private MatchAdapter matchAdapter;
    private Match match;

    public static MatchListFragment newInstance(Match match) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MATCH_KEY, match);
        fragment.setArguments(args);
        return fragment;
    }

    public static MatchListFragment newInstance(League league) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putSerializable(LEAGUE_KEY, league);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        match = (Match) getArguments().getSerializable(MATCH_KEY);
        if (match != null) {
            ISeaLiveAPI.getInstance().getMatchList(match.getLeague(), new APIListener<ArrayList<Match>>() {
                @Override
                public void onRequestCompleted(ArrayList<Match> obj, String leagueName) {
                    if(!isStateSafe()) {
                        return;
                    }

                    for (Match m : obj) {
                        if(m.isLive() == match.isLive()) {
                            matches.add(m);
                        }
                    }
                    setupMatchList();
                    if(getActivity() instanceof MainActivity) {
                        tvLeagueName.setVisibility(View.GONE);
                    } else {
                        tvLeagueName.setText(leagueName);
                    }
                    btnShare.setVisibility(match.isYoutube() ? View.VISIBLE : View.GONE);
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onError(Error e) {

                }
            });
            return;
        }

        League league = (League) getArguments().getSerializable(LEAGUE_KEY);
        if (league != null) {
            matches = league.getMatches();
            setupMatchList();
            tvLeagueName.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void setupMatchList() {
        matchAdapter = new MatchAdapter(matches, this);
        rvMatchList.setAdapter(matchAdapter);
        setupGridViewSpanIfNeeded();
    }

    private void setupGridViewSpanIfNeeded() {
        ViewTreeObserver observer = rvMatchList.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
    }

    @Override
    public void onMatchItemClicked(Match match) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.navigationToPlayerScreen(match);

        if (baseActivity instanceof PlayerActivity) {
            baseActivity.finish();
        }
    }

    @Optional()
    @OnClick({R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                ((BaseActivity) getActivity()).shareApp(match);
                break;
        }
    }

    @Override
    public void onGlobalLayout() {
        if(!isStateSafe()) {
            return;
        }
        ViewTreeObserver o = rvMatchList.getViewTreeObserver();
        o.removeOnGlobalLayoutListener(this);
        int columnWidthInPx = (int) getContext().getResources().getDimension(R.dimen.card_view_width);
        int spanCount = Utils.getOptimalSpanCount(Utils.getScreenWidth(), columnWidthInPx);
        Utils.modifyRecylerViewForGridView(rvMatchList, spanCount, columnWidthInPx);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(rvMatchList != null) {
            ViewTreeObserver o = rvMatchList.getViewTreeObserver();
            o.removeOnGlobalLayoutListener(this);
        }
        matches = null;
        matchAdapter =null;
        match = null;
        unbinder.unbind();
    }
}
