package com.iseasoft.iseagoal.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iseasoft.iseagoal.R;
import com.iseasoft.iseagoal.listeners.OnMatchListener;
import com.iseasoft.iseagoal.models.Match;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_view)
    LinearLayout cardView;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.live_badge)
    TextView liveBadge;

    private Match match;
    private OnMatchListener listener;

    public MatchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public OnMatchListener getListener() {
        return listener;
    }

    public void setListener(OnMatchListener listener) {
        this.listener = listener;
    }

    public void setContent(Match match) {
        Context context = itemView.getContext();
        if (context == null) return;
        this.match = match;
        textView.setText(match.getName());
        loadImage(match, context);
        liveBadge.setVisibility(match.isLive() ? View.VISIBLE : View.GONE);
        TextView time = itemView.findViewById(R.id.time);
        if (time != null) {
            if (match.getTime() != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("hh:mm a");
                time.setText(fmt.format(match.getTime()));
                time.setVisibility(View.VISIBLE);
            } else {
                time.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.btn_play, R.id.mainView})
    public void onClick(View view) {
        if (getListener() == null) {
            return;
        }
        getListener().onMatchItemClicked(match);
    }


    private void loadImage(Match match, Context context) {
        String imageUrl = match.getThumbnailUrl();
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    public LinearLayout getCardView() {
        return cardView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getLiveBadge() {
        return liveBadge;
    }

    public Match getMatch() {
        return match;
    }
}
