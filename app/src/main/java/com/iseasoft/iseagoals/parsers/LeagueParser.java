package com.iseasoft.iseagoals.parsers;

import com.iseasoft.iseagoals.models.League;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeagueParser {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String MATCH = "match";

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
        return league;
    }
}
