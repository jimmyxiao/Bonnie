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
public class FollowOtherFragment extends Fragment {
    ArrayList<String> myDataset;
    RecyclerView followOtherRecyclerView;
    FollowOwnAdapterList mAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow_other, container, false);
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
        followOtherRecyclerView = (RecyclerView) view.findViewById(R.id.follow_other_recycler);
        mAdapter = new FollowOwnAdapterList(myDataset, new FollowOwnListOnClickListener() {
            @Override
            public void onFollowClick(int postion) {
                Log.d("POSTION CLICK", "No." + String.valueOf(postion));
            }
        });
        followOtherRecyclerView.setLayoutManager(linearLayoutManager);
        followOtherRecyclerView.setAdapter(mAdapter);
    }
}
