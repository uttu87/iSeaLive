package com.iseasoft.isealive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.iseasoft.isealive.adapters.ChannelListAdapter;
import com.iseasoft.isealive.adapters.MatchAdapter;
import com.iseasoft.isealive.api.APIListener;
import com.iseasoft.isealive.api.ISeaLiveAPI;
import com.iseasoft.isealive.listeners.OnMatchListener;
import com.iseasoft.isealive.models.League;
import com.iseasoft.isealive.models.Match;
import com.iseasoft.isealive.parsers.LeagueParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

import static com.iseasoft.isealive.ISeaLiveConstants.SPORT_TV_ID;

public class ChannelListFragment extends BaseFragment implements OnMatchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = ChannelListFragment.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.rv_match_list)
    RecyclerView rvMatchList;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    ArrayList<Match> matches = new ArrayList<>();
    private ChannelListAdapter channelListAdapter;

    public static ChannelListFragment newInstance() {
        ChannelListFragment fragment = new ChannelListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        if(LiveApplication.isUseOnlineData()) {
            fetchOnlineData();
        } else {
            fetchLocalData();
        }

    }

    private void fetchOnlineData() {
        ISeaLiveAPI.getInstance().getMatchList(String.valueOf(SPORT_TV_ID), new APIListener<ArrayList<Match>>() {
            @Override
            public void onRequestCompleted(ArrayList<Match> obj, String leagueName) {
                if (!isStateSafe()) {
                    return;
                }
                matches = obj;
                setupMatchList();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Error e) {

            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void fetchLocalData() {
        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("league");
            ArrayList<League> leagues = LeagueParser.createLeagueFromJSONArray(m_jArry);
            for (League league : leagues) {
                if(league.getId() == SPORT_TV_ID) {
                    matches = league.getMatches();
                    setupMatchList();
                    progressBar.setVisibility(View.GONE);
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupMatchList() {
        channelListAdapter = new ChannelListAdapter(matches, this);
        rvMatchList.setAdapter(channelListAdapter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(rvMatchList != null) {
            ViewTreeObserver o = rvMatchList.getViewTreeObserver();
            o.removeOnGlobalLayoutListener(this);
        }
        matches = null;
        channelListAdapter = null;
        unbinder.unbind();
    }

    @Override
    public void onGlobalLayout() {
        if(!isStateSafe()) {
            return;
        }
        ViewTreeObserver o = rvMatchList.getViewTreeObserver();
        o.removeOnGlobalLayoutListener(this);
        int columnWidthInPx = (int) getContext().getResources().getDimension(R.dimen.channel_view_width);
        int spanCount = Utils.getOptimalSpanCount(Utils.getScreenWidth(), columnWidthInPx);
        Utils.modifyRecylerViewForGridView(rvMatchList, spanCount, columnWidthInPx);
    }
}
