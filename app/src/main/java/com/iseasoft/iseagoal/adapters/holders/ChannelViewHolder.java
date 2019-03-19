package com.iseasoft.iseagoal.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iseasoft.iseagoal.R;
import com.iseasoft.iseagoal.listeners.OnMatchListener;
import com.iseasoft.iseagoal.models.Match;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.img_logo)
    ImageView logo;
    @BindView(R.id.channel_badge)
    TextView channelBadge;

    private Match match;
    private OnMatchListener listener;

    public ChannelViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public OnMatchListener getListener() {
        return listener;
    }

    @OnClick({R.id.img_logo})
    public void onClick(View view) {
        if (getListener() == null) {
            return;
        }
        getListener().onMatchItemClicked(match);
    }

    public void setListener(OnMatchListener listener) {
        this.listener = listener;
    }

    public void setContent(Match match) {
        this.match = match;
        Context context = logo.getContext();
        String imageUrl = match.getThumbnailUrl();
        Glide.with(context)
                .load(imageUrl)
                .into(logo);
        setListener(getListener());
        if(!TextUtils.isEmpty(match.getDescription())) {
            channelBadge.setVisibility(View.VISIBLE);
            channelBadge.setText(match.getDescription());
            channelBadge.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            channelBadge.setSelected(true);
        } else {
            channelBadge.setVisibility(View.GONE);
        }
    }
}

