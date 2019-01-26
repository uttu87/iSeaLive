package com.iseasoft.iseagoals.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseagoals.R;
import com.iseasoft.iseagoals.adapters.holders.ChannelViewHolder;
import com.iseasoft.iseagoals.listeners.OnMatchListener;
import com.iseasoft.iseagoals.models.Match;

import java.util.ArrayList;
import java.util.List;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    private final String TAG = ChannelListAdapter.class.getSimpleName();
    private List<Match> mItems;
    private Context mContext;
    private OnMatchListener mItemListener;

    public ChannelListAdapter(ArrayList<Match> items, OnMatchListener listener) {
        this.mItems = items;
        this.mItemListener = listener;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder viewHolder, int position) {
        final Match match = mItems.get(position);
        viewHolder.setContent(match);
        viewHolder.setListener(mItemListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
