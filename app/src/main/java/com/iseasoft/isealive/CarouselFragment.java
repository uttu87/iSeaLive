package com.iseasoft.isealive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.iseasoft.isealive.adapters.CarouselPagerAdapter;
import com.iseasoft.isealive.models.League;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.relex.circleindicator.CircleIndicator;

public class CarouselFragment extends BaseFragment {

    public static final String TAG = CarouselFragment.class.getSimpleName();
    private static final int TIME_INTERVAL = 5; //5 seconds

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    Unbinder unbinder;
    Handler mHandler;
    Runnable mRunnable;

    int page = 0;
    private League league;
    private CarouselPagerAdapter carouselPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carousel, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindListViewAdapter();
        resizeForAspectRatio(view);
        setupPageIndicator(view);
    }

    private void setupPageIndicator(View view) {
        indicator.setViewPager(viewPager);
        carouselPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        pageSwitcher(TIME_INTERVAL);
    }


    private void resizeForAspectRatio(View itemView) {
        int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int height = (widthPixels * 300) / 800;
        int infoBarHeight = Utils.dpToPx(120);
        itemView.getLayoutParams().height = height + infoBarHeight;
        setIndicatorPosition(height);
    }

    private void setIndicatorPosition(int height) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (indicator == null) {
                return;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
            layoutParams.topMargin = height - 25;
            indicator.setLayoutParams(layoutParams);
            indicator.setVisibility(View.VISIBLE);
        });
    }

    public void setData(League obj) {
        league = obj;
        if (carouselPagerAdapter != null) {
            carouselPagerAdapter.updateList(league.getMatches());
        }
        if(viewPager != null) {
            viewPager.setCurrentItem(0, true);
        }
    }

    private void bindListViewAdapter() {
        carouselPagerAdapter = new CarouselPagerAdapter();
        viewPager.setAdapter(carouselPagerAdapter);
        carouselPagerAdapter.setCarouselItemClickListener(carousel -> {
            Log.d(TAG, "carouselitemclicked:" + carousel.getName());
            ((BaseActivity) getActivity()).navigationToPlayerScreen(carousel);
        });
        if (league != null) {
            carouselPagerAdapter.updateList(league.getMatches());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mHandler = null;
        mRunnable = null;
        league = null;
        carouselPagerAdapter = null;
        unbinder.unbind();
    }

    public void pageSwitcher(int seconds) {
        mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                changePage();
                mHandler.postDelayed(mRunnable, 1000 * seconds);
            }
        };
        mHandler.postDelayed(mRunnable, 1000 * seconds);
    }

    private void changePage() {
        if (carouselPagerAdapter == null) {
            return;
        }

        if (viewPager == null) {
            return;
        }

        page = viewPager.getCurrentItem() + 1;
        if (page == carouselPagerAdapter.getCount()) {
            page = 0;
        }
        viewPager.setCurrentItem(page, true);
    }
}
