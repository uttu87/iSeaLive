package com.iseasoft.iseagoal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.iseasoft.iseagoal.adapters.PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        setupHomeView();
        checkToPlayFromPushNotification();
    }

    private void checkToPlayFromPushNotification() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(ISeaLiveConstants.PUSH_URL_KEY);
            String message = intent.getStringExtra(ISeaLiveConstants.PUSH_MESSAGE);

            if (!TextUtils.isEmpty(url)) {
                Intent playerIntent = new Intent(this, PlayerActivity.class);
                playerIntent.putExtra(ISeaLiveConstants.PUSH_URL_KEY, url);
                playerIntent.putExtra(ISeaLiveConstants.PUSH_MESSAGE, message);
                startActivity(playerIntent);
            }
        }
    }

    private void setupHomeView() {
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
