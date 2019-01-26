package com.iseasoft.iseagoals.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseagoals.R;
import com.iseasoft.iseagoals.adapters.holders.MatchViewHolder;
import com.iseasoft.iseagoals.listeners.OnMatchListener;
import com.iseasoft.iseagoals.models.Match;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter {

    private List<Match> mItems;
    private Context mContext;
    private OnMatchListener mItemListener;

    public MatchAdapter(List<Match> items, OnMatchListener listener) {
        this.mItems = items;
        this.mItemListener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, null);

        MatchViewHolder matchViewHolder = new MatchViewHolder(v);
        return matchViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final MatchViewHolder matchViewHolder = (MatchViewHolder) holder;
        final Match match = mItems.get(position);
        matchViewHolder.setContent(match);
        matchViewHolder.setListener(mItemListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateMatchs(List<Match> items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
