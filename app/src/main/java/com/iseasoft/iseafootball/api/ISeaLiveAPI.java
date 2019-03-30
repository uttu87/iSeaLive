package com.iseasoft.iseafootball.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iseasoft.iseafootball.LiveApplication;
import com.iseasoft.iseafootball.models.League;
import com.iseasoft.iseafootball.models.Match;
import com.iseasoft.iseafootball.parsers.LeagueParser;
import com.iseasoft.iseafootball.parsers.MatchParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iseasoft.iseafootball.ISeaLiveConstants.CONFIG_COLLECTION;
import static com.iseasoft.iseafootball.ISeaLiveConstants.FULL_MATCH_COLLECTION;
import static com.iseasoft.iseafootball.ISeaLiveConstants.LEAGUE_COLLECTION;
import static com.iseasoft.iseafootball.ISeaLiveConstants.LIVE_LEAGUE_COLLECTION;
import static com.iseasoft.iseafootball.ISeaLiveConstants.MATCH_KEY;

public class ISeaLiveAPI {
    private static final String TAG = ISeaLiveAPI.class.getSimpleName();
    private static final String MATCH_URL_REGEX = "Hosted by <a href=\"(.*?)\" target=\"_blank\"";
    private static final String URL_REGEX = "href=\"(.*?)\">";
    private static final String IMAGE_URL_REGEX = "poster:\"(.*?)\",name:";
    private static final String STREAM_URL_REGEX = "hls:\"(.*?)\"\\};settings";
    private static final String NAME_REGEX = "name:\"(.*?)\",contentTitle:";


    private static ISeaLiveAPI instance;

    public static String getBaseURLDev() {
        return "http://hoofoot.com";
    }

    public static synchronized ISeaLiveAPI getInstance() {
        if (instance == null) {
            instance = new ISeaLiveAPI();
        }
        return instance;
    }

