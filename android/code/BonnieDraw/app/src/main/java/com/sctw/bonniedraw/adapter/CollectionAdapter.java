package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sctw.bonniedraw.R;

import java.util.List;

/**
 * Created by Fatorin on 2017/11/20.
 */

public class CollectionAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private Context context;
    private String[] tabTitles;

    public CollectionAdapter(Context context, FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.context = context;
        this.mFragmentList = mFragmentList;
        tabTitles = new String[]{context.getString(R.string.all), context.getString(R.string.like_class)};
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
