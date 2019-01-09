package com.iseasoft.isealive.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iseasoft.isealive.R;
import com.iseasoft.isealive.models.Match;

import java.util.ArrayList;
import java.util.List;

public class CarouselPagerAdapter extends PagerAdapter {

    private final Fragment fragment;
    private CarouselPagerAdapter.CarouselItemClickListener carouselItemClickListener;
    //    private final ImageLoader imageLoader;
    private List<Match> items;

    public CarouselPagerAdapter(Fragment fragment) {
        this.fragment = fragment;
        items = new ArrayList<>();
//        imageLoader = Manager.getInstance().getImageLoader();
    }

    public void setCarouselItemClickListener(CarouselPagerAdapter.CarouselItemClickListener carouselItemClickListener) {
        this.carouselItemClickListener = carouselItemClickListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public void updateList(List<Match> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Match carousel = items.get(position);

        View v = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_carousel, null, false);

        ImageView img = v.findViewById(R.id.imageView);
        TextView tv = v.findViewById(R.id.textView);

        tv.setText(carousel.getName());
        if (carousel.getThumbnailUrl() != null) {
            Glide.with(fragment).load(carousel.getThumbnailUrl()).into(img);
        }
        v.setOnClickListener(v1 -> {
            if (carouselItemClickListener != null) {
                carouselItemClickListener.onCarouselItemClicked(carousel);
            }
        });

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }

    public interface CarouselItemClickListener {
        void onCarouselItemClicked(Match carousel);
    }

}