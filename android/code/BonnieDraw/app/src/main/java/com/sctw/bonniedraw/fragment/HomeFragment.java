package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.works.WorkAdapterList;
import com.sctw.bonniedraw.works.WorkListOnClickListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView homeRecyclerView;
    Toolbar toolbar;
    ImageButton toolbarSearch, icBtnDrawer;
    FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_include);
        toolbar.setTitle("");
        icBtnDrawer = (ImageButton) view.findViewById(R.id.toolbar_switch);
        toolbarSearch = (ImageButton) view.findViewById(R.id.toolbar_search);
        toolbarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "搜尋", Toast.LENGTH_SHORT).show();
            }
        });
        icBtnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.main_actitivy_drawlayout)).openDrawer(Gravity.LEFT);
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
            public void onWorkImgClick(int postion) {
                Log.d("POSTION CLICK", "POSTION=" + String.valueOf(postion));
                fm = getChildFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.home_framelayout, new WorkFragment())
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onWorkExtraClick(int postion) {
                final FullScreenDialog extraDialog = new FullScreenDialog(getActivity(), R.layout.works_extra_layout);
                Button extraShare = extraDialog.findViewById(R.id.btn_extra_share);
                Button extraCopyLink = extraDialog.findViewById(R.id.btn_extra_copylink);
                Button extraReport = extraDialog.findViewById(R.id.btn_extra_report);
                Button extraCancel = extraDialog.findViewById(R.id.btn_extra_cancel);
                extraDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogStyle;
                extraShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraShare");
                        extraDialog.dismiss();
                    }
                });

                extraCopyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraCopyLink");
                        extraDialog.dismiss();
                    }
                });

                extraReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("POSTION CLICK", "extraReport");
                        extraDialog.dismiss();
                    }
                });

                extraCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        extraDialog.dismiss();
                    }
                });

                extraDialog.findViewById(R.id.bg_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        extraDialog.dismiss();
                    }
                });

                extraDialog.show();
            }

            @Override
            public void onWorkGoodClick(int postion) {

            }

            @Override
            public void onWorkMsgClick(int postion) {

            }

            @Override
            public void onWorkShareClick(int postion) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setAdapter(mAdapter);
    }
}
