package com.iseasoft.isealive.parsers;

import com.iseasoft.isealive.LiveApplication;
import com.iseasoft.isealive.models.Match;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MatchParser {

    public static final String IS_LIVE = "isLive";
    public static final String IS_YOUTUBE = "isYoutube";
    public static final String IS_HIDDEN = "isHidden";
    public static final String LEAGUE = "league";
    public static final String TYPE = "type";
    public static final String IMAGE_URL = "imageURL";
    public static final String STREAM_URL = "streamURL";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ID = "id";

    public static ArrayList<Match> createMatchFromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Match> matches = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) {
            return matches;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Match match = createMatchFromJSONObject(jsonObject);
            if (!match.isHidden() || LiveApplication.isDebugBuild()) {
                matches.add(match);
            }
        }
        Collections.reverse(matches);
        return matches;
    }

    public static Match createMatchFromJSONObject(JSONObject jsonObject) throws JSONException {
        Match match = new Match();
        if (jsonObject.has(ID)) {
            match.setId(jsonObject.getInt(ID));
        }
        if (jsonObject.has(NAME)) {
            match.setName(jsonObject.getString(NAME));
        }

        if (jsonObject.has(DESCRIPTION)) {
            match.setDescription(jsonObject.getString(DESCRIPTION));
        }

        if (jsonObject.has(STREAM_URL)) {
            match.setStreamUrl(jsonObject.getString(STREAM_URL));
        }
        if (jsonObject.has(IMAGE_URL)) {
            match.setThumbnailUrl(jsonObject.getString(IMAGE_URL));
        }
        if (jsonObject.has(TYPE)) {
            match.setType(jsonObject.getString(TYPE));
        }
        if (jsonObject.has(LEAGUE)) {
            match.setLeague(jsonObject.getString(LEAGUE));
        }
        if (jsonObject.has(IS_LIVE)) {
            match.setLive(jsonObject.getBoolean(IS_LIVE));
        }
        if (jsonObject.has(IS_YOUTUBE)) {
            match.setYoutube(jsonObject.getBoolean(IS_YOUTUBE));
        }
        if (jsonObject.has(IS_HIDDEN)) {
            match.setHidden(jsonObject.getBoolean(IS_HIDDEN));
        } else {
            match.setHidden(false);
        }
        return match;
    }
}
