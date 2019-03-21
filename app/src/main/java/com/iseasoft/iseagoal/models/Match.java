package com.iseasoft.iseagoal.models;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Match extends ViewModel implements Serializable, Comparable {
    private int id;
    private String name;
    private String description;
    private String streamUrl;
    private ArrayList<String> streamUrls;
    private String thumbnailUrl;
    private String type;
    private String league;
    private Date time;
    private boolean isLive;
    private boolean isYoutube;
    private boolean isHidden;
    private boolean isFullMatch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public ArrayList<String> getStreamUrls() {
        return streamUrls;
    }

    public void setStreamUrls(ArrayList<String> streamUrls) {
        this.streamUrls = streamUrls;
        setStreamUrl(streamUrls.get(0));
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isYoutube() {
        return isYoutube;
    }

    public void setYoutube(boolean youtube) {
        isYoutube = youtube;
    }

    public boolean isHidden() {
        if (time != null) {
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(time);
            beginTime.add(Calendar.MINUTE, -30);

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(time);
            endTime.add(Calendar.HOUR, 2);

            Date now = new Date();

            return now.before(beginTime.getTime()) || now.after(endTime.getTime());
        }

        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isFullMatch() {
        return isFullMatch;
    }

    public void setFullMatch(boolean fullMatch) {
        isFullMatch = fullMatch;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Match match = (Match) o;
        return (int) (this.time.getTime() - match.getTime().getTime());
    }
}
