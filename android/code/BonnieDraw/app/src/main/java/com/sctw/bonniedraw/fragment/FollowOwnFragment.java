package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.works.FollowOwnAdapterList;
import com.sctw.bonniedraw.works.FollowOwnListOnClickListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowOwnFragment extends Fragment {

    ArrayList<String> myDataset;
    RecyclerView followOwnRecyclerView;
    FollowOwnAdapterList mAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow_own, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDataset = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            myDataset.add(Integer.toString(i));
        }

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        followOwnRecyclerView = (RecyclerView) view.findViewById(R.id.follow_own_recycler);
        mAdapter = new FollowOwnAdapterList(myDataset, new FollowOwnListOnClickListener() {
            @Override
            public void onFollowClick(int postion) {
                Log.d("POSTION CLICK", "No." + String.valueOf(postion));
            }
        });
        followOwnRecyclerView.setLayoutManager(linearLayoutManager);
        followOwnRecyclerView.setAdapter(mAdapter);
    }
}
