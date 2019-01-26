package com.iseasoft.iseagoals.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iseasoft.iseagoals.R;
import com.iseasoft.iseagoals.Utils;
import com.iseasoft.iseagoals.models.League;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CanvasAdapter extends RecyclerView.Adapter<CanvasAdapter.ViewHolder> {

    private static final int MAX_VISIBLE_PALETTE_ITEM_COUNT = 10;
    private LeagueAdapter.ItemClickListener itemClickListener;
    private OnCanvasListener onCanvasListener;
    private WeakReference<Context> context;
    private ArrayList<League> data;
    private RecyclerView.RecycledViewPool mSharedPool = new RecyclerView.RecycledViewPool();

    public CanvasAdapter(Context context, ArrayList<League> data) {
        this.context = new WeakReference<>(context);
        this.data = data;
    }

    private OnCanvasListener getOnCanvasListener() {
        return onCanvasListener;
    }

    public void setOnCanvasListener(OnCanvasListener onCanvasListener) {
        this.onCanvasListener = onCanvasListener;
    }

    private LeagueAdapter.ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(LeagueAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(context.get()).inflate(R.layout.fragment_horizontal_league,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        League league = data.get(position);
        holder.tvLeagueName.setText(league.getName());

        if (league.getMatches().size() < MAX_VISIBLE_PALETTE_ITEM_COUNT) {
            holder.tvShowMore.setVisibility(View.GONE);
        } else {
            holder.tvShowMore.setVisibility(View.VISIBLE);
            holder.tvShowMore.setOnClickListener(v -> {
                if (getOnCanvasListener() != null) {
                    getOnCanvasListener().onShowMoreClicked(league);
                }
            });
        }

        LeagueAdapter dataAdapter = new LeagueAdapter(context.get(), league.getMatches(),
                MAX_VISIBLE_PALETTE_ITEM_COUNT, R.layout.item_match, false);
        dataAdapter.setItemClickListener(getItemClickListener());
        holder.rvLeague.setAdapter(dataAdapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<League> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    public interface OnCanvasListener {
        void onShowMoreClicked(League league);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLeagueName;
        final TextView tvShowMore;
        final RecyclerView rvLeague;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLeagueName = itemView.findViewById(R.id.tv_league_title);
            tvShowMore = itemView.findViewById(R.id.tv_show_more);
            rvLeague = itemView.findViewById(R.id.list);
            rvLeague.setRecycledViewPool(mSharedPool);
            Utils.modifyListViewForHorizontal(context.get(), rvLeague);
        }
    }
}
