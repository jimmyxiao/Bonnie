package com.sctw.bonniedraw.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.FollowFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.HotFragment;
import com.sctw.bonniedraw.fragment.PaintFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private BottomNavigationViewEx mBottomNavView;
    private ViewPager mViewPager;
    private VpAdapter adapter;
    private List<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mBottomNavView = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_view);
        mBottomNavView.setIconSize(36, 36);
        mBottomNavView.enableShiftingMode(false);
        mBottomNavView.enableItemShiftingMode(false);
        mBottomNavView.setItemHeight(BottomNavigationViewEx.dp2px(this, 66));
        mBottomNavView.setTextVisibility(false);
        fragments = new ArrayList<>();
        // create music fragment and add it
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        homeFragment.setArguments(bundle);

        // create backup fragment and add it
        HotFragment hotFragment = new HotFragment();
        bundle = new Bundle();
        hotFragment.setArguments(bundle);

        // create friends fragment and add it
        FollowFragment followFragment = new FollowFragment();
        bundle = new Bundle();
        followFragment.setArguments(bundle);

        PaintFragment paintFragment = new PaintFragment();
        bundle = new Bundle();
        paintFragment.setArguments(bundle);

        ProfileFragment profileFragment = new ProfileFragment();
        bundle = new Bundle();
        profileFragment.setArguments(bundle);

        // add to fragments for adapter
        fragments.add(homeFragment);
        fragments.add(hotFragment);
        fragments.add(paintFragment);
        fragments.add(followFragment);
        fragments.add(profileFragment);

        // set adapter
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

        // binding with ViewPager
        mBottomNavView.setupWithViewPager(mViewPager);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        if (mBottomNavView.getCurrentItem() != 0) {
            mBottomNavView.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