    public void getConfig(APIListener<Task<QuerySnapshot>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CONFIG_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        listener.onRequestCompleted(task, task.getResult().getMetadata().toString());
                    } else {
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getAllLeague(APIListener<ArrayList<League>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(LEAGUE_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    boolean addDataSuccess = false;
                    if (task.isSuccessful()) {
                        ArrayList<League> leagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                JSONObject jsonObject = new JSONObject(document.getData());
                                //Log.d(TAG, jsonObject.toString());
                                League league = LeagueParser.createLeagueFromJSONObject(jsonObject);
                                if (!league.isHidden() && league.getMatches().size() > 0) {
                                    leagues.add(league);
                                }
                                addDataSuccess = true;
                            } catch (JSONException e) {
                                addDataSuccess = false;
                                e.printStackTrace();
                            }
                        }

                        if (addDataSuccess) {
                            listener.onRequestCompleted(leagues, "");
                        } else {
                            listener.onError(new Error("Get league list failed"));
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getLiveLeague(APIListener<ArrayList<League>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(LIVE_LEAGUE_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    boolean addDataSuccess = false;
                    if (task.isSuccessful()) {
                        ArrayList<League> leagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                JSONObject jsonObject = new JSONObject(document.getData(DocumentSnapshot.ServerTimestampBehavior.NONE));
                                //Log.d(TAG, jsonObject.toString());
                                //League league = LeagueParser.createLeagueFromJSONObject(jsonObject);
                                League league = LeagueParser.createLeagueFromSnapshotDocument(document);
                                if (!league.isHidden() && league.getMatches().size() > 0) {
                                    leagues.add(league);
                                }
                                addDataSuccess = true;
                            } catch (JSONException e) {
                                addDataSuccess = false;
                                e.printStackTrace();
                            }
                        }

                        if (addDataSuccess) {
                            listener.onRequestCompleted(leagues, "");
                        } else {
                            listener.onError(new Error("Get league list failed"));
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getFullMatchLeague(APIListener<ArrayList<League>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(FULL_MATCH_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    boolean addDataSuccess = false;
                    if (task.isSuccessful()) {
                        ArrayList<League> leagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                JSONObject jsonObject = new JSONObject(document.getData());
                                //Log.d(TAG, jsonObject.toString());
                                League league = LeagueParser.createLeagueFromJSONObject(jsonObject);
                                if (league.getMatches().size() > 0) {
                                    leagues.add(league);
                                }
                                addDataSuccess = true;
                            } catch (JSONException e) {
                                addDataSuccess = false;
                                e.printStackTrace();
                            }
                        }

                        if (addDataSuccess) {
                            listener.onRequestCompleted(leagues, "");
                        } else {
                            listener.onError(new Error("Get league list failed"));
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getMatchList(String league, APIListener<ArrayList<Match>> listener) {
        getMatchList(league, false, listener);
    }

    public void getMatchList(String league, boolean isFullMatch, APIListener<ArrayList<Match>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String leagueCollection = isFullMatch ? FULL_MATCH_COLLECTION : LEAGUE_COLLECTION;
        firebaseFirestore.collection(leagueCollection).document(league)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean addDataSuccess = false;
                        if (task.isSuccessful()) {
                            ArrayList<Match> matches = new ArrayList<>();
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, Object> data = documentSnapshot.getData();
                            if (data == null) {
                                listener.onError(new Error(task.getException()));
                                return;
                            }
                            ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) data.get(MATCH_KEY);
                            for (HashMap<String, String> tmp : list) {
                                JSONObject object = new JSONObject(tmp);
                                try {
                                    Match match = MatchParser.createMatchFromJSONObject(object);
                                    if (!match.isHidden() || LiveApplication.isDebugBuild()) {
                                        matches.add(match);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onError(new Error(e));
                                }
                            }
                            Collections.reverse(matches);
                            listener.onRequestCompleted(matches, (String) data.get("name"));
                        } else {
                            listener.onError(new Error(task.getException()));
                        }
                    }
                });
    }

    public String getMatchURLFromWeb(String url) {
        String matchUrl = null;
        Document document = readDataFromWeb(url);
        if (document == null) {
            return null;
        }
        String result = document.body().html();
        if (result == null) {
            return null;
        }

        Log.i(TAG, "html: " + result);
        Pattern p = Pattern.compile(MATCH_URL_REGEX);
        Matcher m = p.matcher(result);
        while (m.find()) {
            matchUrl = m.group(1);
            Log.i(TAG, matchUrl);
        }

        return matchUrl;
    }

    public Match createMatchFromWeb(String url) {
        Document document = readDataFromWeb(url);
        if (document == null) {
            return null;
        }
        String result = document.data();
        if (result == null) {
            return null;
        }
        Match match = new Match();
        Pattern p = Pattern.compile(IMAGE_URL_REGEX);
        Matcher m = p.matcher(result);
        while (m.find()) {
            String imageUrl = "http:" + m.group(1);
            Log.i("imageURL", imageUrl);
            match.setThumbnailUrl(imageUrl);
            String id = imageUrl.substring(imageUrl.length() - 11, imageUrl.length() - 6);
            Log.i("matchId", id);
            match.setId(Integer.valueOf(id));
        }
        p = Pattern.compile(NAME_REGEX);
        m = p.matcher(result);
        while (m.find()) {
            String name = m.group(1);
            Log.i("name", name);
            match.setName(name);

        }
        p = Pattern.compile(STREAM_URL_REGEX);
        m = p.matcher(result);
        while (m.find()) {
            String streamUrl = "http:" + m.group(1);
            Log.i("streamURL", streamUrl);
            match.setStreamUrl(streamUrl);
        }
        match.setLeague("1007");
        match.setType("highlight");
        match.setLive(false);
        return match;
    }

    private Document readDataFromWeb(String url) {
        try {
            return new DownloadData().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadData extends AsyncTask<String, Void, Document> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(params[0]).get();
                return document;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document result) {
            // Set description into TextView
        }
    }
}
