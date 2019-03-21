package com.iseasoft.iseagoal.models;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;

public class League extends ViewModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private ArrayList<Match> matches;
    private boolean isHidden;

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

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
