package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.CollectionAdapter;
import com.sctw.bonniedraw.widget.CollectionDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageButton mImgBtnBack, mImgBtnAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImgBtnBack = (ImageButton) view.findViewById(R.id.imgBtn_collection_back);
        mImgBtnAdd = (ImageButton) view.findViewById(R.id.imgBtn_collection_add);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout_collection);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager_collection);
        setViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
        setOnClick();
    }

    private void setOnClick() {
        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mImgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionDialog dialog=new CollectionDialog();
                dialog.show(getFragmentManager(),"Collection");
            }
        });
    }

    private void setViewPager() {
        CollectionAllFragment collectionAllFragment = new CollectionAllFragment();
        CollectionSelectFragment collectionSelectFragment = new CollectionSelectFragment();
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(collectionAllFragment);
        fragmentList.add(collectionSelectFragment);
        CollectionAdapter myFragmentAdapter = new CollectionAdapter(getChildFragmentManager(), fragmentList);
        mViewPager.setAdapter(myFragmentAdapter);
    }

}
