package com.iseasoft.iseafootball.adapters;

import android.content.Context;
import android.os.Debug;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseafootball.adapters.holders.MatchViewHolder;
import com.iseasoft.iseafootball.models.Match;

import java.util.ArrayList;


public class LeagueAdapter extends RecyclerView.Adapter<MatchViewHolder> {
    private final int itemLayoutResourceId;
    private final boolean isRemovable;
    private final boolean showBadge;
    private final Context context;
    private ItemClickListener itemClickListener;
    private ArrayList<Match> list;
    private int maxItemCount = 5;

    public LeagueAdapter(Context context,
                         ArrayList<Match> list,
                         int maxItemCount,
                         int itemLayoutResourceId,
                         boolean isRemovable) {
        this.context = context;
        this.list = list;
        this.maxItemCount = maxItemCount;
        this.itemLayoutResourceId = itemLayoutResourceId;
        this.isRemovable = isRemovable;
        this.showBadge = Debug.isDebuggerConnected();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void updateList(ArrayList<Match> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResourceId, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        Match item = list.get(position);

        holder.setContent(item);

        holder.setListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClicked(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (maxItemCount == -1) { // show all
            return list.size();
        } else {
            return list.size() > maxItemCount ? maxItemCount : list.size();
        }
    }

    public interface ItemClickListener {
        void onItemClicked(Match media);

        void onItemRemove(Match item);
    }
}
