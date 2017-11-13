package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.NoticeAdapter;
import com.sctw.bonniedraw.utility.NoticeInfo;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    RecyclerView mRv;
    NoticeAdapter mAdapter;
    ArrayList<NoticeInfo> noticeInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRv=view.findViewById(R.id.recyclerView_notice);
        LinearLayoutManager lm=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        noticeInfoList=new ArrayList<>();
        noticeInfoList.add(new NoticeInfo());
        noticeInfoList.add(new NoticeInfo());
        noticeInfoList.add(new NoticeInfo());
        noticeInfoList.add(new NoticeInfo());
        noticeInfoList.add(new NoticeInfo());
        mAdapter =new NoticeAdapter(noticeInfoList);
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(lm);
    }
}
