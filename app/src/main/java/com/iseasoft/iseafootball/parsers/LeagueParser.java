package com.iseasoft.iseafootball.parsers;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iseasoft.iseafootball.models.League;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeagueParser {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String MATCH = "match";
    public static final String IS_HIDDEN = "isHidden";

    public static ArrayList<League> createLeagueFromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<League> leagues = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) return leagues;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            League league = createLeagueFromJSONObject(jsonObject);
            if (league.getMatches().size() > 0) {
                leagues.add(league);
            }
        }
        return leagues;
    }

    public static League createLeagueFromJSONObject(JSONObject jsonObject) throws JSONException {
        League league = new League();
        if (jsonObject.has(ID)) {
            league.setId(jsonObject.getInt(ID));
        }
        if (jsonObject.has(NAME)) {
            league.setName(jsonObject.getString(NAME));
        }
        if (jsonObject.has(DESCRIPTION)) {
            league.setDescription(jsonObject.getString(DESCRIPTION));
        }
        if (jsonObject.has(MATCH)) {
            league.setMatches(MatchParser.createMatchFromJSONArray(jsonObject.getJSONArray(MATCH)));
        }
        if (jsonObject.has(IS_HIDDEN)) {
            league.setHidden(jsonObject.getBoolean(IS_HIDDEN));
        }
        return league;
    }

    public static League createLeagueFromSnapshotDocument(QueryDocumentSnapshot document) throws JSONException {
        League league = new League();
        if (document.contains(ID)) {
            league.setId(Integer.parseInt((String) document.get(ID)));
        }
        if (document.contains(NAME)) {
            league.setName(document.getString(NAME));
        }
        if (document.contains(DESCRIPTION)) {
            league.setDescription(document.getString(DESCRIPTION));
        }
        if (document.contains(MATCH)) {
            ArrayList<Object> matches = (ArrayList<Object>) document.get(MATCH);
            league.setMatches(MatchParser.createMatchFromArrayObject(matches));
        }
        if (document.contains(IS_HIDDEN)) {
            league.setHidden(document.getBoolean(IS_HIDDEN));
        }
        return league;
    }
}
