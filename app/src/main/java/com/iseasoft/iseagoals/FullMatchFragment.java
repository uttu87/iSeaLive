package com.iseasoft.iseagoals;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iseasoft.iseagoals.adapters.CanvasAdapter;
import com.iseasoft.iseagoals.adapters.LeagueAdapter;
import com.iseasoft.iseagoals.api.APIListener;
import com.iseasoft.iseagoals.api.ISeaLiveAPI;
import com.iseasoft.iseagoals.models.League;
import com.iseasoft.iseagoals.models.Match;
import com.iseasoft.iseagoals.parsers.LeagueParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.iseasoft.iseagoals.ISeaLiveConstants.ACTIVE_ADS_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.CAROUSEL_ID;
import static com.iseasoft.iseagoals.ISeaLiveConstants.SPORT_TV_ID;
import static com.iseasoft.iseagoals.ISeaLiveConstants.USE_ONLINE_DATA_FLAG_KEY;

@SuppressWarnings("WeakerAccess")
public class FullMatchFragment extends BaseFragment {

    public static final String TAG = FullMatchFragment.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.shimmer_container)
    ShimmerFrameLayout mShimmerViewContainer;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_league)
    RecyclerView rvLeagueList;
    long homeScreenRequestStartedAt;
    private boolean init = false;
    private ArrayList<League> mLeagues = new ArrayList<>();
    private CanvasAdapter mCanvasAdapter;

    public static FullMatchFragment newInstance() {
        return new FullMatchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_match, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showShimmer();
        Utils.modifyListViewForVertical(getContext(), rvLeagueList);

        if (LiveApplication.isUseOnlineData()) {
            load();
        } else {
            requestConfig();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestConfig();
            }
        });
    }

    private void fetchOnlineData() {
        homeScreenRequestStartedAt = System.currentTimeMillis();
        ISeaLiveAPI.getInstance().getAllLeague(new APIListener<ArrayList<League>>() {
            @Override
            public void onRequestCompleted(ArrayList<League> leagues, String json) {
                if (!isStateSafe()) {
                    return;
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                applyLeague(leagues);

                refresh();
                hideShimmer();
            }

            @Override
            public void onError(Error e) {
                hideShimmer();
            }
        });
    }

    private void applyLeague(ArrayList<League> leagues) {
        mLeagues.clear();

        for (League league : leagues) {
            if (league.getId() == SPORT_TV_ID) {
                continue;
            }
            League highlightLeague = getHighlightLeague(league);
            if (highlightLeague.getMatches().size() > 0) {
                if (league.getId() != CAROUSEL_ID) {
                    mLeagues.add(highlightLeague);
                }
            }
        }
    }

    public void getData() {
        boolean useOnlineData = LiveApplication.isUseOnlineData();
        if (useOnlineData) {
            fetchOnlineData();
        } else {
            fetchLocalData();
        }
    }

    private void showShimmer() {
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();
        }
    }

    private void hideShimmer() {
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.startShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        mCanvasAdapter = null;
        mLeagues = null;
        unbinder.unbind();
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
        if (mLeagues == null) {
            return;
        }

        if (mLeagues.size() > 0) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            hideShimmer();
            return;
        }
        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("league");
            ArrayList<League> leagues = LeagueParser.createLeagueFromJSONArray(m_jArry);

            applyLeague(leagues);

            refresh();
            hideShimmer();
        } catch (JSONException e) {
            hideShimmer();
            e.printStackTrace();
        }
    }

    @NonNull
    private League getHighlightLeague(League league) {
        League highlightLeague = new League();
        highlightLeague.setId(league.getId());
        highlightLeague.setName(league.getName());
        highlightLeague.setDescription(league.getDescription());
        highlightLeague.setMatches(new ArrayList<Match>());
        for (Match match : league.getMatches()) {
            if (!match.isLive()) {
                highlightLeague.getMatches().add(match);
            }
        }
        return highlightLeague;
    }


    public void load() {
        if (init) {
            return;
        }

        getData();
        init = true;
    }

    public void refresh() {
        setupLeagueAdapter();
    }

    private void setupLeagueAdapter() {
        if (mCanvasAdapter != null) {
            mCanvasAdapter.updateData(mLeagues);
            return;
        }
        mCanvasAdapter = new CanvasAdapter(getContext(), mLeagues);
        mCanvasAdapter.setOnCanvasListener(league -> {
            //TODO show league match
            ((BaseActivity) getActivity()).navigationToLeagueScreen(league);
        });
        mCanvasAdapter.setItemClickListener(new LeagueAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(Match item) {
                ((BaseActivity) getActivity()).navigationToPlayerScreen(item);
            }

            @Override
            public void onItemRemove(Match item) {

            }
        });
        rvLeagueList.setAdapter(mCanvasAdapter);
    }

    private void addDataToFireStore(ArrayList<League> leagues) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        for (League league : leagues) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", league.getId());
            data.put("name", league.getName());
            data.put("description", league.getDescription());
            data.put("match", league.getMatches().toArray().toString());
            firebaseFirestore.collection("league")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    public void requestConfig() {
        ISeaLiveAPI.getInstance().getConfig(new APIListener<Task<QuerySnapshot>>() {
            @Override
            public void onRequestCompleted(Task<QuerySnapshot> tasks, String json) {
                boolean isActiveAds = false;
                boolean useOnlineData = false;
                if (tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        try {
                            JSONObject jsonObject = new JSONObject(document.getData());
                            if (jsonObject.has(ACTIVE_ADS_KEY)) {
                                isActiveAds = jsonObject.getBoolean(ACTIVE_ADS_KEY);
                            }

                            if (jsonObject.has(USE_ONLINE_DATA_FLAG_KEY)) {
                                useOnlineData = jsonObject.getBoolean(USE_ONLINE_DATA_FLAG_KEY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                LiveApplication.setActiveAds(isActiveAds);
                LiveApplication.setUseOnlineData(useOnlineData);
                getData();

            }

            @Override
            public void onError(Error e) {

            }
        });
    }
}
