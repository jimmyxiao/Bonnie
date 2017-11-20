package com.sctw.bonniedraw.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Fatorin on 2017/11/20.
 */

public class CollectionAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private String[] tabTitles = new String[]{"全部","收藏分類"};

    public CollectionAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
