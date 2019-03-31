package com.iseasoft.iseagoal.adapters;

/*
  Created by fedor on 28.11.2016.
 */


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.iseasoft.iseagoal.PlayerActivity;
import com.iseasoft.iseagoal.R;
import com.iseasoft.iseagoal.models.M3UItem;
import com.iseasoft.iseagoal.models.Match;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.iseasoft.iseagoal.ISeaLiveConstants.MATCH_KEY;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> implements Filterable {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<M3UItem> mItem = new ArrayList<>();
    private TextDrawable textDrawable;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public PlaylistAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View sView = mInflater.inflate(R.layout.item_playlist, parent, false);
        return new ItemHolder(sView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final M3UItem item = mItem.get(position);
        if (item != null) {
            holder.update(item);
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public void update(List<M3UItem> _list) {
        this.mItem = _list;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() { //TODO search it on github
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItem.clear();
                mItem.addAll((ArrayList<M3UItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<M3UItem> resultList = new ArrayList<>();
                if (!(constraint.length() == 0)) {
                    final String filtePatt = constraint.toString().toLowerCase().trim();
                    for (M3UItem itm : mItem) {
                        if (itm.getItemName().toLowerCase().contains(filtePatt)) {
                            resultList.add(itm);
                        }
                    }
                }
                results.values = resultList;
                results.count = resultList.size();
                return results;
            }
        };
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final PackageManager pm = mContext.getPackageManager();
        TextView name;
        ImageView cImg;

        ItemHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = view.findViewById(R.id.item_name);
            cImg = view.findViewById(R.id.cimg);
        }

        void update(final M3UItem item) {
            try {
                name.setText(item.getItemName());
                int color = generator.getRandomColor();
                textDrawable = TextDrawable.builder()
                        .buildRoundRect(String.valueOf(item.getItemName().charAt(0)), color, 100);

                if (TextUtils.isEmpty(item.getItemIcon())) {
                    cImg.setImageDrawable(textDrawable);
                } else {
                    Picasso.with(mContext)
                            .load(item.getItemIcon())
                            .placeholder(textDrawable)
                            .error(textDrawable)
                            .into(cImg);
                }

            } catch (Exception ignored) {
            }
        }

        public void onClick(View v) {
            try {
                int position = getLayoutPosition();
                final M3UItem imm = mItem.get(position);
                Intent intent = new Intent(mContext, PlayerActivity.class);

                Match match = new Match();
                match.setName(imm.getItemName());
                match.setStreamUrl(imm.getItemUrl());
                match.setLeague("1017");

                Bundle bundle = new Bundle();
                bundle.putSerializable(MATCH_KEY, match);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }
}
