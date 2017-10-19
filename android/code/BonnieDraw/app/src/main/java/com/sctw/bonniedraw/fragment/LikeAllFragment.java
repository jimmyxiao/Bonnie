package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.works.WorkAdapterGrid;
import com.sctw.bonniedraw.works.WorkGridOnClickListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeAllFragment extends Fragment {
    ArrayList<String> myDataset;
    RecyclerView mRecyclerViewLikeAll;
    WorkAdapterGrid mAdapterGrid;
    GridLayoutManager gridLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_like_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDataset = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            myDataset.add(Integer.toString(i));
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerViewLikeAll = (RecyclerView) view.findViewById(R.id.recyclerView_like_all);
        mAdapterGrid = new WorkAdapterGrid(myDataset, new WorkGridOnClickListener() {
            @Override
            public void onWorkClick(int postion) {
                Log.d("POSTION CLICK","No."+String.valueOf(postion));
            }
        });
        mRecyclerViewLikeAll.setLayoutManager(gridLayoutManager);
        mRecyclerViewLikeAll.setAdapter(mAdapterGrid);
    }
}
