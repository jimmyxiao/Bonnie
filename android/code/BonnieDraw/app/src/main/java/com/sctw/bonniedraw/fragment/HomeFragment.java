package com.sctw.bonniedraw.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.works.WorkAdapterList;
import com.sctw.bonniedraw.works.WorkListOnClickListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView homeRecyclerView;
    Toolbar toolbar;
    private ImageButton toolbarSearch;
    FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_include);
        toolbar.setTitle("");
        toolbarSearch = (ImageButton) view.findViewById(R.id.toolbar_search);
        toolbarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "搜尋", Toast.LENGTH_SHORT).show();
            }
        });


        homeRecyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        ArrayList<String> myDataset = new ArrayList<>();
        myDataset.add("USER 8787");
        myDataset.add("USER 1123456789");
        myDataset.add("TEST2233456789");
        myDataset.add("TEST000123");
        myDataset.add("ALDSIJALI@ELQIELM");
        WorkAdapterList mAdapter = new WorkAdapterList(myDataset, new WorkListOnClickListener() {
            @Override
            public void onWorkClick(int postion) {
                Log.d("POSTION CLICK", "POSTION=" + String.valueOf(postion));
                fm = getChildFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.home_framelayout, new WorkFragment())
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onWorkExtraClick(final int postion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.works_extra_layout, null);
                builder.setView(dialogView);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("POSTION CLICK", "POSTION=" + String.valueOf(postion));
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setAdapter(mAdapter);
    }
}
